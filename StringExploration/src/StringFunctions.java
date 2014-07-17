/**
 * Created by saggezza on 7/2/14.
 */
public class StringFunctions {
    private String string;

    public StringFunctions(String string){
        this.string = string;
    }

    public String[] splitAndReplace(String splitToken, String[] replaceTokens){
        String[] splits = string.split(splitToken);
        String replaced = splits[0].replace(replaceTokens[0],replaceTokens[1]);
        String[] output = new String[] {replaced, splits[1]};
        return output;
    }

    public static void main(String[] args) {
        StringFunctions sf = new StringFunctions("12-04-2014 06");
        String[] sar = sf.splitAndReplace(" ", new String[] {"-",""});
        System.out.println(sar[0] + " : " + sar[1]);

        String s = "Hello+World";
        String p = "HelloWorld";
        s = s.replace("+", " ");
        p = p.replace("+", " ");
        System.out.println(s + p);
    }
}
