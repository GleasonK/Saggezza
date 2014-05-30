// File: Tracker.java
// Author: Kevin Gleason
// Date: 5/28/14
// Use: The tracker interface for TrackerC

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

public interface Tracker {



    public void track() throws URISyntaxException, ClientProtocolException, IOException;
    public void track_page_view(String page_url, String page_title, String referrer, String context)
            throws UnsupportedEncodingException, IOException, URISyntaxException, JSONException;
    public void setContractors(PlowContractor<Integer> integerContractor, PlowContractor<String> stringContractor,
                               PlowContractor<Map<String,Object>> dictionaryContractor);
    public void setPayload(PayloadMap payload);
    public void setParam(String param, String val);
    public void setPlatform(String platform);
    public void setUserID(String userID);
    public void setScreenResolution(int width, int height);
    public void setLanguage(String language);
    public PayloadMap getPayload();
}
