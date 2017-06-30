package com.shuxiangbaima.netfile.flowcount;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.shuxiangbaima.netfile.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by DIY on 2017/6/27.
 */

public class FlowActivity extends Activity {
    protected static final int REFRESH_TRAFFIC = 1;
    private RVFlowAdapter adapter;
    private RecyclerView rv;
    private Timer timer;
    private TimerTask timerTask;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_TRAFFIC:
                    adapter.setData(new TrafficManager(FlowActivity.this).getInternetTrafficInfos());
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        rv = (RecyclerView) findViewById(R.id.rv_flow);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        adapter = new RVFlowAdapter(FlowActivity.this);
        rv.setAdapter(adapter);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = REFRESH_TRAFFIC;
                mHandler.sendMessage(msg);
                Log.e(TAG, "====run: .");
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 2000,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer=null;
        timerTask=null;
    }
}
