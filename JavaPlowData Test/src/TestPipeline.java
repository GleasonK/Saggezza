//File: TestCase.java
//Author: Kevin Gleason
//Date: 6/4/14
//Use: Test case scenario for Javaplow tracker


import javaplow.*;

import java.util.Random;

// Nothing to do with a node, just simulating random numbers and data

public class TestPipeline {
    //Instance Variables
    private Tracker t1;
    private double SUCCESS_RATE;
    private double NODE_POWER;
    private String node_id;

    TestPipeline(String node_id){
        this.t1 = new TrackerC("d2pac8zn4o1kva.cloudfront.net", "Data Pipeline MW01 Success and CPU",
                node_id, "com.saggezza", true, true);
        this.SUCCESS_RATE = getPercent() + 25.0;
        this.NODE_POWER = getPercent()-50.0;
        this.node_id = node_id;
    }

    public void runNodeIterations(int n){
        String context = "{'Company':'KevinG inc.', 'Data Work ID':'KGi 001'}";
        for (int i=0; i<n; i++){
            System.out.println(succeedOrFail());
        }
    }

    public double getPercent(){
        Random r = new Random(); //NEED ID RANGE
        double p = r.nextDouble() * 100;
        return p;
    }

    public double getUsageCPU(){
        Random r = new Random(); //NEED ID RANGE
        double uCPU = r.nextDouble() * 100 + this.NODE_POWER;
        return uCPU;
    }

    public boolean succeedOrFail(){
        Random r = new Random(); //NEED ID RANGE
        double failure = getPercent() + this.NODE_POWER;
        return failure <= this.SUCCESS_RATE ? true : false;
    }

    public String toString(){
        return "Node: " + this.node_id + "\nSuccess Rate: " + this.SUCCESS_RATE + "\nPower: " + this.NODE_POWER;
    }

    public static void main(String[] args){
        TestPipeline p1 = new TestPipeline("Node 0001");
        TestPipeline p2 = new TestPipeline("Node 0002");
        System.out.println(p1.toString());
        p1.runNodeIterations(5);
        System.out.println();
        System.out.println(p2.toString());
        p2.runNodeIterations(5);
    }
}
