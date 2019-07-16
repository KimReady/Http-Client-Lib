package com.naver.httpclientlib;

import java.io.IOException;

public interface CallTask<T> {

    Response<T> execute() throws IOException;

    void enqueue();

    void cancel();

    boolean isCanceled();

}
