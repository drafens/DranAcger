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

public class Chuixue extends Sites{
    private static String url_chuixue = "http://m.chuixue.net";

    @Override
    public List<Book> getSearch(String search_id)throws MyNetWorkException{
        List<Book> bookList=new ArrayList<>();
        Document document;
        try {
            search_id = URLEncoder.encode(search_id, "gb2312");
            String url = url_chuixue + "/e/search/?searchget=1&tbname=movie&tempid=1&show=title,keyboard&keyboard=" + search_id;
            document = Jsoup.connect(url).get();
            Elements elements = document.select("ul[id=detail]");
            for(Element ele:elements.select("li")) {
                String id=ele.select("a").first().attr("href");
                Book book = getBook(id,"","","");
                bookList.add(book);
            }
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        return bookList;
    }

    @Override
    public Book getBook(String book_id,String lastReadChapter,String lastReadChapter_id,String lastReadTime) throws MyNetWorkException{
        Book book;
        try {
            String url = url_chuixue + book_id.replace("mh","manhua");
            Document document = Jsoup.connect(url).get();
            String id=book_id;
            String name = document.select("div[class=main-bar bar-bg1]").select("h1").text();
            Elements elements = document.select("div[class=book-detail]");
            String author=elements.select("dd").get(2).text();
            String type=elements.select("dd").get(1).text();
            String updateChapter=elements.select("dd").get(4).select("a").attr("title");
            String updateChapter_id=elements.select("dd").get(4).select("a").attr("href");
            updateChapter_id=updateChapter_id.substring(0,updateChapter_id.indexOf(".html")).replace("http://www.chuixue.net/","/");
            String updateTime="更新于："+elements.select("dd").get(3).text();
            String icon=elements.select("img").attr("src").replace("zlcomic.com","chuixue.net").replace("733mh.com","chuixue.net");
            String briefInfo=elements.select("div[id=bookIntro]").text();
            book = new Book(name,updateChapter,updateChapter_id,updateTime,author,type,id,icon,Sites.CHUIXUE,lastReadChapter,lastReadChapter_id,lastReadTime,briefInfo);
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        return book;
    }

    @Override
    public List<Episode> getEpisode(String book_id) throws MyNetWorkException {
        List<Episode> episode_id=new ArrayList<>();
        try {
            String url = url_chuixue + book_id.replace("mh", "manhua");
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div[class=chapter]");
            for(Element ele:elements.select("li").select("a")) {
                String name=ele.attr("title");
                String id=ele.attr("href");
                id=id.substring(0,id.indexOf(".html")).replace("http://www.chuixue.net/","/");
                Episode episode = new Episode(name,id);
                episode_id.add(episode);
            }
        }catch(Exception e){
            throw new MyNetWorkException();
        }
        Collections.reverse(episode_id);
        return episode_id;
    }

    @Override
    public List<String> getImage(String episode_id) throws MyNetWorkException {
        String url = url_chuixue + episode_id.replace("mh","manhua") + ".html";
        String[] strings;
        List<String> urlList = new ArrayList<>();
        String[] replace_old = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String string;
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("script[type=text/javascript]");
            string = elements.toString();
            if (string.indexOf("photosr[1]")==-1){
                int begin = string.indexOf("packed=\"")+8;
                string=string.substring(begin,string.indexOf("\"",begin));
                string = new String(Base64.decode(string.getBytes(),Base64.DEFAULT));
                string=string.substring(string.indexOf("}('")+3,string.indexOf("split")-2);
                int index=string.indexOf("'");
                String s1=string.substring(0,index-1);
                String s2=string.substring(string.indexOf("'",index+1)+1);
                String[] s1_a=s1.split(";");
                String[] s2_arr=s2.split("\\|");
                String[][] s2_a=new String[s2_arr.length/62+1][62];
                for (int i = 0; i < s1_a.length; i++) {
                    s1_a[i] = "/"+s1_a[i].substring(s1_a[i].indexOf("\"") + 1, s1_a[i].indexOf("."))+"/";
                }
                for (int i=0;i<s2_a.length;i++){
                    for(int j=0;j<62&&j<s2_arr.length-i*62;j++){
                        s2_a[i][j]=s2_arr[i*62+j];
                    }
                }
                String t_value;
                strings=new String[s1_a.length];
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
                                strings[i] = "http://img.fengjingjituan.com/" + s1_a[i].substring(1, s1_a[i].length() - 1) + ".jpg";
                            }
                        }
                    }
                }
                for (int i=0;i<s1_a.length;i++){
                    urlList.add(strings[i]);
                }
            }else {
                int begin = string.indexOf("photosr[1]") + 7;
                int end = string.indexOf("var", begin);
                strings = string.substring(begin, end).split("photosr");
                for (int i = 0; i < strings.length; i++) {
                    begin = strings[i].indexOf("\"") + 1;
                    end = strings[i].indexOf("\"", begin);
                    urlList.add("http://img.fengjingjituan.com/" + strings[i].substring(begin, end));
                }
            }
        }catch (Exception e){
            throw new MyNetWorkException();
        }
        return urlList;
    }
}
