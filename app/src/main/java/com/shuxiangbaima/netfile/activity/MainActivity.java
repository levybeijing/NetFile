package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.DeviceInfo;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.NetConnectUtil;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.downutils.DeviceInfoUploadUtil;
import com.shuxiangbaima.netfile.downutils.WordsDownUtil;
import com.shuxiangbaima.netfile.progress.Download;
import com.shuxiangbaima.netfile.progress.StringUtils;

public class MainActivity extends Activity {
    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final String TAG="MainActivity";
    private String device;

    private LocalBroadcastManager bManager;
    private TextView tv_phone_index;
    private Toolbar toolbar;
    private ProgressBar progress;
    private TextView progress_text;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
                Download download = intent.getParcelableExtra("download");
                progress.setProgress(download.getProgress());
                if (download.getProgress() == 100) {
                    progress_text.setText("File Download Complete");
                } else {
                    progress_text.setText(
                            StringUtils.getDataSize(download.getCurrentFileSize())
                                    + "/" +
                                    StringUtils.getDataSize(download.getTotalFileSize()));
                }
            }
        }
    };
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyLog.e(TAG,"***onCreate***");
        //初始化控件
        initView();
        //下载words字库
        if (NetConnectUtil.isAnyConn(this)) {
            WordsDownUtil.wordsDown(Config.wordsUrl, Config.wordsPath,this);
        } else{
            Toast.makeText(MainActivity.this,"无网络链接,请设置",Toast.LENGTH_SHORT).show();
            MyLog.e(TAG,"无网络连接，字库下载失败");
        }
        //动态注册广播
        registerReceiver();
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

    @Override
    protected void onResume() {
        super.onResume();
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
        //判断是否有设备编号 没有的话 直接跳过后面的代码
        String index = DeviceInfo.getIndex();
        if (index==null){
            MyLog.e("onResume","设备编号为空");
            tv_phone_index.setText("暂无设备编号");
            return;
        }else{
            tv_phone_index.setText("设备编号:"+ index);
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
        if (curTime-oldTime>600000){
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
        progress = (ProgressBar) findViewById(R.id.progress);
        progress_text = (TextView) findViewById(R.id.progress_text);
        tv_phone_index = (TextView) findViewById(R.id.tv_phone_index);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("日志开启中...");
        toolbar.inflateMenu(R.menu.menu_toolbar);
        //device	设备编号，唯一标识
        device = DeviceInfo.getIndex();
        MyLog.e("DeviceInfo","phone_config文件内容："+device);
        tv_phone_index.setText(device==null?"暂无设备编号":"设备编号:"+ device);
    }

    private void registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
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
        bManager.unregisterReceiver(broadcastReceiver);
        preferences=null;
        edit=null;
    }
}