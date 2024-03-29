package com.shuxiangbaima.netfile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.DeviceInfo;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.NetConnectUtil;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.activity.PluginDownActivity;
import com.shuxiangbaima.netfile.activity.VersionUpdate;
import com.shuxiangbaima.netfile.downutils.DeviceInfoUploadUtil;
import com.shuxiangbaima.netfile.downutils.WordsDownUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by DIY on 2017/6/22.
 */

public class RVSetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String[] array;

    public RVSetAdapter(Context context_) {
        this.context = context_;
        this.array = context_.getResources().getStringArray(R.array.sets);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0 || viewType == 1) {
            return new MyViewHolder0(LayoutInflater.from(context).inflate(R.layout.item0_rv_settings, parent, false));
        } else if (viewType == 2) {
            return new MyViewHolder1(LayoutInflater.from(context).inflate(R.layout.item1_rv_settings, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_settings, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();
        final String index = DeviceInfo.getIndex();
        if (position == 0) {
            boolean isCheck = preferences.getBoolean("logToggle", true);
            ((MyViewHolder0) holder).s.setText("日志开关");
            ((MyViewHolder0) holder).s.setChecked(isCheck);
            ((MyViewHolder0) holder).s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MyLog.setLogWritable(isChecked);
                    edit.putBoolean("logToggle", isChecked);
                    edit.commit();
                    if (isChecked) {
                        Toast.makeText(context, "日志开启", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "日志关闭", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (position == 1) {
            final boolean isEnable = preferences.getBoolean("deviceAutoUpdateToggle", true);
            ((MyViewHolder0) holder).s.setText("设备信息自动更新开关");
            ((MyViewHolder0) holder).s.setChecked(isEnable);
            ((MyViewHolder0) holder).s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MyLog.setLogWritable(isChecked);
                    edit.putBoolean("deviceAutoUpdateToggle", isChecked);
                    edit.commit();
                    if (isChecked) {
                        Toast.makeText(context, "自动更新开启", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "自动更新关闭", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (position == 2) {
            ((MyViewHolder1) holder).tv.setText("版本号:" + getVersion());
        } else {
            ((MyViewHolder) holder).tv.setText(array[position - 3]);
            ((MyViewHolder) holder).tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 3:
                            //检测更新
                            if (!NetConnectUtil.isAnyConn(context)) {
                                Toast.makeText(context, "无网络链接,请检查网络", Toast.LENGTH_SHORT).show();
                                MyLog.e(TAG, "无网络链接,版本更新失败");
                                return;
                            }
                            try {
                                VersionUpdate.updatecheck(context);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 4:
                            context.startActivity(new Intent(context, PluginDownActivity.class));
                            break;
                        case 5:
                            tmpFileClean();//垃圾清理
                            break;
                        case 6:
                            //设置编号
                            setDeviceIndex();
                            break;
                        case 7:
                            //信息初始化
                            if (!NetConnectUtil.isAnyConn(context)) {
                                Toast.makeText(context, "无网络链接,请检查网络", Toast.LENGTH_SHORT).show();
                                MyLog.e(TAG, "设备初始化无网络链接");
                                break;
                            }
                            if (index != null) {
                                DeviceInfoUploadUtil.deviceDown(Config.deviceInfoInit + DeviceInfo.getDeviceInfo(context), context);
                                edit.putLong("timeInitSubmit", System.currentTimeMillis());
                                edit.putBoolean("initSubmit", true);
                                edit.commit();
                                MyLog.e(TAG, "设备初始化时间为：" + new Date().toString());
                            } else {
                                Toast.makeText(context, "无设备编号,请设置", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 8:
                            //信息更新
                            if (!NetConnectUtil.isAnyConn(context)) {
                                Toast.makeText(context, "无网络链接,请检查网络", Toast.LENGTH_SHORT).show();
                                MyLog.e(TAG, "设备更新无网络链接");
                                break;
                            }
                            if (index != null) {
                                DeviceInfoUploadUtil.deviceDown(Config.deviceInfoUpdate + DeviceInfo.getDeviceInfo(context), context);
                                edit.putLong("timeLastSubmit", System.currentTimeMillis());
                                edit.commit();
                            } else {
                                Toast.makeText(context, "无设备编号,请设置", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 9:
                            //下载words字库
                            if (NetConnectUtil.isAnyConn(context)) {
                                WordsDownUtil.wordsDown(Config.wordsUrl, Config.wordsPath, context);
                            } else {
                                Toast.makeText(context, "无网络链接,请设置", Toast.LENGTH_SHORT).show();
                                MyLog.e(TAG, "无网络连接，字库下载失败");
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return array.length + 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_set_rv_item);
        }
    }

    class MyViewHolder0 extends RecyclerView.ViewHolder {
        Switch s;

        public MyViewHolder0(View view) {
            super(view);
            s = (Switch) view.findViewById(R.id.switch_setting);
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder1(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_setting);
        }
    }

    public void tmpFileClean() {
        File cleanDir = new File(Config.logsPath);
        boolean flag = MyLog.getLogWritable();
        if (cleanDir.exists()) {
            MyLog.setLogWritable(false);
            cleanFiles(cleanDir);
            Toast.makeText(context, "文件清理完毕", Toast.LENGTH_SHORT).show();
            MyLog.setLogWritable(flag);
        }
    }

    public void cleanFiles(File cleanDir) {
        File[] files = cleanDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                cleanFiles(files[i]);
            } else {
                files[i].delete();
            }
        }
        cleanDir.delete();
    }

    //获取版本信息
    public String getVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setDeviceIndex() {
        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context);
        final String old = DeviceInfo.getIndex();
        editText.setText(old);
        inputDialog.setTitle("请设置设备编号").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String trim = editText.getText().toString().trim();
                        //空指针异常 old
                        if (old != null && old.equals(trim)) {
                            return;
                        }
                        if (!trim.isEmpty()) {
                            String edit = "phone_index:" + trim;
                            MyLog.e("setDeviceIndex:", edit);
                            File file = new File(Config.phoneConfig);
                            try {
                                if (!file.exists()) {
                                    String dir = Config.phoneConfig.substring(0, Config.phoneConfig.lastIndexOf("/"));
                                    new File(dir).mkdirs();
                                    file = new File(Config.phoneConfig);
                                }
                                Log.e(TAG, "=====onClick: " + file.getAbsoluteFile());
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(edit.getBytes());
                                fos.close();
                                SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                                SharedPreferences.Editor e = preferences.edit();
                                e.putBoolean("initSubmit", false);
                                e.commit();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "输入无效,请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null);
        inputDialog.show();

    }
}
