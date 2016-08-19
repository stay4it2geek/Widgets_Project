package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import cn.com.custom.widgetproject.R;

/**
 * Created by custom on 2016/7/11.
 */
public class BanerNetworkImage extends RelativeLayout {
    ImageView networkImageView;
    private RelativeLayout loadfail_rl;
    private ImageLoader imageLoader;
    private int mDefaultImageId;
    private LinearLayout top_fail_ll;
    private RelativeLayout common_fail_rl;
    private ImageView default_img;

    /**
     * 设置图像网址
     *
     * @param url
     * @param imageLoader
     * @param failIconIsTopOftext 错误图标是否在错误文字描述的上方
     */
    public void setImageUrlOfBanner(String url, ImageLoader imageLoader, boolean failIconIsTopOftext) {
        this.imageLoader = imageLoader;
        loadImage(failIconIsTopOftext, url);
    }

    public void setDefaultImageResId(int defaultImage) {

        mDefaultImageId = defaultImage;
    }

    public BanerNetworkImage(Context context) {
        super(context);
        init(context);

    }

    public BanerNetworkImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public BanerNetworkImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.customview_layout_banner_img, this);
        networkImageView = (ImageView) layout.findViewById(R.id.matchparent_network_img);
        default_img = (ImageView) layout.findViewById(R.id.default_img);
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        networkImageView.setLayoutParams(param);
        networkImageView.setAdjustViewBounds(true);
        networkImageView.setMaxHeight(500);
        networkImageView.setMaxWidth(LayoutParams.MATCH_PARENT);
        networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        loadfail_rl = (RelativeLayout) layout.findViewById(R.id.netimg_loadfail_rl);
        common_fail_rl = (RelativeLayout) layout.findViewById(R.id.common_fail_rl);
        top_fail_ll = (LinearLayout) layout.findViewById(R.id.top_fail_ll);

    }


    /**
     * 加载图片
     *
     * @param tmpUrl
     */
    public void loadImage(final boolean isTopOfText, String tmpUrl) {
        //初始显示默认加载图
        if (mDefaultImageId != 0) {
            networkImageView.setVisibility(View.GONE);
            default_img.setVisibility(View.VISIBLE);
            default_img.setImageResource(mDefaultImageId);
        }
        //之后进行加载网路图片
        imageLoader.get(tmpUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                if (response.getBitmap() != null) {
                    networkImageView.setVisibility(View.VISIBLE);
                    default_img.setVisibility(View.GONE);
                    networkImageView.setImageBitmap(response.getBitmap());
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                default_img.setVisibility(GONE);
                setVisiablely(isTopOfText);
            }
        });
    }

    /**
     * 设置视图可见性
     *
     * @param isTopOfText  错误图标是否在错误文字描述的上方
     */
    private void setVisiablely(boolean isTopOfText) {
        loadfail_rl.setVisibility(View.VISIBLE);
        if (isTopOfText) {
            top_fail_ll.setVisibility(View.VISIBLE);
        } else {
            common_fail_rl.setVisibility(View.VISIBLE);

        }
    }


}
