package com.shuxiangbaima.netfile.netspeed;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shuxiangbaima.netfile.R;

/**
 * Created by DIY on 2017/6/30.
 */

public class SpeedActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_speed);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        rv.setLayoutManager(manager);

//        rv.setAdapter();

    }
}
