package com.shuxiangbaima.netfile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.shuxiangbaima.netfile.downutils.IVerification;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/25.
 */

public class VerificationService extends Service {
    private static final String TAG = "VerificationService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File parent = new File("/sdcard/dszh/ocr/");
        if (!parent.exists()) {
            parent.mkdirs();
        }
        //如果result文件存在则删除
        String resultPath = "/sdcard/dszh/ocr/result.txt";
        File file1 = new File(resultPath);
        Log.e(TAG, "============原result文件是否存在:"+file1.exists());
        if (file1.exists()) {
            file1.delete();
        }
        //假如图片文件不存在
        String imagePath = "/sdcard/dszh/ocr/code.png";
        File file2 = new File(imagePath);
        Log.e(TAG, "============图片文件是否存在:"+file2.exists());
        if (!file2.exists()) {
            File file = new File(resultPath);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                String s = "{\n" +
                        "\tstatus: 404\n" +
                        "\tmsg: \"文件不存在\"\n" +
                        "}\n";
                fos.write(s.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "============>>>>>>>>>");
            // 获得传入的参数
            String device = DeviceInfo.getIndex();
            String code_type = intent.getStringExtra("code_type");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://robot.shuxiangbaima.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            Subscriber subscribe = new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {
                    MyLog.e(TAG, "插件下载完毕");
                    Log.e(TAG, "============onCompleted:");
                }

                @Override
                public void onError(Throwable e) {
                    MyLog.e(TAG, "onError:" + e.getMessage());
                    Log.e(TAG, "============onError:"+e.getMessage());
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    Log.e(TAG, "============responseBody");
                    try {
                        FileOutputStream fos = new FileOutputStream(new File("/sdcard/dszh/ocr/result.txt"));
                        fos.write(responseBody.bytes());
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            IVerification iVerification = retrofit.create(IVerification.class);
            iVerification.uploadImgs(RequestBody.create(MediaType.parse("text/*"), device)
                    ,RequestBody.create(MediaType.parse("text/*"), code_type)
                    ,RequestBody.create(MediaType.parse("image/png"), file2))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscribe);
            return super.onStartCommand(intent, flags, startId);

        }
        return flags;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}