import com.saggezza.jtracker.emit.Emitter;
import com.saggezza.jtracker.track.Tracker;
import com.saggezza.jtracker.track.TrackerC;

/**
 * Created by Kevin Gleason on 6/6/2014.
 */
public class GlobalTrack {
    public static Tracker tracker = new TrackerC(new Emitter(), "GlobalTracker 01",
            "Pipeline Test 01", "com.com.saggezza", true, true);

    public static void Trackk(){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    GlobalTrack.tracker.trackPageView("VIEW", "FAKEPAGE", "Kevin", "{'name':'Kevin'}");
                } catch (Exception e) { e.printStackTrace(); }
            }

        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
