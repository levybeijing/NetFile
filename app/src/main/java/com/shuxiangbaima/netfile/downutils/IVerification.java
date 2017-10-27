package com.shuxiangbaima.netfile.downutils;



import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface IVerification {
    @POST("/gr/ocr/decode")
    @Multipart
    Observable<ResponseBody> uploadImgs(
            @Part("device") RequestBody device
            , @Part("code_type") RequestBody code_type
            , @Part("file\"; filename=\"code.png\"") RequestBody file);
}
