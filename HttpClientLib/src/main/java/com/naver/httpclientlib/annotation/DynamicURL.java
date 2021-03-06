package com.naver.httpclientlib.annotation;

import com.naver.httpclientlib.RequestMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface DynamicURL {
    RequestMethod method() default RequestMethod.GET;
}
