package answer.android.phonemp3.api;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import answer.android.phonemp3.R;
import answer.android.phonemp3.log.Logger;
import answer.android.phonemp3.tool.Tool;

/**
 * 接口封装类
 * Created by Micro on 2017/6/12.
 */

public class API {

    public static final class LAST_FM_API {
        public static final String APPLICATION_NAME = "音乐怎么了";
        public static final String API_KEY = "4e70ec9de65366b940679c6fd935bce9";
        public static final String SHARED_SECRET = "334b45312323f5e3b03326213d30bf81";
        public static final String REGISTERED_TO = "Microanswer";

        /**
         * 专辑相关
         */
        public static class ALBUM {
            /**
             * 获取专辑信息
             *
             * @param c
             * @param artist
             * @param ablumName
             * @return
             */
            public static String GETINFO(Context c, String artist, String ablumName) {
                HashMap<String, String> param = new HashMap<>();
                param.put("artist", artist);
                param.put("album", ablumName);
                return request(c, c.getString(R.string.ALBUM_GETINFO), param);
            }
        }

        /**
         * 歌手相关
         */
        public static class ARTIST {
            /**
             * 获取歌手信息
             * @param c
             * @param artist
             * @return
             */
            public static String GETINFO(Context c, String artist) {
                Map<String, String> param = new HashMap<>();
                param.put("artist", artist);
                return request(c, c.getString(R.string.ARTIST_GETINFO), param);
            }
        }

        /**
         * 请求LastFm接口
         *
         * @param method 调用的方法
         * @param param  参数
         * @return
         */
        private static String request(Context c, String method, Map<String, String> param) {
            if (param == null) {
                param = new HashMap<>();
            }

            param.put("api_key", API_KEY);
            param.put("format", "json");
            param.put("method", method);

            RequestParams requestParams = new RequestParams(c.getString(R.string.LAST_FM));

            for (Map.Entry<String, String> en : param.entrySet()) {
                requestParams.addQueryStringParameter(en.getKey(), en.getValue());
            }
            String result = "";
            try {
                result = x.http().requestSync(HttpMethod.GET, requestParams, String.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return result;
        }
    }

    private static Logger logger;

    /**
     * 调用api
     *
     * @param c          context
     * @param api_config 回调
     * @return 可取消的请求
     */
    public static Callback.Cancelable useApi(Context c, final int method, final Config api_config) {
        if (logger == null) {
            logger = Logger.getLogger(c);
        }
        return useApi(c, c.getResources().getString(method), api_config);
    }

    /**
     * 调用api
     */
    public static Callback.Cancelable useApi(Context c, final String method, final Config api_config) {
        final String uri = c.getResources().getString(R.string.HOST_);
        if (logger == null) {
            logger = Logger.getLogger(c);
        }
        logger.setClassName(API.class.getSimpleName());
        RequestParams requestParams = getRequestParams(uri, c, method, api_config.getParam());
        // logger.info("请求:" + uri + ", 参数：" + requestParams.getBodyContent() + ", Header:" + Arrays.toString(requestParams.getHeaders().toArray()));
        return x.http().request(HttpMethod.POST, requestParams,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        // logger.info("接口:" + uri);
                        logger.info("请求结果:" + result);
                        try {
                            api_config.success(new JSONObject(result));
                        } catch (Exception e) {
                            logger.err("检查更新接口的成功回调转换JSONObject错误.");
                            e.printStackTrace();
                            logger.err(e);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        logger.err("接口[" + uri + "]出错:" + Tool.Exception2String(ex));
                        ex.printStackTrace();
                        api_config.fail(ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        logger.err("接口[" + uri + "]取消出错:" + Tool.Exception2String(cex));
                    }

                    @Override
                    public void onFinished() {
                        logger.info("接口[" + uri + "]请求完毕");
                        api_config.finish();
                    }
                });
    }

    private static RequestParams getRequestParams(String uri, Context c, String method, Map<String, String> par) {

        if (TextUtils.isEmpty(method)) {
            throw new RuntimeException("不可以调用空的方法");
        }

        RequestParams requestParams = new RequestParams(uri);
        requestParams.setHeader("Content-Type", "application/json");
        requestParams.setConnectTimeout(c.getResources().getInteger(R.integer.nettimeout));
        try {
            JSONObject d = new JSONObject();
            d.put("method", method);
            JSONObject pa = new JSONObject();

            if (par != null && par.size() > 0) {
                for (Map.Entry<String, String> e : par.entrySet()) {
                    pa.put(e.getKey(), e.getValue());
                }
            }
            d.put("data", pa);
            String p_ = d.toString();
            requestParams.setAsJsonContent(true);
            requestParams.setMethod(HttpMethod.POST);
            requestParams.setBodyContent(p_);
            requestParams.setCharset("UTF-8");
            logger.err("构建请求参数完成：" + p_);
        } catch (Exception e) {
            logger.err("构建请求参数的时候出错：");
            logger.err(e);
        }
        return requestParams;
    }

    public static void close() {
        if (logger != null)
            logger.close();
    }

    /**
     * API请求结果回调接口
     * <p>
     * 回调函数在主线程执行
     * </p>
     */
    public static abstract class Config {

        /**
         * 获取请求参数
         *
         * @return
         */
        public Map<String, String> getParam() {
            return null;
        }

        public void finish() {
        }

        ;

        /**
         * 请求成功回调
         *
         * @param data JSONObject
         */
        public abstract void success(JSONObject data);

        /**
         * 请求失败回调
         *
         * @param e Exception
         */
        public abstract void fail(Throwable e);
    }
}
