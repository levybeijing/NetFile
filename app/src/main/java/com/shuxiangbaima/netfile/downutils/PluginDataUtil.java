package com.shuxiangbaima.netfile.downutils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.shuxiangbaima.netfile.MyLog;
import com.shuxiangbaima.netfile.adapter.RVAdapter;
import com.shuxiangbaima.netfile.bean.PluginBean;
import com.shuxiangbaima.netfile.bean.PluginListBean;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DIY on 2017/6/12.
 */

public class PluginDataUtil {

    private static final String TAG="PluginDataUtil";

    public static void pluginDown(String url, final RecyclerView rv_plugin, final Context context){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.shuxiangbaima.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Subscriber subscribe = new Subscriber<PluginBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                MyLog.e(TAG,"插件下载失败--onError:"+e.getMessage());
            }

            @Override
            public void onNext(PluginBean pluginBean) {
                if (pluginBean.getStatus()!=200){
                    MyLog.e(TAG,pluginBean.getMsg());
                    return;
                }
                List<PluginListBean> plugin_list = pluginBean.getData().getPlugin_list();
                RVAdapter adapter=new RVAdapter(plugin_list,context);
                rv_plugin.setAdapter(adapter);
            }
        };
        IPluginData pluginData = retrofit.create(IPluginData.class);
        pluginData.getData(url)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscribe);
    }
    public interface IPluginData{
        @GET
        Observable<PluginBean> getData(@Url String fileUrl);
    }
}
