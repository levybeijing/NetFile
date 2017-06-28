package com.shuxiangbaima.netfile.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIY on 2017/6/14.
 */

public class VersionDataBean {
    /**
     * package : 包名
     * version : 版本号
     * new_features : 更新内容
     * download_url : 下载链接
     */

    @SerializedName("package")
    private String packageX;
    private String version;
    private String new_features;
    private String download_url;

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNew_features() {
        return new_features;
    }

    public void setNew_features(String new_features) {
        this.new_features = new_features;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}