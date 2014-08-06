package Test;

/**
 * Created by saggezza on 7/15/14.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface TesterInfo {

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    String[] tags() default "";

    String createdBy() default "Kevin";

    String lastModified() default "July 2014";

}