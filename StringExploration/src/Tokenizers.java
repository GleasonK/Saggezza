/**
 * Created by saggezza on 6/19/14.
 */

import java.util.*;

public class Tokenizers {
    private String string;

    public Tokenizers(String string){
        this.string=string;
    }

    public void tokens(){
        StringTokenizer stk = new StringTokenizer(this.string);
        while (stk.hasMoreTokens())
            System.out.println(stk.nextToken());
    }

    public static void main(String[] args) {
        Tokenizers tokens = new Tokenizers("Testing Hello World Kevin Gleason World Cup");
        tokens.tokens();
    }
}
