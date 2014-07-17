package RunControl;

/**
 * Created by saggezza on 7/15/14.
 */
import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CanRun {

}
