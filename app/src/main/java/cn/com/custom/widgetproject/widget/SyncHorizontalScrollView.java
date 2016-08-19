package cn.com.custom.widgetproject.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.callback.TabSelcetCallback;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.DevUtil;

import java.util.ArrayList;

/**
 * 异步水平滚动条
 */
public class SyncHorizontalScrollView extends HorizontalScrollView {
    private LayoutInflater mInflater;
    private CheckableItemGroup checkableItemGroup;
    private int averTabWidth;
    int showTabCount;
    private ArrayList<SearchCategoryModel.Geners> genersArrayList;
    Context context;
    TabSelcetCallback tabSelcetCallback;
    private boolean isNeedNetUrl;

    /**
     * 构造函数区域
     *
     * @param context
     * @param attrs
     */
    public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SyncHorizontalScrollView(Context context) {
        super(context);
        init(context);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SyncHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.customview_layout_horizonsrollerview, this);
        checkableItemGroup = (CheckableItemGroup) view.findViewById(R.id.rg_nav_content);
    }

    /**
     * @param genersArrayList
     * @param tabWidth
     * @param showTabCount    默认需要显示的tab个数
     */
    public void setPara(ArrayList<SearchCategoryModel.Geners> genersArrayList, int tabWidth, int showTabCount,boolean isNeedNetUrl) {
        this.genersArrayList = genersArrayList;
        this.showTabCount = showTabCount;
        this.isNeedNetUrl = isNeedNetUrl;
        averTabWidth = tabWidth;

    }

    public void setTabSelectListner(TabSelcetCallback tabSelcetCallback) {
        this.tabSelcetCallback = tabSelcetCallback;
    }


    public void initNavigationHSV() {
        checkableItemGroup.removeAllViews();
        int count = 0;
        for (final SearchCategoryModel.Geners generses : genersArrayList) {
            final CheckableLayout checkableLayout = (CheckableLayout) mInflater.inflate(R.layout.customview_layout_tabbutton, null);
            checkableLayout.setId(count);
            ++count;
            checkableLayout.setLayoutParams(new ViewGroup.LayoutParams(averTabWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            checkableLayout.setChecked(false);
//            DevUtil.e("generses", generses.icon_highlight);
//            DevUtil.e("generses",generses.icon_default);

            checkableLayout.setpara(generses.icon_highlight, generses.icon_default, generses.name,isNeedNetUrl);
            checkableItemGroup.addView(checkableLayout);
        }
        setTabButtonChecked(0);
        setTabSelectListener();

    }


    /**
     * 设置tab按钮被选中
     *
     * @param position
     */
    public void setTabButtonChecked(int position) {
        if (checkableItemGroup != null && checkableItemGroup.getChildCount() > position) {
            ((CheckableLayout) checkableItemGroup.getChildAt(position)).performClick();

        }
    }

    /**
     * 设置tab选中监听
     */
    private void setTabSelectListener() {


        checkableItemGroup.setOnCheckedChangeListener(new CheckableItemGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableView view, @IdRes int checkedId) {
                if (view.isChecked()) {
                    if (checkableItemGroup.getChildAt(checkedId) != null) {
                        CheckableLayout childAt = (CheckableLayout) checkableItemGroup.getChildAt(checkedId);

                        if (childAt.isChecked()) {
                            tabSelcetCallback.tabbaSelcet(Integer.parseInt(genersArrayList.get(checkedId).genre), checkedId);//这里的checkId就是分类号
                        }
                        SyncHorizontalScrollView.this.smoothScrollTo(
                                (checkedId > 1 ? (childAt).getLeft() : 0) - ((CheckableLayout) checkableItemGroup.getChildAt(2)).getLeft(), 0);
                    }
                }
            }
        });
    }
}