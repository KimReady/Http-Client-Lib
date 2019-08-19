package com.naver.httpclienttest;

import android.app.Application;

import com.naver.ers.CustomData;
import com.naver.ers.Reporter;

import java.util.Date;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CustomData.Builder customDataBuilder = new CustomData.Builder()
                .putData("App Start time", new Date().toString());
        Reporter.setCustomData(customDataBuilder.build());
        Reporter.register(this);
    }
}
