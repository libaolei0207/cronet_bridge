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

    @GET("getJoke/")
    Observable<String> requestJoke(@Query("page") String page, @Query("count") String count, @Query("type") String type);


}
