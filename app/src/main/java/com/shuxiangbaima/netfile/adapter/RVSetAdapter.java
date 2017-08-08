package com.shuxiangbaima.netfile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;

/**
 * Created by DIY on 2017/6/22.
 */

public class RVSetAdapter extends RecyclerView.Adapter<RVSetAdapter.MyViewHolder> {
    private Context context;
    private String[] array;
    private final int A=0;
    private final int B=1;
    private final int C=2;
    private final int D=3;

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
        if (position==0){

        }
        if (position==1){

        }
        holder.tv.setText(array[position]);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case A:
//垃圾清理
                        break;
                    case B:
//设置编号
                        break;
                    case C:
//信息初始化
                        break;
                    case D:
//信息更新
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
