import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin Gleason on 6/16/2014.
 */
public class ProcessBuilding {
    public static String program = "cmd";

    public static void runProcess(String[] args) throws IOException{
        List<String> command = new ArrayList<String>();
        command.add(program); command.add("/c");
//        command.add(program + " /c " + args[0]);
        command.add(args[0]);

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> env = builder.environment();

        final Process p = builder.start();
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        readOut(br);
        System.out.println("Process complete.");
    }

    private static void readOut(BufferedReader br) throws IOException{
        String line;
        while ((line = br.readLine()) != null)
            System.out.println(line);
    }

    public static void main(String[] args) {
        ProcessBuilding.program = "powershell";
        String[] uArgs = {"rmdir KEVIN"};
        try {
            runProcess(uArgs);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
