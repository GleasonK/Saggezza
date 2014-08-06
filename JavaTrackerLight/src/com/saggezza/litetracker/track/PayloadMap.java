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

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;

/**
 * PayloadMapC implements the PayloadMap interface
 *  The PayloadMap is used to store all the parameters and configurations that are used
 *  to send data via the HTTP GET request.
 *
 * @version 0.2.0
 * @author Kevin Gleason
 */

public class PayloadMap {
    private LinkedHashMap<String,String> parameters;

    /**
     * Create an empty PayloadMap with a timestamp.
     */
    public PayloadMap(){
        this.parameters = new LinkedHashMap<String, String>();
        setTimestamp();
    }

    /**
     * Can be constructed with the two payload lists.
     * @param parameters A list of all parameter key-value-pairs
     */
    public PayloadMap(LinkedHashMap<String, String> parameters){
        this.parameters = parameters;
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

    //GUID
    private String makeTransactionID(){
        Random r = new Random(); //NEED ID RANGE
        return String.valueOf(r.nextInt(999999-100000+1) + 100000);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public void setTimestamp(){
        String tstamp = new Timestamp(System.currentTimeMillis()).toString();
        this.parameters.put("dtm",tstamp);
//        this.parameters.put("dtm", String.valueOf(System.currentTimeMillis()));
    }

    /* Addition functions
     *  Used to add different sources of key=>value pairs to a map.
     *  Map is then used to build "Associative array for getter function.
     *  Some use Base64 encoding
     */
    private String base64encode(String string) throws UnsupportedEncodingException{
        Base64 b64 = new Base64(true);
        String safe_str = b64.encodeBase64String(string.getBytes(Charset.forName("US-ASCII")));
        return safe_str;
    }

    /**
     * {@inheritDoc}
     * @param key The parameter key
     * @param val The parameter value
     * @return
     */
    public void add(String key, String val){
        this.parameters.put(key, val);
    }

    /**
     * {@inheritDoc}
     * @param dictInfo Information is parsed elsewhere from string to JSON then passed here
     * @throws java.io.UnsupportedEncodingException
     */
    public void addUnstruct(JSONObject dictInfo)
            throws UnsupportedEncodingException{
        //Encode parameter
        if (dictInfo == null)
            return;  //Catch this in contractor
        String json = dictInfo.toString();
        json = base64encode(json);
        this.parameters.put("ue_px", json);
    }

    /**
     * Add all the events from a generic tracking event to the payload.
     * @param dictInfo All the event information
     * @return New payload map containing all tracked information
     */
    public void addGeneric(Map<String, Object> dictInfo){
        Set<Map.Entry<String,Object>> entries = dictInfo.entrySet();
        for(Map.Entry<String,Object> entry : entries){
            this.parameters.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    /**
     * {@inheritDoc}
     * @param jsonObject JSON object to be added
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void addJSON(JSONObject jsonObject)
        throws UnsupportedEncodingException{
        //Encode parameter
        if (jsonObject == null)  ///CATCH IF JSON LEFT NULL
            return;         ///need to figure out purpose of JSON
        String json = jsonObject.toString();
        json = base64encode(json);
        this.parameters.put("cx", json);
    }

    /**
     * {@inheritDoc}
     * @param p Platform
     * @param tv Tracker Version
     * @param tna Namespace
     * @param aid App_id
     * @return
     */
    public void addStandardNVPairs(String p, String tv, String tna, String aid){
        this.parameters.put("p",p);
        this.parameters.put("tv",tv);
        this.parameters.put("tna",tna);
        this.parameters.put("aid",aid);
    }

    /**
     * {@inheritDoc}
     * @return New updated payload
     */
    public void addSystemInfo(){
        String os_nm = System.getProperty("os.name");
        String os_fam = System.getProperty("os.version");
        this.parameters.put("os_fam",os_fam);
        this.parameters.put("os_nm",os_nm);
    }

    //// WEB RELATED FUNCTIONS
    /**
     * {@inheritDoc}
     * @param page_url The URL or the page being tracked.
     * @param page_title The Title of the page being tracked.
     * @param referrer The referrer of the page being tracked.
     * @param context Additional JSON context (optional)
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackPageView_config(String page_url, String page_title, String referrer,
             JSONObject context) throws UnsupportedEncodingException{
        this.parameters.put("e", "pv");
        this.parameters.put("url", page_url);
        this.parameters.put("page", page_title);
        this.parameters.put("refr", referrer);
        this.parameters.put("evn", TrackerC.DEFAULT_VENDOR);
        if (context==null)
            return;
        this.addJSON(context);
        this.addJSON(context);
    }

    /**
     * {@inheritDoc}
     * @param category Category of the event being tracked.
     * @param action Action of the event being tracked
     * @param label Label of the event being tracked.
     * @param property Property of the event being tracked.
     * @param value Value associated with the property being tracked.
     * @param context Additional JSON context (optional)
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackStructEvent_config(String category, String action, String label, String property,
            String value, JSONObject context)
            throws UnsupportedEncodingException{
        this.parameters.put("e","se");
        this.parameters.put("se_ca", category);
        this.parameters.put("se_ac", action);
        this.parameters.put("se_la", label);
        this.parameters.put("se_pr", property);
        this.parameters.put("se_va", value);
        this.parameters.put("evn", TrackerC.DEFAULT_VENDOR);
        if (context==null)
            return;
        this.addJSON(context);
    }

    /**
     * {@inheritDoc}
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackUnstructEvent_config(String eventVendor, String eventName, JSONObject dictInfo,
                                                  JSONObject context) throws UnsupportedEncodingException{
        this.parameters.put("e","ue");
        this.parameters.put("ue_na", eventName);
        this.parameters.put("evn", eventVendor);
        this.addUnstruct(dictInfo);
        if (context==null)
            return;
        this.addJSON(context);
    }

    /**
     * {@inheritDoc}
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackGenericEvent_config(String eventVendor, String eventName, Map<String,Object> dictInfo,
                                                JSONObject context) throws UnsupportedEncodingException{
        this.parameters.put("e","ge");
        this.parameters.put("ge_na", eventName);
        this.parameters.put("evn", eventVendor);
        this.addGeneric(dictInfo);
        if (context==null)
            return;
        this.addJSON(context);
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
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackEcommerceTransactionItem_config(String order_id, String sku, String price, String quantity,
            String name, String category, String currency, JSONObject context, String transaction_id)
            throws UnsupportedEncodingException{
        this.parameters.put("e","ti");
        this.parameters.put("tid", (transaction_id==null) ? String.valueOf(makeTransactionID()) : transaction_id);
        this.parameters.put("ti_id", order_id);
        this.parameters.put("ti_sk", sku);
        this.parameters.put("ti_nm", name);
        this.parameters.put("ti_ca", category);
        this.parameters.put("ti_pr", String.valueOf(price));
        this.parameters.put("ti_qu", String.valueOf(quantity));
        this.parameters.put("ti_cu", currency);
        this.parameters.put("evn", TrackerC.DEFAULT_VENDOR);
        if (context==null)
            return;
        this.addJSON(context);
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
     * @param context Additional JSON context for the tracking call (optional)
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public void trackEcommerceTransaction_config(String order_id, String total_value, String affiliation, String tax_value,
            String shipping, String city, String state, String country, String currency, JSONObject context)
            throws UnsupportedEncodingException{
        this.setTransactionID();
        this.parameters.put("e", "tr");
        this.parameters.put("tr_id", order_id);
        this.parameters.put("tr_tt", total_value);
        this.parameters.put("tr_af", affiliation);
        this.parameters.put("tr_tx", tax_value);
        this.parameters.put("tr_sh", shipping);
        this.parameters.put("tr_ci", city);
        this.parameters.put("tr_st", state);
        this.parameters.put("tr_co", country);
        this.parameters.put("tr_cu", currency);
        this.parameters.put("evn", TrackerC.DEFAULT_VENDOR);
        if (context==null)
            return;
        this.addJSON(context);
    }

    /* Getter functions.
     *  Can be used to get key sets of parameters and configurations
     *  Also used to get the linked hash maps of the parameters and configurations
    */
    public Set getParamKeySet(){ return this.parameters.keySet(); }

    public String getParam(String key){ return this.parameters.get(key); }

    public LinkedHashMap<String,String> getParams() { return this.parameters; }

    public Set<Map.Entry<String,String>> getParamEntrySet() { return this.parameters.entrySet(); }

    public String toString(){
        return "Parameters: " + this.parameters.toString(); }

    //Test function
    public static void main(String[] args){
        PayloadMap pl = new PayloadMap();
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
