package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;

import org.apache.commons.lang3.StringUtils;

import cn.com.custom.widgetproject.R;

/**
 * Created by custom on 2016/7/29.
 */
public class CheckableImageLayout extends RelativeLayout {
    NetworkImageView checkImg;
    NetworkImageView uncheckImg;

    public CheckableImageLayout(Context context) {
        super(context);
        init(context);

    }

    public CheckableImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public CheckableImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.customview_layout_checkable_img, this);
        checkImg = (NetworkImageView) layout.findViewById(R.id.tab_iv_check);
        uncheckImg = (NetworkImageView) layout.findViewById(R.id.tab_iv_uncheck);

    }

    public void setImageVisiable(boolean checkable, String checkUrl, String uncheckUrl, boolean isNeedNetUrl, ImageLoader imageLoader) {
        checkImg.setErrorImageRes(BitmapFactory.decodeFile(StringUtils.substringAfter(checkUrl, "@")));
        uncheckImg.setErrorImageRes(BitmapFactory.decodeFile(StringUtils.substringAfter(uncheckUrl, "@")));
        checkImg.setDefaultImageRes(BitmapFactory.decodeFile(StringUtils.substringAfter(checkUrl, "@")));
        uncheckImg.setDefaultImageRes(BitmapFactory.decodeFile(StringUtils.substringAfter(uncheckUrl, "@")));
        if (isNeedNetUrl) {
            checkImg.setImageUrl(StringUtils.substringBefore(checkUrl, "@"), imageLoader);
            uncheckImg.setImageUrl(StringUtils.substringBefore(uncheckUrl, "@"), imageLoader);
        }
        if (checkable) {
            uncheckImg.setVisibility(View.GONE);
        } else {
            uncheckImg.setVisibility(View.VISIBLE);
        }

    }
}
