package cn.com.custom.widgetproject.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import cn.com.custom.widgetproject.R;

import cn.com.custom.widgetproject.activity.WebViewActivity;
import cn.com.custom.widgetproject.constant.Constant;
import cn.com.custom.widgetproject.manager.ApiManager;
import cn.com.custom.widgetproject.model.DetailItemModel;
import cn.com.custom.widgetproject.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 对比商铺列表适配器
 */
public class CompareShopAdapter extends BaseAdapter {

    private Activity context;
    //详情的比价店铺集合
    private ArrayList<DetailItemModel.Item.Shops> shopses;

    public CompareShopAdapter(Activity context,ArrayList<DetailItemModel.Item.Shops> shopses) {

        this.context = context;
        this.shopses = shopses;

    }


    @Override
    public DetailItemModel.Item.Shops getItem(int position) {

        return shopses.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getCount() {

        return shopses.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_compare_shoplist, null);

            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.tv_shopName = (TextView) convertView
                    .findViewById(R.id.tv_shopName);

            holder.tv_rmbPrice = (TextView) convertView
                    .findViewById(R.id.tv_rmbPrice);
            holder.tv_rmbNum = (TextView) convertView
                    .findViewById(R.id.tv_rmbNum);
            holder.tv_jpNum = (TextView) convertView
                    .findViewById(R.id.tv_jpNum);
            holder.tv_jpPrice = (TextView) convertView
                    .findViewById(R.id.tv_jpPrice);
            holder.iv_logo = (NetworkImageView) convertView
                    .findViewById(R.id.iv_logo);
            holder.rl_checksee = (FrameLayout) convertView
                    .findViewById(R.id.rl_checksee);
            holder.seeImg = (ImageView) convertView
                    .findViewById(R.id.seeImg);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (shopses!=null&shopses.size() > 0&&shopses.get(position)!=null&&shopses.get(position).prices != null) {
            if(StringUtil.isEmpty(shopses.get(position).prices.get(0).price)){
                holder.tv_rmbPrice.setText("-");
            }
            if(StringUtil.isEmpty(shopses.get(position).prices.get(1).price)){
                holder.tv_jpPrice.setText("-");
            }
            if(shopses.get(position).prices.get(1).currency.equals("cny")) {
                if (shopses.get(position).prices.get(1).price.contains(".")) {
                    holder.tv_rmbPrice.setText(StringUtils.substringBefore(shopses.get(position).prices.get(1).price, "."));
                    holder.tv_rmbNum.setText("." + StringUtils.substringAfter(shopses.get(position).prices.get(1).price, ".") + "元");
                } else {
                    holder.tv_rmbPrice.setText(shopses.get(position).prices.get(1).price);
                }
                if (shopses.get(position).prices.get(0).price.contains(".")) {
                    holder.tv_jpPrice.setText(StringUtils.substringBefore(shopses.get(position).prices.get(0).price, "."));
                    holder.tv_jpNum.setText("." + StringUtils.substringAfter(shopses.get(position).prices.get(0).price, ".") + "円 / ");
                } else {
                    holder.tv_jpPrice.setText(shopses.get(position).prices.get(0).price);
                }
            }else{
                if (shopses.get(position).prices.get(0).price.contains(".")) {
                    holder.tv_rmbPrice.setText(StringUtils.substringBefore(shopses.get(position).prices.get(0).price, "."));
                    holder.tv_rmbNum.setText("." + StringUtils.substringAfter(shopses.get(position).prices.get(0).price, ".") + "元");
                } else {
                    holder.tv_rmbPrice.setText(shopses.get(position).prices.get(1).price);
                }
                if (shopses.get(position).prices.get(1).price.contains(".")) {
                    holder.tv_jpPrice.setText(StringUtils.substringBefore(shopses.get(position).prices.get(1).price, "."));
                    holder.tv_jpNum.setText("." + StringUtils.substringAfter(shopses.get(position).prices.get(1).price, ".") + "円 / ");
                } else {
                    holder.tv_jpPrice.setText(shopses.get(position).prices.get(1).price);
                }
            }
        }

        holder.seeImg.setOnClickListener(new MyListener(position));
        holder.rl_checksee.setOnClickListener(new MyListener(position));

        try {
            holder.tv_shopName.setText(URLDecoder.decode(shopses.get(position).name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.iv_logo.setImageUrl(shopses.get(position).logo, ApiManager.getInstance(context).getImageLoader());


        return convertView;
    }

    class ViewHolder {
        NetworkImageView iv_logo;
        TextView tv_shopName;
        TextView tv_rmbNum;
        TextView tv_jpNum;
        TextView tv_rmbPrice;
        TextView tv_jpPrice;
        ImageView seeImg;
        FrameLayout rl_checksee;//查看的布局

    }

    /**
     * 自定义监听类
     */
    class MyListener implements View.OnClickListener {
        //条目的位置
        int mPosition;

        public MyListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(new Intent(context, WebViewActivity.class));
            intent.putExtra(Constant.WEB_URL, shopses.get(mPosition).url);
            context.startActivity(intent);

        }
    }


}
