package com.saggezza.jtracker.track;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * GenericTracker Interface
 * The generic tracker interface is used to track whatever unstructured data in tabular form.
 * TrackerC is the implementing class. Logs must be written to a separate location than the other trackers
 * information, due to structural differences in the final data processing steps.
 * TODO: Add a specific url for location to write to HDFS.
 * TODO: Add fields to track_generic.
 *  {@inheritDoc}
 * @see TrackerC
 * @version 0.2.0
 * @author Kevin Gleason
 */
public interface GenericTracker {
    /**
     * This command should be used before calling a track command of any sort if analytics
     * is desired. Keeping it separate allows configuration of the user_id from the client side.
     * @param user_id The current user of the JavaPlow tracker as used in analytics.
     */
    public void setupTrack(String user_id, JSONObject metaData);

    /**
     * The basic track command. All other track functions eventually call this.
     * The function compiles all the parameters in the PayloadMap into a proper
     * URI which then is made into a HttpGet instance. The GET request is then sent to
     * the collector URI where it is logged.
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public void track() throws URISyntaxException, IOException;

    /**
     * Track an unstructured event.
     * @param eventVendor The vendor the the event information.
     * @param eventName A name for the unstructured event being tracked.
     * @param dictInfo The unstructured information being tracked in dictionary form.
     * @param context Additional JSON context for the tracking call (optional)
     * @throws JSONException If JSON is in improper formatting
     * @throws IOException If there is an issue with the tracking call.
     * @throws URISyntaxException If there is an issue with processing the HTTP GET
     */
    public void trackGenericEvent(String eventVendor, String eventName, Map<String, Object> dictInfo, String context)
            throws JSONException, IOException, URISyntaxException;

    /**
     * Set Contractors
     *  Not required, but useful if you want to make a contractor with a custom checker for processing.
     *  Requires three inputs, a contractor class of each type.
     * @param integerContractor A contractor of type integer.
     * @param stringContractor A contractory of type String
     * @param dictionaryContractor A Contractor of type Map with key value of String, Object
     */
    public void setContractors(PlowContractor<Integer> integerContractor, PlowContractor<String> stringContractor,
                               PlowContractor<Map<String, Object>> dictionaryContractor);

    /**
     * Used to add custom parameter. Be careful with use, must abide by saggezza table standards.
     * See saggezza documentation
     * @param param Parameter to be set.
     * @param val Value for the parameter.
     */
    public void setParam(String param, String val);

    /**
     * Set the platform for the tracking instance. (optional)
     * The default platform is PC.
     * @param platform The platform being tracked, currently supports "pc", "tv", "mob", "cnsl", and "iot".
     */
    public void setPlatform(String platform);

    /**
     * Set the uesrID parameter (optional)
     * @param userID The User ID String.
     */
    public void setUserID(String userID);

    /**
     * Set the screen resolution width and height (optional)
     * @param width Width of the screen in pixels.
     * @param height Height of the screen in pixels.
     */
    public void setScreenResolution(int width, int height);

    /**
     * Set the viewport of the screen.
     * @param width Width of the viewport in pixels.
     * @param height Height of the viewport in pixels.
     */
    public void setViewport(int width, int height);

    /**
     * Set the color depth (optional)
     * @param depth Depth of the color.
     */
    public void setColorDepth(int depth);

    /**
     * Set the timezone (optioanl)
     * @param timezone Timezone where tracking takes place.
     */
    public void setTimezone(String timezone);

    /**
     * Set the language (optional)
     * @param language Language for info tracked.
     */
    public void setLanguage(String language);

    /**
     * Get the payload, use if you want to understand how it is set up.
     * @return Returns the payload, can be used with caution to customize parameters.
     */
    public PayloadMap getPayload();

}
