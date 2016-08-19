package cn.com.custom.widgetproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.model.ProductListModel;
import cn.com.custom.widgetproject.utils.StringUtil;
import cn.com.custom.widgetproject.widget.MatchParentNetworkImage;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


/**
 * 产品列表适配器
 * Created by custom on 2016/6/14.
 */
public class RecommendProductListAdapter extends BaseAdapter {

    ArrayList<ProductListModel.Items> itemsArrayList;
    Context context;

    public RecommendProductListAdapter(ArrayList<ProductListModel.Items> arayList, Context context) {
        super();
        this.itemsArrayList = arayList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return itemsArrayList.size() >= 3 ? 3 : itemsArrayList.size();
    }

    @Override
    public ProductListModel.Items getItem(int position) {
        return itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_recomend_product_gridview, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.tv_productTilte = (TextView) convertView
                    .findViewById(R.id.tv_productTilte);
            holder.tv_rmbPrice = (TextView) convertView
                    .findViewById(R.id.tv_rmbPrice);
            holder.tv_jpPrice = (TextView) convertView
                    .findViewById(R.id.tv_jpPrice);
            holder.iv_productImg = (MatchParentNetworkImage) convertView
                    .findViewById(R.id.iv_productImg);
            holder.gridView_rl = (RelativeLayout) convertView
                    .findViewById(R.id.gridView_rl);
            holder.tv_rmbNum = (TextView) convertView
                    .findViewById(R.id.tv_rmbNum);
            holder.tv_jpNum = (TextView) convertView
                    .findViewById(R.id.tv_jpNum);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (itemsArrayList!=null&itemsArrayList.size() > 0&&itemsArrayList.get(position)!=null&&itemsArrayList.get(position).prices != null) {
            if(StringUtil.isEmpty(itemsArrayList.get(position).prices.get(0).price)){
                holder.tv_rmbPrice.setText("-");
            }
            if(StringUtil.isEmpty(itemsArrayList.get(position).prices.get(1).price)){
                holder.tv_jpPrice.setText("-");
            }
            if (itemsArrayList.get(position).prices.get(0).price.contains(".")) {
                holder.tv_rmbPrice.setText(StringUtils.substringBefore(itemsArrayList.get(position).prices.get(0).price, "."));
                holder.tv_rmbNum.setText("." + StringUtils.substringAfter(itemsArrayList.get(position).prices.get(0).price, ".") + "元");
            } else {
                holder.tv_rmbPrice.setText(itemsArrayList.get(position).prices.get(0).price);
            }

            if (itemsArrayList.get(position).prices.get(1).price.contains(".")) {
                holder.tv_jpPrice.setText(StringUtils.substringBefore(itemsArrayList.get(position).prices.get(1).price, "."));
                holder.tv_jpNum.setText("." + StringUtils.substringAfter(itemsArrayList.get(position).prices.get(1).price, ".") + "円 / ");
            } else {
                holder.tv_jpPrice.setText(itemsArrayList.get(position).prices.get(1).price);

            }

        }

        holder.tv_productTilte.setText(itemsArrayList.get(position).name);
        holder.iv_productImg.setDefaultImageResId(R.drawable.img_loading);
        holder.iv_productImg.setImageUrlOfCommon(itemsArrayList.get(position).image, false);


        return convertView;
    }

    class ViewHolder {
        TextView tv_productTilte;
        TextView tv_rmbPrice;
        TextView tv_jpPrice;
        TextView tv_rmbNum;
        TextView tv_jpNum;
        RelativeLayout gridView_rl;
        MatchParentNetworkImage iv_productImg;
    }

    /**
     * @param rl
     * @param position
     * @备注：获取高度每个textview的高度,然后进行比较,把最高的设置为TextView的高度
     * @返回类型：void
     * @注意点:1,要把获取的item中TextView的高度存放到全局变量中,这样才会其作用. 2.一定要在addOnGlobalLayoutListener监听器中给TextView设置高度,
     * 禁止把高度取出,然后在getView中这只高度,这样是无效的
     * @设计思路:1.先把TextView的高度,获取出来 2.把高度存到全局变量中, 然后进行和原来的比较, 把大的存到全局变量中
     * 3.然后再把全局变量中的高度设置给TextView
     */
    public void initKeyRelativelayout(final View rl, final int position) {
        ViewTreeObserver vto2 = rl.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (position % Constant.REDCOMMEDN_GRIVEW_COLUMN_NUMS == 0) {
                    Constant.GRIVIEW_COLUMN_HEIGHT = 0;
                }
                if (rl.getHeight() > Constant.GRIVIEW_COLUMN_HEIGHT) {
                    Constant.GRIVIEW_COLUMN_HEIGHT = rl.getHeight();
                }
                setHeight(rl, Constant.GRIVIEW_COLUMN_HEIGHT);
            }
        });
    }

    public void setHeight(View ll, int height) {
        ll.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height));

    }

}

