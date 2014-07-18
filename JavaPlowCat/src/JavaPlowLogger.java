import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.maxmind.geoip.*;

import org.apache.commons.codec.binary.Base64;
import java.sql.Timestamp;
import java.sql.Date;

/**
 * JavaPlowLogger is used to collect logs using snowplow tracker and Apache Tomcat.
 * This product includes GeoLite2 data created by MaxMind, available from
 * <a href="http://www.maxmind.com">http://www.maxmind.com</a>.
 * @author Kevin Gleason
 */
public class JavaPlowLogger extends HttpServlet {
    //Requires a properties file to convert short-hand names to full. Properties file currently located on google drive.
    private static String pathToProp = "/usr/share/tomcat7/webapps/JavaPlow/WEB-INF/classes/";
    private static Map<String, String> tableHeads = loadMap(pathToProp+"propMap.properties");
    public static boolean writeHTML=true;

    /**
     * The basic servlet function, tells how to handle an HTTP GET request and convert it into a log.
     * (Optional) writeHTML boolean can be used to bypass writing html to a page if using a TrackerC class and not
     * Tomcat web interface. Simply writes all data to logs.
     * @param request The http request received.
     * @param response The http get Response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (writeHTML) {
            // Set the response MIME type of the response message
            response.setContentType("text/html");
            // Allocate a output writer to write the response message into the network socket
            PrintWriter out = response.getWriter();

            // Write the response message, in an HTML page
            try {
                out.println("<html>" +
                        "<head><title>JavaPlow Tracker</title><link href=\"stylesheet.css\" rel=\"stylesheet\"/>\n</head>" +
                        "<body><div style='padding:10px; margin:0 auto;'>");
                out.println("<h1>Enter Your Query</h1>");

                // Echo client's request information
                out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
                out.println("<p>Method: " + request.getMethod() + "</p>");
                out.println("<p>GET Request: " + request.getQueryString() + "</p>");

                // Write parameter values to an html table
                out.println("<table><tr><th>Param</th><th>Value</th></tr>");
                Map<String, String[]> params = getBufferMap(request);
                for (Map.Entry<String, String[]> entry : params.entrySet()) {
                    if (entry.getKey().equals("cx") || entry.getKey().equals("ue_px"))
                        entry.setValue(new String[]{base64Decode(entry.getValue()[0])});
                    out.println("<tr><td>" + checkKey(entry.getKey()) + "</td><td>" + entry.getValue()[0] + "</td></tr>");
                }
                out.println("</table>");
                out.println("<p>Protocol: " + request.getProtocol() + "</p>");
                out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
                out.println("<p>Request ID: " + request.getRequestedSessionId() + "</p>");
                out.println("<p>Session ID: " + request.getSession().getId() + "</p>");
                out.println("<p>Remote User: " + request.getRemoteUser() + "</p>");
                out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
                out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");

                // Write the log file using the writeLog function
                if (!params.isEmpty())
                    out.println("<p>Log file written to: " + writeLog(request));
                out.println("</div></body></html>");
            } finally {
                out.close();
            }
        } else { writeLog(request); }
    }

    /**
     * Write the log to Raw and Clean files. The clean file is strictly for visualization
     * in html table format.
     * @param request The http request received.
     * @return Return a string of the path where the written raw log is located.
     */
    public String writeLog(HttpServletRequest request){
        //Write the Log file
        BufferedWriter writer = null;
        String path = "ERROR - Encountered exception when writing log. ";
        try {
            String localPath = "/home/saggezza/Applications/Tomcat/webapps/JavaPlow/Logs/";
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            //Write the raw Query String
            String queryString = request.getQueryString();
            boolean singleVar = queryString.contains("%2C") && queryString.contains("%3D");
            File logFile;
            if (singleVar) { logFile = new File(localPath + "single/" + timeLog);}
            else { logFile = new File(localPath + "raw/" + timeLog); }
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(queryString);
            try { writer.close(); }
            catch (Exception e){ e.printStackTrace(); }
            //Write the cleaned data
            if (!singleVar) {
                File logFileClean = new File(localPath + "clean/" + timeLog + ".html");
                writer = new BufferedWriter(new FileWriter(logFileClean));
                writer.write("<table style = \"border:1px solid black\"><tr><th style = \"border:1px solid black\">Param</th><th style = \"border:1px solid black\">Value</th></tr>");
            }
            for (Map.Entry<String, String[]> entry : getBufferMap(request).entrySet()) {
                if (entry.getKey().equals("cx") || entry.getKey().equals("ue_px"))
                    entry.setValue(new String[] {base64Decode(entry.getValue()[0])});
                writer.write("<tr><td style = \"border:1px solid black\">" + checkKey(entry.getKey()) + "</td><td style = \"border:1px solid black\">" + entry.getValue()[0] + "</td></tr>");
            }
            writer.write("</table>");

            //Set path for return
            path = logFile.getCanonicalPath();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try { writer.close(); }
            catch (Exception e){ e.printStackTrace(); }
        }
        return path;
    }

    /**
     * Base64 decode the encoded string sent from the tracker
     * @param input Base64 encoded string
     * @return Original string
     */
    private String base64Decode(String input){
        Base64 base = new Base64();
        byte[] output = base.decode(input.getBytes());
        return new String(output);
    }

    /**
     * Gather Geo-IP data using MaxMind Geo-IP Database lookup
     * @param ip The public ip address from the request
     * @return Map of all values to be added to parameters
     */
    public Map<String,String[]> geoData(String ip){

        Map<String,String[]> geoData = new HashMap<String, String[]>();
        File db = new File("/usr/share/GeoIP/GeoIP.dat");
        LookupService lookupService = null;
//        String ls = "/home/saggezza/IdeaProjects/JavaPlowCat/Geo Data/GeoLiteCity.dat";

        try {
            System.out.println("CHECK1");
            lookupService = new LookupService("/usr/share/GeoIP/GeoIP.dat", LookupService.GEOIP_STANDARD);
            System.out.println("CHECK2");
            Location l1 = lookupService.getLocation("38.106.245.180");//ip);

            if (l1!=null) {
                geoData.put("geo_country_code", new String[]{l1.countryCode});
                geoData.put("geo_region", new String[]{l1.region});
                geoData.put("geo_city", new String[]{l1.city});

                geoData.put("geo_postcode", new String[]{l1.postalCode});
                geoData.put("geo_latitude", new String[]{String.valueOf(l1.latitude)});
                geoData.put("geo_longitude", new String[]{String.valueOf(l1.longitude)});
            }
        } catch (IOException e) {
            System.out.println("ERROR"); e.printStackTrace(); }
        finally { lookupService.close(); }
        System.out.println("CHECK4");

        return geoData;
    }

    /**
     * Creates a map of additional enrichment features. Will be used to add custom data calculations such as Geo-IP
     * @param request The http request received.
     * @return A parameters map containing all additional values desired to analyze.
     */
    private Map<String,String[]> getBufferMap(HttpServletRequest request){
        Date coll_date = new Date(System.currentTimeMillis());
        Timestamp collector_tstamp = new Timestamp(coll_date.getTime());
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("ctstamp", new String[]{collector_tstamp.toString()});
//        params.putAll(geoData(request.getRemoteAddr()));
        params.putAll(request.getParameterMap());
        System.out.println(params.toString());
        return params;
    }

    /**
     * Load the map from the properties file.
     * @param fname The properties filename
     * @return The loaded properties file in a Map<String,String> form
     */
    private static Map<String,String> loadMap(String fname){
        Properties prop = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(fname);
            prop.load(reader);
        }
        catch (Exception e){ e.printStackTrace(); }
        finally {
            try{reader.close();}
            catch (Exception e){e.printStackTrace();}
        }
        Map<String,String> keyMap = new HashMap<String, String>();
        for (Map.Entry<Object,Object> entry : prop.entrySet())
            keyMap.put(entry.getKey().toString(), entry.getValue().toString());
        return keyMap;
    }

    /**
     * Check whenther or not the shorthand has a correlating key
     * @param shorthand The shorthand notation from the tracker
     * @return Full name value related to the shorthand
     */
    private String checkKey(String shorthand){
        if (this.tableHeads.containsKey(shorthand))
            return tableHeads.get(shorthand);
        return shorthand;
    }

    public static void main(String args[]) throws IOException{
        JavaPlowLogger jpl = new JavaPlowLogger();

//        File db = new File("C:\\Users\\Kevin Gleason\\IdeaProjects\\TomcatTest\\Geo Data\\GeoCity.mmdb");
        String filein = "/usr/share/tomcat7/webapps/JavaPlow/WEB-INF/context/GeoIPCity.dat";
//        Reader reader = new Reader(db);
//        InetAddress addr = InetAddress.getByName("38.106.245.180");
//        JsonNode response = reader.get(addr);
//        System.out.println(response);
//        System.out.println(AnalyticsRecipes.getDepthInfo(response, "country", "iso_code", "en"));
//        System.out.println(jpl.geoData("18.72.0.3"));
//        AnalyticsRecipes.showLocationInfo(jpl.geoData("38.106.245.180"));
//        LookupService lookupService = new LookupService(reader,
//                LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);
//        Location l1 = lookupService.getLocation("38.106.245.180");
//        AnalyticsRecipes.showLocationInfo(l1);
    }
}