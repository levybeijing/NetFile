package com.shuxiangbaima.netfile;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/3/28.
 */

public class SharePF implements ISharePF{
    /*
    * String 注意默认值设置
    * */

    @Override
    public String getString(Context context, String name, String key) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String value = preferences.getString(key, null);
        return value;
    }

    @Override
    public void setString(Context context, String name, String key, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }
    /*
    * Boolean 注意默认值设置
    * */
    @Override
    public Boolean getBoolean(Context context, String name, String key) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Boolean value = preferences.getBoolean(key,false);
        return value;
    }

    @Override
    public void setBoolean(Context context, String name, String key, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }
    /*
     * Long 注意默认值设置
     * */
    @Override
    public Long getLong(Context context, String name, String key) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Long value = preferences.getLong(key,0);
        return value;
    }

    @Override
    public void setLong(Context context, String name, String key, Long value) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

}
