package com.shuxiangbaima.netfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DIY on 2017/3/27.
 */

public class MyLog {

    private static Boolean MYLOG_WRITE_TO_FILE = true;// 日志写入文件开关

    private static String MYLOG_PATH_SDCARD_DIR = "/sdcard/sxbm/logs/";// 日志文件在sdcard中的路径

    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数

    private static String MYLOGFILEName = "Log.txt";//

    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式

    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    private static void log(String tag, String msg, char level) {
        if (MYLOG_WRITE_TO_FILE)
            writeLogtoFile(String.valueOf(level), tag, msg);
    }

    private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFile = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
                + "    " + tag + "    " + text;
        int month = nowtime.getMonth() + 1;
        int year = nowtime.getYear() + 1900;
        String path = MYLOG_PATH_SDCARD_DIR + year + File.separator + month;
        //创建相应目录
        File file = new File(path, needWriteFile
                + MYLOGFILEName);
        if (!file.exists()) {
            new File(path).mkdirs();
            file = new File(path, needWriteFile
                    + MYLOGFILEName);
        }
        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delFile() {// 删除日志文件
        String needDelFiel = logfile.format(getDateBefore());
        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }

    public static void setLogWritable(boolean log) {
        MYLOG_WRITE_TO_FILE = log;
    }

    public static boolean getLogWritable() {
        return MYLOG_WRITE_TO_FILE;
    }
}
