package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.adapter.RVSetAdapter;

import static com.shuxiangbaima.netfile.activity.MainActivity.MESSAGE_PROGRESS;

/**
 * Created by DIY on 2017/6/21.
 */

public class SettingsActivity extends Activity {
    private static final String TAG="SettingsActivity";
    private Toolbar bar;
    private LocalBroadcastManager bManager;

    private boolean isShowDialog=false;
    private ProgressDialog dialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//
//            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
//                //接收到第一时间创建对话框
//                if (!isShowDialog){
////                //创建对话框
//                    dialog = new ProgressDialog(SettingsActivity.this);
//                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条的形式为圆形转动的进度条
//                    dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
//                    dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//                    dialog.setTitle("下载");
//                    dialog.setMax(100);
//                    dialog.show();
//                }
//                Download download = intent.getParcelableExtra("download");
//                int l=download.getProgress();
//                dialog.setProgress(l);
//                if (l>=100){
//                    dialog.dismiss();
//                }
//            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bar = (Toolbar) findViewById(R.id.bar_settings);
        bar.setTitle("设置");
        bar.setNavigationIcon(R.drawable.back_16);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_settings);
        GridLayoutManager manager=new GridLayoutManager(this,1);
        rv.setLayoutManager(manager);
        String[] array = getResources().getStringArray(R.array.sets);
        RVSetAdapter adapter=new RVSetAdapter(this,array);
        rv.setAdapter(adapter);


        registerReceiver();
    }
    private void registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.e(TAG,"***onDestroy***");
        bManager.unregisterReceiver(broadcastReceiver);
    }
}
