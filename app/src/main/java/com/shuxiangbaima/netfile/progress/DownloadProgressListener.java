package com.shuxiangbaima.netfile.progress;

/**
 * Created by DIY on 2017/6/20.
 */

interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
