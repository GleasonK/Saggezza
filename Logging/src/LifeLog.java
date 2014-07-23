// File: LifeLog.java
// Author: Kevin Gleason
// Date: 6/11/14
// Use: Learn how Java logs work

import java.util.logging.*;
import java.io.IOException;



public class LifeLog {
    private final static Logger LOGGER = Logger.getLogger(LifeLog.class.getName());

    public void logEvent() {

        LOGGER.log(Level.ALL, "MESSAGE");

        LOGGER.setLevel(Level.SEVERE);
        LOGGER.severe("This log is of the utmost importance");
        LOGGER.warning("This is a WARNING");
        LOGGER.info("This is an informational log");
        LOGGER.finest("Not important");

        //Set all the
        LOGGER.setLevel(Level.INFO);
        LOGGER.severe("Info Log");
        LOGGER.warning("Info Log");
        LOGGER.info("Info Log");
        LOGGER.finest("Really not important");

        LOGGER.log(Level.SEVERE, "HI");
    }
    public static void main(String[] args){
        LifeLog logger = new LifeLog();
        try { MyLifeLogger.setup(); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem with log files");
        }
        logger.logEvent();
    }
}
