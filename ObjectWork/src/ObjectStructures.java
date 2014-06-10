// File: ObjectStructures.java
// Author: Kevin Gleason
// Date: 6/10/14
// Use: Learn about data structures of type object

import java.util.*;

public class ObjectStructures {
    Map<String, Object> oMap;
    List<Object> oList;


    public ObjectStructures(){
        this.oMap = new LinkedHashMap<String, Object>();
        this.oList = new LinkedList<Object>();
    }

    public String toString(){
        return this.oList.toString() + " " + this.oMap.toString();
    }

    public static void main(String[] args){
        // Map
        ObjectStructures os = new ObjectStructures();
        os.oMap.put("Name",100);
        os.oMap.put("Date",new Date().getTime());
        os.oMap.put(os.toString(), os.oMap);
        System.out.println(os.oMap.toString());

        // List
        os.oList.add("String Object");
        os.oList.add(20);
        os.oList.add(20.001);
        os.oList.add(os.oMap);
        os.oList.add(os.oList);
        System.out.println(os.oList.toString());
    }
}
