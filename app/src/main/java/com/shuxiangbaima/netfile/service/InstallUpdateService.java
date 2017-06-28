package com.shuxiangbaima.netfile.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.shuxiangbaima.netfile.downutils.FileDownUtil;
import com.shuxiangbaima.netfile.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DIY on 2017/3/23.
 */

public class InstallUpdateService extends IntentService {

    private static final String TAG="InstallUpdateService";
    private String url;
    private String path;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public InstallUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MyLog.e(TAG,"***onStartCommand***");
        //1获取地址
        url=intent.getStringExtra("url");
        MyLog.e(TAG,"url:"+url);
        path= Environment.getExternalStorageDirectory()+File.separator+url.substring(url.lastIndexOf("/")+1,url.length());
        MyLog.e(TAG,"path:"+path);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/version/check/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                //打开方式1
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(path)),
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MyLog.e(TAG,"打开安装界面");
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                File file=new File(path);
                //TODO:判断路径是否存在!
                if (!file.exists()){
                    new File(path.substring(0,path.lastIndexOf("/"))).mkdirs();
                    file=new File(path);
                    MyLog.e(TAG,"file.getAbsolutePath:"+file.getAbsolutePath());
                }
                try {
                    String responseString = responseBody.string();
                    MyLog.e(TAG,"返回字符串responseString:"+responseString);
                    FileOutputStream fis=new FileOutputStream(file);
                    fis.write(responseString.getBytes());
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        FileDownUtil.NetData netData = retrofit.create(FileDownUtil.NetData.class);
        netData.getData(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe);
    }
}
