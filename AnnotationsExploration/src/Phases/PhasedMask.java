package Phases;

/**
 * Created by saggezza on 7/15/14.
 */
public interface PhasedMask extends Phased {
    @Phase(index = 0)
    public void construct();

    @Phase(index = 1)
    public void initialize();

    @Phase(index = 2, displayName = "Activating...")
    public void activate();

}
