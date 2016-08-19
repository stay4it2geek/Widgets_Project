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
public class ProductListAdapter extends BaseAdapter {
    ArrayList<ProductListModel.Items> itemsArrayList;
    Context context;
    ArrayList<String> addModelFlagArray;
    int width;

    public ProductListAdapter(ArrayList<ProductListModel.Items> arayList, Context context, ArrayList<String> addModelFlagArray) {
        super();
        this.addModelFlagArray = addModelFlagArray;
        this.itemsArrayList = arayList;
        this.context = context;

    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
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
                    R.layout.item_product_gridview, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.tv_productTilte = (TextView) convertView
                    .findViewById(R.id.tv_productTilte);
            holder.tv_rmbPrice = (TextView) convertView
                    .findViewById(R.id.tv_rmbPrice);
            holder.tv_jpPrice = (TextView) convertView
                    .findViewById(R.id.tv_jpPrice);
            holder.tv_jpPrice = (TextView) convertView
                    .findViewById(R.id.tv_jpPrice);
            holder.iv_productImg = (MatchParentNetworkImage) convertView
                    .findViewById(R.id.iv_productImg);
            holder.comom_gridview_rl = (RelativeLayout) convertView
                    .findViewById(R.id.comom_gridview_rl);
            holder.tv_rmbNum = (TextView) convertView
                    .findViewById(R.id.tv_rmbNum);
            holder.tv_jpNum = (TextView) convertView
                    .findViewById(R.id.tv_jpNum);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (addModelFlagArray.size() > 0 && StringUtil.isEqual("true", addModelFlagArray.get(1))) {

            /**
             * 如果加载成功1条，虚拟增加4条
             *   if(第0/1/2/3/4...个位置>productList只有一个，size为1，起始位下标为0){  //虚拟条目下标大于真实条目所拥有的下标
             *
             *         holder.comom_gridview_rl.setVisibility(View.GONE);
             *
             *   }else (0==0||0<0){  ////虚拟条目下标等于或小于真实条目所拥有的下标
             *        setItemView(position, holder);
             *   }
             *
             *   如果加载成功2条，虚拟增加3条
             *    if(第0/1/2/3/4....>productList只有2个，size为2，起始位下标为0，第二个下标为1){
             *
             *         holder.comom_gridview_rl.setVisibility(View.GONE);
             *
             *   }else (0<=1){////虚拟条目下标等于或小于真实条目所拥有的下标
             *        setItemView(position, holder);
             *   }
             *
             *
             * TODO HASDO  important:  Integer.parseInt(addModelFlagArray.get(0))的值需要和数组下标一样，
             *  TODO HASDO  从0开始，查看SearchListActivity的addVitrialData方法
             *
             *
              */

            if (position > Integer.parseInt(addModelFlagArray.get(0))) {
                holder.comom_gridview_rl.setVisibility(View.GONE);

            } else {
                setItemView(position, holder);
            }

        } else if (addModelFlagArray.size() == 0) {
//            DevUtil.e("addray", addModelFlagArray.size()+"");

            setItemView(position, holder);
            holder.comom_gridview_rl.setVisibility(View.VISIBLE);
        }


//        initKeyRelativelayout(holder.comom_gridview_rl,position);
        return convertView;
    }

    private void setItemView(int position, ViewHolder holder) {
        if (itemsArrayList!=null&itemsArrayList.size() > 0&&itemsArrayList.get(position)!=null&&itemsArrayList.get(position).prices != null) {
            if(StringUtil.isEmpty(itemsArrayList.get(position).prices.get(0).price)){
                holder.tv_rmbPrice.setText("-");
            }
            if(StringUtil.isEmpty(itemsArrayList.get(position).prices.get(1).price)){
                holder.tv_jpPrice.setText("-");
            }
            if (itemsArrayList.get(position).prices.get(0).currency.equals("cny")) {
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
            } else {
                if (itemsArrayList.get(position).prices.get(1).price.contains(".")) {
                    holder.tv_rmbPrice.setText(StringUtils.substringBefore(itemsArrayList.get(position).prices.get(1).price, "."));
                    holder.tv_rmbNum.setText("." + StringUtils.substringAfter(itemsArrayList.get(position).prices.get(1).price, ".") + "元");
                } else {
                    holder.tv_rmbPrice.setText(itemsArrayList.get(position).prices.get(1).price);
                }

                if (itemsArrayList.get(position).prices.get(0).price.contains(".")) {
                    holder.tv_jpPrice.setText(StringUtils.substringBefore(itemsArrayList.get(position).prices.get(0).price, "."));
                    holder.tv_jpNum.setText("." + StringUtils.substringAfter(itemsArrayList.get(position).prices.get(0).price, ".") + "円 / ");
                } else {
                    holder.tv_jpPrice.setText(itemsArrayList.get(position).prices.get(0).price);

                }
            }
        }

        RelativeLayout.LayoutParams ps = (RelativeLayout.LayoutParams) holder.iv_productImg.getLayoutParams();
        ps.height = width / 2 - 45;
        holder.iv_productImg.setLayoutParams(ps);
        holder.iv_productImg.setDefaultImageResId(R.drawable.img_loading);
        holder.iv_productImg.setImageUrlOfCommon(itemsArrayList.get(position).image, false);

        holder.tv_productTilte.setText(itemsArrayList.get(position).name);
    }

    class ViewHolder {
        TextView tv_productTilte;
        TextView tv_rmbPrice;
        TextView tv_jpPrice;
        TextView tv_rmbNum;
        TextView tv_jpNum;
        RelativeLayout comom_gridview_rl;
        MatchParentNetworkImage iv_productImg;
    }

    /**
     * @param view
     * @param position
     * @备注：获取高度每个view的高度,然后进行比较,把最高的设置为View的高度
     * @返回类型：void
     * @注意点:1,要把获取的item中View的高度存放到全局变量中,这样才会其作用. 2.一定要在addOnGlobalLayoutListener监听器中给View设置高度,
     * 禁止把高度取出,然后在getView中这只高度,这样是无效的
     * @设计思路:1.先把View的高度,获取出来 2.把高度存到全局变量中, 然后进行和原来的比较, 把大的存到全局变量中
     * 3.然后再把全局变量中的高度设置给View
     */
    public void initKeyRelativelayout(final View view, final int position) {
        ViewTreeObserver vto2 = view.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (position % Constant.GRIVEW_COLUMN_NUMS == 0) {
                    Constant.GRIVIEW_COLUMN_HEIGHT = 0;
                }
                if (view.getHeight() > Constant.GRIVIEW_COLUMN_HEIGHT) {
                    Constant.GRIVIEW_COLUMN_HEIGHT = view.getHeight();
                }
                setHeight(view, Constant.GRIVIEW_COLUMN_HEIGHT);
            }
        });
    }

    public void setHeight(View ll, int height) {
        ll.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height));

    }
}

