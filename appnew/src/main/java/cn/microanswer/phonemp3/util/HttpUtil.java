package cn.microanswer.phonemp3.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.microanswer.phonemp3.API;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求工具。<br>
 * 此文件依赖：<br>
 * <pre>
 *      &lt;dependency&gt;
 *          &lt;groupId&gt;com.squareup.okhttp3&lt;/groupId&gt;
 *          &lt;artifactId&gt;okhttp&lt;/artifactId&gt;
 *          &lt;version&gt;3.7.0&lt;/version&gt;
 *      &lt;/dependency&gt;
 *      &lt;dependency&gt;
 *          &lt;groupId&gt;com.alibaba&lt;/groupId&gt;
 *          &lt;artifactId&gt;fastjson&lt;/artifactId&gt;
 *          &lt;version&gt;1.2.47&lt;/version&gt;
 *      &lt;/dependency&gt;
 * 	</pre>
 */
public class HttpUtil {

    private static Logger logger = Logger.getLogger(HttpUtil.class);
    private static String CHARSET = "UTF-8";
    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
    private static final MediaType APPLICATION_XML = MediaType.parse("application/xml");
    private static final MediaType X_WWW_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");
    private static OkHttpClient __httpClient;

    private static OkHttpClient getHttpClient() {
        if (__httpClient == null) {
            __httpClient = new OkHttpClient
                    .Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return __httpClient;
    }

    private static Response _requestForResponse(Request request) throws NetException {
        Call call = getHttpClient().newCall(request); // 使用请求数据建立请求。
        Response response = null; // 发起请求。
        try {
            response = call.execute();
            if (response.code() != 200) {
                throw new Exception(request.method() + " error:" + request.url() + " [" + response.code() + "] " + response.message());
            }
        } catch (Exception e) {
            int code = -1;
            if (response != null) {
                code = response.code();
            }
            throw new NetException(e.getMessage(), code);
        }

        return response;
    }

    private static String _requestForString(Request request) throws NetException {
        Response response = _requestForResponse(request);
        String resStr = "";
        try {
            resStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resStr;
    }

    private static String _makeParamUrl(String url, Map<String, String> params) {
        String queryString = map2wwwUrlFormEncode(params);
        if (queryString.length() > 0) {
            if (url.contains("?")) {
                url = url + "&" + queryString;
            } else {
                url = url + "?" + queryString;
            }
        }
        return url;
    }

    private static void _putHearderInRequestBuilder(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Request _makeGetRequest(String url, Map<String, String> params, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        builder.url(_makeParamUrl(url, params));
        _putHearderInRequestBuilder(builder, headers);
        builder.get();
        return builder.build();
    }

    private static void mapForEach(Map<String, String> map, Fun<String, String> fun) throws Exception {
        if (map == null || map.size() <= 0 || fun == null) {
            return;
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            fun.d0fun(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 使用 get方法 请求某地址。
     *
     * @param url     [Y] 要请求的地址
     * @param params  [N] 请求参数
     * @param headers [N] 请求头
     * @return 请求返回的字符串
     */
    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {

        return _requestForString(_makeGetRequest(url, params, headers));
    }

    /**
     * 使用 get方法 请求某地址。
     *
     * @param url    [Y] 要请求的地址。
     * @param params [N] 请参数。
     * @return 请求结果。
     * @throws Exception 错误。
     */
    public static String get(String url, Map<String, String> params) throws Exception {
        return get(url, params, null);
    }

    /**
     * 使用 get 方法请求某地址
     *
     * @param url [Y] 请求地址。
     * @return 结果
     * @throws Exception 错误
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * 使用 POST方法 请求某url
     *
     * @param url       [Y] 要请求的地址
     * @param bodyBytes [N] 数据体
     * @param headers   [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String post(String url, final MediaType contentType, final byte[] bodyBytes, Map<String, String> headers) throws Exception {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        _putHearderInRequestBuilder(builder, headers);
        builder.post(RequestBody.create(contentType, bodyBytes));
        Request request = builder.build(); // 建立请求数据。
        Call call = getHttpClient().newCall(request); // 使用请求数据建立请求。
        Response response = call.execute();
        if (response.code() != 200) {
            throw new Exception("post error:" + url + " [" + response.code() + "] " + response.message());
        }
        return response.body().string();
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url     [Y] 要请求的 url
     * @param params  [N] 请求参数
     * @param headers [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String postFormUrlEncode(String url, Map<String, ?> params, Map<String, String> headers) throws Exception {
        return post(url, X_WWW_FORM_URLENCODED, map2wwwUrlFormEncode(params).getBytes(CHARSET), headers);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url    [Y] 要请求的 url
     * @param params [N] 请求参数
     * @return 结果
     * @throws Exception 错误
     */
    public static String postFormUrlEncode(String url, Map<String, ?> params) throws Exception {
        return postFormUrlEncode(url, params, null);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url     [Y] 要请求的 url
     * @param params  [N] 请求参数
     * @param headers [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, Map<String, ?> params, Map<String, String> headers) throws Exception {
        return postApplicationJson(url, JSON.toJSONString(params), headers);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url    [Y] 要请求的 url
     * @param params [N] 请求参数
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, Map<String, ?> params) throws Exception {
        return postApplicationJson(url, params, null);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url        [Y] 要请求的 url
     * @param jsonObject [N] 请求参数
     * @param headers    [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, JSONObject jsonObject, Map<String, String> headers) throws Exception {
        return postApplicationJson(url, jsonObject.toJSONString(), headers);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url        [Y] 要请求的 url
     * @param jsonObject [N] 请求参数
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, JSONObject jsonObject) throws Exception {
        return postApplicationJson(url, jsonObject.toJSONString(), null);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url     [Y] 要请求的 url
     * @param jsonStr [N] 请求参数
     * @param headers [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, String jsonStr, Map<String, String> headers) throws Exception {
        return post(url, APPLICATION_JSON, jsonStr.getBytes(CHARSET), headers);
    }

    /**
     * 使用 post方法 请求某url
     *
     * @param url     [Y] 要请求的 url
     * @param jsonStr [N] 请求参数
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationJson(String url, String jsonStr) throws Exception {
        return postApplicationJson(url, jsonStr, null);
    }

    /**
     * 使用 post方法 请求某 url
     *
     * @param url     [Y] 要请求的 url
     * @param xmlStr  [N] xml字符串
     * @param headers [N] 请求头
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationXml(String url, String xmlStr, Map<String, String> headers) throws Exception {
        return post(url, APPLICATION_XML, xmlStr.getBytes(CHARSET), headers);
    }

    /**
     * 使用 post方法 请求某 url
     *
     * @param url    [Y] 要请求的 url
     * @param xmlStr [N] xml字符串
     * @return 结果
     * @throws Exception 错误
     */
    public static String postApplicationXml(String url, String xmlStr) throws Exception {
        return postApplicationXml(url, xmlStr, null);
    }

    /**
     * 上传文件到指定的 url.
     * 此方法将使用
     *
     * @param url     [Y] 地址
     * @param params  [N] 参数
     * @param headers [N] 参数
     * @param files   [N] 文件
     * @return 上传完成后，服务器返回的字符串
     * @throws Exception 错误
     */
    public static String upload(String url, Map<String, String> params, Map<String, String> headers, File... files) throws Exception {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        _putHearderInRequestBuilder(builder, headers);
        final MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        if (params != null && params.size() > 0) {
            mapForEach(params, new Fun<String, String>() {
                @Override
                public void d0fun(String key, String value) {
                    multipartBodyBuilder.addFormDataPart(key, value);
                }
            });
        }

        if (files != null && files.length > 0) {
            int index = 0;
            for (File file : files) {
                multipartBodyBuilder.addFormDataPart(
                        "file" + (index > 0 ? index : ""),
                        file.getName(),
                        RequestBody.create(MediaType.parse("*/*"), file)
                );
                index = index + 1;
            }
        }

        builder.post(multipartBodyBuilder.build());
        Request request = builder.build(); // 建立请求数据。
        Call call = getHttpClient().newCall(request); // 使用请求数据建立请求。
        Response response = call.execute();
        if (response.code() != 200) {
            throw new Exception("upload error:" + url + " [" + response.code() + "] " + response.message());
        }
        return response.body().string();
    }

    /**
     * 上传文件到指定的 url.
     * 此方法将使用
     *
     * @param url    [Y] 地址
     * @param params [N] 参数
     * @param files  [N] 文件
     * @return 上传完成后，服务器返回的字符串
     * @throws Exception
     */
    public static String upload(String url, Map<String, String> params, File... files) throws Exception {
        return upload(url, params, null, files);
    }

    /**
     * 上传文件到指定的 url.
     * 此方法将使用
     *
     * @param url   [Y] 地址
     * @param files [N] 文件
     * @return 上传完成后，服务器返回的字符串
     * @throws Exception 错误
     */
    public static String upload(String url, File... files) throws Exception {
        return upload(url, null, files);
    }

    /**
     * 下载文件.如果指定的文件已存在，将会覆盖。
     *
     * @param url       [Y] 下载路径
     * @param params    [N] 参数
     * @param headers   [N] 请求头
     * @param fileOrDir [N] 下载目录或直接对应文件。
     * @return 下载完成后对应的文件
     */
    public static File download(String url, Map<String, String> params, Map<String, String> headers, File fileOrDir) throws Exception {
        Response response = _requestForResponse(_makeGetRequest(url, params, headers));

        if (fileOrDir == null || fileOrDir.isDirectory()) {

            // 处理要保存的文件路径。
            String fileName = "";

            // 从响应头获取一个文件名：
            String contentdisposition = response.header("Content-Disposition");
            if (contentdisposition != null) {
                int filenameIndex = contentdisposition.indexOf("filename");
                if (filenameIndex > -1) {
                    fileName = contentdisposition.substring(filenameIndex + 10, contentdisposition.length() - 1);
                }
            }

            // 没有获取到文件名则从url中解析文件名。
            if (fileName.length() < 1) {
                // 从 url 检索一个文件名：
                String[] split = url.split("\\?")[0].split("/");
                fileName = split[split.length - 1];
            }

            // 依然没有文件名，只有使用时间戳来命名了。
            if (fileName.length() < 1) {
                fileName = String.valueOf(System.currentTimeMillis());
            }

            if (!fileName.contains(".")) {
                // 如果文件中没有 . ，说明没有后缀
                // 尝试从header中获取后缀
                // 使用时间戳命名，文件后缀从 Content-Type 中获取。
                String fileEnd = response.header("Content-Type");
                if (fileEnd != null && fileEnd.length() > 0) {
                    if (fileEnd.contains(";")) {
                        fileEnd = fileEnd.substring(fileEnd.indexOf("/") + 1, fileEnd.indexOf(";"));
                    } else {
                        fileEnd = fileEnd.substring(fileEnd.indexOf("/") + 1);
                    }
                } else {
                    fileEnd = "httputildownload";
                }
                fileName = fileName + "." + fileEnd;
            }

            if (fileOrDir == null) {
                // 如果没有指定，那么将文件保存到临时目录
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                fileOrDir = new File(tempDir, fileName);
            } else {
                // 指定的是目录
                fileOrDir = new File(fileOrDir, fileName);
            }
        }

        // 开始下载
        InputStream inputStream = response.body().byteStream(); // 下载输入流获取到。

        FileOutputStream outputStream = new FileOutputStream(fileOrDir);
        BufferedOutputStream bfo = new BufferedOutputStream(outputStream);

        byte[] data = new byte[1024];
        int dataSize;

        while ((dataSize = inputStream.read(data)) != -1) {
            bfo.write(data, 0, dataSize);
        }

        bfo.flush();
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return fileOrDir;
    }

    /**
     * 将 url 的文件下载到 fileOrDir 指定的路径。如果指定的文件已存在，将会覆盖。
     *
     * @param url       [Y] url下载路径
     * @param fileOrDir [N] 目录或文件保存路径，如果不传，将保存在零时目录
     * @return 下载完成后对应的文件
     */
    public static File download(String url, File fileOrDir) throws Exception {
        return download(url, null, null, fileOrDir);
    }

    /**
     * 将 url 的文件下载到 fileOrDir 指定的路径。如果指定的文件已存在，将会覆盖。
     *
     * @param url           [Y] url下载路径
     * @param fileOrDirPath [N] 目录或文件保存路径，如果不传，将保存在零时目录
     * @return 下载完成后对应的文件
     */
    public static File download(String url, String fileOrDirPath) throws Exception {
        return download(url, new File(fileOrDirPath));
    }

    /**
     * 下载 url 对应的文件。 文件将被保存到零时文件夹。如果指定的文件已存在，将会覆盖。
     *
     * @param url [Y] 下载路径
     * @return 下载完成后对应的文件
     * @throws Exception 错误
     */
    public static File download(String url) throws Exception {
        return download(url, (File) null);
    }

    /**
     * 将 map 集合转换为 www-url-form-encoded 格式的字符串。
     *
     * @param params  [N] 要转换的集合map
     * @param charSet [Y] 编码方式
     * @return 结果
     */
    public static String map2wwwUrlFormEncode(Map<String, ?> params, String charSet) {
        if (params == null || params.size() < 1) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        Set<? extends Map.Entry<String, ?>> entries = params.entrySet();
        for (Map.Entry<String, ?> entry : entries) {
            String value = null;
            try {
                value = URLEncoder.encode(entry.getValue().toString(), charSet);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stringBuilder.append(entry.getKey()).append("=").append(value).append("&");
        }
        return stringBuilder.toString();
    }

    /**
     * 将 map 集合转换为 www-url-form-encoded 格式的字符串。。 默认使用 UTF-8 编码
     *
     * @param params [N] 要转换的集合map
     * @return 结果
     */
    public static String map2wwwUrlFormEncode(Map<String, ?> params) {
        return map2wwwUrlFormEncode(params, CHARSET);
    }

    /**
     * 向 microanswer.cn 发起请求
     *
     * @param method
     * @param param
     */
    public static String postCnMicroanswer(String method, Map<String, String> param) throws Exception {
        final String url = API.URL + method;

        logger.i(String.format("请求接口：%s，参数：%s", url, map2wwwUrlFormEncode(param)));
        String s;
        try {
            s = postFormUrlEncode(url, param);
        } catch (Exception e) {
            e.printStackTrace();
            logger.e("请求网络出错：" + e.getMessage());
            s = "";
        }
        logger.i(String.format("接口返回：%s", s));

        return s;
    }

    /**
     * 向 microanswer.cn 发起请求
     *
     * @param method
     * @param param
     */
    public static void postCnMicroanswer(final String method, final Map<String, String> param, final HttpUtilListener l) {
        if (null == l) {
            return;
        }

        // 构建请求体
        try {
            Task.TaskHelper.getInstance().run(new Task.ITask<Object, JSONObject>() {
                @Override
                public JSONObject run(Object empty) throws Exception {

                    String s = postCnMicroanswer(method, param);

                    JSONObject resp = JSON.parseObject(s);

                    if (resp.getIntValue("code") != 200) {
                        throw new Exception(new Exception(resp.getString("msg")));
                    }

                    return resp;
                }

                @Override
                public void onError(Exception e) {
                    l.onError(e);
                }

                @Override
                public void afterRun(JSONObject data) {
                    if (null == data) {
                        l.onError(new Exception("HttpUtil: 服务器返回数据为空。"));
                        return;
                    }
                    try {
                        l.onResponse(data);
                    } catch (Exception e) {
                        l.onError(e);
                    }
                }
            });

        } catch (Exception e) {
            l.onError(e);
        }
    }

    // 持有2个泛型定义和一个方法的接口
    private interface Fun<A, B> {
        void d0fun(A a, B b) throws Exception;
    }

    public interface HttpUtilListener {
        void onResponse(JSONObject response) throws Exception;

        void onError(Exception e);
    }

    public static class NetException extends Exception {
        private int code;

        public NetException(int code) {
            this("net error", code);
        }

        public NetException(String msg, int code) {
            super(msg);
            this.code = code;
        }
    }
}
