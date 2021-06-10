package com.example.myapplication.method2;

import com.example.myapplication.cronet.CronetHelper;

import org.chromium.net.urlconnection.CronetHttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;


/**
 * * Created by baolei.li on 2021/6/7 4:27 下午.
 */

public class CronetInterceptor implements Interceptor {
    private static final String charset = "utf-8";
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String url = request.url().toString();
        String decodeUrl = URLDecoder.decode(url, charset);

        String method = request.method();
        RequestBody requestBody = request.body();
        CronetHttpURLConnection connection = null;
        OutputStream outputStream = null;
        try {

            connection = (CronetHttpURLConnection) CronetHelper.getInstance().openConnection(new URL((decodeUrl)));

            connection.setRequestMethod(method);

            Set<String> headerlist = request.headers().names();

            for (String headerName : headerlist) {
                connection.addRequestProperty(headerName, request.headers().get(headerName));
            }

            // 只有当POST请求时才会执行此代码段
            if (requestBody != null) {

                if (requestBody.contentType() != null) {
                    connection.setRequestProperty("Content-Type", requestBody.contentType().toString());
                }

                connection.setDoOutput(true);

                try {
                    outputStream = connection.getOutputStream();
                    Sink sink = Okio.sink(outputStream);
                    BufferedSink bufferedSink = Okio.buffer(sink);
                    request.body().writeTo(bufferedSink);
                    bufferedSink.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            long contentLength = connection.getContentLength();

            InputStream inputStream = null;

            if (responseCode >= 200 && responseCode <= 399) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            Map<String, List<String>> respHeaders = connection.getHeaderFields();
            List<String> contentTypeList = respHeaders.get("Content-Type");
            List<String> contentLengthList = respHeaders.get("Content-Length");
            String contentTypeString = "";
            contentLength = 0;
            if (contentTypeList != null && contentTypeList.size() > 0) {
                contentTypeString = contentTypeList.get(contentTypeList.size() - 1);
            }

            if (contentLengthList != null && contentLengthList.size() > 0) {
                contentLength = Long.parseLong(contentLengthList.get(contentLengthList.size() - 1));
            }

            MediaType mediaType = MediaType.parse(contentTypeString);
            ResponseBody responseBody = new CronetResponseBody(connection, mediaType, contentLength, inputStream);

            Response.Builder respBuilder = new Response.Builder();

            for (Map.Entry<String, List<String>> stringListEntry : respHeaders.entrySet()) {
                for (String valueString : stringListEntry.getValue()) {
                    if (stringListEntry.getKey() != null) {
                        respBuilder.addHeader(stringListEntry.getKey(), valueString);
                    }
                }
            }

            return respBuilder
                    .body(responseBody)
                    .code(responseCode)
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .message(responseMessage)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
