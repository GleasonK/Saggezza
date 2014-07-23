import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Kevin Gleason on 6/16/2014.
 */
public class TerminalApp {
    public static String program = "cmd";

    public static void runUnixExec(String command){
        command = program + " /c " + command;
        String s = null;
        try {
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            readOut(stdIn);
            readOut(stdErr);

            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public static void setProgram(String program){ program = program; }

    private static void readOut(BufferedReader br) throws IOException{
        String s = null;
        while ((s = br.readLine()) != null)
            System.out.println(s);
    }

    public static void main(String[] args){
        TerminalApp.program = "powershell";
        runUnixExec("rmdir KEVIN");
//        runUnixExec("ls");
    }
}
