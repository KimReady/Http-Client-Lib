package com.naver.httpclientlib;

import java.io.IOException;

public interface CallBack<T> {
    void onResponse(Response<T> response) throws IOException;
    void onFailure(IOException e);
}
