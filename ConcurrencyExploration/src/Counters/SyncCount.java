package Counters;

/**
 * Created by saggezza on 7/22/14.
 */
public class SyncCount {
    private int c = 0;

    public synchronized void increment() {
        this.c++;
    }
    public synchronized void decrement() {
        this.c--;
    }
    public synchronized int value(){
        return this.c;
    }
}
