package cn.com.custom.widgetproject.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 产品详情数据模型
 * Created by custom on 2016/6/14.
 */
public class DetailItemModel extends  JpCompareBaseModel implements Serializable{

    public Item item;

    public static class Item implements Serializable{
        public ArrayList<String> images;
        public ArrayList<Shops> shops;
        public String name;
        public String genre;
        public String detail;
        public String code;
        public String language;

        public static class Shops implements Serializable{
            public String logo;
            public String name;
            public String url;
            public ArrayList<Prices> prices;

            public static class Prices implements Serializable{
                public String price;
                public String currency;

            }


        }
    }


}
