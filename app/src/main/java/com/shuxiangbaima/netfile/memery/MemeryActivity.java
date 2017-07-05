package com.shuxiangbaima.netfile.memery;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.shuxiangbaima.netfile.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DIY on 2017/6/28.
 */

public class MemeryActivity extends Activity {
    protected static final int REFRESH_TRAFFIC = 1;
    private RVMemeryAdapter adapter;
    private RecyclerView rv;
    private Timer timer;
    private TimerTask timerTask;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_TRAFFIC:
                    adapter.setData(((ActivityManager)getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses());
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memery);
        //安卓5.0以上系统无法获取其他进程信息
        rv = (RecyclerView) findViewById(R.id.rv_memery);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        adapter=new RVMemeryAdapter(this);
        rv.setAdapter(adapter);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = REFRESH_TRAFFIC;
                mHandler.sendMessage(msg);
                Log.e("", "*****run: ");
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,2000,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer=null;
        timerTask=null;
    }
}
