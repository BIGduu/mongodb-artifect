package com.bigduu.mongodbartifect.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author mugeng.du
 */
@Retention (RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD})
@Documented
public @interface LessThan {
    @AliasFor ("value")
    String targetField() default "";

    @AliasFor ("targetField")
    String value() default "";
}
