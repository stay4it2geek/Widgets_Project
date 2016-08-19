package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.utils.ImageDownLoader;

/**
 * 等比例宽高比的分类图片控件
 * Created by custom on 2016/7/11.
 */
public class CommonCategoryImage extends RelativeLayout {
    ImageView networkImageView;
    private RelativeLayout loadfail_rl;
    private int mDefaultImageId;
    private ImageView default_img;
    private ImageDownLoader loader;

    public void setImageUrl(String url) {
        dispalyImage(url);
    }

    /**
     * 设置默认的加载图
     * @param defaultImage
     */

    public void setDefaultImageResId(int defaultImage) {

        mDefaultImageId = defaultImage;
    }

    public CommonCategoryImage(Context context) {
        super(context);
        init(context);

    }

    public CommonCategoryImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public CommonCategoryImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.common_catagory_imagelayout, this);
        networkImageView = (ImageView) layout.findViewById(R.id.adjust_net_img);
        default_img = (ImageView) layout.findViewById(R.id.default_img);
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        networkImageView.setLayoutParams(param);
        networkImageView.setAdjustViewBounds(true);
        networkImageView.setMaxHeight(400);
        networkImageView.setMaxWidth(LayoutParams.MATCH_PARENT);
        networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        loadfail_rl = (RelativeLayout) layout.findViewById(R.id.cate_loadfail_rl);        loader = ImageDownLoader.getInstance(context);

    }


    /**
     * 显示图片
     *
     * @param tmpUrl
     */
    public void dispalyImage( String tmpUrl) {


        //初始显示默认加载图
        if (mDefaultImageId != 0) {
            networkImageView.setVisibility(View.GONE);
            default_img.setVisibility(View.VISIBLE);
            default_img.setImageResource(mDefaultImageId);
        }
        Bitmap bitmap = null;
        bitmap = loader.getBitmapCache(tmpUrl);
        if (bitmap != null) {
            networkImageView.setVisibility(View.VISIBLE);
            default_img.setVisibility(View.GONE);
            networkImageView.setImageBitmap(bitmap);
        } else {
            // 防止滚动时多次下载
            if (!loader.getTaskCollection().containsKey(tmpUrl)) {
                loader.loadImage(tmpUrl,false, new ImageDownLoader.AsyncImageLoaderListener() {
                    @Override
                    public void onSucceseImageLoader(Bitmap bitmap) {
                        if (networkImageView != null && bitmap != null) {
                            networkImageView.setVisibility(View.VISIBLE);
                            default_img.setVisibility(View.GONE);
                            networkImageView.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onErrorImageLoader() {
                        loadfail_rl.setVisibility(View.VISIBLE);
                    }


                });

            }


        }


    }
}
