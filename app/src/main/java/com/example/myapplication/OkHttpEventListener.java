package com.example.myapplication;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * baolei.li Create by 2021/4/5
 */

public class OkHttpEventListener extends EventListener {

    public static final Factory FACTORY = new Factory() {
        final AtomicLong nextCallId = new AtomicLong(1L);

        @Override
        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            return new OkHttpEventListener(callId, call.request().url(), System.nanoTime());
        }
    };

    /**
     * 每次请求的标识
     */
    private final long callId;

    /**
     * 每次请求的开始时间，单位纳秒
     */
    private final long callStartNanos;

    private StringBuilder sbLog;


    public OkHttpEventListener(long callId, HttpUrl url, long callStartNanos) {
        this.callId = callId;
        this.callStartNanos = callStartNanos;
        this.sbLog = new StringBuilder(url.toString()).append(" ");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recordEventLog(String name) {
        long elapseNanos = System.nanoTime() - callStartNanos;
        sbLog.append(String.format(Locale.CHINA, "%s:%.3fms", name, elapseNanos / 1000000d)).append(";\n");
        if (name.equalsIgnoreCase("callEnd") || name.equalsIgnoreCase("callFailed")) {
            Log.e("OkHTTP", sbLog.toString());
        }
    }

    @Override
    public void callStart(Call call) {
        super.callStart(call);
        recordEventLog("callStart");
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        recordEventLog("dnsStart");

    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        recordEventLog("dnsEnd");
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        recordEventLog("connectStart");

    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
        recordEventLog("secureConnectStart");
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        recordEventLog("secureConnectEnd");
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        recordEventLog("connectEnd");
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        recordEventLog("connectFailed");

    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        super.connectionAcquired(call, connection);
        recordEventLog("connectionAcquired");
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        super.connectionReleased(call, connection);
        recordEventLog("connectionReleased");
    }

    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
        recordEventLog("requestHeadersStart");
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
        recordEventLog("requestHeadersEnd");
    }

    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
        recordEventLog("requestBodyStart");
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        recordEventLog("requestBodyEnd");
    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
        recordEventLog("responseHeadersStart");
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        recordEventLog("responseHeadersEnd");
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
        recordEventLog("responseBodyStart");
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        recordEventLog("responseBodyEnd");
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        recordEventLog("callEnd");


    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
        recordEventLog("callFailed");
    }


}
