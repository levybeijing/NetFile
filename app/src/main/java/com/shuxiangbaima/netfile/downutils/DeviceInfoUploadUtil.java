package com.shuxiangbaima.netfile.downutils;

import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.bean.DeviceInfoBackBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DIY on 2017/6/13.
 */

public class DeviceInfoUploadUtil {

    private static final String TAG="DeviceInfoUploadUtil";

    public static void deviceDown(final String url){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<DeviceInfoBackBean>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(DeviceInfoBackBean deviceInfo) {
                if (deviceInfo.getStatus()==200){
                    MyLog.e(TAG,"设备信息上传成功");
                }else{
                    MyLog.e(TAG,"设备信息上传失败:"+deviceInfo.getMsg());
                }
            }
        };
        Device device = retrofit.create(Device.class);
        device.getData(url)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscribe);
    }
    public interface Device{
        @GET
        Observable<DeviceInfoBackBean> getData(@Url String fileUrl);
    }
}
