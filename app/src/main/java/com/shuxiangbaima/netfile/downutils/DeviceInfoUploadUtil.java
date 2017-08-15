package com.shuxiangbaima.netfile.downutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.DeviceInfo;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.bean.DeviceInfoBackBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DIY on 2017/6/13.
 */

public class DeviceInfoUploadUtil {

    private static final String TAG="DeviceInfoUploadUtil";

    public static void deviceDown(final String url, final Context context){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<DeviceInfoBackBean>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"onCompleted");
                SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                boolean aBoolean = preferences.getBoolean("initSubmit", false);
                if (!aBoolean){
                    return;
                }
                if (url.contains(Config.deviceInfoInit)){
                    Toast.makeText(context,"设备信息已经初始化",Toast.LENGTH_SHORT).show();
                    MyLog.e(TAG,"设备信息初始化成功");
                }else{
                    Toast.makeText(context,"设备信息已经更新",Toast.LENGTH_SHORT).show();
                    MyLog.e(TAG,"设备信息更新成功");
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context,"设备信息提交失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(DeviceInfoBackBean deviceInfo) {
                SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = config.edit();

                if (deviceInfo.getStatus()==200){
                    edit.putString("deviceInfo", DeviceInfo.getInfoNoIndex(context).toString());
                    edit.commit();
                    edit.putBoolean("successLastSubmit",true);
                    edit.commit();
                }else{
                    MyLog.e(TAG,"设备信息上传失败:"+deviceInfo.getMsg());
                    edit.putBoolean("successLastSubmit",false);
                    edit.commit();
                }
            }
        };
        IDeviceUpload device = retrofit.create(IDeviceUpload.class);
        device.getData(url)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscribe);
    }
}
