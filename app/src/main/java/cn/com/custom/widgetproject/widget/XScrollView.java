package cn.com.custom.widgetproject.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.Arrays;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.utils.DevUtil;


/**
 * 上拉下拉scrollview
 */
public class XScrollView extends ScrollView implements OnScrollListener {


    private final static int SCROLL_BACK_HEADER = 0;
    private final static int SCROLL_BACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400;

    // when pull up >= 50px
    private final static int PULL_LOAD_MORE_DELTA = 50;

    // support iOS like pull
    private final static float OFFSET_RADIO = 1.8f;

    private float mLastY = -1;

    // used for scroll back
    private Scroller mScroller;
    // user's scroll listener
    private OnScrollListener mScrollListener;
    // for mScroller, scroll back from header or footer.
    private int mScrollBack;

    // the interface to trigger refresh and load more.
    private IXScrollViewListener mListener;

    private LinearLayout mLayout;
    private LinearLayout mContentLayout;

    private XHeaderView mHeader;
    // header view content, use it to calculate the Header's height. And hide it when disable pull refresh.
    private RelativeLayout mHeaderContent;
    private int mHeaderHeight;

    private XFooterView mFooterView;

    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false;

    private boolean mEnablePullLoad = true;
    private boolean mEnableAutoLoad = true;
    private boolean mPullLoading = false;
    private LinearLayout footLayout;

    private  LinearLayout headerLayout;

    private boolean isCanNotLoadMore = false;
    private boolean goFlag;
    private Activity activity;
    private boolean goFlag2;

    public XScrollView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    LinearLayout.LayoutParams params;

    private void initWithContext(Context context) {
        mLayout = (LinearLayout) View.inflate(context, R.layout.xscrollview_layout, null);
        mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);

        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XScrollView need the scroll event, and it will dispatch the event to user's listener (as a proxy).
        this.setOnScrollListener(this);

        // init header view
        mHeader = new XHeaderView(context);
        mHeaderContent = (RelativeLayout) mHeader.findViewById(R.id.header_content);
        headerLayout = (LinearLayout) mLayout.findViewById(R.id.header_layout);
        headerLayout.addView(mHeader);

        // init footer view
        mFooterView = new XFooterView(context);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        footLayout = (LinearLayout) mLayout.findViewById(R.id.footer_layout);
        footLayout.addView(mFooterView, params);

        // init header height
        ViewTreeObserver observer = mHeader.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    mHeaderHeight = mHeaderContent.getHeight();
                    ViewTreeObserver observer = getViewTreeObserver();
                    if (null != observer) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            observer.removeGlobalOnLayoutListener(this);
                        } else {
                            observer.removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }

        this.addView(mLayout);


    }

    public void setFootViewGo(boolean goFlag) {
        this.goFlag = goFlag;
        if (goFlag) {
            DevUtil.e("goFlag",goFlag+"");
            footLayout.setVisibility(GONE);
        } else {
            footLayout.setVisibility(VISIBLE);
            footLayout.removeAllViews();
        }

    }

    /**
     * Set the content ViewGroup for XScrollView.
     *
     * @param content
     */
    public void setContentView(ViewGroup content) {
        if (mLayout == null) {
            return;
        }

        if (mContentLayout == null) {
            mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);
        }

        if (mContentLayout.getChildCount() > 0) {
            mContentLayout.removeAllViews();
        }
        mContentLayout.addView(content);
    }

    /**
     * Set the content View for XScrollView.
     *
     * @param content
     */
    public void setView(View content) {
        if (mLayout == null) {
            return;
        }

        if (mContentLayout == null) {
            mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);
        }
        mContentLayout.addView(content);
    }

    /**
     * Enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;

        // disable, hide the content
        mHeaderContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;

        if (!mEnablePullLoad) {
            mFooterView.setBottomMargin(0);
            mFooterView.hide();
            mFooterView.setPadding(0, 30, 0, mFooterView.getHeight() * (-1));
            mFooterView.setOnClickListener(null);

        } else {
            mPullLoading = false;
            mFooterView.setPadding(0, 30, 0, 0);
            mFooterView.show();
            if (isCanNotLoadMore) {
                mFooterView.setState(XFooterView.STATE_NOMORE);
            } else {
                mFooterView.setState(XFooterView.STATE_NORMAL);
            }
        }
    }


    /**
     * Stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * Stop load more, reset footer view.
     *
     * @param isCanNotLoadMore 是否能再加载下一页标志位， true为可以再加载，false为不能再加载
     */

    public void stopLoadMore(boolean isCanNotLoadMore) {
        this.isCanNotLoadMore = isCanNotLoadMore;
        mPullLoading = false;
        if (isCanNotLoadMore) {
            mFooterView.setState(XFooterView.STATE_NOMORE);
        } else {
            mFooterView.setState(XFooterView.STATE_NORMAL);
        }
        muchRequestControlFlag = false;
    }


    /**
     * Set listener.
     *
     * @param listener
     */
    public void setIXScrollViewListener(IXScrollViewListener listener) {
        mListener = listener;
    }


    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
//        DevUtil.e("mHeader.getVisibleHeight()",""+mHeader.getVisibleHeight());
        mHeader.setVisibleHeight((int) delta + mHeader.getVisibleHeight());
        if (mHeader.getVisibleHeight() > 100) {
            int progress = mHeader.getVisibleHeight() - 100;
            if (progress > 92) {
                progress = 92;
            }
            if (progress < 100) {
                mHeader.setprogress(progress);
            }
        }
        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image unrefreshing
            if (mHeader.getVisibleHeight() > mHeaderHeight) {
                mHeader.setState(XHeaderView.STATE_READY);
            } else {
                mHeader.setState(XHeaderView.STATE_NORMAL);
            }
        }

        // scroll to top each time
        post(new Runnable() {
            @Override
            public void run() {
                XScrollView.this.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void resetHeaderHeight() {
        int height = mHeader.getVisibleHeight();
        if (height == 0) return;

        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderHeight) return;

        // default: scroll back to dismiss header.
        int finalHeight = 0;
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderHeight) {
            finalHeight = mHeaderHeight;
        }

        mScrollBack = SCROLL_BACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);

        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {

            if (height > PULL_LOAD_MORE_DELTA && !isCanNotLoadMore) {
                // height enough to invoke load  more.
                mFooterView.setState(XFooterView.STATE_READY);
            } else {
                if (isCanNotLoadMore) {
                    mFooterView.setState(XFooterView.STATE_NOMORE);
                } else {
                    mFooterView.setState(XFooterView.STATE_NORMAL);
                }
            }
        }

        mFooterView.setBottomMargin(height);

        // scroll to bottom
        post(new Runnable() {
            @Override
            public void run() {
                XScrollView.this.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();

        if (bottomMargin > 0) {
            mScrollBack = SCROLL_BACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {

        mFooterView.setState(XFooterView.STATE_LOADING);
        loadMore();
        mPullLoading = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();

                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                if (isTop() && (mHeader.getVisibleHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();

                } else if (isBottom() && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);

                }
                break;

            default:
                // reset
                mLastY = -1;

                resetHeaderOrBottom();
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void resetHeaderOrBottom() {
        if (isTop()) {
            // invoke refresh
            if (mEnablePullRefresh && mHeader.getVisibleHeight() > mHeaderHeight) {
                mPullRefreshing = true;
                mHeader.setState(XHeaderView.STATE_REFRESHING);
                refresh();
            }
            resetHeaderHeight();

        } else if (isBottom()) {
            // invoke load more.
            if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                mFooterView.setVisibility(VISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mFooterView.getVisibility() == VISIBLE && !isCanNotLoadMore && !goFlag) {
//                            startLoadMore();
//                        }
//                    }
//                }, 1000);
            }
            resetFooterHeight();
        }
    }

    private boolean isTop() {
        return getScrollY() <= 0 || mHeader.getVisibleHeight() > mHeaderHeight || mContentLayout.getTop() > 0;
    }

    private boolean isBottom() {
        return Math.abs(getScrollY() + getHeight() - computeVerticalScrollRange()) <= 5 ||
                (getScrollY() > 0 && null != mFooterView && mFooterView.getBottomMargin() > 0);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLL_BACK_HEADER) {
                mHeader.setVisibleHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }

            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }

    }

    boolean flag;
    public void setFootVisily(boolean flag){
        this.flag=flag;
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        if (mFooterView.getParent() != footLayout) {
            footLayout.addView(mFooterView, params);
        }
//        DevUtil.e("flag",footLayout.getVisibility()==GONE?"YES":"NO");
//        DevUtil.e("flag2",mFooterView.getVisibility()==GONE?"YES":"NO");
//
        DevUtil.e("flag333",flag+"");



            if (footLayout.getVisibility() == GONE&&mFooterView.getVisibility()==GONE&&flag) {
                footLayout.setVisibility(VISIBLE);
                mFooterView.setVisibility(VISIBLE);
            }
        View view = getChildAt(getChildCount() - 1);
        if (null != view) {
            // Calculate the scroll diff
            int diff = (view.getBottom() - (view.getHeight() + view.getScrollY()));
//            DevUtil.e("diff", diff + "" + "mEnableAutoLoad" + mEnableAutoLoad);
            // if diff is zero, then the bottom has been reached
            if (diff == 0 && mEnableAutoLoad) {
                if (isCanNotLoadMore) {
                    mFooterView.setState(XFooterView.STATE_NOMORE);
                } else {
                    mFooterView.setState(XFooterView.STATE_NORMAL);
                }

            }

            if (diff == 0 && !muchRequestControlFlag && !isCanNotLoadMore && getVisilyInScreen(footLayout) && !goFlag) {

                muchRequestControlFlag = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLoadMore();
                    }
                }, 500);
            }
        }

        if (mListener != null) {
            //如果headerview不出现，，则传递滚动的数值
            if (mHeader.getVisibleHeight() < 0 || mHeader.getVisibleHeight() == 0) {
                mListener.onScrollGetVerticalValue(XScrollView.this.getScrollY());
            }
        }


        super.onScrollChanged(l, t, oldl, oldt);
    }

    boolean muchRequestControlFlag = false;

    /**
     * 设置环境
     *
     * @param activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取view进入屏幕可见性
     *
     * @param view
     * @return
     */
    public boolean getVisilyInScreen(View view) {
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        int screenHeight = p.y;


        Rect rect = new Rect(0, 0, screenWidth, screenHeight);


        int[] location = new int[2];
        view.getLocationInWindow(location);
        DevUtil.e("location", Arrays.toString(location));

        if (view.getLocalVisibleRect(rect)) {/*rect.contains(ivRect)*/
            DevUtil.e("location2", "---------控件在屏幕可见区域-----显现-----------------");
            return true;
        } else {

            DevUtil.e("location3", "---------控件已不在屏幕可见区域（已滑出屏幕）-----隐去-----------------");
            return false;
        }
    }


    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

        return 0;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // send to user's listener
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    private void refresh() {
        if (mEnablePullRefresh && null != mListener) {
            mListener.onRefresh();
        }
    }

    private void loadMore() {
        if (mEnablePullLoad && null != mListener) {
            mListener.onLoadMore();
        }
    }

    /**
     * You can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        void onXScrolling(View view);
    }

    /**
     * Implements this interface to get refresh/load more event.
     */
    public interface IXScrollViewListener {
        void onRefresh();

        void onLoadMore();

        void onScrollGetVerticalValue(int scrollYvalue);
    }
}
