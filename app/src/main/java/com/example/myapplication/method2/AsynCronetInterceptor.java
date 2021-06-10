package com.example.myapplication.method2;

import com.example.myapplication.cronet.CronetHelper;
import com.example.myapplication.cronet.CronetUrlRequestCallback;

import org.chromium.net.UrlRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * * Created by baolei.li on 2021/6/7 4:27 下午.
 */

public class AsynCronetInterceptor implements okhttp3.Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (CronetHelper.getInstance().isInit()) {
            Response response = null;
            try {
                response = proceedWithCronet(chain.request(), chain.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        } else {
            return chain.proceed(chain.request());
        }
    }

    private Response proceedWithCronet(Request request, Call call) throws IOException {
        CronetUrlRequestCallback callback = new CronetUrlRequestCallback(request, call);
        UrlRequest urlRequest = CronetHelper.getInstance().buildRequest(request, callback);
        urlRequest.start();
        return callback.waitForDone();
    }


}
