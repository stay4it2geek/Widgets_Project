package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.utils.DevUtil;

/**
 * 详细图册的图片控件
 * Created by imjx on 2016/7/11.
 */
public class DetailNetworkImage extends RelativeLayout {
    ImageView networkImageView;
    private RelativeLayout detail_loadfail_rl;
    private ImageLoader imageLoader;
    private ImageView default_img;
    private boolean hasRequest;

    /**
     * 设置图像网址
     *
     * @param url
     * @param imageLoader
     */
    public void setImageUrlOfDetail(String url, ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        loadImage(url);
    }

    public DetailNetworkImage(Context context) {
        super(context);
        init(context);

    }

    public DetailNetworkImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public DetailNetworkImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化视图
     *
     * @param context
     */
    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.customview_layout_detail_img, this);
        networkImageView = (ImageView) layout.findViewById(R.id.detail_network_img);
        default_img = (ImageView) layout.findViewById(R.id.default_img);

        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        networkImageView.setLayoutParams(param);
        networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        networkImageView.setAdjustViewBounds(true);
        networkImageView.setMaxHeight(LayoutParams.MATCH_PARENT);
        networkImageView.setMaxWidth(LayoutParams.MATCH_PARENT);
        detail_loadfail_rl = (RelativeLayout) layout.findViewById(R.id.detail_loadfail_rl);

    }


    /**
     * 加载图片
     *
     * @param tmpUrl
     */
    public void loadImage(String tmpUrl) {


            imageLoader.get(tmpUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    //网络图片加载成功则显示，加载为空则显示错误提示信息
                    if (response.getBitmap() != null) {
                        networkImageView.setVisibility(View.VISIBLE);
                        default_img.setVisibility(View.GONE);
                        networkImageView.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //加载异常则显示错误提示的画面
                    default_img.setVisibility(View.GONE);
                    detail_loadfail_rl.setVisibility(View.VISIBLE);
                }
            });

    }


}
