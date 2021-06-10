package com.example.myapplication

import com.example.myapplication.method2.AsynCronetInterceptor
import com.example.myapplication.method2.CronetInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * baolei.li Create by 2021/4/5
 */

class Provider {

    class OkHttpClientProvider private constructor() {
        companion object {
            private var instance: OkHttpClient? = null
                get() {
                    if (field == null) {
                        field = OkHttpClient.Builder().cache(null)
                            .connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .eventListenerFactory(OkHttpEventListener.FACTORY)
                            .build()
                    }
                    return field
                }

            @Synchronized
            fun get(): OkHttpClient {
                return instance!!
            }
        }
    }

    class CronetClientProvider private constructor() {
        companion object {
            private var instance: OkHttpClient? = null
                get() {
                    if (field == null) {
                        field =
                            OkHttpClient.Builder().addInterceptor(AsynCronetInterceptor()).build()
                        //or
//                        OkHttpClient.Builder().addInterceptor(CronetInterceptor()).build()
                    }
                    return field
                }

            @Synchronized
            fun get(): OkHttpClient {
                return instance!!
            }
        }
    }

}