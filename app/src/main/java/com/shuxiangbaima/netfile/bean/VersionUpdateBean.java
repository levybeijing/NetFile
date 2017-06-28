package com.shuxiangbaima.netfile.bean;

/**
 * Created by DIY on 2017/3/21.
 */

public class VersionUpdateBean {


    /**
     * status : 0
     * msg : 描述
     * data : {"package":"包名","version":"版本号","new_features":"更新内容","download_url":"下载链接"}
     */

    private String status;
    private String msg;
    private VersionDataBean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VersionDataBean getData() {
        return data;
    }

    public void setData(VersionDataBean data) {
        this.data = data;
    }
}
