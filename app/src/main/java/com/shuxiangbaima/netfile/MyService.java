package com.shuxiangbaima.netfile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shuxiangbaima.netfile.downutils.FileDownUtil;

/**
 * Created by DIY on 2017/3/15.
 */

public class MyService extends Service {

    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "***onCreate***");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.e(TAG, "***onStartCommand***");
        String url = intent.getStringExtra("url");
        MyLog.e(TAG, "下载地址:" + url);
        String path = intent.getStringExtra("path");
        MyLog.e(TAG, "存储路径:" + path);
        if (url != null && path != null) {
            MyLog.e(TAG, "开始下载");
            new FileDownUtil().downloadNetFile(url, path);
        }
        stopSelf();
        MyLog.e(TAG, "停止服务");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.e(TAG, "***onDestroy***");
    }
}
