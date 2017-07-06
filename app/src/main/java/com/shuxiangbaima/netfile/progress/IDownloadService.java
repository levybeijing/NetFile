package com.shuxiangbaima.netfile.progress;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by DIY on 2017/6/20.
 */

public interface IDownloadService {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}

