package cn.com.custom.widgetproject.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * top分类数据模型
 * Created by custom on 2016/6/6.
 */

public class SearchCategoryModel extends JpCompareBaseModel implements Serializable{

    private static final long serialVersionUID = -1283958106855645141L;

    public String timestamp;//时间戳
    public ArrayList<Geners> genres;//分类模型数据的数组

    /**
     * 类别
     */
   public static class Geners implements Serializable{
        private static final long serialVersionUID =-1254534576855645141L;

        public String name;//类别名称
       public String image;//类别图像
       public String genre;//类别ID
       public String icon_default;//类别ICON默认图
       public String icon_highlight;//类别ICON高亮图


   }

}