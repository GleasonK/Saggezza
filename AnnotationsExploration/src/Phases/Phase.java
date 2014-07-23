package Phases; /**
 * Created by saggezza on 7/15/14.
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Phase {
    int index();
    String displayName() default "";
}