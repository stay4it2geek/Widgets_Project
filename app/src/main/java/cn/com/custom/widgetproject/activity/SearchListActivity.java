package cn.com.custom.widgetproject.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.adapter.ProductListAdapter;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.ProductListModel;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.ErrorTipsView;
import cn.com.custom.widgetproject.widget.GridViewFitScrollView;
import cn.com.custom.widgetproject.widget.HistorySearchDBhelper;
import cn.com.custom.widgetproject.widget.LoadingBallView;
import cn.com.custom.widgetproject.widget.XScrollView;

/**
 * 检索列表画面
 * Created by custom on 2016/6/14.
 */
public class SearchListActivity extends AppCompatActivity implements XScrollView.IXScrollViewListener {
    //检索数据失败
    private static final int SEARCH_MODEL_FAIL = 0;
    //检索数据成功
    private static final int SEARCH_MODEL_SUCCESE = 1;
    //请求刷新失败
    private static final int REFRESH_MODEL_FAIL = 2;
    //请求加载更多失败
    private static final int LOADMORE_MODEL_FAIL = 3;
    //刷新成功
    private static final int REFRESH_MODEL_SUCCESE = 4;
    //加载更多成功
    private static final int LOADMORE_MODEL_SUCCESE = 5;
    //加载更多 下一页木有了
    private static final int CANNOT_LOADMORE_LISTDATA = 6;
    //检索到的数据为空
    private static final int SEARCH_MODEL_NULL_FAIL = 7;
    //检索编辑框
    private EditText searchEditText;
    //产品列表控件
    private GridViewFitScrollView productGridView;
    //可写数据库
    private SQLiteDatabase dbWrite;
    //产品列表适配器
    private ProductListAdapter adapter;
    //下拉刷新上拉加载控件
    private XScrollView mScrollView;
    //清除按钮
    private ImageView search_clear;
    //加载跳动球界面
    private LoadingBallView loadBallView;
    //产品数据对象集合
    ArrayList<ProductListModel.Items> productList = new ArrayList<>();
    //当数据条目过少时，Xsrollview无法撑满全屏幕导致无法出现刷新和加载更多
    ArrayList<String> addModelFlagArray = new ArrayList<>();
    //请求页码
    int requestPage = -1;
    //当前请求页码
    int currentPage = -1;
    //能够进行刷新的标志位
    private boolean isCanRequestFlag = false;
    //不能再加载下一页标志位，默认可以进行加载下一页
    boolean isCanNotLoadMore = false;
    //检索列表视图
    private LinearLayout searchListProductLayout;
    //顶部条
    private LinearLayout topPanel;
    private ErrorTipsView serachListErrortipview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlist);
        initViewAndListner();

    }


    /**
     * 初始化视图和监听,xScollview可以上拉下拉
     */
    private void initViewAndListner() {

        dbWrite = HistorySearchDBhelper.getInstance(this).getWritableDatabase();   //可写数据库
        topPanel = (LinearLayout) findViewById(R.id.topPanel);
        topPanlBackAreaAndBtnListner();
        loadBallView = (LoadingBallView) findViewById(R.id.serachList_loadball);

        mScrollView = (XScrollView) findViewById(R.id.serachList_scollview);
        mScrollView.setPullRefreshEnable(true);
        mScrollView.setPullLoadEnable(true);
        mScrollView.setIXScrollViewListener(this);
        mScrollView.setActivity(this);
        mScrollView.setFootVisily(true);

        View content = LayoutInflater.from(this).inflate(R.layout.xscrollview_content_searchlist, null);
        initContentView(content);
        mScrollView.setView(content);

        initSearchEditextListnerAndSearchData();
    }

    /**
     * 初始化XScrollview中间内容视图
     */
    private void initContentView(View content) {
        productGridView = (GridViewFitScrollView) content.findViewById(R.id.searchlist_gridView);
        serachListErrortipview = (ErrorTipsView) content.findViewById(R.id.serachList_errortipview);
        searchListProductLayout = (LinearLayout) content.findViewById(R.id.searchlist_productLayout);

    }

    /**
     * 初始话检索编辑框并进行检索
     */
    private void initSearchEditextListnerAndSearchData() {
        search_clear = (ImageView) findViewById(R.id.search_clear);
        searchEditText = (EditText) findViewById(R.id.et_search);

        searchEditText.setText(this.getIntent().getStringExtra("searchKeyword"));
        searchEditText.clearFocus();
        searchEditText.setCursorVisible(false);        /**
         *   清除按钮的显示逻辑
         */

        if (!StringUtil.isEmpty(searchEditText.getText().toString())) {
            search_clear.setVisibility(View.VISIBLE);
        } else {
            search_clear.setVisibility(View.INVISIBLE);
        }
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.requestFocus();
                searchEditText.setCursorVisible(true);

            }
        });
        // 清除按钮的点击逻辑
        search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.requestFocus();
                searchEditText.setCursorVisible(true);
            }
        });
        // 文字变化导致的清除按钮逻辑
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isEmpty(s.toString())) {
                    search_clear.setVisibility(View.VISIBLE);
                    searchEditText.setCursorVisible(true);

                } else {
                    search_clear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /**
         * 虚拟键盘监听
         */
        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (v.length() != 0) {
                        //把检索的关键词保存到数据库的historySearch表，以字段wordsHistory存储
                        ContentValues cv = new ContentValues();
                        cv.put(HistorySearchDBhelper.WORDS_HISTORY, searchEditText.getText().toString());
                        dbWrite.insert(HistorySearchDBhelper.TABLE_NAME, null, cv);

                        //检索结果错误画面或加载画面不可见时，重新加载动画并请求

                        loadBallView.setVisibility(View.VISIBLE);

                        searchProductBykeyword(searchEditText.getText().toString());


                    }


                    //隐藏虚拟键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                    return true;
                }
                return false;

            }
        });


        /**
         *TODO HASDO 进行检索请求
         */
        searchProductBykeyword(searchEditText.getText().toString());

    }

    /**
     * 顶部的返回区域和返回按钮监听
     */
    private void topPanlBackAreaAndBtnListner() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchListActivity.this, MainActivity.class));
                finish();

            }
        });
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchListActivity.this, MainActivity.class));
                finish();
            }
        });
    }


    /***
     * 通过关键词检索数据列表
     *
     * @param keyword 请求关键词
     */
    private void searchProductBykeyword(String keyword) {

        StorageManager.saveSearchKeyWord(this, keyword);

        //初始化虚拟数据数组和能够加载下一页标志位置

        isCanNotLoadMore = false;
        addModelFlagArray.clear();

        //请求检索开始
        ApiManager.getInstance(this).requestProductByKeyWord(keyword, "1", new ResponseCallback<ProductListModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                mHandler.sendEmptyMessage(SEARCH_MODEL_FAIL);
            }

            @Override
            public void onResponse(Response response, ProductListModel model) {

                if (!StringUtil.isEmpty(model.next)) {
                    currentPage = Integer.parseInt(model.next);
                    isCanNotLoadMore = false;
                } else {
                    currentPage = -1;
                    isCanNotLoadMore = true;
                    mHandler.sendEmptyMessage(CANNOT_LOADMORE_LISTDATA);

                }
                if (model.items != null && model.items.size() > 0) {
                    productList.clear();
                    productList.addAll(model.items);

                    if (productList.size() > 0 && productList.size() < 5) {
                        addVitrialData(model);
                    } else if (productList.size() > 4) {
                        addModelFlagArray.clear();

                    }

                    Message message = mHandler.obtainMessage();
                    message.what = SEARCH_MODEL_SUCCESE;
                    mHandler.sendMessage(message);

                } else {
                    mHandler.sendEmptyMessage(SEARCH_MODEL_NULL_FAIL);
                }
            }


        });
    }

    /**
     * 添加虚拟数据，填充满scrollview
     *
     * @param model
     */
    private void addVitrialData(ProductListModel model) {
        switch (productList.size()) {
            case 1:
                for (int i = 0; i < 4; i++) {
                    productList.add(model.items.get(0));//实际的条目，但是到绑定到适配器时不显示
                }
                addModelFlagArray.clear();
                addModelFlagArray.add("0");//这个flag必须和adapter的position起始值相同，否则会出错
                addModelFlagArray.add("true");
                break;

            case 2:
                for (int i = 0; i < 3; i++) {
                    productList.add(model.items.get(0));
                }
                addModelFlagArray.clear();
                addModelFlagArray.add("1");
                addModelFlagArray.add("true");
                break;

            case 3:
                for (int i = 0; i < 2; i++) {
                    productList.add(model.items.get(0));
                }

                addModelFlagArray.clear();
                addModelFlagArray.add("2");//这个flag必须和adapter的position起始值相同，否则会出错
                addModelFlagArray.add("true");
                break;

            case 4:

                productList.add(model.items.get(0));
                addModelFlagArray.clear();
                addModelFlagArray.add("3");//这个flag必须和adapter的position起始值相同，否则会出错
                addModelFlagArray.add("true");
                break;

        }
    }


    /***
     * * 通过关键词刷新或者加载数据列表
     *
     * @param keyword   检索关键词
     * @param isRefresh 刷新标志位
     * @param isload    加载更多标志位
     */
    private void refreshOrLoadProductBykeyword(String keyword, final boolean isRefresh, final boolean isload) {


        //初始化虚拟数据数组和能够加载下一页标志位置
        addModelFlagArray.clear();
        isCanNotLoadMore = false;


        if (isRefresh) {
            requestPage = 1;
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
                mHandler.sendEmptyMessage(CANNOT_LOADMORE_LISTDATA);

            }
        }
        if (isCanRequestFlag) {
            ApiManager.getInstance(this).requestProductByKeyWord(keyword, String.valueOf(requestPage), new ResponseCallback<ProductListModel>() {
                @Override
                public void onFailure(Request request, Exception e) {
                    if (isRefresh && !isload) {
                        mHandler.sendEmptyMessage(REFRESH_MODEL_FAIL);
                    } else if (!isRefresh && isload) {
                        mHandler.sendEmptyMessage(LOADMORE_MODEL_FAIL);
                    }
                }

                @Override
                public void onResponse(Response response, ProductListModel model) {

                    if (!StringUtil.isEmpty(model.next)) {
                        currentPage = Integer.parseInt(model.next);
                        isCanNotLoadMore = false;
                    } else {
                        currentPage = -1;
                        isCanNotLoadMore = true;
                        mHandler.sendEmptyMessage(CANNOT_LOADMORE_LISTDATA);

                    }
                    if (model.items != null && model.items.size() > 0) {
                        // 刷新或者请求成功
                        //数据模型获取成功并且大小大于0

                        Message message = mHandler.obtainMessage();

                        if (isRefresh && !isload) {

                            productList.addAll(model.items);

                            if (productList.size() > 0 && productList.size() < 5) {
                                addVitrialData(model);
                            } else if (productList.size() > 4) {
                                addModelFlagArray.clear();

                            }

                            message.what = REFRESH_MODEL_SUCCESE;
                            mHandler.sendMessage(message);
                        } else if (!isRefresh && isload) {
                            productList.addAll(model.items);
                            message.what = LOADMORE_MODEL_SUCCESE;
                            mHandler.sendMessage(message);

                        }
                    } else {
                        // 刷新或者请求成功但是
                        //数据模型获取失败
                        if (isRefresh && !isload) {
                            mHandler.sendEmptyMessage(REFRESH_MODEL_FAIL);
                        } else if (!isRefresh && isload) {
                            mHandler.sendEmptyMessage(LOADMORE_MODEL_FAIL);
                        }


                    }
                }
            });
        }


    }


    /**
     * handler消息机制更新ui
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            alphaAnimation.setDuration(2000);

            switch (msg.what) {
                case SEARCH_MODEL_SUCCESE://检索成功


                    setSucceseLoadAction(alphaAnimation);
                    setSeachListView();

                    mScrollView.setFootViewGo(false);
                    mScrollView.stopLoadMore(isCanNotLoadMore);
                    break;

                case SEARCH_MODEL_FAIL://检索失败
                case SEARCH_MODEL_NULL_FAIL://检索数据为空

                    requestOrRefreshFail(true);


                    break;

                case REFRESH_MODEL_SUCCESE://刷新成功
                    loadBallView.setVisibility(View.INVISIBLE);
                    serachListErrortipview.setVisibility(View.INVISIBLE);
                    searchListProductLayout.setVisibility(View.VISIBLE);
                    adapter = null;//这里必须，不然会根据判断只notifydatachange，却显示不了界面
                    setSeachListView();
                    mScrollView.stopRefresh();
                    mScrollView.stopLoadMore(isCanNotLoadMore);
                    mScrollView.setFootViewGo(false);

                    break;

                case REFRESH_MODEL_FAIL://刷新失败

                    requestOrRefreshFail(false);


                    break;


                case LOADMORE_MODEL_SUCCESE://加载成功
                    setSeachListView();
                    mScrollView.setFootViewGo(false);
                    mScrollView.stopLoadMore(isCanNotLoadMore);

                    break;

                case LOADMORE_MODEL_FAIL://加载失败

                    mScrollView.stopLoadMore(false);
                    mScrollView.setFootViewGo(false);

                    break;

                case CANNOT_LOADMORE_LISTDATA://加载木有了
                    mScrollView.stopLoadMore(isCanNotLoadMore);
                    break;

                default:
                    break;
            }


        }
    };

    private void setSucceseLoadAction(AlphaAnimation alphaAnimation) {
        loadBallView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                loadBallView.setVisibility(View.INVISIBLE);
                searchListProductLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
    }

    /**
     * 设置检索列表数据视图
     */
    private void setSeachListView() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (adapter == null) {
            adapter = new ProductListAdapter(productList, SearchListActivity.this, addModelFlagArray);
            adapter.setWidth(width);
            productGridView.setAdapter(adapter);
            productGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SearchListActivity.this, ProductDetailAcitvity.class);
                    intent.putExtra("code", productList.get(position).code);
                    startActivity(intent);
                }
            });
        } else if (adapter != null) { //这里为load起作用
            adapter.notifyDataSetChanged();
        }

    }


    /**
     * @param isSearchRequest true是请求检索，false 是刷新检索
     */
    private void requestOrRefreshFail(boolean isSearchRequest) {

        loadBallView.setVisibility(View.GONE);
        serachListErrortipview.setVisibility(View.VISIBLE);
        searchListProductLayout.setVisibility(View.GONE);
        showErroTipsView(isSearchRequest);
        mScrollView.stopRefresh();
        mScrollView.setFootViewGo(true);
        mScrollView.stopLoadMore(false);


    }


    /**
     * 显示错误提示文字
     *
     * @param isSearchRequest
     */
    private void showErroTipsView(final boolean isSearchRequest) {


        if (isSearchRequest) {//请求检索

            serachListErrortipview.setCannotSearchkeywordProductErrorShow(searchEditText.getText().toString());

        } else {//刷新检索

            serachListErrortipview.setLoadGoneAndErrorTipsVisiable();

        }

    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {

                refreshOrLoadProductBykeyword(StorageManager.getSearchKeyWord(SearchListActivity.this), true, false);

            }
        }.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {

                refreshOrLoadProductBykeyword(StorageManager.getSearchKeyWord(SearchListActivity.this), false, true);

            }
        }.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 获取滑动竖向高度值
     *
     * @param scrollYvalue
     */
    @Override
    public void onScrollGetVerticalValue(int scrollYvalue) {

    }
}
