package com.example.myapplication;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * baolei.li Create by 2021/4/5
 */

public interface NetService {

    @POST("shop/api/v1/home")
    @FormUrlEncoded
    Observable<String> requestHome(@FieldMap Map<String,String> params);


    @GET("shop/api/v1/home")
    Observable<String> requestHome(@Query("aaa")String aa);


    @POST("user/login")
    @FormUrlEncoded
    Observable<String> requestLogin(@FieldMap Map<String,String> params);

}
