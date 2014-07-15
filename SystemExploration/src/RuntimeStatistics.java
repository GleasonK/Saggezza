/**
 * Created by saggezza on 7/15/14.
 */
public class RuntimeStatistics {

    public void getStats(){
        long freeMem = Runtime.getRuntime().freeMemory();
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        int processors = Runtime.getRuntime().availableProcessors();
        Object[] stats = new Object[] { freeMem, maxMem, totalMem, processors };
        this.soutAll(stats);
    }

    public void soutAll(Object[] info){
        for (Object s : info){
            System.out.println( s.toString());
        }
    }

    public static void main(String[] args) {
        RuntimeStatistics rs = new RuntimeStatistics();
        rs.getStats();
    }
}
