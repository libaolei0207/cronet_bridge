package com.example.myapplication.method1;

import androidx.annotation.Nullable;

import com.example.myapplication.cronet.CronetHelper;
import com.example.myapplication.cronet.CronetUrlRequestCallback;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import okio.AsyncTimeout;
import okio.Timeout;

/**
 * baolei.li Create by 2021/4/5
 */
final public class CronetOkHttpCall implements Call {
  private final OkHttpClient client;
  private final EventListener eventListener;
  private Request originalRequest;
  private CronetEngine engine;

  @Nullable
  private UrlRequest mRequest;
  private boolean executed;
  private boolean canceled;

  private Timeout timeout = new AsyncTimeout() {
    @Override
    protected void timedOut() {
      cancel();
    }
  };

  CronetOkHttpCall(OkHttpClient client, CronetEngine engine, Request originalRequest) {
    this.client = client;
    this.engine = engine;
    this.originalRequest = originalRequest;

    eventListener = client.eventListenerFactory().create(this);

    this.timeout.timeout(client.callTimeoutMillis(), TimeUnit.MILLISECONDS);
  }

  @Override
  public Request request() {
    return originalRequest;
  }

  @Override
  public Response execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }

    eventListener.callStart(this);
    CronetUrlRequestCallback callback = new CronetUrlRequestCallback(originalRequest, this, eventListener, null);
    mRequest = CronetHelper.getInstance().buildRequest(originalRequest, callback);
    mRequest.start();

    return callback.waitForDone();
  }

  @Override
  public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    eventListener.callStart(this);
    try {
      CronetUrlRequestCallback callback = new CronetUrlRequestCallback(originalRequest, this, eventListener, responseCallback);
      mRequest = CronetHelper.getInstance().buildRequest(originalRequest, callback);
      mRequest.start();
    } catch (IOException e) {
      responseCallback.onFailure(this, e);
    }
  }

  @Override
  public void cancel() {
    if (mRequest != null && !mRequest.isDone()) {
      canceled = true;
      mRequest.cancel();
    }
  }

  @Override
  public boolean isExecuted() {
    return executed;
  }

  @Override
  public boolean isCanceled() {
    return canceled;
  }

  @Override
  public Timeout timeout() {
    return timeout;
  }

  @SuppressWarnings("CloneDoesntCallSuperClone") // We are a final type & this saves clearing state.
  @Override
  public Call clone() {
    return new CronetOkHttpCall(client, engine, originalRequest);
  }
}
