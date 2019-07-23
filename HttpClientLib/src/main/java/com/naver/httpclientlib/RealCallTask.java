package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

class RealCallTask<T> implements CallTask<T> {
    private RequestFactory requestFactory;
    private okhttp3.Call.Factory okhttpCallFactory;
    private okhttp3.Call okhttpCall;
    private Converter converter;
    private ExecutorService executorService;
    private boolean isCanceled;

    RealCallTask(RequestFactory requestFactory, okhttp3.Call.Factory okhttpCallFactory,
                 Converter<T, ?> converter, ExecutorService executorService) {
        this.requestFactory = requestFactory;
        this.okhttpCallFactory = okhttpCallFactory;
        this.converter = converter;
        this.executorService = executorService;
        this.isCanceled = false;
        this.okhttpCall = newOkhttpCall();
    }

    @Override
    public Response<T> execute() throws IOException {
        Utils.checkIsFalse(isCanceled, "the CallTask has been canceled. so you can't execute it.");
        return convertResponse(okhttpCall.execute());
    }

    @Override
    public void enqueue(final CallBack callback) {
        Utils.checkIsFalse(isCanceled, "the CallTask has been canceled. so you can't execute it.");
        Runnable asyncCall = new Runnable() {
            @Override
            public void run() {
                try {
                    Response<T> response = convertResponse(okhttpCall.execute());
                    callback.onResponse(response);
                } catch(IOException e) {
                    callback.onFailure(e);
                }
            }
        };
        executorService.execute(asyncCall);
    }

    @Override
    public synchronized void cancel() {
        if (okhttpCall != null && !isCanceled) {
            isCanceled = true;
            okhttpCall.cancel();
        }
    }

    @Override
    public synchronized boolean isCanceled() {
        return isCanceled;
    }

    private okhttp3.Call newOkhttpCall() {
        okhttp3.Call call;
        try {
            call = okhttpCallFactory.newCall(requestFactory.create(converter));
        } catch (IOException e) {
            throw new IllegalStateException("can't create Call, because of " + e.getMessage());
        }
        Utils.checkNotNull(call, "there is no matching call");
        return call;
    }

    private Response<T> convertResponse(okhttp3.Response response) {
        return new Response<>(response, converter);
    }

}
