import java.util.ArrayList;
import java.util.List;

/**
 * Created by saggezza on 7/2/14.
 */
public class ParseLS {
    public static void showArray(String[] sarr){
        for (String i : sarr)
            System.out.print(i + ",");
    }

    public static List<String> makeRecord(String rec){
        List<String> recArray = new ArrayList<String>();
        String[] recSplit = rec.split("%2C");
        for (String s : recSplit) {
            recArray.add(s);
        }
        return recArray;
    }

    public static void main(String[] args) {
        String s = "-rw-rw-r--  1 saggezza saggezza    6 Jul  3 15:21 0.txt";
        String[] p = s.split(" +");
        s = s.replaceAll("[ ]+", ",");
        System.out.println(s);
        showArray(p);
    }
}
