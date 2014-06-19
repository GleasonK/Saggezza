import com.snowplow.javaplow.*;

/**
 * Created by Kevin Gleason on 6/6/2014.
 */
public class GlobalTrack {
    public static Tracker tracker = new TrackerC("d2pac8zn4o1kva.cloudfront.net","GlobalTracker 01",
            "Pipeline Test 01", "com.saggezza", true, true);
}
