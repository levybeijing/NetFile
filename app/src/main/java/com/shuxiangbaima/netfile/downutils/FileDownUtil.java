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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DIY on 2017/3/27.
 */

public class FileDownUtil {

    private static final String TAG="FileDownUtil";
    private File tempFile;
    private File sxbm;

    public FileDownUtil(){
        sxbm = new File(File.separator+"sdcard"+File.separator+"shuxiangbaima");
        if (!sxbm.exists()){
            sxbm.mkdirs();
        }
        tempFile = new File(sxbm,"tmp.txt");
        MyLog.e(TAG,"临时文件路径:"+ this.tempFile.getAbsolutePath());
    }
    public boolean downloadNetFile(String url, final String path) {
        tempFile.deleteOnExit();
        if (path==null||url==null){
            return false;
        }
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"----任务结束----\n");
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError:"+e.getMessage());
                MyLog.e(TAG,"----任务失败----\n");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String response=responseBody.string();
                    MyLog.e(TAG,"responseBody:"+response);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(response.getBytes());
                    fos.flush();
                    fos.close();
                    MyLog.e(TAG,"临时文件大小:"+tempFile.length()+"b");
                    boolean b = tempFile.renameTo(new File(path));
                    MyLog.e(TAG,"文件是否rename成功?:"+b);
                    MyLog.e(TAG,"临时文件是否存在:"+tempFile.exists());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        INetFile netData = retrofit.create(INetFile.class);
        netData.getData(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe);
        return true;
    }
}
