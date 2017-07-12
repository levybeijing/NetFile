package com.shuxiangbaima.netfile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;
import com.shuxiangbaima.netfile.flowcount.FlowActivity;
import com.shuxiangbaima.netfile.memery.MemeryActivity;
import com.shuxiangbaima.netfile.wifinearby.WifiInfoActivity;

/**
 * Created by DIY on 2017/6/22.
 */

public class RVSetAdapter extends RecyclerView.Adapter<RVSetAdapter.MyViewHolder> {
    private Context context;
    private String[] array;
    private final int RUBBISH_CLEAN=0;
    private final int FLOW_COUNT=1;
    private final int MEMERY_USED=2;
    private final int WIFI_NEARBY=3;
    public RVSetAdapter(Context context_,String[] array_){
        this.context=context_;
        this.array=array_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_settings,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv.setText(array[position]);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case RUBBISH_CLEAN:

                        break;
                    case FLOW_COUNT:
                        context.startActivity(new Intent(context, FlowActivity.class));
                        break;
//                    case INTERNET_SPEED:
//                        context.startActivity(new Intent(context, SpeedActivity.class));
//                        break;
                    case MEMERY_USED:
                        context.startActivity(new Intent(context, MemeryActivity.class));
                        break;
                    case WIFI_NEARBY:
                        context.startActivity(new Intent(context, WifiInfoActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv;
        public MyViewHolder(View view)
        {
            super(view);
            tv= (TextView) view.findViewById(R.id.tv_set_rv_item);
        }
    }

}
