// File: MyLifeLogger.java
// Author: Kevin Gleason
// Date: 6/11/14
// Use: Learn how Java logs work

import sun.util.logging.PlatformLogger;

import java.io.IOException;
import java.util.logging.*;
import java.util.Date;

public class MyLifeLogger {
    //Instance Vars

    static public void setup() throws IOException {
        // Get global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Logger logtest = Logger.getLogger("My Log");
        FileHandler fileTxt,
                    fileHTML;
        SimpleFormatter formatterTxt;
        Formatter formatterHTML;

        // Suppress the logging output to the console

        ////Turn off Console
//        Logger rootLogger = Logger.getLogger("");
//        Handler[] handlers = rootLogger.getHandlers();
//        if (handlers[0] instanceof ConsoleHandler)
//            rootLogger.removeHandler(handlers[0]);
//        logger.setLevel(Level.INFO);


        fileTxt = new FileHandler("Logging.txt");
        fileHTML = new FileHandler("Logging.html");

        //Create txt formatter
        formatterTxt = new SimpleFormatter();
        logtest.addHandler(fileTxt);
        logger.addHandler(fileTxt);
        fileTxt.setFormatter(formatterTxt);

        //Create HTML formatter
        formatterHTML = new LifeLogFormat();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
        logtest.addHandler(fileHTML);

    }

    public void log(){
        Logger logtest = Logger.getLogger(MyLifeLogger.class.getName());
        logtest.setLevel(Level.ALL);
        logtest.log(Level.ALL,"LOGGG");

    }

    public static void main(String[] args){
        MyLifeLogger logger = new MyLifeLogger();
        try {
            logger.setup();
            System.out.println("HERE");
        }
        catch (IOException e){e.printStackTrace();}
        logger.log();
    }
}
