package cn.com.custom.widgetproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.activity.ProductDetailAcitvity;
import cn.com.custom.widgetproject.adapter.ProductListAdapter;
import cn.com.custom.widgetproject.callback.LoadEvent;
import cn.com.custom.widgetproject.callback.OnCommonResultListener;
import cn.com.custom.widgetproject.callback.RefreshEvent;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.callback.ReuqstEvent;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.ProductListModel;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.JsonUtil;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.CommonCategoryImage;
import cn.com.custom.widgetproject.widget.GridViewFitScrollView;
import cn.com.custom.widgetproject.widget.LoadBallViewFragment;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * 分类通用fragment
 */
public class CommonUIFragment extends Fragment {


    ArrayList<ProductListModel.Items> productList = new ArrayList<>();

    ArrayList<SearchCategoryModel.Geners> genersArrayList;

    private CommonCategoryImage commonCategoryImage;

    private GridViewFitScrollView gridView;

    private ProductListAdapter adapter;

    int currentPage = -1;

    int requestPage = -1;

    boolean isCanRequestFlag = false;

    public static final int REQUEST_MODEL_SUCCESE = 8;

    public static final int REFRESH_MODEL_SUCCESE = 9;

    public static final int REFRESH_MODEL_FAIL = 10;

    public static final int REQUEST_MODEL_FAIL = 11;

    //请求加载更多失败
    public static final int LOADMORE_MODEL_FAIL = 12;
    //加载更多成功
    public static final int LOADMORE_MODEL_SUCCESE = 13;
    //加载更多没有了
    public static final int CAN_NOT_LOADMORE = 14;

    String fragmentId;

    OnCommonResultListener onCommonResultListener;

    private LoadBallViewFragment loadBallView;
    private LinearLayout productLayout;
    //当数据条目过少时，Xsrollview无法撑满全屏幕导致无法出现刷新和加载更多
    ArrayList<String> addModelFlagArray = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selection_common, container, false);
        commonCategoryImage = (CommonCategoryImage) rootView.findViewById(R.id.category_net_image);
        genersArrayList = JsonUtil.toObjectList(StorageManager.getGenresCategory(getActivity()), SearchCategoryModel.Geners.class);
        gridView = (GridViewFitScrollView) rootView.findViewById(R.id.gridView);
        loadBallView = (LoadBallViewFragment) rootView.findViewById(R.id.load_ball_layout);
        productLayout = (LinearLayout) rootView.findViewById(R.id.productLayout);

        if (getArguments() != null) {
            fragmentId = getArguments().getString("key");
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    boolean isCannotLoadMore;

    /**
     * 请求产品列表
     *
     * @param cateGoryCode
     */
    private void requestProductList(final int cateGoryCode) {

        isCannotLoadMore = false;
        ApiManager.getInstance(getActivity()).requestProductListModel(String.valueOf(cateGoryCode), new ResponseCallback<ProductListModel>() {

            @Override
            public void onFailure(Request request, Exception e) {
                mHandler.sendEmptyMessage(REQUEST_MODEL_FAIL);

            }

            @Override
            public void onResponse(Response response, ProductListModel model) {

                if (model != null) {
                    if (!StringUtil.isEmpty(model.next)) {
                        currentPage = Integer.parseInt(model.next);
                    } else {
                        currentPage = -1;
                        isCannotLoadMore = true;
                        mHandler.sendEmptyMessage(CAN_NOT_LOADMORE);
                    }

                    productList.clear();
                    productList.addAll(model.items);

                    if (productList.size() > 0 && productList.size() < 5) {
                        addVitrialData(model);
                    } else if (productList.size() > 4) {
                        addModelFlagArray.clear();

                    }
                    mHandler.sendEmptyMessage(REQUEST_MODEL_SUCCESE);
                } else {
                    mHandler.sendEmptyMessage(REQUEST_MODEL_FAIL);

                }

            }
        }, false);


    }

    /**
     * 刷新产品列表
     *
     * @param cateGoryCode
     * @param refresh      刷新
     * @param load         加载的标志位
     */
    private void refreshOrLoadProductList(final int cateGoryCode, final boolean refresh, final boolean load) {
        isCannotLoadMore = false;

        if (refresh) {
            requestPage = 0;
            productList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            isCanRequestFlag = true;
        } else {
            if (currentPage > 0) {
                requestPage = currentPage;
                isCanRequestFlag = true;
            } else {
                isCanRequestFlag = false;
                mHandler.sendEmptyMessage(CAN_NOT_LOADMORE);

            }
        }
        if (isCanRequestFlag) {
            ApiManager.getInstance(getActivity()).refreshProductListModel(String.valueOf(requestPage), String.valueOf(cateGoryCode), new ResponseCallback<ProductListModel>() {
                @Override
                public void onFailure(Request request, Exception e) {
                    if (refresh && !load) {
                        mHandler.sendEmptyMessage(REFRESH_MODEL_FAIL);
                    } else if (!refresh && load) {
                        mHandler.sendEmptyMessage(LOADMORE_MODEL_FAIL);
                    }
                }

                @Override
                public void onResponse(Response response, ProductListModel model) {

                    if (!StringUtil.isEmpty(model.next)) {
                        currentPage = Integer.parseInt(model.next);

                    } else {
                        currentPage = -1;
                        isCannotLoadMore = true;
                    }
                    if (model != null && model.items.size() > 0) {

                        if (refresh && !load) {
                            productList.addAll(model.items);

                            if (productList.size() > 0 && productList.size() < 5) {
                                addVitrialData(model);
                            } else if (productList.size() > 4) {
                                addModelFlagArray.clear();

                            }
                            mHandler.sendEmptyMessage(REFRESH_MODEL_SUCCESE);
                        } else if (!refresh && load) {
                            productList.addAll(model.items);
                            mHandler.sendEmptyMessage(LOADMORE_MODEL_SUCCESE);

                        }

                    } else {
                        if (refresh && !load) {
                            mHandler.sendEmptyMessage(REFRESH_MODEL_FAIL);
                        } else if (!refresh && load) {
                            mHandler.sendEmptyMessage(LOADMORE_MODEL_FAIL);
                        }
                    }

                }
            });
        }


    }

    /**
     * 添加虚拟数据，填充满scrollview
     *
     * @param model
     */
    private void addVitrialData(ProductListModel model) {
        if (productList.size() > 0 && productList.size() < 5) {
            switch (productList.size()) {
                case 1:
                    for (int i = 0; i < 4; i++) {
                        productList.add(model.items.get(0));
                    }
                    addModelFlagArray.clear();
                    addModelFlagArray.add("1");
                    addModelFlagArray.add("true");
                    break;

                case 2:
                    for (int i = 0; i < 3; i++) {
                        productList.add(model.items.get(0));
                    }
                    addModelFlagArray.clear();
                    addModelFlagArray.add("2");
                    addModelFlagArray.add("true");
                    break;

                case 3:
                    for (int i = 0; i < 2; i++) {
                        productList.add(model.items.get(0));
                    }

                    addModelFlagArray.clear();
                    addModelFlagArray.add("3");
                    addModelFlagArray.add("true");
                    break;

                case 4:

                    productList.add(model.items.get(0));
                    addModelFlagArray.clear();
                    addModelFlagArray.add("4");
                    addModelFlagArray.add("true");
                    break;


            }
        }
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            alphaAnimation.setDuration(1000);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            final int width = wm.getDefaultDisplay().getWidth();
            switch (msg.what) {


                case REQUEST_MODEL_SUCCESE://请求成功
                    loadBallView.startAnimation(alphaAnimation);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            loadBallView.setVisibility(View.INVISIBLE);
                            productLayout.setVisibility(View.VISIBLE);
                            onCommonResultListener.onCommonRequestSuccese(isCannotLoadMore);
                            commonCategoryImage.setVisibility(View.VISIBLE);
                            commonCategoryImage.setDefaultImageResId(R.drawable.img_loading);
                            if (!StringUtil.isEmpty(genersArrayList.get(Integer.parseInt(StorageManager.getTabPositionNum(getContext()))).image)) {
                                commonCategoryImage.setImageUrl(genersArrayList.get(Integer.parseInt(StorageManager.getTabPositionNum(getContext()))).image);
                            }
                            adapter = new ProductListAdapter(productList, getActivity(), addModelFlagArray);
                            adapter.setWidth(width);
                            gridView.setAdapter(adapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(), ProductDetailAcitvity.class);
                                    intent.putExtra(Constant.PRODUCT_CODE, productList.get(position).code);
                                    startActivity(intent);
                                }
                            });
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
                    requestOrRefreshFail(true, alphaAnimation);
                    break;

                case REFRESH_MODEL_SUCCESE://刷新成功
                    loadBallView.setVisibility(View.INVISIBLE);
                    commonCategoryImage.setVisibility(View.VISIBLE);
                    commonCategoryImage.setDefaultImageResId(R.drawable.img_loading);
                    if (!StringUtil.isEmpty(genersArrayList.get(Integer.parseInt(StorageManager.getTabPositionNum(getContext()))).image)) {
                        commonCategoryImage.setImageUrl(genersArrayList.get(Integer.parseInt(StorageManager.getTabPositionNum(getContext()))).image);
                    }
                    productLayout.setVisibility(View.VISIBLE);
                    adapter = null;//这里必须，不然会根据判断只notifydatachange，却显示不了界面
                    setCommonView(width);
                    onCommonResultListener.onCommonReFreshSuccese(isCannotLoadMore);

                    break;

                case REFRESH_MODEL_FAIL://刷新错误
                    requestOrRefreshFail(false, alphaAnimation);

                    break;


                case LOADMORE_MODEL_SUCCESE://加载成功
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        loadBallView.setVisibility(View.INVISIBLE);
                        onCommonResultListener.onCommonLoadMoreSuccese(isCannotLoadMore);
                    }
                    break;


                case LOADMORE_MODEL_FAIL://加载失败
                    onCommonResultListener.onCommonLoadMoreFail();

                    break;


                case CAN_NOT_LOADMORE:
                    //不能继续加载下一页
                    loadBallView.setVisibility(View.INVISIBLE);
                    onCommonResultListener.onCommonCanNotLoadMore();

                    break;


                default:
                    break;
            }


        }
    };


    private void requestOrRefreshFail(boolean isRequstFlag, AlphaAnimation alphaAnimation) {
        if (isRequstFlag) {
            onCommonResultListener.onCommonRequestFail(loadBallView);
        } else {
            onCommonResultListener.onCommonReFreshFail(loadBallView);

        }

        loadBallView.setVisibility(View.VISIBLE);
        productLayout.setVisibility(View.GONE);
        loadBallView.setLoadGoneAndErrorTipsVisiable();

    }


    private void setCommonView(int width) {
        if (adapter == null) {
            adapter = new ProductListAdapter(productList, getActivity(), addModelFlagArray);
            adapter.setWidth(width);
            gridView.setAdapter(adapter);
            if (productList.size() > 0) {
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ProductDetailAcitvity.class);
                        intent.putExtra(Constant.PRODUCT_CODE, productList.get(position).code);
                        startActivity(intent);
                    }
                });
            }
        } else if (adapter != null) { //这里为load起作用
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCommonResultListener = (OnCommonResultListener) context;
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEventAsync(final ReuqstEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEqual("REQUEST", event.mMsg)) {
                    if (StringUtil.isEqual(fragmentId, event.cateGoryCode + "")) {
                        requestProductList(event.cateGoryCode);
                    }

                }

            }


        });
    }


    @Subscribe
    public void onEventAsync(final RefreshEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEqual("Refresh", event.mMsg))
                    if (StringUtil.isEqual(fragmentId, event.cateCode + "")) {
                        refreshOrLoadProductList(event.cateCode, true, false);
                    }
            }
        });
    }

    @Subscribe
    public void onEventAsync(final LoadEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEqual("Load", event.mMsg)) {
                    if (StringUtil.isEqual(fragmentId, event.cateCode + "")) {
                        refreshOrLoadProductList(event.cateCode, false, true);
                    }
                }

            }
        });
    }
}
