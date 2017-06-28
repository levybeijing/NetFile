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

public class WordsDownUtil {

    private static final String TAG="WordsDownUtil";

    public static void wordsDown(String url, final String path){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"----字库更新结束----\n");
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"字库更新失败--onError:"+e.getMessage());
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                File wordFile=new File(path);
                if (!wordFile.exists()){
                    new File(wordFile.getParent()).mkdirs();
                    wordFile=new File(path);
                }
                try {
                    FileOutputStream fos=new FileOutputStream(wordFile);
                    fos.write(responseBody.bytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        IWord iWord = retrofit.create(IWord.class);
        iWord.getData(url)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscribe);
    }
    public interface IWord{
        @GET
        Observable<ResponseBody> getData(@Url String fileUrl);
    }
}
