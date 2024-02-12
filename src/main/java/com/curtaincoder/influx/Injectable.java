package com.curtaincoder.influx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a marker annotation which is used to indicate that a class is injectable. Influx will use this to generate a
 * <b>META-INF/services</b> file that will be used to locate injectable classes at runtime via {@link java.util.ServiceLoader}.
 * @author John Dunlap
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Injectable {
}
