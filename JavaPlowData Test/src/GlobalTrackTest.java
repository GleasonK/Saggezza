import javaplow.TrackerC;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * Created by Kevin Gleason on 6/6/2014.
 */
public class GlobalTrackTest {
    //Instance Vars
    private double SUCCESS_RATE;
    private double NODE_POWER;
    private String node_id;

    //Constructor
    GlobalTrackTest(String node_id){
        this.node_id = node_id;
        this.SUCCESS_RATE = getPercent()+30.0;
        this.NODE_POWER = getPercent()-40.0;
        TrackerC.debug=true;
        TrackerC.track=false;
    }

    //NOT THREAD SAFE TO THE MILLISECOND
    public void testThreadedExecution() throws InterruptedException{
        Thread thread1 = new Thread(){
            @Override
            public void run(){
                try {new GlobalTrackTest("Node 0006").structuredIter(5);}
                catch (JSONException j){}
                catch (IOException e) {}
                catch (URISyntaxException u){}
            }
        };
        Thread thread2 = new Thread(){
            @Override
            public void run(){
                try {new GlobalTrackTest("Node 0006").unstructuredIter(5);}
                catch (JSONException j){}
                catch (IOException e) {}
                catch (URISyntaxException u){}
            }
        };
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public void structuredIter(int n)throws JSONException, IOException, URISyntaxException {
        String context = "{'Company':'KevinG inc.', 'Data Work ID':'KGi 001'}";
        for (int i=0; i<n; i++){
            double CPU = getUsageCPU();
            boolean succeeded = succeedOrFail(CPU);
            GlobalTrack.tracker.track_struct_event("Pipeline Work", "Node 0006 Processing", "Succeed and CPU", succeeded ? "OK" : "FAILED",
                    (int) CPU, "com.saggezza", context);
            try { Thread.sleep(200 * getRandIntZeroToN(10)); }
            catch (InterruptedException e){}
        }
    }

    public void unstructuredIter(int n) throws JSONException, IOException, URISyntaxException{
        String context = "{'Event Action':'Unstructured global tracker test', 'Reason':'The sake of testing and improvement.'}";
        for (int i=0; i<n; i++){

            double CPU = getUsageCPU();
            boolean succeeded = succeedOrFail(CPU);
            String dictInfo = buildInfo(CPU, succeeded, i);
            GlobalTrack.tracker.track_unstruct_event("Saggezza", "Pipeline Node " + this.node_id + " Statistics",
                    dictInfo, context);

            try { Thread.sleep(200 * getRandIntZeroToN(10)); }
            catch (InterruptedException e){}
        }
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
        return work <= this.SUCCESS_RATE ? true : false;
    }

    public String toString(){
        return "Node: " + this.node_id + "\nSuccess Rate: " + this.SUCCESS_RATE + "\nPower: " + this.NODE_POWER;
    }

    public static void main(String[] args) throws JSONException, IOException, URISyntaxException, InterruptedException{
        GlobalTrackTest gt = new GlobalTrackTest("Node 0006");
        gt.testThreadedExecution();
    }
}
