/**
 * Created by saggezza on 6/20/14.
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class Anagrams  {
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        private Text sortedText = new Text();
        private Text originalText = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
                throws IOException {
            String word = value.toString();
            char[] wordChars = word.toCharArray();
            Arrays.sort(wordChars);
            String sortedWord = new String(wordChars);
            sortedText.set(sortedWord);
            originalText.set(word);
            output.collect(sortedText, originalText);
        }
    }


    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
        private Text outputKey = new Text();
        private Text outputValue = new Text();

        public void reduce(Text anagramKey, Iterator<Text> anagramValues, OutputCollector<Text,Text> results,
                           Reporter reporter) throws IOException {
            String output = "";
            while (anagramValues.hasNext()){
                Text anagram = anagramValues.next();
                output = output + anagram.toString() + "~";
            }
            StringTokenizer outputTokenizer = new StringTokenizer(output, "~");
            outputKey.set(output);
            ......
        }
    }
}
