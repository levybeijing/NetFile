package com.shuxiangbaima.netfile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.bean.VersionUpdateBean;
import com.shuxiangbaima.netfile.progress.DownloadService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by DIY on 2017/8/9.
 */

public class VersionUpdate {

    public static void updatecheck( final Context context) throws PackageManager.NameNotFoundException {

        final Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<VersionUpdateBean>() {
            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(final VersionUpdateBean dataBean) {
                if (dataBean==null){
                    MyLog.e(TAG,"dataBean为空!任务结束");
                    return;
                }
                String status = dataBean.getStatus();
                String msg = dataBean.getMsg();
                MyLog.e(TAG,"状态码status:"+status+msg);
                if (status.equals("200")){
                    String download_url = dataBean.getData().getDownload_url();
                    MyLog.e(TAG,"download_url:"+download_url);
                    MyLog.e(TAG,"showDialog");
                    showDialog(download_url);
                }else if (status.equals("204")){
                    Toast.makeText(context,"当前内容已经是最新的了",Toast.LENGTH_SHORT).show();
                }else if (status.equals("400")){
                    Toast.makeText(context,"缺少参数",Toast.LENGTH_SHORT).show();
                }
            }

            public void showDialog(final String download_url) {
                new AlertDialog.Builder(context).setTitle("软件升级")
                        .setMessage("现在更新么！")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyLog.e(TAG,"软件开始更新");
                                /////
                                Intent intent=new Intent(context,DownloadService.class);
                                intent.putExtra("url",download_url);
                                context.startService(intent);
                                Toast.makeText(context,"开始下载",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("返回",null).show();
            }
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"***onCompleted***");
            }
        };
        IUpdateVersion update = retrofit.create(IUpdateVersion.class);
        update.checkUpdate(context.getPackageName(),context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName)///
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe);
    }
}
