package cn.com.custom.widgetproject.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.constant.Constant;

/**
 * 关于画面
 * Created by custom on 2016/6/14.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewById(R.id.rl_pingjia).setOnClickListener(this);
        findViewById(R.id.rl_fankui).setOnClickListener(this);

        findViewById(R.id.about_rl_back).setOnClickListener(this);
        findViewById(R.id.about_iv_back).setOnClickListener(this);
        TextView verson = (TextView) findViewById(R.id.verson);
        try {
            verson.setText("应用版本：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, WebViewActivity.class);

        switch (v.getId()) {
            //跳往评价
            case R.id.rl_pingjia:

                intent.putExtra(Constant.WEB_URL, "http://www.custom.com.cn/");
                startActivity(intent);
                break;
            //跳往邮件，等待详细设计完善
            case R.id.rl_fankui:

                //Todo 填写邮箱
                mailContact("");
                break;

            case R.id.about_iv_back:
            case R.id.about_rl_back:
                finish();
                break;
            default:
                break;
        }


    }

    /**
     * 打开邮箱应用发送邮件
     *
     * @param mailAdress
     */
    public void mailContact(String mailAdress) {
        Uri uri = Uri.parse("mailto:" + mailAdress);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "请填写主题"); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "请填写你的建议或意见"); // 正文
        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

}