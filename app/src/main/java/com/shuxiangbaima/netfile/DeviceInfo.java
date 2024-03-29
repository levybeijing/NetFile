package com.shuxiangbaima.netfile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by DIY on 2017/6/13.
 */

public class DeviceInfo {

    public static StringBuilder getDeviceInfo(Context context) {

        String device = getIndex();//设备编号

        StringBuilder sb = getInfoNoIndex(context);
        sb.append("&");
        sb.append("device=").append(device);
        return sb;
    }

    public static StringBuilder getInfoNoIndex(Context context) {
        //动态获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
            }
        }
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        //获取手机信息
        String brand = Build.BRAND;//brand	品牌

        String model = Build.MODEL;//model	机型

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();//mac		MAC地址

        String ipText = intToIp(info.getIpAddress());//wifi		内网IP

        String ssid = info.getSSID();//router	wifi名
        ssid = ssid.replace("\"", "");

        StringBuilder sb = new StringBuilder();
        sb.append("?");
        sb.append("brand=").append(brand);
        sb.append("&");
        sb.append("model=").append(model);
        sb.append("&");
        sb.append("mac=").append(mac);
        sb.append("&");
        sb.append("wifi=").append(ipText);
        sb.append("&");
        sb.append("router=").append(ssid);
        sb.append("&");
        sb.append("imei=").append(imei);
        return sb;
    }

    public static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    public static String getIndex() {
        File index = new File(Config.phoneConfig);
        if (index.exists()) {
            try {
                String phone_index = new BufferedReader(new FileReader(index)).readLine();
                return phone_index.substring(phone_index.indexOf(":") + 1, phone_index.length());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
