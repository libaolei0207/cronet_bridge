package com.example.myapplication.method1;

import com.example.myapplication.cronet.CronetHelper;

import org.chromium.net.CronetEngine;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * baolei.li Create by 2021/4/5
 */

public class CronetOkHttpCallFactory implements Call.Factory {

    private final OkHttpClient client;

    public CronetOkHttpCallFactory(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Call newCall(Request request) {
        CronetEngine cronetEngine = CronetHelper.getInstance().getCronetEngine();
        if (cronetEngine != null) {
            return new CronetOkHttpCall(client, cronetEngine, request);
        } else {
            return client.newCall(request);
        }
    }
}
