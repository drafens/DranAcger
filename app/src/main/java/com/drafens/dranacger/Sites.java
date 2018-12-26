package com.drafens.dranacger;

import com.drafens.dranacger.Animation.Kirikiri;
import com.drafens.dranacger.Comic.Chuixue;
import com.drafens.dranacger.Comic.Gufeng;
import com.drafens.dranacger.Comic.Pufei;
import com.drafens.dranacger.Error.MyNetWorkException;

import java.util.ArrayList;
import java.util.List;

public abstract class Sites {
    public static final String CHUIXUE = "m.chuixue.net";
    public static final String KIRIKIRI = "www.kirikiri.tv";
    public static final String PUFEI = "www.pufei.net";
    public static final String GUFENG = "m.gufengmh.com";
    public static final String ANIMATION_GROUP[] = {Sites.KIRIKIRI};
    public static final String COMIC_GROUP[] = {Sites.CHUIXUE,Sites.PUFEI,Sites.GUFENG};
    public static final String NOVEL_GROUP[] = {};

    /**
     *
     * @param search_id 字符串搜索内容：狐妖
     * @return Book_id列表
     * 解析失败返回空列表
     */
    public abstract List<Book> getSearch(String search_id)throws MyNetWorkException;

    /**
     *
     * @param book
     * @param lastReadChapter 非收藏接收""
     * @param lastReadChapter_id 非收藏接收""
     * @param lastReadTime 非收藏接收""
     * @return Book格式的对象
     * 解析失败返回null
     */
    public abstract Book getBook(Book book,String lastReadChapter,String lastReadChapter_id,String lastReadTime)throws MyNetWorkException;

    /**
     *
     * @param book_id
     * @return Episode对象列表
     * 解析失败返回空列表
     */
    public abstract List<Episode> getEpisode(String book_id)throws MyNetWorkException;

    /**
     *
     * @param episode_id
     * @return 图片url列表
     * 解析失败返回空列表
     */
    public List<String> getImage(String episode_id)throws MyNetWorkException{
        return null;
    }

    /**
     *
     * @param episode_id
     * @return 视频不同服务器下url的列表
     */
    public List<String> getVideo(String episode_id)throws MyNetWorkException{
        return null;
    }
    /**
     *
     * COMIC项目必须采用
     * @param strings 图片url数组
     * @return 格式化后的图片url列表
     */
    public static List<String> getUrlsList(List<String> strings){
        List<String> stringList=strings;
        stringList.add(0,"0");
        stringList.add("0");
        return stringList;
    }

    /**
     *
     * @param sites 为本文件静态变量，保存在book.getWebSite()中
     * @return 对应网站对象
     */
    public static Sites getSites(String sites){
        switch (sites){
            case CHUIXUE:
                return new Chuixue();
            case PUFEI:
                return new Pufei();
            case GUFENG:
                return new Gufeng();
            case KIRIKIRI:
                return new Kirikiri();
        }
        return null;
    }
}
