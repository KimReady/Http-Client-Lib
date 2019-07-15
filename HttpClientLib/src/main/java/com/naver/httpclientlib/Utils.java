package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {
    private static final Pattern PATH_PARAM_URL_REG = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9_-]*}");

    /**
     * object가 Null 여부 검사
     * @param object 검사 대상
     * @param message null 일 경우 표시할 Error Message
     */
    static <T> T checkNotNull(T object, String message) {
        if(object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    /**
     * method가 Http Method에 대한 Annotation을 적용 했는지 검사
     */
    static void checkSupportedMethod(Method method) {
        if(method.getReturnType() != CallTask.class) {
            throw new UnsupportedOperationException("Return Type of the method should be 'CallTask'.");
        }

        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations) {
            if(annotation instanceof RequestMapping) {
                return;
            }
        }
        throw new UnsupportedOperationException("please add a annotation '@RequestMapping' above the method. ");
    }

    static void checkValidateParamType(Object object) {
        if(object instanceof Number || object instanceof String || object instanceof Map) {
            return ;
        }
        throw new IllegalArgumentException("Invalid parameter type");
    }

    static void checkNotMap(String key, Object value) {
        if(value instanceof Map) {
            throw new IllegalArgumentException("type of the '"+ key + "' must not be Map.");
        }
    }

    static Matcher matchPathUrl(String relUrl) {
        return PATH_PARAM_URL_REG.matcher(relUrl);
    }

    static Object encodeQuery(Object query, boolean isEncoded) {
        if(isEncoded) {
            return query;
        }
        try {
            return URLEncoder.encode(String.valueOf(query), "UTF-8");
        } catch(UnsupportedEncodingException e) {
            return null;
        }
    }
}
