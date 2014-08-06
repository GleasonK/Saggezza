import com.saggezza.jtracker.emit.Emitter;
import com.saggezza.jtracker.track.Tracker;
import com.saggezza.jtracker.track.TrackerC;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Kevin Gleason on 6/5/2014.
 */
public class ParseTable {
    public static void main(String[] args) throws IOException, JSONException, URISyntaxException {
        Tracker t1 = new TrackerC(new Emitter(), "javaplow.Tracker Test", "JavaPlow", "com.com.saggezza", true, true);
//        t1.track();
        t1.setUserID("User1");
        t1.setLanguage("ital");
        t1.setPlatform("mob");
        t1.setColorDepth(45);
        t1.setTimezone("CST");
        t1.setViewport(600,550);
        t1.setScreenResolution(760, 610);
        String context = "{'Universal Test':'JavaPlow0.0.2', 'Tester':'Kevin', 'Time':'12:34pm'}";

        ///// E COMMERCE TEST
        Map<String, String> items = new HashMap<String, String>();
        items.put("sku", "SKUVAL");
        items.put("quantity", "2");
        items.put("price", "19.99");
        List<Map<String, String>> lst = new LinkedList<Map<String, String>>();
        lst.add(items);
        TrackerC.debug = true;

        /////TRACK TEST
        for (int i = 0; i < 5; i++) {
//            t1.track();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println("Loop " + i);
            String dict = "{'Iteration Number':'" + i + "'}";
            t1.trackUnstructEvent("Lube Insights", "Data Loop", dict, context);

        }
    }
}
