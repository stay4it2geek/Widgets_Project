package cn.com.custom.widgetproject.config;

/**
 * api配置
 * Created by imjx on 2016/6/6.
 */
public class ApiConfig {

    public interface Scheme {
        String JPANSHOP_SECHEME = "http";
    }

    public interface Domain {
        String JPANSHOP_DOMAIN_API ="api.parity.imjx.com.cn";
    }

    public interface PathSegment {
        String SEGMENT_API = "api";
        String SEGMENT_LIST = "list";
        String SEGMENT_PRODUCT = "product";
        String VERSION ="v1" ;
    }


    public interface RequestPath {
        String SEARCH_CATEGORY = "category";
        String BANNER_ADS = "ads";
        String DETAIL = "detail";
        String SEARCH = "search";
        String SEARCH_PRODUCT_BY_CATE = "byCategory";
        String SEARCH_PRODUCT_HOTWORD = "keywords";


    }
    public interface TranslatConfig{
        //申请者开发者id
        public static final   String APP_ID = "20160620000023626";
        //申请成功后的证书token
        public static final  String TOKEN = "M_H4pR53wHZBx4MbElOD";
        //翻译的请求网址
        public static final  String TRANSLATE_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    }
}
