package com.shuxiangbaima.netfile.flow;

import java.text.DecimalFormat;

/**
 * Created by DIY on 2017/6/27.
 */

public class TextFormat {
    /**
     * 格式化数据
     * @param data
     * @return
     */
    public static String formatByte(long data){
        DecimalFormat format = new DecimalFormat("##.##");
        if(data < 1024){
            return data+"bytes";
        }else if(data < 1024 * 1024){
            return format.format(data/1024f) +"KB";
        }else if(data < 1024 * 1024 * 1024){
            return format.format(data/1024f/1024f) +"MB";
        }else if(data < 1024 * 1024 * 1024 * 1024){
            return format.format(data/1024f/1024f/1024f) +"GB";
        }else{
            return "超出统计范围";
        }
    }
}
