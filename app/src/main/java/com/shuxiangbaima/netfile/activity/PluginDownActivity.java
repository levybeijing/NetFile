package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.shuxiangbaima.netfile.Config;
import com.shuxiangbaima.netfile.NetConnectUtil;
import com.shuxiangbaima.netfile.downutils.PluginDataUtil;
import com.shuxiangbaima.netfile.R;

/**
 * Created by DIY on 2017/6/16.
 */

public class PluginDownActivity extends Activity {

    private RecyclerView rv_plugin;
    private Toolbar bar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        bar = (Toolbar) findViewById(R.id.bar_plugin);
        bar.setTitle("插件下载列表");
        bar.setNavigationIcon(R.drawable.back_16);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_plugin = (RecyclerView) findViewById(R.id.rv_main_plugin);
        rv_plugin.setLayoutManager(manager);
        //插件数据展示
        if (NetConnectUtil.isAnyConn(this))
        PluginDataUtil.pluginDown(Config.pluginUrl,rv_plugin,this);
        else Toast.makeText(this,"请检查网络",Toast.LENGTH_SHORT).show();
    }
}
