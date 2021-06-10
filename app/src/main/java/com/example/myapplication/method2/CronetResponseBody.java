package com.example.myapplication.method2;

import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * @ProjectName: My Application
 * @Package: zy.demo.myapplication.record
 * @ClassName: CronetResponseBody
 * @Description: java类作用描述
 * @Author: zhangyi
 * @CreateDate: 2021/5/13 10:20
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
        BufferedSource bufferedSource = Okio.buffer(source);
        return bufferedSource;
    }

    @Override
    public void close() {
        super.close();
    }
}
