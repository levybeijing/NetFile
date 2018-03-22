package com.shuxiangbaima.netfile.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.DeviceInfo;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.NetConnectUtil;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.downutils.DeviceInfoUploadUtil;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private String device;
    private TextView tv_phone_index;
    private Toolbar toolbar;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取权限 没有的话 直接退出应用
        getPermission();

        MyLog.e(TAG,"***onCreate***");
        //初始化控件
        initView();
        //制定一个文件 存储所有配置信息
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        edit = preferences.edit();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                        break;
                }
                return false;
            }
        });
    }

    //动态获取读写权限
    private void getPermission(){
        //判断当前系统是否高于或等于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10001);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE){
            for (int grant : grantResults)
                if (grant != PackageManager.PERMISSION_GRANTED){
                    finish(); //System.exit(0);
                }
        }
        if (requestCode == 10001){
            for (int grant : grantResults)
                if (grant != PackageManager.PERMISSION_GRANTED){
                    finish(); //System.exit(0);
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是否有设备编号 没有的话 直接跳过后面的代码
        String index = DeviceInfo.getIndex();
        if (index==null){
            MyLog.e("onResume","设备编号为空");
            tv_phone_index.setText("暂无设备编号");
            return;
        }else{
            tv_phone_index.setText("设备编号:"+ index);
        }
        //检测日至开关
        boolean isCheck = preferences.getBoolean("logToggle", true);
        toolbar.setTitle(isCheck?"日志开启中...":"日志关闭中...");
        //设置设备更新的开关
        boolean isEnable=preferences.getBoolean("deviceAutoUpdateToggle", true);
        boolean aBoolean = preferences.getBoolean("initSubmit", false);
        if (!isEnable&&!aBoolean){
            return;
        }
        //监测网络是否可用
        if (!NetConnectUtil.isAnyConn(this)){
            MyLog.e("onResume","无网络");
            return;
        }
        //处理逻辑  上次提交是否成功
        boolean lastSubmit = preferences.getBoolean("successLastSubmit", true);
        if (!lastSubmit){
            //不成功就上传设备信息
            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this),MainActivity.this);
            return;
        }
        // 然后上次提交时间是否超过十分钟 大于10分钟  则更新
        final long curTime =System.currentTimeMillis();
        long oldTime = preferences.getLong("timeLastSubmit", 0);
        MyLog.e(TAG,"与记录的时间间隔为:"+(curTime-oldTime)/1000+"秒");
        if (curTime-oldTime>6000){
            MyLog.e("===========",DeviceInfo.getDeviceInfo(MainActivity.this));
            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this),MainActivity.this);
            edit.putLong("timeLastSubmit",curTime);
            edit.commit();
            return;
        }
        // 最后是设备信息是否发生改变(除devide外)
        StringBuilder deviceInfoNew = DeviceInfo.getInfoNoIndex(this);
        String deviceInfo = preferences.getString("deviceInfo", null);
        if (deviceInfo!=null&&!deviceInfo.equals(deviceInfoNew.toString())){
            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this),MainActivity.this);
            edit.putString("deviceInfo",deviceInfoNew.toString());
            edit.commit();
            MyLog.e(TAG,"新的设备信息:"+deviceInfoNew);
        }
    }

    private void initView() {
        tv_phone_index = (TextView) findViewById(R.id.tv_phone_index);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("日志开启中...");
        toolbar.inflateMenu(R.menu.menu_toolbar);
        //device	设备编号，唯一标识
        device = DeviceInfo.getIndex();
        MyLog.e("DeviceInfo","phone_config文件内容："+device);
        tv_phone_index.setText(device==null?"暂无设备编号":"设备编号:"+ device);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e(TAG,"***onStop***");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.e(TAG,"***onDestroy***");
        preferences=null;
        edit=null;
    }
}