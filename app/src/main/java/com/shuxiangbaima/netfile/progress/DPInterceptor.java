package com.shuxiangbaima.netfile.progress;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by DIY on 2017/6/20.
 */

class DPInterceptor implements Interceptor {

    private DPListener listener;

    public DPInterceptor(DPListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DPResponseBody(originalResponse.body(), listener))
                .build();
    }
}
