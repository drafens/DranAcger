package com.drafens.dranacger.Comic;

import android.util.Base64;

import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Book;
import com.drafens.dranacger.Error.MyNetWorkException;
import com.drafens.dranacger.Sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pufei extends Sites {
    private static String url_pufei = "http://m.pufei.net";

    @Override
    public List<Book> getSearch(String search_id) throws MyNetWorkException{
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            search_id = URLEncoder.encode(search_id, "gb2312");
            String url = url_pufei + "/e/search/?searchget=1&tbname=mh&show=title,player,playadmin,bieming,pinyin&tempid=4&keyboard=" + search_id;
            document = Jsoup.connect(url).get();
            Elements elements = document.select("ul[id=detail]");
            for(Element ele:elements.select("li")) {
                String id=ele.select("a").first().attr("href");
                String icon = ele.select("img").attr("data-src");
                String name = ele.select("h3").text();
                String author=ele.select("dd").get(0).text();
                String type=ele.select("dd").get(1).text();
                String updateChapter = ele.select("dd").get(2).text();
                String updateTime="更新于："+elements.select("dd").get(3).text();
                String updateChapter_id = "";
                String lastReadChapter="";
                String lastReadChapter_id="";
                String lastReadTime="";
                String briefInfo="";
                Book book = new Book(name,updateChapter,updateChapter_id,updateTime,author,type,id,icon,Sites.PUFEI,lastReadChapter,lastReadChapter_id,lastReadTime,briefInfo);
                bookList.add(book);
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return bookList;
    }

    @Override
    public Book getBook(Book book,String lastReadChapter,String lastReadChapter_id,String lastReadTime) throws MyNetWorkException {
        try {
            String url = url_pufei + book.getId();
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=book-detail]");
            String briefInfo=elements.select("div[id=bookIntro]").text();
            Element element = document.select("div[class=chapter]").select("li").get(0);
            String updateChapter_id = element.select("a").attr("href");
            updateChapter_id = updateChapter_id.substring(0,updateChapter_id.indexOf(".html"));
            book.setLastReadChapter(lastReadChapter);
            book.setLastReadChapter_id(lastReadChapter_id);
            book.setLastReadTime(lastReadTime);
            book.setBriefInfo(briefInfo);
            book.setUpdateChapte_id(updateChapter_id);
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyNetWorkException {
        List<Episode> episodeList=new ArrayList<>();
        try {
            String url = url_pufei + book_id;
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=chapter]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.attr("title");
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html"));
                Episode episode = new Episode(name,id);
                episodeList.add(episode);
            }
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        Collections.reverse(episodeList);
        return episodeList;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetWorkException {
        String url = url_pufei + episode_id + ".html";
        String[] strings;
        List<String> urlList = new ArrayList<>();
        String[] replace_old = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String string;
        try {
            Document document = Jsoup.connect(url).get();
            Element element = document.select("script[type=text/javascript]").get(5);
            string = element.toString();
            int begin = string.indexOf("cp=\"")+4;
            string=string.substring(begin,string.indexOf("\"",begin));
            string = new String(Base64.decode(string.getBytes(),Base64.DEFAULT));
            string=string.substring(string.indexOf("}('")+3,string.indexOf("split")-2);
            int index=string.indexOf("]");
            String s1=string.substring(string.indexOf("[")+3,index-2).replace("\\'","");
            String s2=string.substring(string.indexOf("'",index+2)+1);
            String[] s1_a=s1.split(",");
            String[] s2_arr=s2.split("\\|");
            String[][] s2_a=new String[s2_arr.length/62+1][62];
            for (int i = 0; i < s1_a.length; i++) {
                s1_a[i] = "/"+s1_a[i].replace(".","/./")+"/";
            }
            for (int i=0;i<s2_a.length;i++){
                for(int j=0;j<62&&j<s2_arr.length-i*62;j++){
                    s2_a[i][j]=s2_arr[i*62+j];
                }
            }
            strings = new String[s1_a.length];
            String t_value;
            for (int t=0;t<s2_a.length;t++){
                if(t==0) {
                    t_value="";
                }else {
                    t_value= String.valueOf(t);
                }
                for (int i=0;i<s1_a.length;i++){
                    for (int j=0;j<62;j++) {
                        if (s2_a[t][j] != null && !(s2_a[t][j].equals(""))) {
                            s1_a[i] = s1_a[i].replace("/" + t_value + replace_old[j] + "/", "/" + s2_a[t][j] + "/");
                            s1_a[i] = s1_a[i].replace("/" + t_value + replace_old[j] + "/", "/" + s2_a[t][j] + "/");
                            strings[i] = "http://res.img.pufei.net/" + s1_a[i].substring(1, s1_a[i].length() - 1).replace("/./",".");
                        }
                    }
                }
            }
            for (int i=0;i<s1_a.length;i++){
                urlList.add(strings[i]);
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return urlList;
    }
}
