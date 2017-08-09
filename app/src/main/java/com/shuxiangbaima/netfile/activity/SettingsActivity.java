package com.shuxiangbaima.netfile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_settings);
        GridLayoutManager manager=new GridLayoutManager(this,1);
        rv.setLayoutManager(manager);
        String[] array = getResources().getStringArray(R.array.sets);
        RVSetAdapter adapter=new RVSetAdapter(this,array);
        rv.setAdapter(adapter);
    }
}
