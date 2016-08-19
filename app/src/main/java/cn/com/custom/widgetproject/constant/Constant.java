package cn.com.custom.widgetproject.constant;

/**
 * 常量类
 * Created by custom on 2016/6/6.
 */
public class Constant {


    /**
     * 不可变常量区
     */
//存储分类json的key
    public static final String SEARCH_GENRES_JSON_KEY = "searchGeneres_json_Key";
    //存储分类json的key
    public static final String SEARCH_CATEGORY_JSON_KEY = "searchCategory_json_Key";
    //存储json中时间戳的key
    public static final String CATEGORY_JSON_TIMETAMP_KEY = "timeTampsKey";
    //存储选择tab分类的key
    public static final String CATEGORY_SELECT_NUM_KEY = "tabSelect_Key";
    //存储选择tab的位置key
    public static final String TAB_SELECT_POSITION_KEY = "tab_num_positionKey";
    //分类号表名
    public static final String CATEGORY_SELECT_NUM = "categoryCode";
    //tab位置表名
    public static final String TAB_SELECT_POSITION = "serialcategoryStorage";
    //分类json数据表名
    public static final String CATEGORY_STORAGE_JSON = "category_json";
    //时间戳表名
    public static final String CATEGORY_JSON_TIMETAMP = "json_timetmap";
    //网址标志key
    public static final String WEB_URL = "webUrl";
    //产品编码或者isbn编码
    public static final String PRODUCT_CODE = "code";
    // 二维码请求的type
    public static final String REQUEST_SCAN_TYPE = "type";
    //普通类型，扫完即关闭
    public static final int REQUEST_SCAN_TYPE_COMMON = 0;
    //服务商登记类型，扫描
    public static final int REQUEST_SCAN_TYPE_REGIST = 1;

    /**
     * 扫描类型
     */
    public static final String REQUEST_SCAN_MODE = "ScanMode";

    //条形码： REQUEST_SCAN_MODE_BARCODE_MODE
    public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;

    //二维码：REQUEST_SCAN_MODE_ALL_MODE
    public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;

    // 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE

    public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;


    //**************************************************************************************************************************************************************

    /**
     * 可变常量区
     */
    public static int GRIVIEW_COLUMN_HEIGHT = 0;// griview设置的高度
    public static int GRIVEW_COLUMN_NUMS = 2;//    通用分类gridview列数
    public static int REDCOMMEDN_GRIVEW_COLUMN_NUMS = 3;// 推荐分类gridview列数

    public static boolean isFirstLoadActivity = false;// 推荐分类gridview列数

}
