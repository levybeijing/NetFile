package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.adapter.RVSetAdapter;

/**
 * Created by DIY on 2017/6/21.
 */

public class SettingsActivity extends Activity {
    private Toolbar bar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bar = (Toolbar) findViewById(R.id.bar_settings);
        bar.setTitle("设置");
        bar.setNavigationIcon(R.drawable.back_16);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences preferences=getSharedPreferences("logToggle",MODE_PRIVATE);
        boolean isCheck = preferences.getBoolean("isCheck", true);
        Switch s= (Switch) findViewById(R.id.switch1);
        s.setChecked(isCheck);
        final SharedPreferences.Editor edit = preferences.edit();
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("isCheck", true);
                edit.commit();
            }
        });

        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            ((TextView)findViewById(R.id.tv_set)).setText("当前版本为:"+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_settings);
        GridLayoutManager manager=new GridLayoutManager(this,1);
        rv.setLayoutManager(manager);
        String[] array = getResources().getStringArray(R.array.sets);
        RVSetAdapter adapter=new RVSetAdapter(this,array);
        rv.setAdapter(adapter);
    }
}
