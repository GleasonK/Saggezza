/*
 * Copyright (c) 2012-2014 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package com.saggezza.litetracker.track;

import com.saggezza.litetracker.emit.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 *  <p>TrackerC is the implementing class for the Tracker interface.</p>
 *  <p>It can be used to track many different types of events. The current version
 *   of the tracker is 0.1.0 meaning it is functional but likely contains some bugs.
 *   If you find any please contact me at kaagleason@gmail.com or contact com.saggezza
 *   via their website.</p>
 *
 *  <p><b>To use:</b></p>
 *  <ul>
 *      <li>Import the com.com.saggezza.com.saggezza.litetracker package</li>
 *      <li>You must first declare a payload and a tracker</li>
 *      <li>Tracker will instantiate with a good-for-tracking payload</li>
 *      <li>(recommended) Build the payload identity with TrackerC set functions (e.g setUser(String user))</li>
 *      <li>(optional) Add custom payload to Tracker with setPayload(). <br/>Payload will configure itself further and prepare for request.</li>
 *      <li>Call a specific tracking event. Payload will be automatically configured further</li>
 *      <li>Get request is sent to server</li>
 *  </ul>
 *
 *  <p><b>There are two static functions in the TrackerC class: "debug" and "track"</b></p>
 *  <ul>
 *    <li>To see the output you are sending to the web as it is sending, put `TrackerC.debug = true` in your code.</li>
 *    <li>If you would like to test what the output would look like in your usage scenario without<br/>
 *      sending logs to your S3 bucket, put `TrackerC.track = false` in your code.</li>
 *    <li>Default values are `TrackerC.debug = false` and `TrackerC.track = true`</li>
 *  </ul>
 *  @version 0.2.0
 *  @author Kevin Gleason
 */

public class TrackerC implements Tracker, GenericTracker {
    //Static Class variables
    private static final String VERSION = "0.2.0";
    private static final String DEFAULT_PLATFORM = "pc";
    public static final String DEFAULT_VENDOR = "com.com.saggezza";

    //Static
    public static boolean debug = false;
    public static boolean track = true;
    public static boolean singleVar=false;

    //Instance Variables
    private PayloadMap payload = new PayloadMap();
    private String namespace,
            app_id,
            context_vendor=DEFAULT_VENDOR;
    private Emitter emitter;

    //Base Constructor
    public TrackerC(Emitter emitter, String namespace) {
        this.emitter=emitter;
        this.namespace = namespace;
        this.app_id = this.context_vendor = "";
        this.setPayload(new PayloadMap());
    }

    //Constructor with all arguments
    public TrackerC(Emitter emitter, String namespace, String app_id,
                    String context_vendor) {
        this.emitter=emitter;
        this.namespace = namespace;
        this.app_id = app_id;
        this.context_vendor = context_vendor;
        this.setPayload(new PayloadMap());
    }

    /**
     * {@inheritDoc}
     * @param user_id The current user of the JavaPlow tracker as used in analytics.
     */
    public void setupTrack(String user_id, JSONObject metaData){

        this.payload.addSystemInfo();
        this.setUserID(user_id);
    }

    /**
     * {@inheritDoc}
     * @throws java.io.IOException
     */
    public void track() {
        this.payload.setTimestamp();
        if (TrackerC.debug) {
            System.out.println("Payload:\n" + payload.toString());
            System.out.println("Making HttpGet...");
        }
        this.emitter.emit(this.payload);
        this.clearPayload();
    }

    /**
     * {@inheritDoc}
     * @param page_url The url of the page where the tracking call lies.
     * @param page_title The title of the page where the tracking call lies. (optional)
     * @param referrer The one who referred you to the page (optional)
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackPageView(String page_url, String page_title, String referrer, String context)
            throws JSONException, IOException{
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackPageView_config(page_url, page_title, referrer, jsonContext);
        }
        else {
            this.payload.trackPageView_config(page_url, page_title, referrer, null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param category The category of the structured event.
     * @param action The action that is being tracked. (optional)
     * @param label A label for the tracking event. (optional)
     * @param property The property of the structured event being tracked. (optional)
     * @param value The value associated with the property being tracked.
     * @param vendor The vendor the the property being tracked. (optional)
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackStructEvent(String category, String action, String label, String property,
                                 int value, String vendor, String context)
            throws JSONException, IOException {
        String valueStr = String.valueOf(value);
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackStructEvent_config(category, action, label, property, valueStr,
                    jsonContext);
        } else {
            this.payload.trackStructEvent_config(category, action, label, property, valueStr,
                    null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackUnstructEvent(String eventVendor, String eventName, Map<String, Object> dictInfo, String context)
            throws JSONException, IOException{
        JSONObject jsonDict = mapToJSON(dictInfo); //Make compatible for Map<String, Object>
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackUnstructEvent_config(eventVendor, eventName, jsonDict, jsonContext);
        } else {
            this.payload.trackUnstructEvent_config(eventVendor, eventName, jsonDict, null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackUnstructEvent(String eventVendor, String eventName, String dictInfo, String context)
            throws JSONException, IOException {
        JSONObject jsonDict = stringToJSON(dictInfo); //Make compatible for Map<String, Object>
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackUnstructEvent_config(eventVendor, eventName, jsonDict, jsonContext);
        } else {
            this.payload.trackUnstructEvent_config(eventVendor, eventName, jsonDict, null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackGenericEvent(String eventVendor, String eventName, Map<String,Object> dictInfo, String context)
            throws JSONException, IOException {
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackGenericEvent_config(eventVendor, eventName, dictInfo, jsonContext);
        } else {
            this.payload.trackGenericEvent_config(eventVendor, eventName, dictInfo, null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param name The name of the screen view being tracked
     * @param id The ID of the screen view being tracked.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackScreenView(String name, String id, String context)
            throws JSONException, IOException {
        Map<String, Object> screenViewProperties = new LinkedHashMap<String, Object>();
        screenViewProperties.put("Name", name); // or String screenVie... = "{'name': '"+ name + "'}"
        if (id != null)
            this.payload.add("id", id);
        this.trackUnstructEvent(DEFAULT_VENDOR, "screen_view", screenViewProperties, context);
    }

    /**
     * {@inheritDoc}
     * @param order_id ID of the item.
     * @param sku SKU value of the item.
     * @param price Prive of the item.
     * @param quantity Quantity of the item.
     * @param name Name of the item.
     * @param category Category of the item.
     * @param currency Currency used for the purchase.
     * @param context Additional JSON context for the tracking call (optional)
     * @param transaction_id Transaction ID, if left blank new value will be generated.
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public void trackEcommerceTransactionItem(String order_id, String sku, Double price, Integer quantity,
                                              String name, String category, String currency, String context, String transaction_id)
            throws JSONException, IOException {
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackEcommerceTransactionItem_config(order_id, sku, doubleCheck(price),
                    integerCheck(quantity), stringCheck(name), stringCheck(category), stringCheck(currency), jsonContext, null);
        } else {
            this.payload.trackEcommerceTransactionItem_config(order_id, sku, doubleCheck(price),
                    integerCheck(quantity), stringCheck(name), stringCheck(category), stringCheck(currency), null, null);
        }
        this.track();
    }

    /**
     * {@inheritDoc}
     * @param order_id The transaction ID, will be generated if left null
     * @param total_value The total value of the transaction.
     * @param affiliation Affiliations to the transaction (optional)
     * @param tax_value Tax value of the transaction (optional)
     * @param shipping Shipping costs of the transaction (optional)
     * @param city The customers city.
     * @param state The customers state.
     * @param country The customers country.
     * @param currency The currency used for the purchase
     * @param items A list containing a Map of Strings. Each item must have order ID, sku, price, and quantity.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws org.json.JSONException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    public void trackEcommerceTransaction(String order_id, Double total_value, String affiliation, Double tax_value,
                                          Double shipping, String city, String state, String country, String currency, List<Map<String,String>> items, String context)
            throws JSONException, IOException {
        //Track ecommerce event.
        if (context != null && !context.equals("")) {
            JSONObject jsonContext = stringToJSON(context);
            this.payload.trackEcommerceTransaction_config(order_id, doubleCheck(total_value), stringCheck(affiliation),
                    doubleCheck(tax_value), doubleCheck(shipping), stringCheck(city), stringCheck(state), stringCheck(country),
                    stringCheck(currency), jsonContext);
        } else {
            this.payload.trackEcommerceTransaction_config(order_id, doubleCheck(total_value), stringCheck(affiliation),
                    doubleCheck(tax_value), doubleCheck(shipping), stringCheck(city), stringCheck(state), stringCheck(country),
                    stringCheck(currency), null);
        }
        this.track();
        for (Map<String,String> item : items){
            this.trackEcommerceTransactionItem(order_id, mapCheck(item, "sku"), dParseCatch(mapCheck(item, "price")),
                    iParseCatch(mapCheck(item, "quantity")), mapCheck(item, "name"), mapCheck(item, "category"), mapCheck(item, "currency"), null,
                    this.payload.getParam("tid"));
        }
    }

    /* Checker or Helper functions
     *   doubleCheck is used to catch fields that aren't required but double cant be null
     *    and it gets messier handling it with Integers that can be null
     */
    private String integerCheck(Integer i) { return i==null ? "" : String.valueOf(i); }
    private String doubleCheck(Double d) { return d==null ? "" : String.valueOf(d); }
    private String stringCheck(String s) { return s==null ? "" : s; }
    private String mapCheck(Map<String,String> m, String s){ return m.containsKey(s) ? m.get(s) : ""; }
    private double dParseCatch(String s){
        try{ return Double.parseDouble(s); }
        catch (NumberFormatException nfe) { throw new NumberFormatException("Item requires fields: 'sku', 'price','quantity'"); }
    }
    private int iParseCatch(String s){
        try{ return Integer.parseInt(s); }
        catch (NumberFormatException nfe) { throw new NumberFormatException("Item requires fields: 'sku', 'price','quantity'"); }
    }



    //Turn String input into JSONObject
    private JSONObject stringToJSON(String jsonStr) throws JSONException{
        return new JSONObject(jsonStr);
    }
    private JSONObject mapToJSON(Map<String, Object> oMap) { return new JSONObject(oMap); }

    //Clear Payload for next iterations
    private void clearPayload() {
        String[] standards = new String[] {"uid", "res", "vp", "cd", "tz", "br_lang", "os_nm", "os_fam"};
        PayloadMap standPairs = new PayloadMap();
        Map<String, String> currentParam = this.payload.getParams();
        for (String s : standards)
            if (currentParam.containsKey(s))
                standPairs.add(s, currentParam.get(s));
        this.setPayload(standPairs);
    }

    private void setPayload(PayloadMap payload){
        this.payload=payload;
        setStandardNV();
    }

    //Only called once when the Payload class is attacked to the com.com.saggezza.com.saggezza.litetracker.track.Tracker
    private void setStandardNV(){
        this.payload.addStandardNVPairs(DEFAULT_PLATFORM, VERSION, this.namespace, this.app_id);
    }

    /**
     * {@inheritDoc}
     * @param param Parameter to be set.
     * @param val Value for the parameter.
     */
    public void setParam(String param, String val){
        this.payload.add(param, val);
    }

    /**
     * {@inheritDoc}
     * @param platform The platform being tracked, currently supports "pc", "tv", "mob", "cnsl", and "iot".
     */
    public void setPlatform(String platform){//contract true
        this.payload.add("p", platform);
    }

    /**
     * {@inheritDoc}
     * @param userID The User ID String.
     */
    public void setUserID(String userID){
        this.payload.add("uid", userID);
    }

    /**
     * {@inheritDoc}
     * @param width Width of the screen in pixels.
     * @param height Height of the screen in pixels.
     */
    public void setScreenResolution(int width, int height){
        this.payload.add("res", String.valueOf(width) + "x" + String.valueOf(height));
    }

    /**
     * {@inheritDoc}
     * @param width Width of the viewport in pixels.
     * @param height Height of the viewport in pixels.
     */
    public void setViewport(int width, int height){
        this.payload.add("vp", String.valueOf(width) + "x" + String.valueOf(height));
    }

    /**
     * {@inheritDoc}
     * @param depth Depth of the color.
     */
    public void setColorDepth(int depth){
        this.payload.add("cd", String.valueOf(depth));
    }

    /**
     * {@inheritDoc}
     * @param timezone Timezone where tracking takes place.
     */
    public void setTimezone(String timezone){
        this.payload.add("tz", timezone);
    }

    /**
     * {@inheritDoc}
     * @param language Language for info tracked.
     */
    public void setLanguage(String language){
        this.payload.add("br_lang", language);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public PayloadMap getPayload(){ return this.payload; }

    //Test case main function
    public static void main(String[] args) throws IOException, JSONException, URISyntaxException {
        ///// Setup

        ///// CONFIGURATIONS
        TrackerC.debug=false;
        TrackerC.track=false;
        TrackerC.singleVar=false;

        ///// EmitterC
        Emitter emitter = new Emitter();

        ///// REGULAR TRACKER
        Tracker t1 = new TrackerC(emitter, "Tracker Test", "JavaPlow", "com.com.saggezza");
//        t1.setUserID("User1");
        t1.setLanguage("eng");
        t1.setPlatform("pc");
        t1.setScreenResolution(1260, 1080);
        String context = "{'Zone':'USA', 'Phone':'Droid', 'Time':'2pm'}";

        ///// E COMMERCE TEST
//        Map<String,String> items = new HashMap<String, String>();
//        items.put("sku", "SKUVAL"); items.put("quantity","2"); items.put("price","19.99");
//        List<Map<String,String>> lst = new LinkedList<Map<String, String>>();
//        lst.add(items);

        ///// GENERICS
//        GenericTracker t2 = new TrackerC(emitter, "GenericTracker Test", "JavaPlow", "com.com.saggezza");
//        t2.setLanguage("English");
//        t2.setPlatform("pc");
//        t2.setScreenResolution(1200, 1080);

        ///// GENERIC MAP
//        Map<String, Object> dict = new LinkedHashMap<String, Object>();
//
//        dict.put("Username", System.getProperty("user.name"));
//        dict.put("OperatingSystem", System.getProperty("os.name"));
//        dict.put("OS_Version", System.getProperty("os.version"));
//        dict.put("JRE_Version", System.getProperty("java.version"));

        /////TRACK TEST
        for (int i = 0; i < 1; i++) {
//            dict.put("Iteration", i);
//            System.out.println(dict.toString() + "\n" + t2.getPayload().toString());
//            t2.setupTrack("Kevin");
//            t2.trackGenericEvent("Lube Insights", "Data Loop", dict, context);
            t1.setupTrack("Kevin", new JSONObject());
            long start = System.currentTimeMillis();
            t1.trackPageView("saggezza.com", "Home Page", "KevinReferred", context);
            t1.trackPageView("saggezza.com", "Home Page", "KevinReferred", context);
            long stop = System.currentTimeMillis();
            System.out.print(stop-start + " ");
//            t1.trackEcommerceTransactionItem("IT1023", "SKUVAL", 29.99, 2, "boots", "Shoes","USD",null,null);
//            t1.trackEcommerceTransaction("OID", 19.99, "Kohls", 2.50, 1.99, "Chagrin", "OH", "USA", "USD", lst, context);
        }
    }
}
