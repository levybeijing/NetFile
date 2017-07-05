package com.shuxiangbaima.netfile.netspeed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.flowcount.TrafficInfo;

import java.util.List;

/**
 * Created by DIY on 2017/6/30.
 */

public class RVSpeedAdapter extends RecyclerView.Adapter<RVSpeedAdapter.MyViewHolder> {
    private Context context;
    private List<TrafficInfo> list;

    public RVSpeedAdapter(Context context_){
        this.context=context_;
//        list=;
    }

    public void setData(List<TrafficInfo> list_){
        list=list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_speed,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv,tv1,tv2;
        public MyViewHolder(View view)
        {
            super(view);
            tv= (TextView) view.findViewById(R.id.tv_speed_rv_item);
            tv1= (TextView) view.findViewById(R.id.tv1_speed_rv_item);
            tv2= (TextView) view.findViewById(R.id.tv2_speed_rv_item);
        }
    }
}
