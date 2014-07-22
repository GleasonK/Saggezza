// File: RegExTest.java
// Author: Kevin Gleason
// Date: 6/10/14
// Use: To learn regex in java

import java.util.regex.*;

public class RegExTest {
    public Matcher simpleMatchABCE(String s){
        Pattern p = Pattern.compile("a*b[ce]");
        Matcher m = p.matcher(s);
        return m;
    }

    public boolean matchMe(String s, Pattern p){
        Matcher m = p.matcher(s);
        return m.find();
    }

    public static void main(String[] args){
        RegExTest ret = new RegExTest();
        String b = ret.simpleMatchABCE("abc").toString();
        System.out.println(b);
        boolean matched = ret.matchMe("H", Pattern.compile("^$"));
        System.out.println(matched);
    }

}
