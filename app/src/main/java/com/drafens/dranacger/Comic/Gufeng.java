package com.drafens.dranacger.Comic;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gufeng extends Sites {
    private static String TAG = "GUFENG";
    private static String url_gufeng = "http://m.gufengmh.com";
    @Override
    public List<Book> getSearch(String search_id) throws MyNetWorkException {
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            String url = url_gufeng + "/search/?keywords=" + search_id;
            document = Jsoup.connect(url).get();
            Elements elements = document.select("[class=UpdateList]");
            int i=0;
            for(Element ele:elements.select("[class=itemTxt]")) {
                String id=ele.select("a").attr("href").replace(url_gufeng,"");
                String name = ele.select("a").text();
                String author=ele.select("p").get(0).text();
                String type=ele.select("p").get(1).text();
                String updateTime="更新于："+ele.select("p").get(2).text();
                String icon=elements.select("[class=itemImg]").get(i).select("mip-img").attr("src");
                String briefInfo="";
                String updateChapter_id = "";
                String updateChapter = "";
                String lastReadChapter="";
                String lastReadChapter_id="";
                String lastReadTime="";
                Book book = new Book(name,updateChapter,updateChapter_id,updateTime,author,type,id,icon,Sites.GUFENG,lastReadChapter,lastReadChapter_id,lastReadTime,briefInfo);
                bookList.add(book);
                i++;
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book, String lastReadChapter, String lastReadChapter_id, String lastReadTime) throws MyNetWorkException {
        book.setLastReadChapter(lastReadChapter);
        book.setLastReadChapter_id(lastReadChapter_id);
        book.setLastReadTime(lastReadTime);
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyNetWorkException {
        List<Episode> episodeList=new ArrayList<>();
        try {
            String url = url_gufeng + book_id;
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("[id=chapter-list-1]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.select("span").text();
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
            elements = document.select("[id=chapter-list-13]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.select("span").text();
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        return episodeList;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetWorkException {
        String url = url_gufeng + episode_id + ".html";
        String[] strings;
        List<String> urlList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            if (document.toString().contains("mip-img")){
                int total = Integer.parseInt(document.select("[id=k_total]").text());
                String s = document.select("mip-img").get(0).attr("src");
                urlList.add(s);
                for (int i=2;i<=total;i++) {
                    document = Jsoup.connect(url_gufeng + episode_id + "-" + i + ".html").get();
                    s = document.select("mip-img").get(0).attr("src");
                    urlList.add(s);
                }
            }else {
                Element element = document.select("script").get(1);
                String s = element.toString();
                int begin = s.indexOf("chapterPath") + 15;
                String chapterPath = "/" + s.substring(begin, s.indexOf(";", begin) - 1);
                begin = s.indexOf("[") + 1;
                String chapterImages = s.substring(begin, s.indexOf("]", begin));
                strings = chapterImages.split(",");
                for (int i = 0; i < strings.length; i++) {
                    urlList.add("http://res.gufengmh.com" + chapterPath + strings[i].substring(1, strings[i].length() - 1));
                }
            }
            Log.d(TAG, urlList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }
}
