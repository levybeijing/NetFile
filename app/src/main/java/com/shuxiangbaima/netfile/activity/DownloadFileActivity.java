package com.shuxiangbaima.netfile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shuxiangbaima.netfile.downutils.FileDownUtil;
import com.shuxiangbaima.netfile.MyLog;

/**
 * Created by DIY on 2017/3/23.
 */

public class DownloadFileActivity extends AppCompatActivity {

    private static final String TAG="DownloadFileActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url=getIntent().getStringExtra("url");
        MyLog.e(TAG,"url地址:"+ url);
        String path=getIntent().getStringExtra("path");
        MyLog.e(TAG,"目标路径path:"+ path);
        if (url!=null&&path!=null){
            new FileDownUtil().downloadNetFile(url,path);
            MyLog.e(TAG,"开始下载");
        }
    }
}
