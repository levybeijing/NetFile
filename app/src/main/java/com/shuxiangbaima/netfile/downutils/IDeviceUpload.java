package com.shuxiangbaima.netfile.downutils;

import com.shuxiangbaima.netfile.bean.DeviceInfoBackBean;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by DIY on 2017/8/9.
 */

public interface IDeviceUpload {
    @GET
    Observable<DeviceInfoBackBean> getData(@Url String fileUrl);
}
