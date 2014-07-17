import java.util.ArrayList;
import java.util.List;

/**
 * Created by saggezza on 7/15/14.
 */
public class JPLogParse {
    public String parseRecord(String record){
        List<String> recArray = ParseLS.makeRecord(record);
        String output = parseData(recArray);
        return output;
    }

    private String parseData(List<String> rec){
        for (int i = 0; i < rec.size(); i++){
            String val = rec.get(i);
            if (i==0){
                val = val.split("=")[1];
            }
            String[] data = val.split("%3D");
            System.out.println("REC " + i + " " + data[0] + ":" + data[1]);
        }
        return "";
    }

    public static void main(String[] args) {
        JPLogParse jlp = new JPLogParse();
        String record = "params=dtm%3D1405438585336%2Cp%3Dmob%2Ctv%3D0.1.0%2Ctna%3DTracker+Test%2Caid%3DJavaPlow%2Clang" +
                "%3Dital%2Cres%3D760x610%2Cuid%3DKevin%2Ce%3Dpv%2Curl%3Dwww.saggezza.com%2Cpage%3DSaggezza+Home%2Crefr" +
                "%3DKG%2Cevn%3Dcom.saggezza";
        jlp.parseRecord(record);
    }
}
