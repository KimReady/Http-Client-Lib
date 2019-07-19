package com.naver.httpclientlib;

import java.io.IOException;

import okhttp3.Callback;

public interface CallTask<T> {

    Response<T> execute() throws IOException;

    void enqueue(CallBack callback);

    void cancel();

    boolean isCanceled();

}
