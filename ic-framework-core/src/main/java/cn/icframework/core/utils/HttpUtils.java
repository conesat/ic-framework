package cn.icframework.core.utils;


import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


/**
 * HTTP工具类
 *
 * @author hzl
 */
public class HttpUtils {

    static OkHttpClient okHttpClient = new OkHttpClient();

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doGet(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        if (headers != null) {
            builder.headers(Headers.of(headers));
        }
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
