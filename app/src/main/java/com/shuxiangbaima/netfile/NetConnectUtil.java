package com.shuxiangbaima.netfile;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by DIY on 2017/6/20.
 */

public class NetConnectUtil {

    public static boolean isWIfiConn(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static boolean isNetConn(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    public static boolean isAnyConn(Context context) {
        return isNetConn(context) || isWIfiConn(context);
    }
}
