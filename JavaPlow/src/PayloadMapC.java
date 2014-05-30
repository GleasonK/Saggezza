// File: PayloadMapC.java
// Author: Kevin Gleason
// Date: 5/28/14
// Use: The implementation for the PayloadMap interface

import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
// org.json class files used as well


public class PayloadMapC implements PayloadMap{
    private LinkedHashMap<String,String> parameters;
    private LinkedHashMap<String,Boolean> configurations;


    //Create empty PayloadMap
    public PayloadMapC(){
        this.parameters = new LinkedHashMap<String, String>();
        this.configurations = new LinkedHashMap<String, Boolean>();
        setTransactionID();
        setTimestamp();
    }

    //Fill tree with preset parameters
    public PayloadMapC(LinkedHashMap<String,String> parameters, LinkedHashMap<String,Boolean> configurations){
        this.parameters = parameters;
        this.configurations = configurations;
    }


    /* Transaction Configuration functions
     *   Sets the transaction id once for the life of the event
     *   Timestamp of the event -- Needed with SQL autofill?
     *   Left void/mutable since they take place on instantiation.
     */
    public void setTransactionID(){
        Random r = new Random(); //NEED ID RANGE
        int tid = r.nextInt(999999-100000+1) + 100000;
        this.parameters.put("tid", String.valueOf(tid));
    }

    public void setTimestamp(){
        Date date = new Date(System.currentTimeMillis());
        this.parameters.put("dtm", date.toString());
    }

    /* Addition functions
     *  Used to add different sources of key=>value pairs to a map.
     *  Map is then used to build "Associative array for getter function.
     *  Some use Base64 encoding
     */
    private String base64encode(String string) throws UnsupportedEncodingException{
        Base64 b64 = new Base64(true);
        String safe_str = b64.encodeBase64String(string.getBytes(StandardCharsets.US_ASCII));
        return safe_str;
    }

    public PayloadMap add(String key, String val){
        this.parameters.put(key, val);
        return new PayloadMapC(parameters, configurations);
    }

    public PayloadMap add(Map<String,String> dict){
        this.parameters.putAll(dict);
        return new PayloadMapC(parameters, configurations);
    }

    // Assert that JSONObject is not empty
    // How to handle the json input? Strings/Map/what?
    public PayloadMap add_json(JSONObject jsonObject, boolean encode_base64)
        throws UnsupportedEncodingException{
        //Encode parameter
        if (jsonObject == null)  ///CATCH IF JSON LEFT NULL
            return this;         ///need to figure out purpose of JSON
        String json = jsonObject.toString();
        if (encode_base64) {
            json = base64encode(json);
            this.parameters.put("cx", json);
        }
        else
            this.parameters.put("co", json);
        return new PayloadMapC(this.parameters, this.configurations);
    }

    public PayloadMap add_standard_nv_pairs(String p, String tv, String tna, String aid){
        this.parameters.put("p",p);
        this.parameters.put("tv",tv);
        this.parameters.put("tna",tna);
        this.parameters.put("aid",aid);
        return new PayloadMapC(this.parameters, this.configurations);
    }

    public PayloadMap add_config(String config_title, boolean config){
        this.configurations.put(config_title, config);
        return new PayloadMapC(this.parameters, this.configurations);
    }

    /* Web Tracker functions
     *   Functions used to configure the different types of trackers
    */

//    t.track_page_view("http://www.films.com", "Homepage", context={
//        "movie_poster": {
//            "movie_name": "Solaris",
//                    "poster_country": "JP",
//                    "poster_year$dt": new Date(1978, 1, 1)
//        }
//    })

    public PayloadMap track_page_view_config(String page_url, String page_title, String referrer,
                                             String vendor, JSONObject context) throws UnsupportedEncodingException{
        Map<String, Object> page_view_config = new HashMap<String, Object>();
        this.parameters.put("e", "pv");
        this.parameters.put("url", page_url);
        this.parameters.put("page", page_title);
        this.parameters.put("refr", referrer);
        this.parameters.put("evn", vendor);
        if (context==null)
            return new PayloadMapC(this.parameters, this.configurations);
        PayloadMap tmp = new PayloadMapC(this.parameters, this.configurations);
        tmp = tmp.add_json(context, this.configurations.get("encode_base64"));
        return tmp;
    }

    /* Getter functions.
     *  Can be used to get key sets of parameters and configurations
     *  Also used to get the linked hash maps of the parameters and configurations
    */
    public Set getParamKeySet(){ return this.parameters.keySet(); }

    public Set getConfigKeySet(){ return this.configurations.keySet(); }

    public String getParam(String key){ return this.parameters.get(key); }

    public boolean getConfig(String key){ return this.configurations.get(key); }

    public LinkedHashMap<String,String> getParams() { return this.parameters; }

    public LinkedHashMap<String,Boolean> getConfigs() { return this.configurations; }

    public String toString(){
        return "Parameters: " +this.parameters.toString() +
                "\nConfigurations: " + this.configurations.toString();
    }

    //Test function
    public static void main(String[] args){
        PayloadMap pl = new PayloadMapC();
        LinkedHashMap<String,String> configurations = new LinkedHashMap<String, String>();

        System.out.println(pl.toString());
        LinkedHashMap<String, String> params = pl.getParams();
        Set<String> paramsSet = pl.getParams().keySet();
        for(String s : paramsSet){
            System.out.println(s + ":" + params.get(s));
        }

        System.out.println(configurations.toString().length());
        System.out.println(configurations.toString().equals("{}"));
    }
}
