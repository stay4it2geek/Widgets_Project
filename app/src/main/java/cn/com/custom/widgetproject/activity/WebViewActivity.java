package cn.com.custom.widgetproject.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.widget.LoadingBallView;


/**
 * 共通WebView画面
 * <p/>
 * Created by custom on 2016/5/6.
 */
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    //网页控件
    private WebView webview;
    TextView navigationTitle;
    private LoadingBallView webviewLoadview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        navigationTitle = (TextView) findViewById(R.id.navigationTitle);
        webviewLoadview = (LoadingBallView) findViewById(R.id.webview_loadview);

        this.findViewById(R.id.btn_back).setOnClickListener(this);
        this.findViewById(R.id.rl_back).setOnClickListener(this);
        webview = (WebView) findViewById(R.id.webview);
        initWebView();
        String url = this.getIntent().getStringExtra(Constant.WEB_URL);
        webview.loadUrl(url);


    }

    /**
     * WebView初期配置
     */
    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this, "smoopa");
        webview.setWebChromeClient(new WebChromeClient());
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webview.loadUrl("javascript:smoopa.setNavigationTitle(document.title)");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webviewLoadview.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//网页存在重定向问题的解决方法
                return true;
            }


        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Webview Title变更
     *
     * @param title 标题
     */
    @JavascriptInterface
    public void setNavigationTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationTitle.setText(title);
            }
        });
    }

}
