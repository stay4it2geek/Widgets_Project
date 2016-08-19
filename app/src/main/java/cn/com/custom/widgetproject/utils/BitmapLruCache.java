package cn.com.custom.widgetproject.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

/**
 * Bitmap图片缓存工具类
 *  * Created by custom on 2016/6/12.
 */

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    Context context;
	public BitmapLruCache(int maxSize ,Context context) {
		super(maxSize);
        this.context=context;
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
	}

    @Override
    public Bitmap getBitmap(String url) {
        return ImageFileCacheUtils.getInstance(context).getImage(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        ImageFileCacheUtils.getInstance(context).saveBitmap(bitmap, url);
    }
}
