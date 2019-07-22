package com.naver.httpclientlib.annotation;

import com.naver.httpclientlib.RequestMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface RequestMapping {

    String value() default Default.relativeURL;
    RequestMethod method() default RequestMethod.GET;
}
