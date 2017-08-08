package com.shuxiangbaima.netfile.downutils;

import android.content.Context;
import android.widget.Toast;

import com.shuxiangbaima.netfile.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
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

public class ConcretePluginUtil {

    private static String plugin_path="/sdcard/MobileAnJian/Plugin";

    private static final String TAG="ConcretePluginUtil";

    public static void pluginDown(final String url, final Context context){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"插件下载完毕");
                Toast.makeText(context,"插件下载完毕",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context,"插件下载失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String pluginName = url.substring(url.lastIndexOf("/") + 1, url.length());
                try {
                    FileOutputStream fos=new FileOutputStream(new File(plugin_path+File.separator+pluginName));
                    fos.write(responseBody.bytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        IConcretePluginUtil concretePlugin = retrofit.create(IConcretePluginUtil.class);
        concretePlugin.getData(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe);
    }
    public interface IConcretePluginUtil{
        @GET
        Observable<ResponseBody> getData(@Url String fileUrl);
    }
}
