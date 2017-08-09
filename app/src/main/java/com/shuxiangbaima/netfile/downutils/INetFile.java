package com.shuxiangbaima.netfile.downutils;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by DIY on 2017/8/9.
 */

public interface INetFile {
    @GET
    Observable<ResponseBody> getData(@Url String fileUrl);
}
