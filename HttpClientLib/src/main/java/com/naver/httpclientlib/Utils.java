package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private static final Pattern PATH_PARAM_URL_REG = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9_-]*}");

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
     * method가 Http Method에 대한 Annotation을 적용 했는지 검사
     */
    static void checkSupportedMethod(Method method) {
        if (method.getReturnType() != CallTask.class) {
            throw new UnsupportedOperationException("Return Type of the method should be 'CallTask'.");
        }

        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestMapping) {
                return;
            }
        }
        throw new UnsupportedOperationException("please add a annotation '@RequestMapping' above the method. ");
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
    static String encodeQuery(Object query, boolean isEncoded) {
        if (isEncoded) {
            return String.valueOf(query);
        }
        try {
            return URLEncoder.encode(String.valueOf(query), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    static boolean checkResolvableType(Type type) {
        if (type instanceof Class<?>) {
            return false;
        } else if (type instanceof TypeVariable || type instanceof WildcardType) {
            return true;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (checkResolvableType(typeArgument)) {
                    return true;
                }
            }
            return false;
        } else if (type instanceof GenericArrayType) {
            return checkResolvableType(((GenericArrayType) type).getGenericComponentType());
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType or GenericArrayType");
    }

    public static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type paramType = type.getActualTypeArguments()[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    static String hasToString(Object object) {
        if(object instanceof String) {
            return String.valueOf(object);
        }
        Method[] methods = object.getClass().getDeclaredMethods();
        for(Method method : methods) {
            if(method.getName().equals("toString")) {
                return object.toString();
            }
        }
        throw new IllegalArgumentException(object.getClass() + " must implement 'toString()' method or be type of String.");
    }
}
