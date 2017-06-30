package com.shuxiangbaima.netfile.flowcount;

import android.content.Context;
import android.net.TrafficStats;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;

import java.util.List;

/**
 * Created by DIY on 2017/6/22.
 */

public class RVFlowAdapter extends RecyclerView.Adapter<RVFlowAdapter.MyViewHolder> {
    private Context context;
    private List<TrafficInfo> list;

    public RVFlowAdapter(Context context_){
        this.context=context_;
        list=new TrafficManager(context_).getInternetTrafficInfos();
    }
    public void setData(List<TrafficInfo> list_){
        list=list_;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_flow,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        TrafficInfo info = list.get(position);
        holder.iv.setImageDrawable(info.getIcon());
        String name = info.getAppname();
        if(name.length() > 8){
            name = name.substring(0, 7)+"...";
        }
        holder.tv_appname.setText(name);
        int uid = info.getUid();
        long tx = TrafficStats.getUidTxBytes(uid);
        if(tx < 0){
            tx = 0;
        }
        long rx = TrafficStats.getUidRxBytes(uid);
        if(rx < 0){
            rx = 0;
        }
        holder.tv_up.setText("上传:"+TextFormat.formatByte(tx));
        holder.tv_down.setText("下载:"+TextFormat.formatByte(rx));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv;
        TextView tv_up,tv_down,tv_appname;
        public MyViewHolder(View view)
        {
            super(view);
            tv_up= (TextView) view.findViewById(R.id.tv1_flow_rv_item);
            tv_down= (TextView) view.findViewById(R.id.tv2_flow_rv_item);
            tv_appname= (TextView) view.findViewById(R.id.tv_flow_rv_item);
            iv= (ImageView) view.findViewById(R.id.iv_flow_rv_item);
        }
    }
}
