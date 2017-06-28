package com.shuxiangbaima.netfile.bean;

/**
 * Created by DIY on 2017/6/12.
 */

public class PluginListBean {
    /**
     * plugin : luaarray
     * desc : 基础插件
     * download_url : http://f.shuxiangbaima.com/x/按键精灵插件/luaarray.lua
     */

    private String plugin;
    private String desc;
    private String download_url;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}
