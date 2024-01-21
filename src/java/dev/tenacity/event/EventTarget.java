package dev.tenacity.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTarget {
    dev.tenacity.event.EventPriority priority() default dev.tenacity.event.EventPriority.LOW;
}
