package cn.com.custom.widgetproject.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.callback.CommonDialogCallback;

/**
 * 共通Dialog
 */
public class CommonMessageDialog extends BaseAlterDialog {
    private CommonDialogCallback callback;
    private String strRes;
    private int strResMessgae;
    private TextView dialogTextView;
    private TextView dialogTextTitleView;
    private boolean isShowTitle;

    public CommonMessageDialog(Context context, String strRes, int strResMessgae, boolean isShowTitle) {
        super(context, R.style.Translucent_background);
        this.strRes = strRes;
        this.strResMessgae = strResMessgae;
        this.isShowTitle = isShowTitle;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.customview_common_errordialog);
        TextView tv_confirm = (TextView) this.findViewById(R.id.dialog_confirm);
        TextView tv_cancle = (TextView) this.findViewById(R.id.dialog_cancle);
        dialogTextView = (TextView) this.findViewById(R.id.dialog_msg);
        dialogTextTitleView = (TextView) this.findViewById(R.id.dialog_title_msg);
        if (isShowTitle) {
            dialogTextTitleView.setVisibility(View.VISIBLE);
        }
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onConfirmClick();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancleClick();
            }
        });
        dialogTextView.setText(strRes);
        dialogTextTitleView.setText(strResMessgae);

    }

    public void setCallback(CommonDialogCallback callback) {
        this.callback = callback;

    }

}
