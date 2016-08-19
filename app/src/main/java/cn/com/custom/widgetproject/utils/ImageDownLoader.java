package cn.com.custom.widgetproject.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import cn.com.custom.widgetproject.manager.ApiManager;

/**
 * 具有内存缓存，超过删除缓存的图片下载工具类
 */
public class ImageDownLoader {
    private static final String ImageDownLoader_Log = DevUtil.makeLogTag(ImageDownLoader.class);
    private static ImageDownLoader mImageLoaderInstance;

    /**
     * 保存正在下载或等待下载的URL和相应失败下载次数（初始为0），防止滚动时多次下载
     */
    private Hashtable<String, Integer> taskCollection;
    /**
     * 缓存类
     */
    private LruCache<String, Bitmap> lruCache;
    /**
     * 线程池
     */
    private ExecutorService threadPool;
    /**
     * 缓存文件目录 （如无SD卡，则data目录下）
     */
    private File cacheFileDir;
    /**
     * 缓存文件夹
     */
    private static final String DIR_CACHE = "cache";
    /**
     * 缓存磁盘文件夹最大容量限制（50M）
     */
    private static final long DIR_CACHE_LIMIT = 50* 1024 * 1024;
    /**
     * 图片下载失败重试次数
     */
    private static final int IMAGE_DOWNLOAD_FAIL_TIMES = 2;

    Context context;

    private ImageDownLoader(Context context) {
        this.context = context.getApplicationContext();
        // 获取系统分配给每个应用程序的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 给LruCache分配最大内存的1/8
        lruCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        taskCollection = new Hashtable<String, Integer>();
        // 创建线程数
        threadPool = Executors.newFixedThreadPool(10);
        cacheFileDir = FileUtils.createFileDir(context, DIR_CACHE);
    }

    /**
     * 获取单例方法
     *
     * @return mApiManagerInstance api管理的单例对象
     */
    public static synchronized ImageDownLoader getInstance(Context context) {
        if (mImageLoaderInstance == null) {
            mImageLoaderInstance = new ImageDownLoader(context);
        }
        return mImageLoaderInstance;

    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    private void addLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            lruCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取Bitmap
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return lruCache.get(key);
    }

    /**
     * 异步下载图片，并按指定宽度和高度压缩图片
     *
     * @param url
     * @param listener 图片下载完成后调用接口
     */
    public void loadImage(final String url, final boolean sizeChange,
                          AsyncImageLoaderListener listener) {
        DevUtil.e(ImageDownLoader_Log, "download:" + url);
        final ImageHandler handler = new ImageHandler(listener);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url, sizeChange);
                Message msg = handler.obtainMessage();
                msg.obj = bitmap;
                handler.sendMessage(msg);
                // 将Bitmap 加入内存缓存
                addLruCache(url, bitmap);
                // 加入文件缓存前，需判断缓存目录大小是否超过限制，超过则清空缓存再加入
                long cacheFileSize = FileUtils.getFileSize(cacheFileDir);
                DevUtil.e("CACHESIZE", cacheFileDir
                        + " size has NOT exceed limit." + cacheFileSize);
                if (cacheFileSize > DIR_CACHE_LIMIT) {
                    DevUtil.e(ImageDownLoader_Log, cacheFileDir
                            + " size has exceed limit." + cacheFileSize);
                    FileUtils.delFile(cacheFileDir, true);
                    taskCollection.clear();
                }
                // 缓存文件名称（ 替换url中非字母和非数字的字符，防止系统误认为文件路径）
                String urlKey = url.replaceAll("[^\\w]", "");
                // 将Bitmap加入文件缓存
                FileUtils.savaBitmap(cacheFileDir, urlKey, bitmap);
            }
        };
        // 记录该url，防止滚动时多次下载，0代表该url下载失败次数
        taskCollection.put(url, 0);
        threadPool.execute(runnable);
    }

    /**
     * 获取Bitmap, 若内存缓存为空，则去磁盘文件缓存中获取
     *
     * @param url
     * @return 若缓存中没找到，则返回null
     */
    public Bitmap getBitmapCache(String url) {
        // 去处url中特殊字符作为文件缓存的名称
        String urlKey = url.replaceAll("[^\\w]", "");
        if (getBitmapFromMemCache(url) != null) {
            return getBitmapFromMemCache(url);
        } else if (FileUtils.isFileExists(cacheFileDir, urlKey)
                && FileUtils.getFileSize(new File(cacheFileDir, urlKey)) > 0) {
            // 从文件缓存中获取Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(cacheFileDir.getPath()
                    + File.separator + urlKey);
            // 将Bitmap 加入内存缓存
            addLruCache(url, bitmap);
            return bitmap;
        }
        return null;
    }

    /**
     * 下载图片，并按指定高度和宽度压缩
     *
     * @param urlPath
     * @return
     */
    private Bitmap downloadImage(String urlPath, boolean sizeChange) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlPath);
            urlConnection = (HttpURLConnection) url.openConnection();
            DevUtil.e("code :", urlConnection.getResponseCode() + "");
            BitmapFactory.Options options = new BitmapFactory.Options();
            //设置只加载图片的格式尺寸信息到内存，不加载具体的图片字节。
            options.inJustDecodeBounds = true;
            //获取图片的高度和宽度
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            urlConnection = (HttpURLConnection) url.openConnection();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig=Bitmap.Config.RGB_565;//通过设置图片的格式即像素大小来进行图片的压缩
            if (sizeChange) {
                options.inSampleSize = 2;
            } else {
                options.inSampleSize = 1;

            }
            //获取图片的高度和宽度
            inputStream.close();

            bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream(), null, options);


        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        // 下载失败，再重新下载
        if (taskCollection.get(urlPath) != null) {
            int times = taskCollection.get(urlPath);
            if (bitmap == null
                    && times < IMAGE_DOWNLOAD_FAIL_TIMES) {
                times++;
                taskCollection.put(urlPath, times);
                bitmap = downloadImage(urlPath, sizeChange);
                DevUtil.e(ImageDownLoader_Log, "Re-download " + urlPath + ":" + times);
            }
        }
        return bitmap;
    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTasks() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public Hashtable<String, Integer> getTaskCollection() {
        return taskCollection;
    }

    /**
     * 异步加载图片接口
     */
    public interface AsyncImageLoaderListener {
        void onSucceseImageLoader(Bitmap bitmap);

        void onErrorImageLoader();
    }

    /**
     * 异步加载完成后，图片处理
     */
    static class ImageHandler extends Handler {

        private AsyncImageLoaderListener listener;

        public ImageHandler(AsyncImageLoaderListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((Bitmap) msg.obj != null) {
                listener.onSucceseImageLoader((Bitmap) msg.obj);
            } else {
                listener.onErrorImageLoader();
            }
        }
    }
}
