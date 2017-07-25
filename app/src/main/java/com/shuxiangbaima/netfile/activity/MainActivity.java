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
            WordsDownUtil.wordsDown(Config.wordsUrl, Config.wordsPath);
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
                    case R.id.version_update:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"无网络链接,下载失败");
                            break;
                        }
                        updatecheck();
                        break;
                    case R.id.rubbish_clean:
                        tmpFileClean();
                        break;
                    case R.id.log_open:
                        MyLog.setLogWritable(true);
                        toolbar.setTitle("日志开启中...");
                        edit.putBoolean("logToggle",true);
                        edit.commit();
                        Toast.makeText(MainActivity.this, "日志开启", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.log_close:
                        MyLog.setLogWritable(false);
                        toolbar.setTitle("日志关闭中...");
                        edit.putBoolean("logToggle",false);
                        edit.commit();
                        Toast.makeText(MainActivity.this, "日志关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.plugin_down:
                        startActivity(new Intent(MainActivity.this,PluginDownActivity.class));
                        break;
                    case R.id.set_index:
                        setDeviceIndex();
                        break;
                    case R.id.device_init:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"设备初始化无网络链接");
                            break;
                        }
                        if (device !=null){
                            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoInit+DeviceInfo.getDeviceInfo(MainActivity.this),MainActivity.this);
                            Toast.makeText(MainActivity.this,"设备已经提交初始化",Toast.LENGTH_SHORT).show();
                            edit.putLong("timeInitSubmit",System.currentTimeMillis());
                            edit.commit();
                            MyLog.e(TAG,"设备初始化时间为："+new Date().toString());
                        }else{
                            Toast.makeText(MainActivity.this,"无设备编号,请设置",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.device_update:
                        if (!NetConnectUtil.isAnyConn(MainActivity.this)){
                            Toast.makeText(MainActivity.this,"无网络链接,请检查网络",Toast.LENGTH_SHORT).show();
                            MyLog.e(TAG,"设备更新无网络链接");
                            break;
                        }
                        if (device !=null){
                            DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate+DeviceInfo.getDeviceInfo(MainActivity.this),MainActivity.this);
                            Toast.makeText(MainActivity.this,"已经提交更新",Toast.LENGTH_SHORT).show();
                            edit.putLong("timeLastSubmit",System.currentTimeMillis());
                            edit.commit();
                        }else{
                            Toast.makeText(MainActivity.this,"无设备编号,请设置",Toast.LENGTH_SHORT).show();
                        }
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

    private void registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
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
