package cn.com.custom.widgetproject.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.adapter.CompareShopAdapter;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.callback.TanslationHttpCallback;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.DetailItemModel;
import cn.com.custom.widgetproject.utils.DevUtil;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.CheckableView;
import cn.com.custom.widgetproject.widget.ErrorTipsView;
import cn.com.custom.widgetproject.widget.ImageViewpagerView;
import cn.com.custom.widgetproject.widget.LoadingBallView;
import cn.com.custom.widgetproject.widget.TranslateCheckableLayout;
import cn.com.custom.widgetproject.widget.XScrollView;

/**
 * 产品详情画面
 * Created by custom on 2016/6/14.
 */
public class ProductDetailAcitvity extends AppCompatActivity implements CheckableView.OnCheckedChangeListener, XScrollView.IXScrollViewListener {
    private static final int REQUEST_DETAIL_FAIL = 0;
    private static final int REQUEST_DETAIL_SUCCESE = 1;
    private static final int REREFSH_DETAIL_SUCCESE = 2;
    //图片切换控件
    private ImageViewpagerView productGallaryView;
    //比较商店列表控件
    private ListView compareListview;

    //产品详细控件
    private TextView tvProductIntrduce;
    //产品标题控件
    private TextView tvProductTitle;
    //产品详细的数据模型
    private DetailItemModel prouctDetail;
    //翻译控件
    RadioButton translateRadioButton;
    //加载小球的视图控件
    LoadingBallView loadBallView;

    //是否已经从中文翻译成了日文的标志位，默认是false
    private boolean isChinaToJapan;
    //是否已经从日文翻译成了中文的标志位，默认是false
    private boolean isJanToChina;
    //是否已经从保存翻译结果
    private RelativeLayout topPanel;

    private XScrollView mScrollView;
    private LinearLayout detailLayout;
    private TranslateCheckableLayout rl_translate;
    private ErrorTipsView errorTipsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initFindView();
        initBtnListner();
        requestDetail();

    }

    /**
     * 初始化按钮监听
     */
    private void initBtnListner() {
        findViewById(R.id.detail_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailAcitvity.this.finish();
            }
        });
        findViewById(R.id.detail_rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailAcitvity.this.finish();
            }
        });

    }


    /**
     * 初始化控件
     */
    private void initFindView() {

        topPanel = (RelativeLayout) findViewById(R.id.topPanel);
        mScrollView = (XScrollView) findViewById(R.id.detail_scrollview);

        loadBallView = (LoadingBallView) findViewById(R.id.detail_loadball);
        mScrollView.setPullLoadEnable(false);
        mScrollView.setPullRefreshEnable(true);
        mScrollView.setIXScrollViewListener(this);
        mScrollView.setActivity(this);
        mScrollView.setFootVisily(true);
        mScrollView.setFootViewGo(true);
        View content = LayoutInflater.from(this).inflate(R.layout.xscrollview_content_detail, null);
        initContentView(content);
        mScrollView.setView(content);


    }


    /**
     * 请求数据
     */
    void requestDetail() {
        ApiManager.getInstance(ProductDetailAcitvity.this).requestProductDeatilModel(getIntent().getStringExtra(Constant.PRODUCT_CODE), new ResponseCallback<DetailItemModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                mHandler.sendEmptyMessage(REQUEST_DETAIL_FAIL);
            }

            @Override
            public void onResponse(Response response, DetailItemModel model) {

                if (model != null) {
                    Message message = mHandler.obtainMessage();
                    message.obj = model;
                    message.what = REQUEST_DETAIL_SUCCESE;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(REQUEST_DETAIL_FAIL);
                }

            }
        });

    }

    /**
     * 请求刷新数据
     */
    void refreshDetail() {
        detailLayout.setVisibility(View.INVISIBLE);
        StorageManager.clearTranslationResult(this);
        ApiManager.getInstance(ProductDetailAcitvity.this).requestProductDeatilModel(getIntent().getStringExtra(Constant.PRODUCT_CODE), new ResponseCallback<DetailItemModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                mHandler.sendEmptyMessage(REQUEST_DETAIL_FAIL);
            }

            @Override
            public void onResponse(Response response, DetailItemModel model) {

                if (model != null) {
                    Message message = mHandler.obtainMessage();
                    message.obj = model;
                    message.what = REREFSH_DETAIL_SUCCESE;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(REQUEST_DETAIL_FAIL);
                }

            }
        });

    }

    /**
     * 初始化中间内容视图
     *
     * @param content
     */
    private void initContentView(View content) {
        translateRadioButton = (RadioButton) content.findViewById(R.id.translateRadioButton);
        compareListview = (ListView) content.findViewById(R.id.compareListview);
        tvProductIntrduce = (TextView) content.findViewById(R.id.tv_productIntrduce);
        errorTipsView = (ErrorTipsView) content.findViewById(R.id.error_tipsview);

        tvProductTitle = (TextView) content.findViewById(R.id.productdetail_title);
        productGallaryView = (ImageViewpagerView) content.findViewById(R.id.productGallaryView);
        detailLayout = (LinearLayout) content.findViewById(R.id.detail_layout);
        rl_translate = (TranslateCheckableLayout) content.findViewById(R.id.rl_translate);
        rl_translate.setOnCheckedChangeListener(this);
        rl_translate.setChecked(false);
    }


    /**
     * handler更新UI
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            alphaAnimation.setDuration(2000);
            prouctDetail = (DetailItemModel) msg.obj;
            switch (msg.what) {
                case REQUEST_DETAIL_FAIL:
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int height = wm.getDefaultDisplay().getHeight();
                    FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) errorTipsView.getLayoutParams();
                    lp2.height = height - topPanel.getMeasuredHeight() - 40;
                    errorTipsView.setLayoutParams(lp2);
                    errorTipsView.requestLayout();
                    loadBallView.setVisibility(View.GONE);
                    errorTipsView.setVisibility(View.VISIBLE);
                    detailLayout.setVisibility(View.INVISIBLE);
                    errorTipsView.setLoadGoneAndErrorTipsVisiable();
                    mScrollView.stopRefresh();

                    break;

                case REQUEST_DETAIL_SUCCESE:
                    if (prouctDetail != null) {
                        initProductGalleryView(prouctDetail);
                        initDetail(prouctDetail);
                    }


                    loadBallView.startAnimation(alphaAnimation);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            loadBallView.setVisibility(View.GONE);
                            detailLayout.setVisibility(View.VISIBLE);
                            mScrollView.stopRefresh();

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }


                    });


                    break;

                case REREFSH_DETAIL_SUCCESE:

                    errorTipsView.setVisibility(View.GONE);
                    detailLayout.setVisibility(View.VISIBLE);
                    if (prouctDetail != null) {
                        initProductGalleryView(prouctDetail);
                        initDetail(prouctDetail);
                    }

                    mScrollView.stopRefresh();
                    break;

                default:
                    break;


            }

        }
    };


    /**
     * 初始化详细页面的信息
     *
     * @param prouctDetail
     */

    private void initDetail(DetailItemModel prouctDetail) {
        //比价商店集合
        ArrayList<DetailItemModel.Item.Shops> shopses = new ArrayList<>();
        for (int i = 0; i < prouctDetail.item.shops.size(); i++) {
            shopses.add(prouctDetail.item.shops.get(i));
        }

        tvProductTitle.setText(Html.fromHtml(prouctDetail.item.name));
        compareListview.setAdapter(new CompareShopAdapter(ProductDetailAcitvity.this, shopses));
        tvProductIntrduce.setText(prouctDetail.item.detail);
        if (StringUtil.isEqual("cn", prouctDetail.item.language)) {
            rl_translate.setVisibility(View.INVISIBLE);
        } else {
            rl_translate.setVisibility(View.VISIBLE);
        }
        detailLayout.setVisibility(View.VISIBLE);
        loadBallView.setVisibility(View.GONE);


    }


    /**
     * 初始化产品图册切换视图控件
     *
     * @param prouctDetail
     */
    private void initProductGalleryView(DetailItemModel prouctDetail) {


        productGallaryView.setLoadViewName(false);
        List<String> bannerList = new ArrayList<>();

        bannerList.add("http://p0.so.qhmsg.com/bdr/_240_/t016013b0d70e221a30.png");
        bannerList.add("");
        bannerList.add("http://p4.so.qhmsg.com/bdr/_240_/t014a150e180202e074.jpg");
        for (int i = 0; i < prouctDetail.item.images.size(); i++) {
            bannerList.add(prouctDetail.item.images.get(i));
        }


        productGallaryView.setAutoPlayAble(false);
        productGallaryView.setImagesUrl(bannerList);


    }


    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDetail();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onScrollGetVerticalValue(int scrollYvalue) {

    }

    /**
     * 点击翻译按钮进行中英文切换翻译,如果内容是中文翻译按钮是不显示的，如果简介是日文，翻译成中文
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CheckableView buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            if (prouctDetail != null) {
                if (!StringUtil.isEmpty(prouctDetail.item.detail) && StringUtil.isEqual("jp", prouctDetail.item.language)) {

                    if (StringUtil.isEmpty(StorageManager.getTranslationResult(ProductDetailAcitvity.this, prouctDetail.item.code))) {
                        String str = prouctDetail.item.detail.replace("\n", "|");
                        try {
                            ApiManager.translate(str, "jp", "zh", new TanslationHttpCallback() {
                                @Override
                                public void onTranslateSuccess(String result) {
                                    if (!StringUtil.isEqual(StorageManager.getTranslationResult(ProductDetailAcitvity.this, prouctDetail.item.code), result)) {
                                        StorageManager.saveTranslationResult(ProductDetailAcitvity.this, result, prouctDetail.item.code);
                                    }
                                    tvProductIntrduce.setText(result.toString());
                                }

                                @Override
                                public void onTranslateFailure(String exception) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        DevUtil.e("fdsf", "fdsfds");
                        tvProductIntrduce.setText(StorageManager.getTranslationResult(ProductDetailAcitvity.this, prouctDetail.item.code));

                    }
                }


            }
        } else {
            tvProductIntrduce.setText(prouctDetail.item.detail);
        }
    }
}