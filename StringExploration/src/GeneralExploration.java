import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by saggezza on 7/15/14.
 */
public class GeneralExploration {

    public void getDate(String record){
        List<String> recArray = ParseLS.makeRecord(record);
        long milliseconds = Long.valueOf(recArray.get(3));
        System.out.println(makeSQLDate(milliseconds));
    }

    public String makeSQLDate(long milliseconds){
        Timestamp date = new Timestamp(milliseconds);
        return date.toString();
    }

    public String makeUtilDate(long milliseconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String date = format.format(new Date(milliseconds));
        return date;
    }

    public static void main(String[] args) {
        GeneralExploration ge = new GeneralExploration();
        String record = "JavaPlow,mob,,1405438585336,pv,com.saggezza,,,Tracker+Test,0.1.0,,,Kevin,,,,,,,,,,,," +
                "www.saggezza.com,Saggezza+Home,KG,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,," +
                ",,,,,,,,,,,,,,,,,";

        ge.getDate(record);
    }

}
