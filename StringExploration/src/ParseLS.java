/**
 * Created by saggezza on 7/2/14.
 */
public class ParseLS {
    public static void showArray(String[] sarr){
        for (String i : sarr)
            System.out.print(i + ",");
    }

    public static void main(String[] args) {
        String s = "-rw-rw-r--  1 saggezza saggezza    6 Jul  3 15:21 0.txt";
        String[] p = s.split(" +");
        s = s.replaceAll("[ ]+", ",");
        System.out.println(s);
        showArray(p);
    }
}
