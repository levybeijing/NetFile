package com.shuxiangbaima.netfile;

import android.content.Context;

/**
 * Created by Administrator on 2018/3/28.
 */

interface ISharePF {

    String getString(Context context,String name, String key);

    void setString(Context context,String name,String key,Boolean value);

    Boolean getBoolean(Context context,String name, String key);

    void setBoolean(Context context,String name,String key,Boolean value);

    Long getLong(Context context,String name, String key);

    void setLong(Context context,String name,String key,Long value);
}
