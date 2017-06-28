package com.shuxiangbaima.netfile.bean;

/**
 * Created by DIY on 2017/6/12.
 */

public class PluginBean {

    /**
     * status : 200
     * msg : 成功。
     * data : {"plugin_num":1,"plugin_list":[{"plugin":"luaarray","desc":"基础插件","download_url":"http://f.shuxiangbaima.com/x/按键精灵插件/luaarray.lua"}]}
     */

    private int status;
    private String msg;
    private PluginDataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PluginDataBean getData() {
        return data;
    }

    public void setData(PluginDataBean data) {
        this.data = data;
    }
}
