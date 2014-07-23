package RunControl;

/**
 * Created by saggezza on 7/15/14.
 */
import java.lang.reflect.Method;

public class MyTest {

    public static void main(String[] args) {

        AnnotationRunner runner = new AnnotationRunner();
        Method[] methods = runner.getClass().getMethods();

        for (Method method : methods) {
            CanRun annos = method.getAnnotation(CanRun.class);
            if (annos != null) {
                try {
                    long start = System.nanoTime();
                    method.invoke(runner);
                    long end = System.nanoTime();
                    long duration = end - start;
                    System.out.println("Duration - " + duration/10e5 + "ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}