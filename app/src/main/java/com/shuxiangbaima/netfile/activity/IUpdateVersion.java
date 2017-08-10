package com.shuxiangbaima.netfile.activity;

import com.shuxiangbaima.netfile.bean.VersionUpdateBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by DIY on 2017/8/9.
 */

public interface IUpdateVersion {
    @POST("version/check")
    @FormUrlEncoded
    Observable<VersionUpdateBean> checkUpdate(@Field("package") String pkg,
                                              @Field("version") String ver);
}
