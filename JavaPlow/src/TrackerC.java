// File: Tracker.java
// Author: Kevin Gleason
// Date: 5/28/14
// Use: The implementation of the Tracker interface


/* To use:
 *   You must first declare a payload and a tracker.
 *   Build up the payload with whatever it needs
 *   Add the payload to Tracker with setPayload()
 *     Payload will configure itself further and prepare for request
 *   Call the Track specific track event
 *     Payload will be configured further
 *     Get request sent to server
 */

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerC implements Tracker {
    //Static Class variables
    private static final String VERSION = Version.VERSION;
    private static final String DEFAULT_PLATFORM = "pc";
    private static final String DEFAULT_VENDOR = "com.saggezza";

    //Instance Variables
    private PayloadMap payload = new PayloadMapC();
    private PlowContractor<String> stringContractor = new PlowContractor<String>();
    private PlowContractor<Integer> integerContractor = new PlowContractor<Integer>();
    private PlowContractor<Map<String, Object>> dictionaryContractor = new PlowContractor<Map<String, Object>>();
    private String collector_uri,
                   namespace,
                   app_id,
                   context_vendor;
    private boolean base64_encode,
                    contracts;


    //Base Constructor
    public TrackerC(String collector_uri, String namespace) {
        this.collector_uri = (collector_uri);
        this.namespace = namespace;
        this.app_id = this.context_vendor = null;
        this.base64_encode = this.contracts = true;
        this.setPayload(new PayloadMapC());
    }

    //Constructor with all arguments
    public TrackerC(String collector_uri, String namespace, String app_id,
                    String context_vendor, boolean base64_encode, boolean contracts) {
        this.collector_uri = (collector_uri);
        this.namespace = namespace;
        this.app_id = app_id;
        this.context_vendor = context_vendor;
        this.base64_encode = base64_encode;
        this.contracts = contracts;
        this.setPayload(new PayloadMapC());
    }

    /* Tracker functions
     *   A fre types of tracker functions depending on object being tracked
     *   All call the main track() function after configuring payload.
     */
    public void track() throws URISyntaxException, ClientProtocolException, IOException{
        URI uri = buildURI("http", collector_uri, "/i");
        System.out.println("Payload:\n" + this.payload.toString());
        HttpGet httpGet = makeHttpGet(uri);
        System.out.println("URI: " + uri);
        System.out.println("Making HttpGet...");
        makeRequest(httpGet);
    }

    public void track_page_view(String page_url, String page_title, String referrer, String context)
            throws UnsupportedEncodingException, IOException, URISyntaxException, JSONException{
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload = this.payload.track_page_view_config(page_url, page_title, referrer, DEFAULT_VENDOR, jsonContext);
        }
        else {
            this.payload = this.payload.track_page_view_config(page_url, page_title, referrer, DEFAULT_VENDOR, null);

        }
        this.track();
    }

//    public void

    /* Web functions
     *   Functions used to configure the Get request
     *   Split into several functions to configure HTTP errors catches in the future
     *   buildURI sets all the parameters up for the HttpGet
     *   HttpGet makes an HttpGet object.
     */
    private URI buildURI(String scheme, String host, String path) throws URISyntaxException{
        URIBuilder uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path);
        Set<String> params = this.payload.getParamKeySet();
        for (String i : params){
            uri.setParameter(i, this.payload.getParam(i));
        }
        return uri.build();
    }

    // Make a HttpGet class based on the URI
    private HttpGet makeHttpGet(URI uri){
        return new HttpGet(uri);
    }

    // Make the request, do the work you need to, then close the response.
    // All acceptable status codes are in the 200s
    private void makeRequest(HttpGet httpGet)
        throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (Math.floor(statusCode/100)!=2){
            throw new Error("HTTP Error - Error code " + statusCode);
        }
        try{
            Header[] headers = response.getAllHeaders();
            for (Header h : headers)
                System.out.println(h.toString()); //DEBUG
        }
        finally {
            response.close();
        }
    }

    //Turn String input into JSONObject
    private JSONObject stringToJSON(String jsonStr) throws JSONException{
        return new JSONObject(jsonStr);
    }

    //View all headers on an HttpResponse
    private List<Object> viewHeaders(HttpResponse response) {
        HeaderIterator hi = response.headerIterator();
        List<Object> headers = new ArrayList<Object>();
        while (hi.hasNext())
            headers.add(hi.next());
        return headers;
    }

    //View headers corresponding to certain string
    private List<Object> viewHeaders(HttpResponse response, String s) {
        HeaderIterator hi = response.headerIterator(s);
        List<Object> headers = new ArrayList<Object>();
        while (hi.hasNext())
            headers.add(hi.next());
        return headers;
    }

    /* Setter functions
     *   Used to set different attributes of the payload.
     *   ID checker should be called on instantiation.
     *   Timestamp sets time value and allows overwriting
     *   Need to add Contractor checks for values.
     */

    // Will be used if employing custom contracts.
    public void setContractors(PlowContractor<Integer> integerContractor, PlowContractor<String> stringContractor,
                               PlowContractor<Map<String,Object>> dictionaryContractor){
        this.integerContractor=integerContractor;
        this.stringContractor=stringContractor;
        this.dictionaryContractor=dictionaryContractor;
    }

    //Needs to be called before tracking can accurately happen.
    public void setPayload(PayloadMap payload){
        this.payload=payload;
        this.payload = this.payload.add_config("encode_base64", this.base64_encode);
        setStandardNV();
    }

    //Only called once when the Payload class is attacked to the Tracker
    private void setStandardNV(){
        this.payload = this.payload.add_standard_nv_pairs(DEFAULT_PLATFORM, VERSION, this.namespace, "");
    }

    // Set a generic parameter - maybe not needed if using table, maybe unstructured
    public void setParam(String param, String val){
        this.payload = this.payload.add(param, val);
    }

    //Default platform is pc, only call if using different platform.
    public void setPlatform(String platform){//contract true
        assert this.stringContractor.checkContract(this.contracts, PlowContractor.is_supported_platform, platform);
        this.payload = this.payload.add("p", platform);
    }

    public void setUserID(String userID){
        assert this.stringContractor.checkContract(this.contracts, PlowContractor.non_empty_string, userID);
        this.payload = this.payload.add("uid", userID);
    }

    public void setScreenResolution(int width, int height){
        assert this.integerContractor.checkContract(this.contracts, PlowContractor.positive_number, height);
        assert this.integerContractor.checkContract(this.contracts, PlowContractor.positive_number, width);
        this.payload = this.payload.add("res", String.valueOf(width) + "x" + String.valueOf(height));
    }

    public void setColorDepth(int depth){
        assert this.integerContractor.checkContract(this.contracts, PlowContractor.positive_number, depth) || depth==0;
        this.payload = this.payload.add("cd", String.valueOf(depth));
    }

    public void setTimeZone(String timezone){
        assert this.stringContractor.checkContract(this.contracts, PlowContractor.non_empty_string, timezone);
        this.payload = this.payload.add("tz", timezone);
    }

    public void setLanguage(String language){
        assert this.stringContractor.checkContract(this.contracts, PlowContractor.non_empty_string, language);
        this.payload = this.payload.add("lang", language);
    }

    //Getter functions
    public PayloadMap getPayload(){ return this.payload; }

    //Test case main function
    public static void main(String[] args) throws URISyntaxException, IOException, ClientProtocolException, JSONException {
//        PayloadMap pd = new PayloadMapC();
//        Tracker t1 = new TrackerC("d31jxa70e9zxsp.cloudfront.net","Main Tracker");
//        t1.setPayload(pd);
        Tracker t1 = new TrackerC("d31jxa70e9zxsp.cloudfront.net", "Tracker Test", "JavaPlow", "com.saggezza", true,true);
        t1.setUserID("Kevin");
        t1.setLanguage("eng");
        t1.setPlatform("cnsl");
        t1.setScreenResolution(1260, 1080);
        String context = "{" +
                            " 'movie':'Godzilla', " +
                            " 'character':'Kevin', " +
                            " 'role':'Programmer' " +
                         "}";
        t1.track_page_view("www.saggezza.com", "Saggezza Home", "Kevin Gleason", context);
    }
}


