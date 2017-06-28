package com.shuxiangbaima.netfile.bean;

import java.util.List;

/**
 * Created by DIY on 2017/6/12.
 */

public class PluginDataBean {
    /**
     * plugin_num : 1
     * plugin_list : [{"plugin":"luaarray","desc":"基础插件","download_url":"http://f.shuxiangbaima.com/x/按键精灵插件/luaarray.lua"}]
     */

    private int plugin_num;
    private List<PluginListBean> plugin_list;

    public int getPlugin_num() {
        return plugin_num;
    }

    public void setPlugin_num(int plugin_num) {
        this.plugin_num = plugin_num;
    }

    public List<PluginListBean> getPlugin_list() {
        return plugin_list;
    }

    public void setPlugin_list(List<PluginListBean> plugin_list) {
        this.plugin_list = plugin_list;
    }
}
