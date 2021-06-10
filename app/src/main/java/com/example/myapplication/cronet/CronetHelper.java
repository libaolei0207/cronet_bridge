package com.example.myapplication.cronet;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;


import org.chromium.net.CronetEngine;
import org.chromium.net.ExperimentalCronetEngine;
import org.chromium.net.NetworkQualityRttListener;
import org.chromium.net.RequestFinishedInfo;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;


/**
 * * Created by baolei.li on 2021/6/7 4:27 下午.
 */

public class CronetHelper {
    private static final String TAG = "CronetHelper";
    //    private static final String NET_LOG_PATH = Environment.getExternalStorageDirectory().getPath();
    // private static final String str1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
    private ExperimentalCronetEngine cronetEngine;

    private static volatile CronetHelper helper = null;
    private Executor executor;


    private CronetHelper() {
        executor = Executors.newSingleThreadExecutor();
    }

    public static CronetHelper getInstance() {
        if (helper == null) {
            synchronized (CronetHelper.class) {
                if (helper == null) {
                    helper = new CronetHelper();
                }
            }
        }
        return helper;
    }

    public void initCronetNetWorking(Context context) {

        ExperimentalCronetEngine.Builder builder = new ExperimentalCronetEngine.Builder(context);
        builder.enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISABLED, 100 * 1024) // cache
                .addQuicHint("www.litespeedtech.com", 443, 443)
                .enableHttp2(true)  // Http/2.0 Supprot
                .enableQuic(true)   // Quic Supprot
                .enableNetworkQualityEstimator(true);
        cronetEngine = builder.build();
        URL.setURLStreamHandlerFactory(cronetEngine.createURLStreamHandlerFactory());
        cronetEngine.addRttListener(new NetworkQualityRttListener(executor) {
            @Override
            public void onRttObservation(int i, long l, int i1) {
                Log.d(TAG, "Rtt info: i = " + i + " l = " + l + " i1 = " + i1);
                int kbps = cronetEngine.getDownstreamThroughputKbps();
                Log.d(TAG, "kbps = " + kbps);
                int type = cronetEngine.getEffectiveConnectionType();
                Log.d(TAG, "type = " + type);
            }
        });
        cronetEngine.addRequestFinishedListener(new RequestFinishedInfo.Listener(executor) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onRequestFinished(RequestFinishedInfo requestFinishedInfo) {
                int i = cronetEngine.getHttpRttMs();
                Log.e(TAG, "Rtt info: i = " + i);
                onRequestFinishedHandle(requestFinishedInfo);
            }
        });
    }

    public boolean isInit() {
        return cronetEngine != null;
    }


    public UrlRequest buildRequest(Request request, UrlRequest.Callback callback) throws IOException {
        String url = request.url().toString();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(url, callback, executor);
        requestBuilder.setHttpMethod(request.method());
        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i += 1) {
            requestBuilder.addHeader(headers.name(i), headers.value(i));
        }
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                requestBuilder.addHeader("Content-Type", contentType.toString());
            }
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            requestBuilder.setUploadDataProvider(UploadDataProviders.create(buffer.readByteArray()), executor);
        }
        return requestBuilder.build();
    }


    public HttpURLConnection openConnection(URL httpUrl) {
        try {
            return (HttpURLConnection) cronetEngine.openConnection(httpUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Print the request info.
     *
     * @param requestInfo requestInfo
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onRequestFinishedHandle(final RequestFinishedInfo requestInfo) {
        Log.e(TAG, "############# url: " + requestInfo.getUrl() + " #############");
        Log.e(TAG, "onRequestFinished: " + requestInfo.getFinishedReason());
        RequestFinishedInfo.Metrics metrics = requestInfo.getMetrics();
        if (metrics != null) {
            Log.e(TAG, "RequestStart: " + (metrics.getRequestStart() == null ? -1 : metrics.getRequestStart().getTime()));
            Log.e(TAG, "DnsStart: " + (metrics.getDnsStart() == null ? -1 : metrics.getDnsStart().getTime()));
            Log.e(TAG, "DnsEnd: " + (metrics.getDnsEnd() == null ? -1 : metrics.getDnsEnd().getTime()));
            Log.e(TAG, "ConnectStart: " + (metrics.getConnectStart() == null ? -1 : metrics.getConnectStart().getTime()));
            Log.e(TAG, "ConnectEnd: " + (metrics.getConnectEnd() == null ? -1 : metrics.getConnectEnd().getTime()));
            Log.e(TAG, "SslStart: " + (metrics.getSslStart() == null ? -1 : metrics.getSslStart().getTime()));
            Log.e(TAG, "SslEnd: " + (metrics.getSslEnd() == null ? -1 : metrics.getSslEnd().getTime()));
            Log.e(TAG, "SendingStart: " + (metrics.getSendingStart() == null ? -1 : metrics.getSendingStart().getTime()));
            Log.e(TAG, "SendingEnd: " + (metrics.getSendingEnd() == null ? -1 : metrics.getSendingEnd().getTime()));
            Log.e(TAG, "PushStart: " + (metrics.getPushStart() == null ? -1 : metrics.getPushStart().getTime()));
            Log.e(TAG, "PushEnd: " + (metrics.getPushEnd() == null ? -1 : metrics.getPushEnd().getTime()));
            Log.e(TAG, "ResponseStart: " + (metrics.getResponseStart() == null ? -1 : metrics.getResponseStart().getTime()));
            Log.e(TAG, "RequestEnd: " + (metrics.getRequestEnd() == null ? -1 : metrics.getRequestEnd().getTime()));
            Log.e(TAG, "TotalTimeMs: " + metrics.getTotalTimeMs());
            Log.e(TAG, "RecvByteCount: " + metrics.getReceivedByteCount());
            Log.e(TAG, "SentByteCount: " + metrics.getSentByteCount());
            Log.e(TAG, "SocketReused: " + metrics.getSocketReused());
            Log.e(TAG, "TtfbMs: " + metrics.getTtfbMs());

        }

        Exception exception = requestInfo.getException();
        if (exception != null) {
            Log.e(TAG, "exception = " + exception.toString());
        }
        UrlResponseInfo urlResponseInfo = requestInfo.getResponseInfo();
        if (urlResponseInfo != null) {
            Log.e(TAG, "Cache: " + urlResponseInfo.wasCached());
            Log.e(TAG, "Protocol: " + urlResponseInfo.getNegotiatedProtocol());
            Log.e(TAG, "HttpCode: " + urlResponseInfo.getHttpStatusCode());
            Log.e(TAG, "ProxyServer: " + urlResponseInfo.getProxyServer());
            List<Map.Entry<String, String>> headers = urlResponseInfo.getAllHeadersAsList();
            for (Map.Entry<String, String> entry : headers) {
                Log.e(TAG, "=== " + entry.getKey() + " : " + entry.getValue() + " ===");
            }
        }
        Log.e(TAG, "############# END #############");
    }

    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }

}
