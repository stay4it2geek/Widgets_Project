package cn.com.custom.widgetproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import cn.com.custom.widgetproject.R;

import cn.com.custom.widgetproject.adapter.TabFragmentPagerAdapter;
import cn.com.custom.widgetproject.callback.LoadEvent;
import cn.com.custom.widgetproject.callback.OnCommonResultListener;
import cn.com.custom.widgetproject.callback.OnRecommondResultListener;
import cn.com.custom.widgetproject.callback.RefreshEvent;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.callback.ReuqstEvent;
import cn.com.custom.widgetproject.callback.TabSelcetCallback;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.fragment.CommonUIFragment;
import cn.com.custom.widgetproject.fragment.RedcommendUIFragment;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.BannerListModel;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.DevUtil;
import cn.com.custom.widgetproject.utils.DisplayUtil;
import cn.com.custom.widgetproject.utils.JsonUtil;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.AutoFitSrollerViewViewPager;
import cn.com.custom.widgetproject.widget.ImageViewpagerView;
import cn.com.custom.widgetproject.widget.LoadBallViewFragment;
import cn.com.custom.widgetproject.widget.SyncHorizontalScrollView;
import cn.com.custom.widgetproject.widget.XScrollView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页TopPage画面
 * Created by custom on 2016/6/7.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabSelcetCallback, XScrollView.IXScrollViewListener, OnRecommondResultListener, OnCommonResultListener {
    //无数据
    private static final int NONE_BANNER_DATA = 21;
    //有数据
    private static final int HASE_BANNER_DATA = 20;
    // 广告自动轮播控件
    private ImageViewpagerView bannerView;

    //异步横向滚动tab切换控件
    private SyncHorizontalScrollView mScrollTabView;
    //分类集合
    private ArrayList<SearchCategoryModel.Geners> genersArrayList;
    //滑动画面控件
    private AutoFitSrollerViewViewPager mProductViewpager;
    //tab布局与其父类布局的顶部距离
    private int tabLayoutTop;
    //有banner的tab布局与其父类布局的顶部距离
    private int tabBannerLayoutTop;
    //屏幕顶部悬浮相对性布局
    private RelativeLayout topFloatRelativelayout;
    //tab的线性布局
    private LinearLayout tabLinearLayout;
    //轮播广告的布局
    private FrameLayout bannerlayout;
    //tabFragment的页面适配器
    private TabFragmentPagerAdapter mAdapter;
    //刷新加载自定义控件
    private XScrollView mScrollView;
    //没有广告数据标志位
    boolean noBannerData = false;
    //tab显示的个数
    private int showTabCount = 5;
    //tab的宽度
    private int tabWidth = 0;
    //生成碎片集合
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    //生成banner集合
    private ArrayList<BannerListModel.Banner> bannerArrayList;
    //生成推荐碎片
    private RedcommendUIFragment recommendFragment = new RedcommendUIFragment();
    //顶部
    private LinearLayout topPanel;
    private boolean isNeedNetUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toppage);
        Constant.isFirstLoadActivity = true;
        genersArrayList = JsonUtil.toObjectList(StorageManager.getTopPageNeedGenres(MainActivity.this), SearchCategoryModel.Geners.class);
        initView();
        setViewListner();
        initViewpagerAndFragment();
        requestBanner();
        tabWidth = DisplayUtil.dip2Px(this, 60);
        isNeedNetUrl = getIntent().getBooleanExtra("isNeedNetUrl", false);
        mScrollTabView.setPara(genersArrayList, tabWidth, showTabCount, isNeedNetUrl);
        mScrollTabView.initNavigationHSV();
        tabLinearLayout.setVisibility(View.VISIBLE);
        initTabNumStorage();


    }

    /**
     * 初始化tab分类号存储
     */
    private void initTabNumStorage() {
        StorageManager.clearNum(this, Constant.CATEGORY_SELECT_NUM);
        StorageManager.clearNum(this, Constant.TAB_SELECT_POSITION);
        StorageManager.saveTabCategoryNum(this, 0);
        StorageManager.saveTabPositionNum(this, 0);
    }

    /**
     * 初始化滑动控件和碎片界面数据
     */
    private void initViewpagerAndFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("recommendFragment", "0");
        recommendFragment.setArguments(bundle);
        mFragmentList.add(recommendFragment);
        for (int i = 0; i < genersArrayList.size() - 1; i++) {
            CommonUIFragment commonUIFragment = new CommonUIFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString("key", genersArrayList.get(i + 1).genre + "");
            commonUIFragment.setArguments(bundle2);
            mFragmentList.add(commonUIFragment);
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mAdapter.setList(mFragmentList);
        mProductViewpager.setAdapter(mAdapter);


        /**
         * 添加页面切换监听
         */
        mProductViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DevUtil.e("zhixing", "22test");

                mScrollTabView.setTabButtonChecked(position);
            }

            /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
            arg0 ==1的时辰默示正在滑动，
            arg0==2的时辰默示滑动完毕了，
            arg0==0的时辰默示什么都没做。*/
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 初始化界面控件
     */
    private void initView() {
        initParentContainerView();
        initContentView();

    }

    private void initParentContainerView() {
        mScrollView = (XScrollView) findViewById(R.id.scrollViewContainViewpager);
        topPanel = (LinearLayout) findViewById(R.id.topPanel);
        topFloatRelativelayout = (RelativeLayout) findViewById(R.id.topFloatRelativelayout);
    }

    private void initContentView() {
        View content = LayoutInflater.from(this).inflate(R.layout.xscrollview_content_toppage, null);
        bannerView = (ImageViewpagerView) content.findViewById(R.id.widget_bannerview);
        mProductViewpager = (AutoFitSrollerViewViewPager) content.findViewById(R.id.productViewpager);
        mScrollTabView = (SyncHorizontalScrollView) content.findViewById(R.id.mHsv);
        tabLinearLayout = (LinearLayout) content.findViewById(R.id.tabLinearLayout);
        bannerlayout = (FrameLayout) content.findViewById(R.id.bannerlayout);
        mScrollView.setView(content);
        mScrollView.setFootViewGo(true);
    }


    /**
     * 给控件设置监听
     */
    private void setViewListner() {
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);
        mScrollView.setPullRefreshEnable(true);
        mScrollView.setPullLoadEnable(true);
        mScrollView.setActivity(this);
        mScrollView.setIXScrollViewListener(this);
        mScrollTabView.setTabSelectListner(this);
    }


    List<String> urlList = new ArrayList<>();

    /**
     * 初始化轮播广告视图
     *
     * @param bannerDataList
     */
    private void initBannerView(final ArrayList<BannerListModel.Banner> bannerDataList) {
        List<String> bannerList = new ArrayList<>();

        bannerList.add("http://img4.imgtn.bdimg.com/it/u=3018903864,2610851834&fm=15&gp=0.jpg");
        bannerList.add("http://s1.sinaimg.cn/bmiddle/0022uBKXzy6Yod6uk7Df9");
        bannerList.add("http://n.sinaimg.cn/fashion/transform/20160112/3oJw-fxnkkux1197870.gif");
//        for (int i = 0; i < bannerDataList.size(); i++) {
//            bannerList.add(bannerDataList.get(i).image);
//            urlList.add(bannerDataList.get(i).url);
//        }
        bannerView.setImagesUrl(bannerList);

        bannerView.setOnItemClickListener(new ImageViewpagerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra(Constant.WEB_URL, "http://baike.baidu.com/link?url=8RD_3J8ua3v4qaQ1Sx1iOwSL07_WoH1Xrvv8K2IJa-JT6hbc2O-_PaC9FiHLWt_VeS3aCg2jPIbGFdxTjZ0GhK");
                startActivity(intent);


            }


        });

    }

    /**
     * 请求轮播广告图的网络数据并通过Handler消息机制更新UI
     */
    private void requestBanner() {
        ApiManager.getInstance(MainActivity.this).requestBannerModel(new ResponseCallback<BannerListModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(NONE_BANNER_DATA);
                noBannerData = true;
            }

            @Override
            public void onResponse(Response response, BannerListModel model) {

                Message message = mHandler.obtainMessage();
                message.obj = model.banners;
                message.what = HASE_BANNER_DATA;
                if (model.banners.size() == 0 || model.banners.size() < 0) {
                    noBannerData = true;
                } else {
                    noBannerData = false;

                }
                mHandler.sendMessage(message);
            }
        });
    }

    /**
     * 设置banner可见性
     */

    private void showBannerVisily() {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        bannerlayout.startAnimation(mShowAction);
        if (noBannerData) {
            bannerlayout.setVisibility(View.GONE);
        } else {
            bannerlayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重新生成fragment 保证界面数据独立性
     *
     * @param position
     */
    private void initFragmentInViewpager(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(mFragmentList.get(position));
        mAdapter = null;
        Constant.isFirstLoadActivity = false;
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mAdapter.setList(mFragmentList);
        mProductViewpager.setAdapter(mAdapter);
        //设置当前画面
        mProductViewpager.setCurrentItem(position);
        //设置viewpager的缓存个数
        mProductViewpager.setOffscreenPageLimit(genersArrayList.size());
    }


    /**
     * handler更新UI
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NONE_BANNER_DATA:
                    bannerlayout.setVisibility(View.GONE);
                    break;

                case HASE_BANNER_DATA:
                    bannerlayout.setVisibility(View.VISIBLE);
                    bannerArrayList = (ArrayList<BannerListModel.Banner>) msg.obj;


//                    BannerListModel.Banner banner = new BannerListModel.Banner();
//                    banner.image = "";
//                    banner.url = "";
//                    bannerArrayList.add(banner);
                    initBannerView(bannerArrayList);


                    break;


                default:
                    break;

            }

        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            if (bannerlayout.getVisibility() == View.VISIBLE) {
                tabBannerLayoutTop = bannerlayout.getBottom();//获取bannerlayout的底部位置
            } else {
                tabBannerLayoutTop = topFloatRelativelayout.getTop();//获取topFloatRelativelayout的顶部位置

            }
            tabLayoutTop = topFloatRelativelayout.getTop();//获取topFloatRelativelayout的顶部位置
        }

    }


    @Override
    public void tabbaSelcet(int catatagory, int position) {
        if (mScrollTabView.getParent() != tabLinearLayout) {
            topFloatRelativelayout.removeView(mScrollTabView);
            topFloatRelativelayout.setVisibility(View.GONE);
            tabLinearLayout.addView(mScrollTabView);
        }
        mScrollView.stopRefresh();
        mScrollView.stopLoadMore(false);

        //如果是0即推荐分类，显示广告view  如果是其他分类，隐藏广告view
        if (0 == position) {

            showBannerVisily();
            mScrollView.setFootVisily(false);
            mScrollView.setFootViewGo(true);
        } else {

            bannerlayout.setVisibility(View.GONE);
            mScrollView.setFootVisily(true);
            mScrollView.setFootViewGo(false);

        }


        initFragmentInViewpager(position);
        //保存分类号
        StorageManager.saveTabCategoryNum(MainActivity.this, catatagory);
        //保存分类tab的位置
        StorageManager.saveTabPositionNum(MainActivity.this, position);
        DevUtil.e("zhixing2", "" + StorageManager.getTabPositionNum(MainActivity.this));

        // 分开进行请求
        if (StringUtil.isEqual("0", String.valueOf(catatagory))) {
            EventBus.getDefault().post(new ReuqstEvent("REQUEST_RECOMMEND", catatagory));
        } else {
            EventBus.getDefault().post(new ReuqstEvent("REQUEST", catatagory));
        }
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan://扫描按钮
                startActivity(new Intent(this, ScancodeActivity.class));
                break;
            case R.id.about://关于按钮
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.search://检索按钮
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;

        }
    }

    @Override
    protected void onStop() {

        super.onStop();

    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    public void onRefresh() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {

                /**
                 * 请求刷新产品
                 */
                if (!StringUtil.isEmpty(StorageManager.getTabCateGoryNum(MainActivity.this))) {
                    EventBus.getDefault().post(new RefreshEvent("Refresh", Integer.parseInt(StorageManager.getTabCateGoryNum(MainActivity.this))));   //通过EventBus发送请求事件
                }


            }
        }.sendEmptyMessageDelayed(0, 500);


    }

    @Override
    public void onLoadMore() {
/**
 * 请求加载更多产品
 */
        if (!StringUtil.isEmpty(StorageManager.getTabCateGoryNum(MainActivity.this)) && !StringUtil.isEqual("0", StorageManager.getTabCateGoryNum(MainActivity.this))) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    EventBus.getDefault().post(new LoadEvent("Load", Integer.parseInt(StorageManager.getTabCateGoryNum(MainActivity.this))));   //通过EventBus发送请求事件
                }
            }.sendEmptyMessageDelayed(0, 500);

        }
    }


    /**
     * 滚动的回调方法，当滚动的Y距离大于或者等于 tab布局距离父类布局顶部的位置，就显示tab的悬浮框
     * 当滚动的Y的距离小于 tab布局距离父类布局顶部的位置加上tab布局的高度就移除tab的悬浮框
     * 监听滚动Y值变化，通过addView和removeView来实现悬停效果
     */

    @Override
    public void onScrollGetVerticalValue(int scrollYvalue) {

        if (StringUtil.isEqual("0", StorageManager.getTabCateGoryNum(MainActivity.this))) {

//            DevUtil.e("scrollYvalue", scrollYvalue + "==" + tabBannerLayoutTop);

            if (scrollYvalue > tabBannerLayoutTop) {
                if (mScrollTabView.getParent() != topFloatRelativelayout) {
                    tabLinearLayout.removeView(mScrollTabView);
                    topFloatRelativelayout.setVisibility(View.VISIBLE);
                    topFloatRelativelayout.addView(mScrollTabView);

                }

            } else if (scrollYvalue < tabBannerLayoutTop || scrollYvalue == tabBannerLayoutTop) {

                if (mScrollTabView.getParent() != tabLinearLayout) {
                    topFloatRelativelayout.removeView(mScrollTabView);
                    topFloatRelativelayout.setVisibility(View.GONE);
                    tabLinearLayout.addView(mScrollTabView);
                }
            }
        } else {
//            DevUtil.e("scrolddlYvalue", scrollYvalue + "==" + tabLayoutTop);

            if (scrollYvalue > tabLayoutTop) {
                if (mScrollTabView.getParent() != topFloatRelativelayout) {
                    tabLinearLayout.removeView(mScrollTabView);
                    topFloatRelativelayout.setVisibility(View.VISIBLE);
                    topFloatRelativelayout.addView(mScrollTabView);
                }

            } else if (scrollYvalue < tabLayoutTop || scrollYvalue == tabLayoutTop) {
                if (mScrollTabView.getParent() != tabLinearLayout) {
                    topFloatRelativelayout.removeView(mScrollTabView);
                    topFloatRelativelayout.setVisibility(View.GONE);
                    tabLinearLayout.addView(mScrollTabView);
                }
            }
        }
    }


    /**
     * 通用fragment请求回调区域
     */

    @Override
    public void onCommonRequestSuccese(boolean isCannotLoadMore) {
        mScrollView.stopLoadMore(isCannotLoadMore);
        mScrollView.setFootViewGo(false);
    }


    @Override
    public void onCommonRequestFail(LoadBallViewFragment loadingBallView) {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) loadingBallView.getLayoutParams();
        lp2.height = height - tabLinearLayout.getMeasuredHeight() - topPanel.getMeasuredHeight() - 50;
        loadingBallView.setLayoutParams(lp2);
        loadingBallView.requestLayout();

        mScrollView.stopRefresh();
        mScrollView.setFootViewGo(true);
    }


    @Override
    public void onCommonReFreshSuccese(boolean isCannotLoadMore) {
        mScrollView.stopRefresh();
        mScrollView.setFootViewGo(false);
        mScrollView.stopLoadMore(isCannotLoadMore);

    }

    @Override
    public void onCommonReFreshFail(LoadBallViewFragment loadingBallView) {
        mScrollView.setFootViewGo(true);
        mScrollView.stopLoadMore(false);
        mScrollView.stopRefresh();


        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) loadingBallView.getLayoutParams();
        lp2.height = height - tabLinearLayout.getMeasuredHeight() - topPanel.getMeasuredHeight() - 40;
        loadingBallView.setLayoutParams(lp2);
        loadingBallView.requestLayout();

    }


    @Override
    public void onCommonLoadMoreSuccese(boolean isCanNotLoadMore) {
        mScrollView.setFootViewGo(false);
        mScrollView.stopLoadMore(isCanNotLoadMore);
    }

    @Override
    public void onCommonLoadMoreFail() {
        mScrollView.stopLoadMore(false);
        mScrollView.setFootViewGo(false);

    }

    @Override
    public void onCommonCanNotLoadMore() {
        mScrollView.stopLoadMore(true);

    }


    /***
     * 推荐页请求回调区域
     */

    @Override
    public void onReCommondRequestFail(LoadBallViewFragment loadingBallView) {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) loadingBallView.getLayoutParams();
        DevUtil.e("height", bannerlayout.getMeasuredHeight() + tabLinearLayout.getMeasuredHeight() + "");
        lp2.height = height - bannerlayout.getMeasuredHeight() - tabLinearLayout.getMeasuredHeight() - 50;
        loadingBallView.setLayoutParams(lp2);
        loadingBallView.requestLayout();

    }


    @Override
    public void onReCommondTabSelect(int position) {
        if (mAdapter != null) {
            mProductViewpager.setCurrentItem(position);
        }
    }

    @Override
    public void onReCommondReFreshSuccese() {
        mScrollView.setFootViewGo(true);
        mScrollView.stopLoadMore(false);
        mScrollView.stopRefresh();
    }

    @Override
    public void onReCommondRequestSuccese() {
        mScrollView.setFootViewGo(true);
        mScrollView.stopLoadMore(false);
        mScrollView.stopRefresh();
    }


    @Override
    public void onReCommondReFreshFail(LoadBallViewFragment loadingBallView) {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) loadingBallView.getLayoutParams();
        lp2.height = height - bannerlayout.getMeasuredHeight() - tabLinearLayout.getMeasuredHeight() + 40;
        loadingBallView.setLayoutParams(lp2);
        loadingBallView.requestLayout();
        mScrollView.stopRefresh();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DevUtil.e("isNeedNetUrl", isNeedNetUrl + "1231");
        if (isNeedNetUrl) {
            StorageManager.clearGenresCategory(MainActivity.this);
            StorageManager.clearTimeTamp(MainActivity.this);
        }
    }
}
