package com.naver.httpclientlib;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class CallAdapterFactory {

    static CallAdapter<?, ?> create(Type returnType) {
        final Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType) returnType);

        return new CallAdapter<Object, CallTask<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public CallTask<Object> adpat(CallTask<Object> callTask) {
                // TODO : implements call back function
                return null;
            }
        };
    }
}
