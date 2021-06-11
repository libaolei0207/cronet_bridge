package com.example.myapplication.method2;

import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import retrofit2.internal.EverythingIsNonNull;

/**
 * * Created by baolei.li on 2021/6/7 4:27 下午.
 */

public class CronetResponseBody extends ResponseBody {
    private MediaType mMediaType;
    private long mContentLength;
    private InputStream mInputStream;
    private HttpURLConnection mConnection;

    public CronetResponseBody(HttpURLConnection connection, MediaType contentTye, long contentLength, InputStream inputStream) {
        this.mMediaType = contentTye;
        this.mContentLength = contentLength;
        this.mInputStream = inputStream;
    }

    @Override
    public MediaType contentType() {
        return mMediaType;
    }

    @Override
    public long contentLength() {
        return mContentLength;
    }

    @Override
    public BufferedSource source() {
        Source source = Okio.source(mInputStream);
        return Okio.buffer(source);
    }
    @Override
    public void close() {
        super.close();
    }
}
