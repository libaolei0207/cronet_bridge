### cronet_bridge：Connect cronet to your project at low cost


 If you're trying to access the Chromium Network Stack, Cronet is a good choice right now. But how to access Cronet is a problem. If you use Retrofit or Okhttp in your project, Congratulations, this library is just right for you.
This library provides two ways
* 1. If you use Retrofit, you can use OkHttpCall to do it, like this:
```java
val retrofitOkHttpClient =
      Retrofit.Builder().baseUrl(BASE_ URL)             
                     .callFactory(CronetOkHttpCallFactory(Provider.OkHttpClientProvider.get()))
                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                     .addConverterFactory(ScalarsConverterFactory.create())
                     .build()
```
* 2. If you use okhttp directly, you can also do it based on interceptor, but there are two ways, synchronous and asynchronous. like this:
```java
class CronetClientProvider private constructor() {
companion object {
private var instance: OkHttpClient? =  null
                  get() {
                        if (field == null) {
                               field =OkHttpClient.Builder().addInterceptor(AsynCronetInterceptor()).build()
                              //or                  
                              // OkHttpClient.Builder().addInterceptor(CronetInterceptor()).build()
                       }
                         return field
                      }
       @Synchronized
       fun get(): OkHttpClient {
         return instance!!
        }
     }
}
```

 If you have any questions, please contact me. Email :libaolei0207@sina.com
