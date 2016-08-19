package cn.com.custom.widgetproject.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.custom.widgetproject.zxing.ScanListener;

import cn.com.custom.widgetproject.R;


import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.widget.ScanImageWithLine;
import cn.com.custom.widgetproject.zxing.ScanManager;
import cn.com.custom.widgetproject.zxing.decode.DecodeThread;
import cn.com.custom.widgetproject.zxing.decode.Utils;

/**
 * 条码扫描画面
 * Created by custom on 2016/6/14.
 */
public class ScancodeActivity extends AppCompatActivity implements ScanListener, View.OnClickListener {
    private static final int PHOTOREQUESTCODE = 111;
    //绘图view
    SurfaceView scanPreview = null;
    //扫描外层view
    View scanContainer;
    //扫描内层view
    View scanCropView;
    //扫描移动线
    ImageView scanLine;
    //扫描管理器
    ScanManager scanManager;
    //扫描模型（条形，二维码，全部）
    private int scanMode;
    //扫描外层四边角线
    @Bind(R.id.scan_image)
    ScanImageWithLine scan_image;
    //闪光灯
    @Bind(R.id.light)
    Button light;
    //回退按钮，以图片显示
    @Bind(R.id.scan_btn_back)
    ImageView btn_back;
    //扫描提示文字
    @Bind(R.id.scan_hint)
    TextView scan_hint;
    //闪光灯点击区域
    private RelativeLayout rl_light;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scancode);
        ButterKnife.bind(this);
        scanMode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        switch (scanMode) {
            case DecodeThread.BARCODE_MODE:
                scan_hint.setText(R.string.scan_barcode_hint);
                break;
            case DecodeThread.QRCODE_MODE:
                scan_hint.setText(R.string.scan_qrcode_hint);
                break;
            case DecodeThread.ALL_MODE:
                scan_hint.setText(R.string.scan_allcode_hint);
                break;
        }
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        rl_light = (RelativeLayout) findViewById(R.id.rl_light);
        final EditText et_code = (EditText) findViewById(R.id.et_code);
        findViewById(R.id.scanloacalButton).setOnClickListener(this);
        btn_back.setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);
        light.setOnClickListener(this);
        rl_light.setOnClickListener(this);
        et_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_code.setCursorVisible(true);
            }
        });
        et_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.length() != 0) {
                        doSearch(et_code.getText().toString());
                    }

                    return true;
                }
                return false;

            }


        });
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode, this);
    }

    /**
     * 通过code请求检索
     *
     * @param code 扫描到的二维码code或者条形码code
     */
    private void doSearch(String code) {

                    Intent intent = new Intent(ScancodeActivity.this, ProductDetailAcitvity.class);
                    intent.putExtra(Constant.PRODUCT_CODE,code);
                    startActivity(intent);
                    ScancodeActivity.this.finish();


    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
        scan_image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }

    /**
     * 处理扫描结果
     */
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        //scanManager.reScan();
        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
            //设置扫描动态显示
            scan_image.setVisibility(View.VISIBLE);
            Bitmap barcode = null;
            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
            if (compressedBitmap != null) {
                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
            }
            scan_image.setImageBitmap(barcode);
        }
        scan_image.setVisibility(View.VISIBLE);
        doSearch(rawResult.getText());
    }


    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if (e.getMessage() != null && e.getMessage().startsWith("相机")) {
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 处理本地二维码，条形码，一维码的方法

     */
    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    /**
     * 处理本地二维码，条形码，一维码的方法回调
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_light:
            case R.id.light:
                if (scanManager.switchLight()) {
                    light.setBackgroundResource(R.drawable.btn_lighton);
                } else {
                    light.setBackgroundResource(R.drawable.btn_lightoff);

                }
                break;
            case R.id.rl_back:
            case R.id.scan_btn_back:
                finish();
                break;
            case R.id.scanloacalButton:
                showPictures(PHOTOREQUESTCODE);
            default:
                break;
        }
    }


}
