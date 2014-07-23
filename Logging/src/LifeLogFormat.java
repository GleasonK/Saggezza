// File: LifeLogFormat.java
// Author: Kevin Gleason
// Date: 6/11/14
// Use: Learn how Java logs work

import java.util.logging.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class LifeLogFormat extends Formatter{
    public String format(LogRecord log){
        StringBuffer sbuff = new StringBuffer(100);
        sbuff.append("\n<h1>LifeLog Level " + log.getLevel() + "</h1>\n");
        sbuff.append("<p>Log Message" + log.getMessage() + "</p>\n");
        sbuff.append("<p></p>");
        sbuff.append(new Date());
        return sbuff.toString();
    }

    public String getHead(Handler h){
            return "<!DOCTYPE html>\n<head>\n<style type='text/css'>\n" +
                    "CSS~~~" +
                    "</style>\n</head>\n<body>\n" +
                    "";
    }

    public String getTail(Handler h){
        return "</body>\n</html>";
    }

    public static void main(String[] args){
        LifeLogFormat llf = new LifeLogFormat();
        System.out.println(llf.format(new LogRecord(Level.FINE, "I'm ok")));
    }

}
