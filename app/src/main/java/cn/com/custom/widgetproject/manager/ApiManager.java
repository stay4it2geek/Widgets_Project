package cn.com.custom.widgetproject.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.com.custom.widgetproject.callback.TanslationHttpCallback;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.config.ApiConfig;
import cn.com.custom.widgetproject.config.ApiConfig.TranslatConfig;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.model.BannerListModel;
import cn.com.custom.widgetproject.model.DetailItemModel;
import cn.com.custom.widgetproject.model.HotWordModel;
import cn.com.custom.widgetproject.model.JpCompareBaseModel;
import cn.com.custom.widgetproject.model.ProductListModel;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.BitmapLruCacheProduct;
import cn.com.custom.widgetproject.utils.DevUtil;
import cn.com.custom.widgetproject.utils.MD5Encoder;
import cn.com.custom.widgetproject.utils.StringUtil;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * api管理类
 * Created by custom on 2016/6/6.
 */
public class ApiManager {

    private static ApiManager mApiManagerInstance = null;
    private ImageLoader mImageLoader;//图片下载管理器
    private RequestQueue mRequestQueue;//图片请求队列
    private OkHttpClient mOkHttpClient;//网络请求队列对象
    private Gson mGson;//谷歌解析对象


    /**
     * 构造器方法私有化，只允许实例化一次，到处使用
     *
     * @param context
     */
    private ApiManager(Context context) {

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);

        mRequestQueue = getRequestQueue(context);
        mGson = new GsonBuilder().serializeNulls().create();
        //使用可用缓存的4分之一进行缓存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCacheProduct(cacheSize));
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        File cacheDir = new File(context.getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        mRequestQueue.start();

        // clear all volley caches.
        mRequestQueue.add(new ClearCacheRequest(cache, null));//构建清除之前缓存的请求，为了清空已有的缓存文件
        return mRequestQueue;
    }

    /**
     * 获取单例方法
     *
     * @return mApiManagerInstance api管理的单例对象
     */
    public static synchronized ApiManager getInstance(Context context) {
        if (mApiManagerInstance == null) {
            mApiManagerInstance = new ApiManager(context);
        }
        return mApiManagerInstance;

    }

    /**
     * 实例化图片下载管理器
     *
     * @return 图片下载管理器对象
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    /**
     * Json解析
     *
     * @param cls              Class 模型的类名
     * @param responseCallback Api请求返回值
     * @return callback     请求回调
     */
    private Callback parseJPBCJson(final Class<?> cls, final ResponseCallback responseCallback, final Request request) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                responseCallback.onFailure(request, e);
                DevUtil.e(ApiManager.class.getName(), "onFailure", e);
                int serversLoadTimes = 0;
                int maxLoadTimes = 5;
                DevUtil.e(this.getClass().getName(), "parseJanpanConpareJson", e);

                if (e.equals(java.net.SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                {

                    serversLoadTimes++;
                    mOkHttpClient.newCall(request).enqueue(this);
                    DevUtil.e("serversLoadTimes", serversLoadTimes + "");
                } else {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String json = response.body().string();
                    DevUtil.e("api", String.format("Request:\n%s\nData:\n%s", response.request().urlString(), json));
                    JpCompareBaseModel jpCompareBaseModel = (JpCompareBaseModel) mGson.fromJson(json, cls);
                    String status = jpCompareBaseModel.status;
                    if (StringUtil.isEqual("Ok", status) || StringUtil.isEqual("OK", status) || StringUtil.isEqual("ok", status) || StringUtil.isEqual("oK", status)) {
                        responseCallback.onResponse(response, jpCompareBaseModel);
                    } else {
                        throw new Exception("Status Error!");
                    }

                } catch (Exception e) {
                    responseCallback.onFailure(response.request(), e);

                    int serversLoadTimes = 0;
                    int maxLoadTimes = 5;
                    DevUtil.e(this.getClass().getName(), "parseJanpanConpareJson", e);

                    if (e.equals(java.net.SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                    {
                        serversLoadTimes++;
                        mOkHttpClient.newCall(request).enqueue(this);
                        DevUtil.e("serversLoadTimes", serversLoadTimes + "");
                    } else {
                        e.printStackTrace();
                    }

                }
            }
        };
        return callback;
    }


    /**
     * 请求百度翻译API对内容进行翻译
     *
     * @param q        翻译原文
     * @param from     翻译源语言种类代码
     * @param to       翻译后语言种类代码
     * @param callBack 视图回调
     * @throws Exception 异常
     */
    public static void translate(final String q, final String from, final String to, final TanslationHttpCallback callBack) throws Exception {

        //随机数，用于生成md5值，开发者使用时请激活下边第四行代码
        Random random = new Random();
        //用于md5加密
        int salt = random.nextInt(10000);
        // 对appId+源文+随机数+token计算md5值
        //应该对 appid+q+salt+密钥 拼接成的字符串做MD5得到32位小写的sign。确保要翻译的文本q为UTF-8编码。
        String md5String = TranslatConfig.APP_ID + new String(q.getBytes(), "utf-8") + salt + TranslatConfig.TOKEN;
        final String sign = MD5Encoder.encode(md5String.toString());
        //注意在生成签名拼接 appid+q+salt+密钥 字符串时，q不需要做URL encode，在生成签名之后，发送HTTP请求之前才需要对要发送的待翻译文本字段q做URL encode。
        final URL url1 = new URL(TranslatConfig.TRANSLATE_URL + "?" + "q=" + URLEncoder.encode(q, "utf-8") + "&from=" + from + "&to=" + to + "&appid=" + TranslatConfig.APP_ID + "&salt=" + salt + "&sign=" + sign);
        //异步执行翻译请求线程，避免在UI主线程卡顿导致ANR
        new AsyncTask<Void, Integer, String>() {
            public StringBuilder stringbuider = new StringBuilder();

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection conn = null;
                String text = null;
                try {
                    conn = (HttpURLConnection) url1.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str).append("\n");
                    }

                    //转化为json对象，注：Json解析的jar包可选其它
                    JSONObject resultJson = new JSONObject(builder.toString());

                    //处理错误
                    try {
                        String error_code = resultJson.getString("error_code");
                        if (error_code != null) {
                            System.out.println("出错代码:" + error_code);
                            System.out.println("出错信息:" + resultJson.getString("error_msg"));
                            callBack.onTranslateFailure("出错信息:" + resultJson.getString("error_msg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                        //获取返回翻译结果
                        JSONArray array = (JSONArray) resultJson.get("trans_result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dst = (JSONObject) array.get(i);
                            text = dst.getString("dst");
                            text = URLDecoder.decode(text, "utf-8");
                            stringbuider.append(text);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return stringbuider.toString().replace("|", "\n");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //请求成功时返回数据
                callBack.onTranslateSuccess(s);
            }

        }.execute();

    }


    /**
     * 请求tab分类数据
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call requestSearchCategoryModel(final ResponseCallback<SearchCategoryModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                .addPathSegment(ApiConfig.RequestPath.SEARCH_CATEGORY)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_LIST)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
//        http://api.parity.custom.com.cn/v1/api/category/list
//                .url("http://dev.custom.com.cn/api/price-comparison/search_category.php")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(SearchCategoryModel.class, callback, request));
        return call;
    }


    /**
     * 请求广告图数据
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call requestBannerModel(final ResponseCallback<BannerListModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                .addPathSegment(ApiConfig.RequestPath.BANNER_ADS)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_LIST)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
//                .url("http://dev.custom.com.cn/api/price-comparison/ads_list.php")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(BannerListModel.class, callback, request));
        return call;
    }


    /**
     * 请求产品列表数据
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call requestProductListModel(String categoryId, final ResponseCallback<ProductListModel> callback, boolean isRecommond) {
        HttpUrl httpUrl;
        if (isRecommond) {
            httpUrl = new HttpUrl.Builder()
                    .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                    .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                    .addPathSegment(ApiConfig.PathSegment.VERSION)
                    .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                    .addPathSegment(ApiConfig.RequestPath.SEARCH)
                    .addPathSegment(ApiConfig.PathSegment.SEGMENT_PRODUCT)
                    .addPathSegment(ApiConfig.RequestPath.SEARCH_PRODUCT_BY_CATE)
                    .addQueryParameter("categoryId", categoryId)
                    .addQueryParameter("limit", "all")
                    .build();
        } else {
            httpUrl = new HttpUrl.Builder()
                    .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                    .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                    .addPathSegment(ApiConfig.PathSegment.VERSION)
                    .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                    .addPathSegment(ApiConfig.RequestPath.SEARCH)
                    .addPathSegment(ApiConfig.PathSegment.SEGMENT_PRODUCT)
                    .addPathSegment(ApiConfig.RequestPath.SEARCH_PRODUCT_BY_CATE)
                    .addQueryParameter("categoryId", categoryId)
                    .build();
        }

        Request request = new Request.Builder()
//                .url("http://dev.custom.com.cn/api/price-comparison/search_products_by_category.php")
                .url(httpUrl)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(ProductListModel.class, callback, request));
        return call;
    }

    /**
     * 刷新产品列表数据
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call refreshProductListModel(String pageNo, String categoryId, final ResponseCallback<ProductListModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                .addPathSegment(ApiConfig.RequestPath.SEARCH)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_PRODUCT)
                .addPathSegment(ApiConfig.RequestPath.SEARCH_PRODUCT_BY_CATE)
                .addQueryParameter("categoryId", categoryId)
                .addQueryParameter("pageNo", pageNo)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
//                .url("http://dev.custom.com.cn/api/price-comparison/search_products_by_category.php")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(ProductListModel.class, callback, request));
        return call;
    }


    /**
     * 请求产品详情数据
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call requestProductDeatilModel(String code, final ResponseCallback<DetailItemModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.RequestPath.DETAIL)
                .addPathSegment("")
                .addQueryParameter(Constant.PRODUCT_CODE, code)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
//                .url("http://dev.custom.com.cn/api/price-comparison/products_detail.php")
                .build();
        DevUtil.e("httpUrl",httpUrl.toString());
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(DetailItemModel.class, callback, request));
        return call;
    }


    /**
     * 关键词检索
     *
     * @param callback 视图回调
     * @return call  请求
     */
    public Call requestProductByKeyWord(String keyword, String pageNo, final ResponseCallback<ProductListModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.RequestPath.SEARCH)
                .addPathSegment("")
                .addQueryParameter("kw", keyword)
                .addQueryParameter("pageNo", pageNo)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
//                .url("http://dev.custom.com.cn/api/price-comparison/search_products_by_keyword.php")
                .build();
        DevUtil.e("yri", httpUrl.toString());
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(ProductListModel.class, callback, request));
        return call;
    }

    /**
     * 热词检索
     *
     * @param callback
     * @return call  请求
     */
    public Call requestHotSearch(final ResponseCallback<HotWordModel> callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(ApiConfig.Scheme.JPANSHOP_SECHEME)
                .host(ApiConfig.Domain.JPANSHOP_DOMAIN_API)
                .addPathSegment(ApiConfig.PathSegment.VERSION)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_API)
                .addPathSegment(ApiConfig.RequestPath.SEARCH_PRODUCT_HOTWORD)
                .addPathSegment(ApiConfig.PathSegment.SEGMENT_LIST)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(parseJPBCJson(HotWordModel.class, callback, request));
        return call;
    }


}
