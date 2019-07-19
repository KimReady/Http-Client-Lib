package com.naver.httpclientlib;

import java.io.IOException;

public interface CallBack {
    void onResponse(Response<?> response);
    void onFailure(Response<?> response, IOException e);
}
