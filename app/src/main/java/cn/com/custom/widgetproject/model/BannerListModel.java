package cn.com.custom.widgetproject.model;

import java.util.ArrayList;

/**
 *  广告图模型
 * Created by custom on 2016/6/7.
 */
public class BannerListModel extends JpCompareBaseModel {

    public ArrayList<Banner> banners;//广告数据集合

    public static class Banner {
        public String image;
        public String url;

    }
}
