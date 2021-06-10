package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.cronet.CronetHelper
import com.example.myapplication.method1.CronetOkHttpCallFactory
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * baolei.li Create by 2021/4/5
 */

const val BASE_URL = "https://api.apiopen.top/"


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isGrantExternalRW(this)
        CronetHelper.getInstance().initCronetNetWorking(this)
        btn_request_1.setOnClickListener {
            loadData1()
        }

        btn_request_2.setOnClickListener {
            loadData2()
        }
     }

    private fun loadData1() {
           tv_result.text = ""
        val retrofitOkHttpClient =
            Retrofit.Builder().baseUrl(BASE_URL).client(Provider.CronetClientProvider.get())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        retrofitOkHttpClient.create(NetService::class.java)
             .requestJoke("1","2","video")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    tv_result.text = e.message
                }

                override fun onComplete() {

                }

                override fun onNext(t: String) {
                    tv_result.text = t
                }
            })

     }


    private fun loadData2() {
        tv_result.text = ""
        val retrofitOkHttpClient =
            Retrofit.Builder().baseUrl(BASE_URL)
                .callFactory(CronetOkHttpCallFactory(Provider.OkHttpClientProvider.get()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        retrofitOkHttpClient.create(NetService::class.java)
            .requestJoke("1","2","video")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    tv_result.text = e.message
                }

                override fun onComplete() {

                }

                override fun onNext(t: String) {
                    tv_result.text = t
                }
            })
    }


    private fun isGrantExternalRW(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
            return false
        }
        return true
    }
}
