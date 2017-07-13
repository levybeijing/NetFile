package com.shuxiangbaima.netfile.wifinearby;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DIY on 2017/6/29.
 */

public class WifiInfoActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    getWifi();
                    break;
                default:
                    break;
            }
        }
    };
    private TextView tv;
    private WifiManager wm;
    private List<ScanResult> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifinearby);

        tv  = (TextView) findViewById(R.id.tv_wifi);
        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        getWifi();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,2000);
    }

    private void getWifi() {


        WifiInfo info = wm.getConnectionInfo();
        int strength = info.getRssi();
        int speed = info.getLinkSpeed();
        String units = WifiInfo.LINK_SPEED_UNITS;
        String ssid = info.getSSID();

        String otherwifi = "The existing network is: \n\n";
        //4.4无法获取所有wifi值 只能获取当前wifi值
        results = wm.getScanResults();
        int i=0;
        for (ScanResult result : results) {
            otherwifi += result.SSID  + ":" + result.level + "\n";
            if (++i==10){
                break;
            }
        }

        String text = "We are connecting to " + ssid + " at " + String.valueOf(speed) + "   " + String.valueOf(units) + ". Strength : " + strength;
        otherwifi += "\n\n";
        otherwifi += text;

        tv.setText(otherwifi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer=null;
        timerTask=null;
    }
}
