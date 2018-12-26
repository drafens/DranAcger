package com.drafens.dranacger.Animation;

import android.util.Base64;
import android.util.Log;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Sites;
import com.drafens.dranacger.Tools.EscapeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Kirikiri extends Sites{
    private static String url_kirikir = "http://www.kirikiri.tv";

    @Override
    public List<Book> getSearch(String search_id) throws MyNetWorkException{
        List<Book> bookList = new ArrayList<>();
        Document document;
        try {
            String url = url_kirikir + "/index.php?m=vod-search";
            document = Jsoup.connect(url)
                    .data("wd", search_id)
                    .post();
            Elements elements = document.select("div[class=wrap center]");
            for(Element ele:elements.select("li")) {
                String id = ele.select("a").attr("href");
                Book book = new Book("","","","","","","","","","","","","");
                //getBook(id,"","","");
                bookList.add(book);
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book, String lastReadChapter, String lastReadChapter_id, String lastReadTime) throws MyNetWorkException {
        try {
            String url = url_kirikir + book.getId();
            Document document = Jsoup.connect(url).get();
            String id = book.getId();
            Elements elements = document.select("div[class=img_wrap]");
            String name = elements.select("img").attr("alt");
            String icon = url_kirikir+elements.select("img").attr("data-original");
            String type = elements.select("em[class=note_text]").text();
            elements = document.select("div[class=detail_wrap]");
            String author = elements.select("li").get(1).select("a").text();
            String updateTime = elements.select("li").get(4).select("span[class=ctime]").text();
            String briefInfo = elements.select("li").get(6).text();
            elements = document.select("div[class=mlist scroll]");
            Element element = elements.select("a").last();
            String updateChapter_id = element.attr("href");
            int index1 = updateChapter_id.indexOf("src");
            int index2 = updateChapter_id.indexOf("num");
            updateChapter_id = updateChapter_id.substring(0,index1)+"src-1-"+updateChapter_id.substring(index2);
            updateChapter_id=updateChapter_id.substring(0,updateChapter_id.indexOf(".html"));
            String updateChapter = element.text();
            book = new Book(name,updateChapter,updateChapter_id,updateTime,author,type,id,icon,Sites.KIRIKIRI
                    ,lastReadChapter,lastReadChapter_id,lastReadTime,briefInfo);
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyNetWorkException {
        boolean getEpisodeable = true;
        List<Episode> episodeList=new ArrayList<>();
        try {
            String url = url_kirikir + book_id;
            Document document = Jsoup.connect(url).get();
            for (Element element : document.select("div[class=playlist big_shadow mt30]")) {
                Elements elements = element.select("div[class=mlist scroll]");
                if (getEpisodeable) {
                    for (Element ele : elements.select("a")) {
                        String name = ele.text();
                        String id = ele.attr("href");
                        id = id.substring(0,id.indexOf("html")-1);
                        Episode episode = new Episode(name, id);
                        episodeList.add(episode);
                    }
                }
                getEpisodeable=false;
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return episodeList;
    }

    @Override
    public List<String> getVideo(String episode_id) throws MyNetWorkException {
        String[] myServer= {"http://moe.chinagame.net.cn","http://www.nightdream.cc:8080"};
        List<String> videoUrl=new ArrayList<>();
        List<Integer> serveList=new ArrayList<>();
        try {
            String url = url_kirikir + episode_id + ".html";
            Document document = Jsoup.connect(url).get();
            Element element = document.select("div[id=play_wrap][class=play_wrap]").select("script").get(0);
            String string = element.toString();
            int begin = string.indexOf("base64decode") + 14;
            string = string.substring(begin, string.indexOf("'", begin));
            string = new String(Base64.decode(string.getBytes(), Base64.DEFAULT));
            string = EscapeUtils.unescape(string);
            //找get url
            String[] str_arr = string.split("\\$\\$\\$");
            for (int i = 0; i < str_arr.length; i++) {
                String[] strings = str_arr[i].split("#");
                int position = Integer.parseInt(episode_id.substring(episode_id.indexOf("num") + 4)) - 1;
                string = strings[position];
                string = string.substring(string.indexOf("$") + 1);
                videoUrl.add(i, string);
            }
            //找host
            for (Element ele : document.select("div[class=playlist big_shadow mt30]")) {
                String s = ele.select("h4").text();
                if (s.equals("キリキリΔ")) {
                    serveList.add(1);
                } else if (s.equals("キリキリμ")) {
                    serveList.add(0);
                }
            }
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        if (serveList.size()>0) {
            for (int i = 0; i < videoUrl.size(); i++) {
                try {
                    String string = myServer[serveList.get(i)] + videoUrl.get(i);
                    Document doc = Jsoup.connect(string)
                            .referrer("http://kirikiri.tv" + episode_id.replace("src-1","src-"+String.valueOf(i+1)))
                            .get();
                    string = doc.select("body").select("script").toString();
                    int begin = string.indexOf("url: '") + 6;
                    videoUrl.set(i, string.substring(begin, string.indexOf("'", begin))
                            .replace("amp;", "")
                            .replace("\\/", "/")
                            .replace("\\-", "-")
                            .replace("\\.", ".")
                            .replace("\\?", "?")
                            .replace("\\x26", "&"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return videoUrl;
    }
}
