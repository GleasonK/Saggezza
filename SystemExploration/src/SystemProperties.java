import java.io.*;
import java.util.*;

/**
 * Created by saggezza on 7/14/14.
 */
public class SystemProperties {
    private Properties props = System.getProperties();
    public void writeFileOut(){
        File fout = new File("./propList.txt");
        try {
            int cafebabe = 0xCAFEBABE;
            FileWriter writer = new FileWriter(fout);
            writer.write("CAFE: " + cafebabe);
            writer.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public String writeProps(){
        File fout = new File("./prop.txt");
        try {
            OutputStream os = new FileOutputStream(fout);
            this.props.store(os, "Current Properties");

        }
        catch (IOException e) { e.printStackTrace(); }
        return "";
    }

    public static void main(String[] args) {
        SystemProperties sp = new SystemProperties();
        sp.writeFileOut();
    }
}
