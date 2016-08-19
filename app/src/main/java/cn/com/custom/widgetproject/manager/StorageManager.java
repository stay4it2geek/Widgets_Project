package cn.com.custom.widgetproject.manager;

import android.content.Context;
import android.content.SharedPreferences;

import cn.com.custom.widgetproject.constant.Constant;

/**
 * 存储管理类
 * Created by custom on 2016/6/6.
 */
public class StorageManager {

    /**
     * 根据保存Key存储数据
     *
     * @param context 上下文
     * @param key     保存数据Key
     * @param value   保存数据值
     * @param tabName 表名
     */
    private static void putValue(Context context, String key, String value, String tabName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(tabName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 根据保存Key获取数据
     *
     * @param context 上下文
     * @param key     保存数据Key
     * @param tabName 表名
     * @return
     */
    private static String getValue(Context context, String key, String tabName) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(tabName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }


    /**
     * 保存分类json字符串
     *
     * @param context 上下文
     * @param value   分类json字符串
     */
    public static void saveGenresCategory(Context context, String value) {
        putValue(context, Constant.SEARCH_CATEGORY_JSON_KEY, value, Constant.CATEGORY_STORAGE_JSON);

    }

    /**
     * 保存首页需要的分类json字符串
     *
     * @param context 上下文
     * @param value   分类json字符串
     */
    public static void saveTopPageNeedGenres(Context context, String value) {
        putValue(context, Constant.SEARCH_GENRES_JSON_KEY, value, Constant.CATEGORY_STORAGE_JSON);

    }

    /**
     * 保存选中的分类号
     *
     * @param context 上下文
     * @param value   教程画面版本
     */
    public static void saveTabCategoryNum(Context context, int value) {
        putValue(context, Constant.CATEGORY_SELECT_NUM_KEY, String.valueOf(value), Constant.CATEGORY_SELECT_NUM);
    }

    /**
     * 保存选中的分类顺序号
     *
     * @param context 上下文
     * @param value   教程画面版本
     */
    public static void saveTabPositionNum(Context context, int value) {
        putValue(context, Constant.TAB_SELECT_POSITION_KEY, String.valueOf(value), Constant.TAB_SELECT_POSITION);
    }

    /**
     * 保存时间戳
     *
     * @param context 上下文
     * @param value   教程画面版本
     */
    public static void saveTimeTamp(Context context, String value) {
        putValue(context, Constant.CATEGORY_JSON_TIMETAMP_KEY, value, Constant.CATEGORY_JSON_TIMETAMP);
    }
    /**
     * 保存默认icon图片路径集合的大小
     *
     * @param context 上下文
     */
    public static void saveDefaultIconListsize(Context context, String value) {
        putValue(context, "DefaulticonListsize", value, "sizeTable");
    }

    /**
     * 获取icon图片路径集合的大小
     *
     * @param context 上下文
     */
    public static String getHighlightIconListsize(Context context) {
        return getValue(context, "HighlighticonListsize","sizeTable");
    }
    /**
     * 保存高亮icon图片路径集合的大小
     *
     * @param context 上下文
     */
    public static void saveHighlightIconListsize(Context context, String value) {
        putValue(context, "HighlighticonListsize", value, "sizeTable");
    }

    /**
     * 获取默认icon图片路径集合的大小
     *
     * @param context 上下文
     */
    public static String getDefaultIconListsize(Context context) {
        return getValue(context, "DefaulticonListsize","sizeTable");
    }
    /**
     * 获取分类json字符串
     *
     * @param context 上下文
     * @return 返回分类json字符串，默认返回空字符串
     */
    public static String getGenresCategory(Context context) {
        return getValue(context, Constant.SEARCH_CATEGORY_JSON_KEY, Constant.CATEGORY_STORAGE_JSON);
    }

    /**
     * 获取分类json字符串
     *
     * @param context 上下文
     * @return 返回分类json字符串，默认返回空字符串
     */
    public static String getTopPageNeedGenres(Context context) {
        return getValue(context, Constant.SEARCH_GENRES_JSON_KEY, Constant.CATEGORY_STORAGE_JSON);
    }
    /**
     * 清除分类数据
     *
     * @param context
     */
    public static void clearGenresCategory(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.CATEGORY_STORAGE_JSON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }



    /**
     * 清除时间戳
     *
     * @param context
     */
    public static void clearTimeTamp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.CATEGORY_JSON_TIMETAMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * 清除翻译结果
     *
     * @param context
     */
    public static void clearTranslationResult(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TranslationTable", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 获取时间戳
     *
     * @param context 上下文
     * @return 返回时间戳，默认返回空字符串
     */
    public static String getTimeTamp(Context context) {
        return getValue(context, Constant.CATEGORY_JSON_TIMETAMP_KEY, Constant.CATEGORY_JSON_TIMETAMP);
    }


    /**
     * 获取选中的分类号
     *
     * @param context 上下文
     * @return 返回时间戳，默认返回空字符串
     */
    public static String getTabCateGoryNum(Context context) {
        return getValue(context, Constant.CATEGORY_SELECT_NUM_KEY, Constant.CATEGORY_SELECT_NUM);
    }

    /**
     * 获取选中的分类顺序号
     *
     * @param context 上下文
     * @return 返回时间戳，默认返回空字符串
     */
    public static String getTabPositionNum(Context context) {
        return getValue(context, Constant.TAB_SELECT_POSITION_KEY, Constant.TAB_SELECT_POSITION);
    }

    /**
     * 清楚数字顺序或者位置
     *
     * @param context
     * @param tabName 表名
     */
    public static void clearNum(Context context, String tabName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(tabName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 获取翻译好的内容
     *
     * @param context 上下文
     * @return 返回翻译好的内容，默认返回空字符串
     */
    public static String getTranslationResult(Context context,String productCodeForKey) {
        return getValue(context,productCodeForKey, "TranslationTable");
    }


    /**
     * 保存翻译结果
     *
     * @param context 上下文
     */
    public static void saveTranslationResult(Context context, String translationResultForValue,String productCodeForKey) {
        putValue(context,productCodeForKey,translationResultForValue, "TranslationTable");
    }


    /**
     * 获取检索关键词
     *
     * @param context 上下文
     * @return 返回翻译好的内容，默认返回空字符串
     */
    public static String getSearchKeyWord(Context context) {
        return getValue(context,"searchkeyword", "searchkeywordTable");
    }


    /**
     * 保存检索关键词
     *
     * @param context 上下文
     */
    public static void saveSearchKeyWord(Context context, String searchkeywordValue) {
        putValue(context,"searchkeyword",searchkeywordValue, "searchkeywordTable");
    }

}
