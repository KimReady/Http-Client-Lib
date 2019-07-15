package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.RequestMapping;

public interface InvalidHttpService {
    CallTask<Integer> getID();

    @RequestMapping(value="/abc/{pw}", method= RequestMethod.GET)
    String getPW();
}
