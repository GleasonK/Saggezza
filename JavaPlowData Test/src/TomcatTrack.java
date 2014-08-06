//File: TestCase.java
//Author: Kevin Gleason
//Date: 6/4/14
//Use: Test case scenario for Javaplow tracker


//import com.saggezza.jtracker.emit.Emitter;
//import com.saggezza.jtracker.track.Tracker;
//import com.saggezza.jtracker.track.TrackerC;

import com.saggezza.litetracker.emit.Emitter;
import com.saggezza.litetracker.track.Tracker;
import com.saggezza.litetracker.track.TrackerC;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

// Nothing to do with a node, just simulating random numbers and data

public class TomcatTrack {
    public static boolean runTracker = false;
    //Instance Variables
    private Tracker t1;
    private double SUCCESS_RATE;
    private double NODE_POWER;
    private String node_id;

    TomcatTrack(String node_id){
//        this.t1 = new TrackerC(new Emitter(), "Data Pipeline MW03 Struct and Unstruct", node_id, "com.com.saggezza", true, true);
        this.t1 = new TrackerC(new Emitter(), "Data Pipeline MW03 Struct and Unstruct", node_id, "com.com.saggezza");
        this.SUCCESS_RATE = getPercent() + 25.0;
        this.NODE_POWER = getPercent() - 45.0;
        this.node_id = node_id;
    }

    public void runNodeIterations(int n)throws JSONException, IOException, URISyntaxException{
//        TrackerC.debug=true;
//        TrackerC.track=false;
        long procStart = System.currentTimeMillis();
        String context = "{'Company':'KevinG inc.', 'Data Work ID':'KGi 002'}";
        for (int i=0; i<n; i++){
            double CPU = getUsageCPU();
            boolean succeeded = succeedOrFail(CPU);
            try {Thread.sleep(5);}catch (InterruptedException e){ e.printStackTrace(); }
            if (runTracker) {
                long start = System.currentTimeMillis();
                System.out.println("BEGIN SETUP TRACK");
                t1.setupTrack("KG", new JSONObject());
                long elapsed = System.currentTimeMillis()-start;
//                long start1 = System.currentTimeMillis();
                System.out.println("END SETUP TRACK\nBEGIN TRACK PAGE VIEW");
                t1.trackPageView("sg","home","Kevin", "");
                System.out.println("END PAGE VIEW\nBEGIN TRACK UNSTRUCTURED EVENT");
                t1.trackUnstructEvent("Saggezza", "Pipeline Statistics", buildInfo(CPU, succeeded, i), context);

//                long elapsed1 = System.currentTimeMillis()-start1;
//                System.out.println("Iter " + i + " - " + elapsed + " " + elapsed1);
            }
//            t1.track_struct_event("Pipeline Work", "Node Processing","Succeed and CPU", succeeded ? "OK" : "FAILED",
//                    (int) CPU,"com.com.saggezza",context);
//            try { Thread.sleep(500 * getRandIntZeroToN(10)); }
//            catch (InterruptedException e){}
        }
        long procElapsed = System.currentTimeMillis() - procStart;
        System.out.println("Proc Elapsed " + procElapsed);
    }

    public void resourcesUsed() throws JSONException, IOException, URISyntaxException {
        System.out.println("BEGIN SETUP CONFIG");
        List<Map<String,String>> items = new ArrayList<Map<String, String>>();
        Map<String,String> item = new HashMap<String, String>();
        item.put("sku","4"); item.put("price", "4"); item.put("quantity","2");
        items.add(item);

        String context = "{'Company':'KevinG inc.', 'Data Work ID':'KGi 002'}";
        long start = System.currentTimeMillis();
        System.out.println("BEGIN SETUP TRACK");
        t1.setupTrack("KG", new JSONObject());
        System.out.println("END SETUP TRACK\nBEGIN TRACK PAGE VIEW");
        t1.trackPageView("sg", "home", "Kevin", "");
        System.out.println("END PAGE VIEW\nBEGIN TRACK SCREEN VIEW");
        t1.trackScreenView("screenview", "thisScreen", context);
        System.out.println("END SCREEN VIEW\nBEGIN UNSTRUCTURED EVENT");
        t1.trackUnstructEvent("Saggezza", "Pipeline Statistics", buildInfo(1.1, true, 1), context);
        System.out.println("END UNSTRUCTURED\nBEGIN STRUCTURED EVENT");
        t1.trackStructEvent("cat", "act", "lab", "prop", 2, "ven", context);
        System.out.println("END STRUCTURED\nBEGIN ECOMMERCE EVENT");
        t1.trackEcommerceTransaction("id", 22.2, "aff", 1.22, 12.0, "cit", "oh", "us", "usd", items, context);
        System.out.println("END ECOMMERCE\nBEGIN EMIT");
        t1.setEmitter(new Emitter("localhost:80", "JavaPlow/urlinfo"));
        t1.track();
        System.out.println("END EMIT");
    }

    public void quickTrack() throws URISyntaxException, IOException {
        t1.setEmitter(new Emitter("localhost:80", "JavaPlow/urlinfo"));
        t1.track();
    }

    public String buildInfo(double CPU, boolean succeeded, int i) throws JSONException{
        JSONObject jsonDict = new JSONObject();
        jsonDict.put("CPU", (int) CPU);
        jsonDict.put("Status", succeeded ? "OK" : "FAILED");
        jsonDict.put("Iteration", i);
        return jsonDict.toString();
    }

    public double getPercent(){
        Random r = new Random(); //NEED ID RANGE
        double p = r.nextDouble() * 100;
        return p;
    }

    public double getUsageCPU(){
        double uCPU = getPercent() + this.NODE_POWER;
        if (uCPU > 100)
            uCPU = 100;
        if (uCPU < 5)
            uCPU = 5;
        return uCPU;
    }

    public int getRandIntZeroToN(int n){
        Random r = new Random();
        return r.nextInt(n+1);
    }

    public boolean succeedOrFail(double work){
        return work <= this.SUCCESS_RATE;
    }

    public String toString(){
        return "Node: " + this.node_id + "\nSuccess Rate: " + this.SUCCESS_RATE + "\nPower: " + this.NODE_POWER;
    }

    public static void main(String[] args) throws JSONException, IOException, URISyntaxException{
        TomcatTrack p1 = new TomcatTrack("Node 0001");
//        TomcatTrack p2 = new TomcatTrack("Node 0002");
//        TomcatTrack p3 = new TomcatTrack("Node 0003");
//        TomcatTrack p4 = new TomcatTrack("Node 0004");
//        TomcatTrack p5 = new TomcatTrack("Node 0005");
        long s1 = System.currentTimeMillis();
        p1.quickTrack();
        long e1 = System.currentTimeMillis()-s1;
        long start = System.currentTimeMillis();
        p1.quickTrack();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed + " " + e1);
//        p1.resourcesUsed();
//        System.out.println(p1.toString());
//        System.out.println(p1.buildInfo(90,false,3));
//        System.out.println(p2.toString());
//        System.out.println(p3.toString());
//        System.out.println(p4.toString());
//        System.out.println(p5.toString());
//        p2.toString();
//        int iters = 1;
//        long start = System.currentTimeMillis();
//        p2.runNodeIterations(iters);
//        long elapsed = System.currentTimeMillis()-start;
//
//        TomcatTrack.runTracker=true;
//        long start2 = System.currentTimeMillis();
//        p2.runNodeIterations(iters);
//        long elapsed2 = System.currentTimeMillis()-start2;
//        long start3 = System.currentTimeMillis();
//        p2.runNodeIterations(iters);
//        long elapsed3 = System.currentTimeMillis()-start3;
//        System.out.println("Elapsed Without " + elapsed + "\nElapsed With " + elapsed2 + "\nElapsed With2 " + elapsed3);

//        p2.runNodeIterations(10);
//        p3.runNodeIterations(10);
//        p4.runNodeIterations(10);
//        p5.runNodeIterations(10);
    }
}
