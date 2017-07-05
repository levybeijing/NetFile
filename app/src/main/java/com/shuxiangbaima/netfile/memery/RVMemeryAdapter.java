package com.shuxiangbaima.netfile.memery;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuxiangbaima.netfile.R;

import java.util.List;

/**
 * Created by DIY on 2017/6/28.
 */

public class RVMemeryAdapter extends RecyclerView.Adapter<RVMemeryAdapter.MyViewHolder> {

    private Context context;
    private List<ActivityManager.RunningAppProcessInfo> list;

    public RVMemeryAdapter(Context context_){
        this.context=context_;
        list=((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
    }

    public void setData(List<ActivityManager.RunningAppProcessInfo> list_){
        list=list_;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_memery,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        int pid =list.get(position).pid;
        String processName =list.get(position).processName;
        int[] pids = new int[] {pid};
        Debug.MemoryInfo[] memoryInfo =((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getProcessMemoryInfo(pids);
        int memorySize = memoryInfo[0].dalvikPrivateDirty;
        holder.tv1.setText(processName);
        holder.tv2.setText(memorySize+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv1,tv2;
        public MyViewHolder(View view)
        {
            super(view);
            tv1= (TextView) view.findViewById(R.id.tv1_memery_rv_item);
            tv2= (TextView) view.findViewById(R.id.tv_memery_rv_item);
        }
    }
}
