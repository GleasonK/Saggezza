import java.io.*;

/**
 * Created by saggezza on 7/31/14.
 */
public class ParseGeoData {
    public static final String TAB = "\t";
    public static final String SPACES = "[ ]+";

    public static void parseData(File geoData, File output){
        BufferedReader reader;
        BufferedWriter writer;
        try{
            reader = new BufferedReader(new FileReader(geoData));
            writer = new BufferedWriter(new FileWriter(output));
            String line = reader.readLine();
            while(line != null){
//                System.out.println("LINE: " + line);
                line = line.replaceAll(",","");
                String outline = buildLine(parseLine(line));
//                System.out.println("OUTLINE: " + outline);
//                writer.write(outline);
                line=reader.readLine();
            }
            reader.close();
//            writer.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static String[] parseLine(String line){
        String[] parsed = line.split(TAB);
        if (!parsed[7].equals("")){
            System.out.println(parsed[6] + ", " + parsed[7] + ", " + parsed[8]);
        }
        return parsed;
    }

    public static String[] shiftStringArray(String[] parsedLine){
        for (int i=3; i<parsedLine.length-1; i++){
            parsedLine[i]=parsedLine[i+1];
        }
        parsedLine[parsedLine.length-1]=null;
        return parsedLine;
    }

    public static String buildLine(String[] parsedLine){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<parsedLine.length; i++){
            sb.append(parsedLine[i]);
            if (i<parsedLine.length-1){
                sb.append(",");
            }
        }
        return sb.toString()+"\n";
    }

    public static void main(String[] args) {
//        File geoData = new File("sampleGeo.txt");
        File geoData = new File("/home/saggezza/Documents/Saggezza/GeoLocation/Data/AreaCode_allCountries.txt");
        File fout = new File("geooDataOut.csv");
        ParseGeoData.parseData(geoData, fout);

    }
}
