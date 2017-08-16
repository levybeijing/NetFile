package com.shuxiangbaima.netfile.progress;

/**
 * Created by DIY on 2017/6/20.
 */

interface DPListener {
    void update(long bytesRead, long contentLength, boolean done);
}
