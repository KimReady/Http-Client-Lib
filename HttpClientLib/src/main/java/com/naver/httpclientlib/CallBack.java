package com.naver.httpclientlib;

import java.io.IOException;

public interface CallBack {
    void onResponse(Response<?> response) throws IOException;
    void onFailure(IOException e);
}
