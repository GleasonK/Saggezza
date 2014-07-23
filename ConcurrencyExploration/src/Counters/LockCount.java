package Counters;

/**
 * Created by saggezza on 7/22/14.
 */
public class LockCount {
    private long c1 = 0;
    private long c2 = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    //Only use when the two values will never be incremented by the same process.
    public void inc1() {
        synchronized(lock1) {
            c1++;
        }
    }

    public void inc2() {
        synchronized(lock2) {
            c2++;
        }
    }
}