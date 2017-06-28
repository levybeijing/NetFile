package com.shuxiangbaima.netfile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shuxiangbaima.netfile.downutils.ConcretePluginUtil;
import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.bean.PluginListBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIY on 2017/6/12.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
    private List<PluginListBean> list=new ArrayList<>();
    private Context context;
    private String plugin_path="/sdcard/MobileAnJian/Plugin";

    public RVAdapter(List<PluginListBean> list_, Context context_){
        this.list=list_;
        this.context=context_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_main,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String name = list.get(position).getPlugin();
        MyLog.e("插件名称:",name);
        holder.tv.setText(name);
        holder.ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载插件
                if (new File(plugin_path).exists()){
                    ConcretePluginUtil.pluginDown(list.get(position).getDownload_url(),context);
                }else{
                    Toast.makeText(context,"请启动按键精灵",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv;
        Button ib;
        public MyViewHolder(View view)
        {
            super(view);
            tv= (TextView) view.findViewById(R.id.recyclerview_item_tv);
            ib= (Button) view.findViewById(R.id.recyclerview_item_btn);
        }
    }

}
