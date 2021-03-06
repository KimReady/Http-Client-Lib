package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.DynamicURL;
import com.naver.httpclientlib.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;

class Utils {
    private static final Pattern PATH_PARAM_URL_REG = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9_-]*\\}");

    static final long DEFAULT_CALL_TIMEOUT = 0;
    static final long DEFAULT_TIMEOUT = 10000;

    private Utils(){}

    /**
     * object가 Null 여부 검사
     *
     * @param object  검사 대상
     * @param message null 일 경우 표시할 Error Message
     */
    static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    /**
     * bool parameter가 True인지 검사
     * @param message Exception 발생시 출력할 message.
     */
    static void checkIsTrue(boolean bool, String message) {
        if(!bool) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * bool parameter가 False인지 검사
     * @param message Exception 발생시 출력할 message.
     */
    static void checkIsFalse(boolean bool, String message) {
        if(bool) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * service method가 Http Method에 대한 Annotation을 적용 했는지 검사
     */
    static void checkSupportedMethod(Method method) {
        if (method.getReturnType() != CallTask.class) {
            throw new UnsupportedOperationException("Return Type of the method should be 'CallTask'.");
        }

        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestMapping || annotation instanceof DynamicURL) {
                return;
            }
        }
        throw new UnsupportedOperationException("please add a annotation '@RequestMapping' or '@DynamicURL' on your method. ");
    }

    /**
     *  parameter value가 String으로 변환 가능한지 검사
     * @param key parameter name
     * @param value actual parameter value
     */
    static void checkValidParam(String key, Object value) {
        if (value instanceof String || value instanceof Number) {
            return;
        }
        throw new IllegalArgumentException("the type of '" + key + "' can't cast to String.");
    }

    static List<Object> checkIsList(Object object) {
        if(object instanceof Object[]) {
            return Arrays.asList((Object[]) object);
        } else if(object instanceof List) {
            return (List<Object>) object;
        }
        throw new IllegalArgumentException("the type of '@Queries' must be Array or List.");
    }


    /**
     * relative URL에서 {} 로 표기된 path parameter 검출
     * @param relUrl 변환되기 전 relative URL
     */
    static Matcher matchPathUrl(String relUrl) {
        return PATH_PARAM_URL_REG.matcher(relUrl);
    }

    /**
     * Encode Request Query value
     */
    static String encodeQuery(Object query, String encodeType, boolean isEncoded) {
        if (isEncoded) {
            return String.valueOf(query);
        }
        try {
            return URLEncoder.encode(String.valueOf(query), encodeType);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type paramType = type.getActualTypeArguments()[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    static HttpUrl getHttpUrl(Object url) {
        if(url instanceof String) {
            return HttpUrl.get((String) url);
        } else if(url instanceof java.net.URL || url instanceof URI) {
            return HttpUrl.get(url.toString());
        } else if(url instanceof HttpUrl) {
            return (HttpUrl) url;
        }
        return null;
    }
}
