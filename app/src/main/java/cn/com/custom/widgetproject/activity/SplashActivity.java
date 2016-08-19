package cn.com.custom.widgetproject.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.google.gson.Gson;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.callback.CommonDialogCallback;
import cn.com.custom.widgetproject.callback.ResponseCallback;
import cn.com.custom.widgetproject.dialogs.CommonMessageDialog;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.manager.StorageManager;
import cn.com.custom.widgetproject.model.SearchCategoryModel;
import cn.com.custom.widgetproject.utils.ConnectivityUtil;
import cn.com.custom.widgetproject.utils.JsonUtil;
import cn.com.custom.widgetproject.utils.StringUtil;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * 启动画面
 */
public class SplashActivity extends AppCompatActivity {
    //logo
    private ImageView mImageLogo;
    //错误信息提示对话框
    private CommonMessageDialog commonErrorDialog;
    private ArrayList<SearchCategoryModel.Geners> genersArrayList;
    private UnCheckIconThread uncheckThread;
    private CheckIconThread checkThread;
    private SaveListThread saveListThread;

    ArrayList<String> iconDefaultPath = new ArrayList<>();
    ArrayList<String> iconHightPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mImageLogo = (ImageView) findViewById(R.id.iv_logo);
        checkThread = new CheckIconThread();
        uncheckThread = new UnCheckIconThread();
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(4000);
        animationSet.addAnimation(alphaAnimation);
        mImageLogo.startAnimation(animationSet);
      animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        requestSearchCategory();
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    class CheckIconThread extends Thread {

        @Override
        public void run() {
            ArrayList<SearchCategoryModel.Geners> genersArrayList = JsonUtil.toObjectList(StorageManager.getGenresCategory(SplashActivity.this), SearchCategoryModel.Geners.class);
            try {
                for (SearchCategoryModel.Geners generses : genersArrayList) {
//                    DevUtil.e("generses", generses.icon_highlight);
                    URL url = new URL(generses.icon_highlight);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6 * 1000);  // 注意要设置超时，设置时间不要超过10秒，避免被android系统回收
                    if (conn.getResponseCode() != 200)
                        throw new RuntimeException("请求url失败");
                    InputStream inSream = conn.getInputStream();
                    //把图片保存到项目的根目录
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + "hig" + StringUtils.substringAfterLast(generses.icon_highlight, "/"));
                    readAsFile(inSream, file);
                    iconHightPath.add(file.getAbsolutePath());
                    StorageManager.saveHighlightIconListsize(SplashActivity.this, iconHightPath.size() + "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UnCheckIconThread extends Thread {

        @Override
        public void run() {
            try {
                ArrayList<SearchCategoryModel.Geners> genersArrayList = JsonUtil.toObjectList(StorageManager.getGenresCategory(SplashActivity.this), SearchCategoryModel.Geners.class);

                for (SearchCategoryModel.Geners generses : genersArrayList) {
//                    DevUtil.e("generses2", generses.icon_default);
                    URL url = new URL(generses.icon_default);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6 * 1000);  // 注意要设置超时，设置时间不要超过10秒，避免被android系统回收
                    if (conn.getResponseCode() != 200) throw new RuntimeException("请求url失败");
                    InputStream inSream = conn.getInputStream();
                    //把图片保存到项目的根目录
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + "def" + StringUtils.substringAfterLast(generses.icon_default, "/"));
                    readAsFile(inSream, file);
                    iconDefaultPath.add(file.getAbsolutePath());
                    StorageManager.saveDefaultIconListsize(SplashActivity.this, iconDefaultPath.size() + "");

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SaveListThread extends Thread {

        private SearchCategoryModel model;

        public SaveListThread(SearchCategoryModel model) {

            this.model = model;

        }

        @Override
        public void run() {
            try {
                Gson gson = new Gson();
                if (genersArrayList.size() == iconDefaultPath.size() && iconDefaultPath.size() == iconHightPath.size()) {
                    for (int i = 0; i < genersArrayList.size(); i++) {
                        model.genres.get(i).icon_default = model.genres.get(i).icon_default + "@" + iconDefaultPath.get(i);
                        model.genres.get(i).icon_highlight = model.genres.get(i).icon_highlight + "@" + iconHightPath.get(i);
                    }

                    StorageManager.saveTopPageNeedGenres(SplashActivity.this, gson.toJson(model.genres));
                    startNextActivity(false);


                } else {
                    try {
                        checkThread.start();
                        if (!checkThread.isAlive()) {
                            checkThread.start();
                        }
                        checkThread.join();
                        uncheckThread.start();
                        if (!uncheckThread.isAlive()) {
                            uncheckThread.start();
                        }
                        uncheckThread.join();
                        saveListThread.start();
                        if (!saveListThread.isAlive()) {
                            saveListThread.start();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void readAsFile(InputStream inSream, File file) throws Exception {
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inSream.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 进行网络检查,通过则检索数据，如果有错误对话框则让其消失
         */
        if (commonErrorDialog != null && ConnectivityUtil.checkNetwork(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestSearchCategory();
                    commonErrorDialog.dismiss();
                }
            }, 500);
        }


    }


    /**
     * 表示Error Dialog
     */
    private void showErrorDialog(final String string, final boolean isShowTitle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (commonErrorDialog == null) {
                    commonErrorDialog = new CommonMessageDialog(SplashActivity.this, string, R.string.dialog_title, isShowTitle);
                    commonErrorDialog.setCallback(new CommonDialogCallback() {
                        @Override
                        public void onConfirmClick() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestSearchCategory();
                                }
                            }, 2000);
                            dismissErrorProgress();
                        }

                        @Override
                        public void onCancleClick() {
                            SplashActivity.this.finish();
                        }

                    });
                    commonErrorDialog.show();
                }
            }
        });

    }

    /**
     * 隐藏Error Dialog
     */
    private void dismissErrorProgress() {
        if (commonErrorDialog != null) {
            commonErrorDialog.dismiss();
            commonErrorDialog = null;
        }
    }


    /**
     * 请求分类数据
     */
    private void requestSearchCategory() {
        ApiManager.getInstance(this).requestSearchCategoryModel(new ResponseCallback<SearchCategoryModel>() {

            @Override
            public void onFailure(Request request, Exception e) {
                showErrorDialog(getResources().getString(R.string.error_net), true);
            }

            @Override
            public void onResponse(Response response, SearchCategoryModel model) {
                if (model != null) {
                    saveListThread = new SaveListThread(model);
                    Gson gson = new Gson();

                    StorageManager.saveGenresCategory(SplashActivity.this, gson.toJson(model.genres));
                    genersArrayList = JsonUtil.toObjectList(StorageManager.getGenresCategory(SplashActivity.this), SearchCategoryModel.Geners.class);

                    if (StringUtil.isEmpty(StorageManager.getTimeTamp(SplashActivity.this))) { //如果时间戳保存为空

                        //如果本地时间戳为空，则保存网络时间戳和网络分类字符串
                        StorageManager.saveTimeTamp(SplashActivity.this, model.timestamp);


                        try {

                            checkThread.start();
                            if (!checkThread.isAlive()) {
                                checkThread.start();
                            }
                            checkThread.join();
                            uncheckThread.start();
                            if (!uncheckThread.isAlive()) {
                                uncheckThread.start();
                            }
                            uncheckThread.join();
                            saveListThread.start();
                            if (!saveListThread.isAlive()) {
                                saveListThread.start();
                            }

                        } catch (InterruptedException e) {
                            if (Integer.parseInt(StorageManager.getDefaultIconListsize(SplashActivity.this)) != genersArrayList.size()
                                    || Integer.parseInt(StorageManager.getHighlightIconListsize(SplashActivity.this)) != genersArrayList.size()) {
                                startNextActivity(true);
                            } else {
                                startNextActivity(false);
                            }
                        }


                    } else {//如果时间戳保存不为空


                        if (!StringUtil.isEqual(StorageManager.getTimeTamp(SplashActivity.this), model.timestamp)) {

                            //如果本地时间戳与网络时间戳不一样，则保存网络时间戳
                            StorageManager.saveTimeTamp(SplashActivity.this, model.timestamp);

                            try {
                                checkThread.start();
                                if (!checkThread.isAlive()) {
                                    checkThread.start();
                                }
                                checkThread.join();
                                uncheckThread.start();
                                if (!uncheckThread.isAlive()) {
                                    uncheckThread.start();
                                }
                                uncheckThread.join();
                                saveListThread.start();
                                if (!saveListThread.isAlive()) {
                                    saveListThread.start();
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        } else {//如果本地时间戳与网络时间戳一样，

                            if (Integer.parseInt(StorageManager.getDefaultIconListsize(SplashActivity.this)) != genersArrayList.size()
                                    || Integer.parseInt(StorageManager.getHighlightIconListsize(SplashActivity.this)) != genersArrayList.size()) {
                                startNextActivity(true);
                            } else {
                                startNextActivity(false);
                            }

                        }

                    }


                } else {
                    //数据为空的处理，errorDialog或者是重新请求
                    showErrorDialog(getResources().getString(R.string.error_net), false);

                }
            }
        });

    }

    private void startNextActivity(final boolean isNeedNetUrl) {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                       intent.putExtra("isNeedNetUrl", isNeedNetUrl);
                       startActivity(intent);
                       SplashActivity.this.finish();
                   }
               },2000);
           }
       });


    }

}
