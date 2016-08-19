package cn.com.custom.widgetproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.activity.ProductDetailAcitvity;
import cn.com.custom.widgetproject.adapter.RecommendProductListAdapter;
import cn.com.custom.widgetproject.callback.OnRecommondResultListener;
import cn.com.custom.widgetproject.callback.RefreshEvent;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.callback.ReuqstEvent;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.ProductListModel;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.DevUtil;
import cn.com.custom.widgetproject.utils.DisplayUtil;
import cn.com.custom.widgetproject.utils.JsonUtil;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.GridViewFitScrollView;
import cn.com.custom.widgetproject.widget.LoadBallViewFragment;
import cn.com.custom.widgetproject.widget.RecomondeCategoryImage;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 推荐碎片
 */
public class RedcommendUIFragment extends Fragment {


    HashMap<String, ArrayList<ProductListModel.Items>> arrayListHashMap;//各个分类产品的集合
    private OnRecommondResultListener onRecomondResultListener;//刷新加载监听接口
    ArrayList<SearchCategoryModel.Geners> genersArrayList;//分类集合
    public static final int REQUEST_MODEL_SUCCESE = 1;

    public static final int REFRESH_MODEL_SUCCESE = 2;

    public static final int REQUEST_MODEL_NULL_DATA = 3;

    public static final int REFRESH_MODEL_NULL_DATA = 4;

    public static final int REFRESH_MODEL_FAIL = 5;

    public static final int REQUEST_MODEL_FAIL = 6;


    //加载进度球
    private LoadBallViewFragment loadView;
    //加载失败提示界面
    LinearLayout.LayoutParams param_line;

    private LinearLayout productLayout;

    ProductListModel model;

    private String fragmentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selection_recommend, container, false);
        productLayout = (LinearLayout) rootView.findViewById(R.id.productLayout);
        loadView = (LoadBallViewFragment) rootView.findViewById(R.id.loadingBallView);

        if (getArguments() != null) {
            fragmentId = getArguments().getString("recommendFragment");
        }
        genersArrayList = JsonUtil.toObjectList(StorageManager.getGenresCategory(getActivity()), SearchCategoryModel.Geners.class);

        arrayListHashMap = new HashMap<>();

        if (Constant.isFirstLoadActivity) {
            requstProductList();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 请求产品数据
     */
    private void requstProductList() {

        arrayListHashMap.clear();
        ApiManager.getInstance(getActivity()).requestProductListModel("0", new ResponseCallback<ProductListModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                mHandler.sendEmptyMessage(REQUEST_MODEL_FAIL);
            }

            @Override
            public void onResponse(Response response, ProductListModel model) {
                if (model.items.size() > 0) {
                    for (int i = 0; i < model.items.size(); i++) {

                        if (!arrayListHashMap.containsKey(model.items.get(i).genre)) {
                            arrayListHashMap.put(model.items.get(i).genre, new ArrayList<ProductListModel.Items>());
                        }
                        if (arrayListHashMap.containsKey(model.items.get(i).genre)) {
                            arrayListHashMap.get(model.items.get(i).genre).add(model.items.get(i));
                        }
                    }

                    Message message = mHandler.obtainMessage();
                    message.obj = model;
                    message.what = REQUEST_MODEL_SUCCESE;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(REQUEST_MODEL_NULL_DATA);
                }
            }
        }, true);
    }

    /**
     * 刷新产品请求
     */
    private void refreshProductList() {
        productLayout.setVisibility(View.INVISIBLE);
        ApiManager.getInstance(getActivity()).requestProductListModel("0", new ResponseCallback<ProductListModel>() {
            @Override
            public void onFailure(Request request, Exception e) {

                mHandler.sendEmptyMessage(REFRESH_MODEL_FAIL);
            }

            @Override
            public void onResponse(Response response, ProductListModel model) {
                arrayListHashMap.clear();

                if (model.items.size() > 0) {

                    for (int i = 0; i < model.items.size(); i++) {
                        if (!arrayListHashMap.containsKey(model.items.get(i).genre)) {
                            arrayListHashMap.put(model.items.get(i).genre, new ArrayList<ProductListModel.Items>());
                        }

                        if (arrayListHashMap.containsKey(model.items.get(i).genre)) {
                            arrayListHashMap.get(model.items.get(i).genre).add(model.items.get(i));
                        }
                    }

                    Message message = mHandler.obtainMessage();
                    message.obj = model;
                    message.what = REFRESH_MODEL_SUCCESE;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(REFRESH_MODEL_NULL_DATA);
                }
            }

        }, true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onRecomondResultListener = (OnRecommondResultListener) context;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            model = (ProductListModel) msg.obj;
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            alphaAnimation.setDuration(1000);
            switch (msg.what) {


                case REQUEST_MODEL_SUCCESE:

                    //请求成功

                    loadView.startAnimation(alphaAnimation);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            loadView.setVisibility(View.INVISIBLE);
                            productLayout.setVisibility(View.VISIBLE);
                            if (model != null) {
                                setFragmentView(model);
                            }

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                    });

                    break;

                case REQUEST_MODEL_FAIL:
                case REQUEST_MODEL_NULL_DATA:
                    requestOrRefreshFail(true, alphaAnimation);

                    break;
                case REFRESH_MODEL_SUCCESE:
                    loadView.setVisibility(View.INVISIBLE);
                    productLayout.setVisibility(View.VISIBLE);
                    if (model != null) {
                        setFragmentView(model);
                    }else {
                        DevUtil.e("REFRESH_MODEL_SUCCESE","REFRESH_MO99999DEL_SUCCESE");

                    }

                    onRecomondResultListener.onReCommondReFreshSuccese();


                    break;
                case REFRESH_MODEL_NULL_DATA:
                case REFRESH_MODEL_FAIL:
                    requestOrRefreshFail(false, alphaAnimation);

                    break;

                default:
                    break;


            }

        }
    };

    private void requestOrRefreshFail(boolean flag, AlphaAnimation alphaAnimation) {
        if (flag) {
            onRecomondResultListener.onReCommondRequestFail(loadView);
        } else {
            onRecomondResultListener.onReCommondReFreshFail(loadView);

        }

        loadView.setVisibility(View.VISIBLE);
        productLayout.setVisibility(View.GONE);
        loadView.setLoadGoneAndErrorTipsVisiable();


    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 订阅请求
     *
     * @param event
     */
    @Subscribe
    public void onEventAsync(final ReuqstEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEqual("REQUEST_RECOMMEND", event.mMsg)) {
                    if (StringUtil.isEqual(fragmentId, event.cateGoryCode + "")) {
                        requstProductList();
                    }
                }
            }
        });
    }

    /**
     * 订阅刷新
     *
     * @param event
     */
    @Subscribe
    public void onEventAsync(final RefreshEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEqual("Refresh", event.mMsg)) {
                    if (StringUtil.isEqual(fragmentId, event.cateCode + "")) {
                        refreshProductList();
                    }
                }

            }
        });
    }


    /**
     * 请求数据成功
     */
    private void setFragmentView(final ProductListModel model) {
        productLayout.removeAllViews();
        int count=1;
        param_line = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2Px(getActivity(), 7));
        for (final SearchCategoryModel.Geners geners:genersArrayList) {
            if (null != arrayListHashMap && arrayListHashMap.size() > 0) {
                if (arrayListHashMap.containsKey(geners.genre)) {
                    final HashMap<String,Integer> nums=new HashMap<>();
                    nums.put(geners.genre, count);
                    count++;
                    View lineView = new View(getActivity());
                    lineView.setBackgroundColor(getResources().getColor(R.color.topagelistbg));
                    lineView.setLayoutParams(param_line);
                    productLayout.addView(lineView);
                    RecomondeCategoryImage recomondeCategoryImage = new RecomondeCategoryImage(getContext());
                    recomondeCategoryImage.setDefaultImageResId(R.drawable.img_loading);
                    recomondeCategoryImage.setImageUrl(geners.image);
                    recomondeCategoryImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DevUtil.e("zhixing",nums.get(geners.genre)+"pppp");
                            onRecomondResultListener.onReCommondTabSelect(nums.get(geners.genre));
                        }
                    });
                    productLayout.addView(recomondeCategoryImage);
                    final GridViewFitScrollView gridView = new GridViewFitScrollView(getActivity());
                    gridView.setNumColumns(3);
                    gridView.setVerticalSpacing(DisplayUtil.px2Dip(getActivity(), 60));
                    gridView.setAdapter(new RecommendProductListAdapter(arrayListHashMap.get(geners.genre), getActivity()));
                    productLayout.addView(gridView);
                    View footlineView = new View(getActivity());
                    footlineView.setBackgroundColor(getResources().getColor(R.color.topagelistbg));
                    footlineView.setLayoutParams(param_line);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ProductDetailAcitvity.class);
                            DevUtil.e("position",position+"");
                            intent.putExtra(Constant.PRODUCT_CODE, arrayListHashMap.get(geners.genre).get(position).code);
                            startActivity(intent);
                        }
                    });
                }
            }//for循环到此结束


        }


    }
}
