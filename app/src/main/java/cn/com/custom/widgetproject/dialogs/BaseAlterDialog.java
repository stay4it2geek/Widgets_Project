package cn.com.custom.widgetproject.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

/**
 * Base Dialog
 */
public class BaseAlterDialog extends AlertDialog {
    private Context context;

    protected BaseAlterDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseAlterDialog.this.setCancelable(false);
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = BaseAlterDialog.this.getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        lp.width = size.x; //���ÿ��
        lp.height = size.y;//���ø߶�
        BaseAlterDialog.this.getWindow().setAttributes(lp);
    }
}
