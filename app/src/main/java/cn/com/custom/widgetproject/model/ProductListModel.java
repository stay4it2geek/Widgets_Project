package cn.com.custom.widgetproject.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 产品列表模型
 * Created by custom on 2016/6/14.
 */
public class ProductListModel extends JpCompareBaseModel implements Serializable {

    public ArrayList<Items> items;
    public String next;

    public static class Items implements Serializable {
        public String image;//产品图片网址
        public String genre;
        public ArrayList<Prices> prices;
        public String language;
        public String name;
        public String code;

        public static class Prices implements Serializable {
            public String price;
            public String currency;
        }
    }
}
