package com.shuxiangbaima.netfile.downutils;

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
 * Created by DIY on 2017/6/12.
 */

public class PluginDownUtil {

    private static String plugin_path="/sdcard/MobileAnJian/Plugin";

    private static final String TAG="PluginDownUtil";

    public static void pluginDown(final String url){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String name = url.substring(url.lastIndexOf("/") + 1, url.length());
                File file=new File(plugin_path+File.separator+name);
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    fos.write(responseBody.bytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        IPluginAnJian pluginAnJian = retrofit.create(IPluginAnJian.class);
        pluginAnJian.getData(url)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscribe);
    }
    public interface IPluginAnJian{
        @GET
        Observable<ResponseBody> getData(@Url String fileUrl);
    }
}
