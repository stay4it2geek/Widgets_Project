package cn.com.custom.widgetproject.widget;

import android.widget.Checkable;

/**
 * Created by custom on 2016/7/29.
 */
public interface CheckableView extends Checkable {

    void setOnCheckedChangeListener(OnCheckedChangeListener widgetListener);

    void setOnCheckedChangeWidgetListener(
            OnCheckedChangeListener widgetListener);

    int getId();

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CheckableView buttonView, boolean isChecked);
    }
}
