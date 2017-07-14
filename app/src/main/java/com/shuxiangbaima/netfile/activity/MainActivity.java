package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.DeviceInfo;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.NetConnectUtil;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.bean.VersionUpdateBean;
import com.shuxiangbaima.netfile.downutils.DeviceInfoUploadUtil;
import com.shuxiangbaima.netfile.downutils.WordsDownUtil;
import com.shuxiangbaima.netfile.progress.Download;
import com.shuxiangbaima.netfile.progress.DownloadService;
import com.shuxiangbaima.netfile.progress.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyLog.e(TAG,"***onCreate***");
        //初始化控件
        initView();

        //下载words字库
        if (NetConnectUtil.isAnyConn(this)) {
            WordsDownUtil.wordsDown(Config.wordsUrl, Config.wordsPath);
        } else{
            Toast.makeText(MainActivity.this,"无网络链接,请设置",Toast.LENGTH_SHORT).show();
            MyLog.e(TAG,"无网络连接，字库下载失败");
        }
        //动态注册广播
        registerReceiver();
        //记录wifi值  每次启动应用检测
        wificheck();

        //判断时间是否超过24hours
        SharedPreferences preferences = getSharedPreferences("time", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();
        final long curTime =System.currentTimeMillis();
        long oldTime = preferences.getLong("time", 0);
        MyLog.e(TAG,"与记录的时间间隔为:"+(curTime-oldTime)/3600000+"小时");
        //大于24小时  则更新
        if (curTime-oldTime>86400000&&oldTime!=0){
            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this));
            edit.putLong("time",curTime);
            edit.commit();
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                        break;
                    case R.id.cloud_update:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"无网络链接,下载失败");
                            break;
                        }
                        updatecheck();
                        break;
                    case R.id.folder_delete:
                        tmpFileClean();
                        break;
                    case R.id.log_open:
                        MyLog.setLogWritable(true);
                        toolbar.setTitle("日志开启中...");
                        Toast.makeText(MainActivity.this, "日志开启", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.log_close:
                        MyLog.setLogWritable(false);
                        toolbar.setTitle("日志关闭中...");
                        Toast.makeText(MainActivity.this, "日志关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.plugin_down:
                        startActivity(new Intent(MainActivity.this,PluginDownActivity.class));
                        break;
                    case R.id.setIndex:
                        setDeviceIndex();
                        break;
                    case R.id.deviceInit:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"设备初始化无网络链接");
                            break;
                        }
                        if (device !=null){
                            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoInit+DeviceInfo.getDeviceInfo(MainActivity.this));
                            Toast.makeText(MainActivity.this,"已经提交初始化",Toast.LENGTH_SHORT).show();
                            edit.putLong("time",curTime);
                            edit.commit();
                            MyLog.e(TAG,"初始化时间为："+new Date().toString());
                        }else{
                            Toast.makeText(MainActivity.this,"无设备编号,请设置",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.deviceUpdate:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"设备更新无网络链接");
                            break;
                        }
                        if (device !=null){
                            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this));
                            Toast.makeText(MainActivity.this,"已经提交更新",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"无设备编号,请设置",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void wificheck() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String ssid = info.getSSID().replace("\"", "");
        //先比较  然后记录
        SharedPreferences sf = getSharedPreferences("wifi", Context.MODE_PRIVATE);
        String idOld = sf.getString("wifiId", null);
        if (!ssid.equals(idOld)&&ssid!=null&&ssid.length()!=0){
            SharedPreferences.Editor edit1 = sf.edit();
            edit1.putString("wifiId",ssid);
            edit1.commit();
            MyLog.e(TAG,"wifi前值为:"+idOld);
            MyLog.e(TAG,"wifi新值为:"+ssid);
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
        tv_phone_index.setText(device==null?"暂无设备编号":"设备编号:"+ device);
    }

    private void setDeviceIndex() {
        final EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(MainActivity.this);
        editText.setText(DeviceInfo.getIndex());
        inputDialog.setTitle("请设置设备编号").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().trim().isEmpty()){
                            String edit="phone_index:"+editText.getText().toString().trim();
                            MyLog.e("setDeviceIndex:",edit);
                            File file=new File(Config.phoneConfig);
                                try {
                                    if(!file.exists()){
                                        String dir=Config.phoneConfig.substring(0,Config.phoneConfig.lastIndexOf("/"));
                                        new File(dir).mkdirs();
                                        file=new File(Config.phoneConfig);
                                    }
                                    FileOutputStream fos=new FileOutputStream(file);
                                    fos.write(edit.getBytes());
                                    fos.close();
                                    tv_phone_index.setText("设备编号:"+editText.getText().toString().trim());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                            Toast.makeText(MainActivity.this, "输入无效,请重新输入", Toast.LENGTH_SHORT).show();
                            }
                    }
                })
        .setNegativeButton("取消",null);
        inputDialog.show();
    }

    public String getVersion() {
        try {
                PackageManager manager = this.getPackageManager();
                PackageInfo info = manager.getPackageInfo(this.getPackageName(),0);
                String version = info.versionName;
                return version;
             } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
    }

    public void updatecheck() {

        final Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<VersionUpdateBean>() {
            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(final VersionUpdateBean dataBean) {
                if (dataBean==null){
                    MyLog.e(TAG,"dataBean为空!任务结束");
                    return;
                }
                String status = dataBean.getStatus();
                String msg = dataBean.getMsg();
                MyLog.e(TAG,"状态码status:"+status+msg);
                if (status.equals("200")){
                    String download_url = dataBean.getData().getDownload_url();
                    MyLog.e(TAG,"download_url:"+download_url);
                    MyLog.e(TAG,"showDialog");
                    showDialog(download_url);
                }else if (status.equals("204")){
                    Toast.makeText(MainActivity.this,"当前内容已经是最新的了",Toast.LENGTH_SHORT).show();
                }else if (status.equals("400")){
                    Toast.makeText(MainActivity.this,"缺少参数",Toast.LENGTH_SHORT).show();
                }
            }

            public void showDialog(final String download_url) {
                new AlertDialog.Builder(MainActivity.this).setTitle("软件升级")
                        .setMessage("现在更新么！")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyLog.e(TAG,"软件开始更新");
                                progress.setVisibility(View.VISIBLE);
                                Intent intent=new Intent(MainActivity.this,DownloadService.class);
                                intent.putExtra("url",download_url);
                                startService(intent);
                            }
                        }).setNegativeButton("返回",null).show();
            }
            @Override
            public void onCompleted() {
                MyLog.e(TAG,"***onCompleted***");
            }
        };
        UpdateApp updateApp = retrofit.create(UpdateApp.class);
        updateApp.checkUpdate(getPackageName(),getVersion())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe);
    }

    public interface UpdateApp {
        @POST("version/check")
        @FormUrlEncoded
        Observable<VersionUpdateBean> checkUpdate(@Field("package") String pkg,
                                                  @Field("version") String ver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e(TAG,"***onStop***");
    }
    private void registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.e(TAG,"***onDestroy***");
        bManager.unregisterReceiver(broadcastReceiver);
    }

    public void tmpFileClean() {
        File cleanDir=new File(Config.logsPath);
        boolean flag=MyLog.getLogWritable();
        if (cleanDir.exists()){
            MyLog.setLogWritable(false);
            cleanFiles(cleanDir);
            Toast.makeText(this, "文件清理完毕", Toast.LENGTH_SHORT).show();
            MyLog.setLogWritable(flag);
        }
    }

    public void cleanFiles(File cleanDir) {
        File[] files = cleanDir.listFiles();
        for (int i=0;i<files.length;++i){
            if (files[i].isDirectory()) {
                cleanFiles(files[i]);
            }else{
                files[i].delete();
            }
        }
        cleanDir.delete();
    }
}
