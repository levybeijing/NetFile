package com.shuxiangbaima.netfile.wifinearby;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DIY on 2017/6/29.
 */

public class WifiInfoActivity extends Activity {
    private Timer timer;
    private TimerTask timerTask;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                    WifiInfo info = wm.getConnectionInfo();
                    int strength = info.getRssi();
                    int speed = info.getLinkSpeed();
                    String units = WifiInfo.LINK_SPEED_UNITS;
                    String ssid = info.getSSID();

                    Log.e("", "=========handleMessage: "+wm);
                    List<ScanResult> results = wm.getScanResults();
                    String otherwifi = "The existing network is: \n\n";

                    for (ScanResult result : results) {
                        otherwifi += result.SSID + ":" + result.level + "\n";
                    }

                    String text = "We are connecting to " + ssid + " at " + String.valueOf(speed) + "   " + String.valueOf(units) + ". Strength : " + strength;
                    otherwifi += "\n\n";
                    otherwifi += text;

                    tv.setText(otherwifi);
                    break;
                default:
                    break;
            }
        }
    };
    private TextView tv;
    private WifiManager wm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_wifinearby);

        tv = (TextView) findViewById(R.id.tv_wifi);

        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wm.getConnectionInfo();
        int strength = info.getRssi();
        int speed = info.getLinkSpeed();
        String units = WifiInfo.LINK_SPEED_UNITS;
        String ssid = info.getSSID();

        String otherwifi = "The existing network is: \n\n";
        List<ScanResult> results = wm.getScanResults();

        for (ScanResult result : results) {
            otherwifi += result.SSID + ":" + result.level + "\n";
        }


        String text = "We are connecting to " + ssid + " at " + String.valueOf(speed) + "   " + String.valueOf(units) + ". Strength : " + strength;
        otherwifi += "\n\n";
        otherwifi += text;

        tv.setText(otherwifi);
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                Message msg = new Message();
//                msg.what = 0;
//                mHandler.sendMessage(msg);
//            }
//        };
//        timer = new Timer();
//        timer.schedule(timerTask, 0, 2000);
    }
}
