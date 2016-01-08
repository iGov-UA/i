package org.igov.log;

import java.lang.annotation.*;

/**
 * @author dgroup
 * @since 08.01.2016
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Send {
}
