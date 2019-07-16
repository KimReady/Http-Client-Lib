package com.naver.httpclientlib;

import java.lang.reflect.Type;

interface CallAdapter<R, T> {
    Type responseType();

    T adpat(CallTask<R> callTask);
}
