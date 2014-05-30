import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Kevin Gleason on 5/20/2014.
 */
public class LinkedReverserC<T> implements LinkedReverser<T> {
    //Instance Variables
    private Deque<T> dq;

    public LinkedReverserC(){
        this.dq = new LinkedList<T>();
    }

    public LinkedReverserC(Deque<T> dq){
        this.dq = dq;
    }

    public LinkedReverser<T> add(T info){
        Deque<T> tmp = this.dq;
        tmp.add(info);
        return new LinkedReverserC<T>(tmp);
    }

    //Remove an element.
    public LinkedReverser<T> remove(){
        Deque<T> tmp = this.dq;
        tmp.pop();
        return new LinkedReverserC<T>(tmp);
    }

    public LinkedReverser<T> remove(T info){
        Deque<T> tmp = this.dq;
        tmp.remove(info);
        return new LinkedReverserC<T>(tmp);
    }

    public LinkedReverser<T> reverse(){
        Deque<T> tmp = this.dq;
        Deque<T> rev = new LinkedList<T>();
        while (!tmp.isEmpty())
            rev.add(tmp.pollLast());
        return new LinkedReverserC<T>(rev);
    }

    public String toString(){
        return this.dq.toString();
    }

    public static void main(String[] args){
        LinkedReverser<String> strRev = new LinkedReverserC<String>();
        strRev = strRev.add("Hi");
        strRev = strRev.add("Hello");
        System.out.println(strRev.toString());
        strRev = strRev.reverse();
        System.out.println(strRev.toString());
        strRev = strRev.add("Ciao");
        strRev = strRev.add("Hola");
        System.out.println(strRev.toString());
        strRev = strRev.reverse();
        System.out.println(strRev.toString());
        strRev = strRev.remove();
        System.out.println(strRev.toString());
        strRev.remove("Hi");
        System.out.println(strRev.toString());
    }
}
