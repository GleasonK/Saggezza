import javaplow.Tracker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javaplow.*;
import org.json.JSONException;

/**
 * Created by Kevin Gleason on 6/5/2014.
 */
public class ParseTable {
    public static void main(String[] args) throws IOException, JSONException, URISyntaxException {
        Tracker t1 = new TrackerC("d2pac8zn4o1kva.cloudfront.net", "javaplow.Tracker Test", "JavaPlow", "com.saggezza", true, true);
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
            t1.track_unstruct_event("Lube Insights", "Data Loop", dict, context);
            t1.track_struct_event("Items", "Stuff", "Pants", "Green Blue", 3, "com.saggezza", context);
            t1.track_page_view("www.saggezza.com", "Saggezza Home", "Kevin Gleason", null);
//            t1.track_ecommerce_transaction_item("IT1023", "SKUVAL", 29.99, 2, "boots", "Shoes","USD",null,null);
            t1.track_ecommerce_transaction("OID", 19.99, "Kohls", 2.50, 1.99, "Chagrin", "OH", "USA", "USD", lst, context);
            t1.track_screen_view("GleasonK", "Home Screen", context);
        }
    }
}
