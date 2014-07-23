<<<<<<< HEAD
import javaplow.Tracker;
import javaplow.TrackerC;
=======
import com.snowplow.javaplow.*;
>>>>>>> bad74a14a7c133ed9aa6c94dd0feb0a9dbcf200f

/**
 * Created by Kevin Gleason on 6/6/2014.
 */
public class GlobalTrack {
    public static Tracker tracker = new TrackerC("d2pac8zn4o1kva.cloudfront.net","GlobalTracker 01",
            "Pipeline Test 01", "com.saggezza", true, true);
}
