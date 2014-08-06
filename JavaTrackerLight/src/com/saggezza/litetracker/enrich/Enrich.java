package com.saggezza.litetracker.enrich;

import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Enrich class is used to have records passed through it and configure them to a proper fat table format</p>
 * <p>Currently enrich is separate from the EnrichGeneric class. May be combined with an if statement in a
 *   future version.</p>
 * <p>Do not allow data tracked to contain commas, will throw off the CSV</p>
 * @author Kevin Gleason
 * @version 0.0.2
 */
public class Enrich {

    public Enrich(){}

    public String enrich(ArrayList<String> record) {
        //String[] to Array, then run through record finding index and inserting per record.
//        List<String> LIrecord = getSampleRecord(record);
//        String newRec = LIrecord.toString().replace(", ",",");
//        System.out.println(newRec.substring(1,newRec.length()-1));
        ArrayList<String> newRecord = getRecordArray();
        for (String s : record){
            String[] data = s.split("=");
            int pos = FatFormat.getIndex(data[0]);
            //Currently toss out records that dont conform to standards. Error thrown by FatFormat.getIndex as it is.
            if (data[0].equals("ue_px") || data[0].equals("cx"))
                data[1] = base64Decode(data[1]);
            if (pos < 103)
                newRecord.set(pos,data[1]);
        }
//        System.out.println(makeNewData(newRecord));
        return makeNewData(newRecord);
    }

    private List<String> getSampleRecord(String record){
        String[] splits = record.split("&");
        List<String> list = new ArrayList<String>();
        for (String s : splits)
            list.add(s);
        return list;
    }

    /**
     * Makes an array that is the size of the fat table, populated with empty spaces for insertion.
     * @return The array with 103 items
     */
    private ArrayList<String> getRecordArray(){
        ArrayList<String> recordArray = new ArrayList<String>(103);
        for (int i=0; i<103; i++)
            recordArray.add("");
        return recordArray;
    }

    /**
     * Base64 decode the encoded string sent from the tracker
     * @param input Base64 encoded string
     * @return Original string
     */
    private String base64Decode(String input){
        Base64 base = new Base64();
        byte[] output = base.decode(input.getBytes());
        return new String(output);
    }
//
//    private String makeNewDataDep(List<String> newRecordList){
//        String newRecord = newRecordList.toString().replace(", ",",");
//        return newRecord.substring(1, newRecord.length()-1);
//    }

    /**
     * Get all data using an iterator. Probably the better choice.
     * @param newRecordList
     * @return
     */
    private String makeNewData(List<String> newRecordList){
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = newRecordList.iterator();
        while(iter.hasNext()){
            String s = iter.next();
            sb.append(s);
            if (iter.hasNext())
                sb.append(",");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String sampleRecord = "dtm=1402945115563&p=pc&tv=0.1.0&tna=Data+Pipeline+MW03+Struct+and+Unstruct&aid=Node+0001&e=ue&" +
                "ue_na=Pipeline+Statistics&evn=Saggezza&ue_px=eyJTdGF0dXMiOiJPSyIsIkl0ZXJhdGlvbiI6MywiQ1BVIjo4MH0%3D&" +
                "cx=eyJEYXRhIFdvcmsgSUQiOiJLR2kgMDAyIiwiQ29tcGFueSI6IktldmluRyBpbmMuIn0%3D";
        Enrich e = new Enrich();
//        String data = e.enrich(sampleRecord);
//        System.out.println("NEW RECORD:\n" + data.replace("+"," "));
    }

}
