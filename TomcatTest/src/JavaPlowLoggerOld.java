import java.io.*;
import java.io.Reader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.*;
import org.apache.commons.codec.binary.Base64;
import java.sql.Timestamp;
import java.sql.Date;

public class JavaPlowLoggerOld extends HttpServlet {
    //Requires a properties file to convert short-hand names to full. Properties file currently located on google drive.
    private static String pathToProp = "C:\\Apache\\apache-tomcat-8.0.8\\webapps\\JavaPlow\\WEB-INF\\context\\";
    private static Map<String, String> tableHeads = loadMap(pathToProp+"propMap.properties");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type of the response message
        response.setContentType("text/html");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        try {
            out.println("<html>");
            out.println("<head><title>JavaPlow Tracker</title>" +
                    "<link href=\"stylesheet.css\" rel=\"stylesheet\"/>\n</head>");
            out.println("<body>");
            out.println("<h1>Enter your Query!</h1>");  // says Hello
            // Echo client's request information
            out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
            out.println("<p>Method: " + request.getMethod() + "</p>");
            out.println("<p>GET Request: " + request.getQueryString() + "</p>");

            //Write parameter values to an html table
            out.println("<table><tr><th>Param</th><th>Value</th></tr>");
            Map<String,String[]> params = getBufferMap(request);
            for (Map.Entry<String,String[]> entry : params.entrySet()){
                if (entry.getKey().equals("cx") || entry.getKey().equals("ue_px"))
                    entry.setValue(new String[] {base64Decode(entry.getValue()[0])});
                out.println("<tr><td>" + checkKey(entry.getKey()) + "</td><td>" + entry.getValue()[0] + "</td></tr>");
            }
            out.println("</table>");
            out.println("<p>Protocol: " + request.getProtocol() + "</p>");
            out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
            out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
            //Write the log file
            if (!params.isEmpty())
                out.println("<p>Log file written to: " + writeLog(request));
            out.println("</body></html>");
        } finally {
            out.close();  // Always close the output writer
        }
    }

    public String writeLog(HttpServletRequest request){
        //Write the Log file
        BufferedWriter writer = null;
        String path = "ERROR - Encountered exception when writing log.";
        try {
            String localPath = "C:\\Apache\\apache-tomcat-8.0.8\\webapps\\JavaPlow\\Logs\\";
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            //Write the raw Query String
            File logFile = new File(localPath + "raw\\" + timeLog);
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(request.getQueryString());
            try { writer.close(); }
            catch (Exception e){ e.printStackTrace(); }
            //Write the cleaned data
            File logFileClean = new File(localPath + "clean\\" + timeLog + ".html");
            writer = new BufferedWriter(new FileWriter(logFileClean));

            writer.write("<table style = \"border:1px solid black\"><tr><th style = \"border:1px solid black\">Param</th><th style = \"border:1px solid black\">Value</th></tr>");
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
     * Takes the request parameters and adds the enrichment instance parameters. This is where Geo-IP would be added.
     * @param request The HTTP GET request.
     * @return A parameters map including the generated details
     */
    private Map<String,String[]> getBufferMap(HttpServletRequest request){
        Date coll_date = new Date(System.currentTimeMillis());
        Timestamp collector_tstamp = new Timestamp(coll_date.getTime());
        Map<String,String[]> params = new HashMap<String, String[]>();
        params.put("ctstamp", new String[] {collector_tstamp.toString()});
        params.putAll(request.getParameterMap());
        return params;
    }

    /**
     * Load the map from the properties file
     * @param fname The filename of the properties map to load.
     * @return Returns the loaded map.
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


      ////////////////
     //BEGIN FUTURE//
    ////////////////
    /**
     * FOR FUTURE DATABASE
     * @param ip
     * @return
     */
    public Map<String,String[]> geoData(String ip){
        Map<String,String[]> geoData = new HashMap<String, String[]>();
        File db = new File("C:\\Users\\Kevin Gleason\\IdeaProjects\\TomcatTest\\Geo Data\\GeoCity.mmdb");
        com.maxmind.db.Reader reader = null;
        try {
            reader = new com.maxmind.db.Reader(db);
            JsonNode response = reader.get(InetAddress.getByName(ip));
            if (response!=null) {
                geoData.put("geo_country_code", new String[]{(getJsonGeoInfo(response, "country", "iso_code", null))});
                geoData.put("geo_region", new String[]{response.get("subdivisions").get(0).get("iso_code").toString()});
                geoData.put("geo_city", new String[]{getJsonGeoInfo(response, "city", "names", "en")});
                geoData.put("geo_postcode", new String[]{getJsonGeoInfo(response, "postal", "code", null)});
                geoData.put("geo_latitude", new String[]{getJsonGeoInfo(response, "location", "latitude", null)});
                geoData.put("geo_longitude", new String[]{getJsonGeoInfo(response, "location", "longitude", null)});
            }
        } catch (IOException e) { e.printStackTrace(); }
        finally {
            try { reader.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        return geoData;
    }

    private String getJsonGeoInfo(JsonNode response, String p1, String p2, String p3){
        return p3==null ? response.get(p1).get(p2).toString() : response.get(p1).get(p2).get(p3).toString();
    }
      //////////////
     //END FUTURE//
    //////////////

    /**
     * Check the Key to see if it is in the buffered map, turn shorthand to full name
     * @param shorthand
     * @return
     */
    private String checkKey(String shorthand){
        if (this.tableHeads.containsKey(shorthand))
            return tableHeads.get(shorthand);
        return shorthand;
    }

    public static void main(String args[]) {
//        JavaPlowLogger jpl = new JavaPlowLogger();
        Timestamp tstamp = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        System.out.println(tstamp.getTime());
        System.out.println(new Date(System.currentTimeMillis()).toString());
        System.out.println(new Date(System.currentTimeMillis()));

    }
}