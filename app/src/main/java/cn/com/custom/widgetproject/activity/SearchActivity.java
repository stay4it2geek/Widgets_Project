package cn.com.custom.widgetproject.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.custom.widgetproject.R;

import cn.com.custom.widgetproject.callback.TanslationHttpCallback;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.flowlayout.FlowLayout;
import cn.com.custom.widgetproject.flowlayout.HotWordListAdapter;
import cn.com.custom.widgetproject.flowlayout.TagFlowLayout;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.model.HotWordModel;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.HistorySearchDBhelper;
import cn.com.custom.widgetproject.widget.ListViewFitScrollView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;

/**
 * 关键词检索画面
 * Created by custom on 2016/6/14.
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    //热词code
    private static final int HOTWORD = 1;
    //删除单条code
    private static final int HISTORY_KEYWORD_DELETE_SINGLE = 3;
    //全部删除code
    private static final int DELETEALL = 4;
    //检索编辑框
    private EditText searchEditText;
    //历史检索listview
    private ListViewFitScrollView historySearchListView;
    //历史检索数据适配器
    private HistroySearchListAdapter histroySearchListAdapter;
    //历史检索词集合
    private ArrayList<String> historyKeywords = new ArrayList<>();

    private Cursor mCursor;//游标集

    private HotWordModel hotWordModel;
    private SQLiteDatabase dbRead, dbWrite;
    ContentValues cv = new ContentValues();
    private boolean autoToJapan;
    private ImageView iv_search_clear;
    private TagFlowLayout mFlowLayout;
    private LinearLayout hotsearch_ll;
    private ImageView search_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        iv_search_clear = (ImageView) findViewById(R.id.iv_search_clear);
        iv_search_clear.setOnClickListener(this);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);


        findViewById(R.id.rl_deleteAll).setOnClickListener(this);
        historySearchListView = (ListViewFitScrollView) findViewById(R.id.listView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.clearFocus();
        searchEditText.setCursorVisible(false);


        search_clear = (ImageView) findViewById(R.id.iv_search_clear);


        hotsearch_ll = (LinearLayout) findViewById(R.id.hotsearch_ll);

        //获得单例数据库帮助类
        //获取可读数据库
        dbRead = HistorySearchDBhelper.getInstance(this).getReadableDatabase();
        //获取可写数据库
        dbWrite = HistorySearchDBhelper.getInstance(this).getWritableDatabase();
        //设置历史检索数据列表
        initHistorySearchListViewData();
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.requestFocus();
            }
        });

        /**
         *   清除按钮的显示逻辑
         */

        if (!StringUtil.isEmpty(searchEditText.getText().toString())) {
            search_clear.setVisibility(View.VISIBLE);
        } else {
            search_clear.setVisibility(View.INVISIBLE);
        }
        // 清除按钮的点击逻辑
        search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.requestFocus();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isEmpty(s.toString())) {
                    iv_search_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_search_clear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 清除按钮的点击逻辑
        iv_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.requestFocus();
                searchEditText.setCursorVisible(true);
            }
        });
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.requestFocus();
                searchEditText.setCursorVisible(true);
            }
        });
        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (v.length() != 0) {
                        //把检索的关键词保存到数据库的historySearch表，以字段wordsHistory存储
                        cv.put(HistorySearchDBhelper.WORDS_HISTORY, searchEditText.getText().toString());
                        dbWrite.insert(HistorySearchDBhelper.TABLE_NAME, null, cv);
                        Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
                        intent.putExtra("searchKeyword", searchEditText.getText().toString());
                        startActivity(intent);
                    }
                    //隐藏虚拟键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);


                    return true;
                }
                return false;

            }


        });


        initHistorySearchListViewData();
        requestHotWord();
    }

    /**
     * 获得焦点时，重新设置历史检索数据列表
     */
    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        historyKeywords.clear();
        mCursor = dbRead.query(true, "historySearch", new String[]{HistorySearchDBhelper.KEY_ID, HistorySearchDBhelper.WORDS_HISTORY}, null, null, HistorySearchDBhelper.WORDS_HISTORY, null, "_id desc", null, null);
        while (mCursor.moveToNext()) {
            String keyword = mCursor.getString(mCursor.getColumnIndex(HistorySearchDBhelper.WORDS_HISTORY));
            historyKeywords.add(keyword);
        }

        histroySearchListAdapter.notifyDataSetChanged();

    }

    /**
     * 初始化历史检索数据列表
     */
    @SuppressLint("NewApi")
    private void initHistorySearchListViewData() {
        //通过_id降序查询置顶历史检索词，以wordHistory字段为分组，distinic去重查询得到游标集
        mCursor = dbRead.query(true, HistorySearchDBhelper.TABLE_NAME, new String[]{HistorySearchDBhelper.KEY_ID, HistorySearchDBhelper.WORDS_HISTORY}, null, null, HistorySearchDBhelper.WORDS_HISTORY, null, "_id desc", null, null);
        while (mCursor.moveToNext()) {
            String keyword = mCursor.getString(mCursor.getColumnIndex(HistorySearchDBhelper.WORDS_HISTORY));
            historyKeywords.add(keyword);
        }
        histroySearchListAdapter = new HistroySearchListAdapter(this);
        historySearchListView.setAdapter(histroySearchListAdapter);
        mCursor.close();

    }

    /**
     * 请求热门检索词数据
     */
    private void requestHotWord() {
        ApiManager.getInstance(this).requestHotSearch(new ResponseCallback<HotWordModel>() {
            @Override
            public void onFailure(Request request, Exception e) {
                handler.sendEmptyMessage(12);
            }

            @Override
            public void onResponse(Response response, final HotWordModel model) {
                Message message = handler.obtainMessage();
                message.obj = model;
                message.what = HOTWORD;
                handler.sendMessage(message);


            }
        });


    }

    /**
     * handler 更新列表数据UI
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HOTWORD) {
                hotWordModel = (HotWordModel) msg.obj;
                if (hotWordModel.keywords != null) {
                    hotsearch_ll.setVisibility(View.VISIBLE);
                    mFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
                    //mFlowLayout.setMaxSelectCount(3);
                    mFlowLayout.setAdapter(new HotWordListAdapter<String>(hotWordModel.keywords) {

                        @Override
                        public View getView(FlowLayout parent, int position, String s) {
                            final LayoutInflater mInflater = LayoutInflater.from(SearchActivity.this);
                            TextView tv = (TextView) mInflater.inflate(R.layout.item_hotword_textlayout,
                                    mFlowLayout, false);
                            tv.setText(s);
                            return tv;
                        }
                    });

                    mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                        @Override
                        public boolean onTagClick(View view, final int mPosition, FlowLayout parent) {
                            cv.put(HistorySearchDBhelper.WORDS_HISTORY, hotWordModel.keywords.get(mPosition));
                            dbWrite.insert(HistorySearchDBhelper.TABLE_NAME, null, cv);

                            Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
                            intent.putExtra("searchKeyword", hotWordModel.keywords.get(mPosition));
                            startActivity(intent);


                            return true;
                        }
                    });


                }
            } else if (msg.what == HISTORY_KEYWORD_DELETE_SINGLE) {
                historyKeywords.remove(msg.arg1);
                histroySearchListAdapter.notifyDataSetChanged();
            } else if (msg.what == DELETEALL) {
                historyKeywords.removeAll(historyKeywords);
                histroySearchListAdapter.notifyDataSetChanged();

            } else if (msg.what == 12) {
                hotsearch_ll.setVisibility(View.GONE);

            }

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_search_clear:
                searchEditText.setText("");
                break;
            case R.id.iv_deleteAll:
            case R.id.rl_deleteAll://删除全部历史检索数据
                dbWrite.execSQL("delete from " + HistorySearchDBhelper.TABLE_NAME);
                handler.sendEmptyMessage(DELETEALL);
                break;
            default:
                break;

        }

    }

    /**
     * 历史检索列表数据适配器
     */
    private class HistroySearchListAdapter extends BaseAdapter {

        private Activity context;

        public HistroySearchListAdapter(Activity context) {
            this.context = context;

        }

        @Override
        public String getItem(int position) {
            return historyKeywords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return historyKeywords.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_historykeywords_layout, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.rl_deleteBtnl = (RelativeLayout) convertView
                        .findViewById(R.id.rl_deleteBtn);
                holder.historyText = (TextView) convertView
                        .findViewById(R.id.historyText);
                holder.deleteBtn = (ImageButton) convertView
                        .findViewById(R.id.deleteBtn);
                holder.rl_historykeyword = (RelativeLayout) convertView
                        .findViewById(R.id.rl_historykeyword);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.historyText.setText(historyKeywords.get(position));
            holder.deleteBtn.setOnClickListener(new MyListener(position));
            holder.rl_historykeyword.setOnClickListener(new MyListener(position));
            holder.rl_deleteBtnl.setOnClickListener(new MyListener(position));

            return convertView;
        }

        class ViewHolder {
            TextView historyText;
            ImageButton deleteBtn;
            RelativeLayout rl_deleteBtnl;
            RelativeLayout rl_historykeyword;

        }


        class MyListener implements View.OnClickListener {
            int mPosition;

            public MyListener(int inPosition) {
                mPosition = inPosition;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.deleteBtn:
                    case R.id.rl_deleteBtn:
                        mCursor.moveToPosition(mPosition);
                        int itemID = mCursor.getInt(mCursor.getColumnIndex(HistorySearchDBhelper.KEY_ID));
                        dbWrite.delete(HistorySearchDBhelper.TABLE_NAME, HistorySearchDBhelper.KEY_ID + "=?", new String[]{itemID + ""});
                        Message message = handler.obtainMessage();
                        message.arg1 = mPosition;
                        message.what = HISTORY_KEYWORD_DELETE_SINGLE;
                        handler.sendMessage(message);
                        break;


                    case R.id.rl_historykeyword:
                        Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
                        intent.putExtra("searchKeyword", historyKeywords.get(mPosition));
                        startActivity(intent);
                        break;
                }

            }
        }
    }
}
