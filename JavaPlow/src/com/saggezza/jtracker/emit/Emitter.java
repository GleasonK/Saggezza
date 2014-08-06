package com.saggezza.jtracker.emit;

/**
 * An asynchronous emitter class to store requests and send them to the
 *  collector with minimal effect on code.
 */
public class Emitter {
    //Static
    public static boolean debug = false;
    public static boolean track = true;
    public static boolean singleVar=false;

    private String collectorURI, path;

    public Emitter(String collectorURI, String path){
        this.collectorURI=collectorURI;
        this.path=path;
    }

}
