package Phases;

/**
 * Created by saggezza on 7/15/14.
 */
public class MyMask implements PhasedMask {
    @Phase(index = 0)
    public void construct() {
        System.out.println("Constructing");
    }

    @Phase(index = 1)
    public void initialize(){
        System.out.println("Initializing");
    }

    @Phase(index = 2)
    public void activate(){
        System.out.println("Activating");
    }

}
