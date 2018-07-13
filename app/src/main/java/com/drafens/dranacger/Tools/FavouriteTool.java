package com.drafens.dranacger.Tools;

import android.os.Environment;

import com.drafens.dranacger.Book;
import com.drafens.dranacger.Episode;
import com.drafens.dranacger.Error.MyJsonFormatException;
import com.drafens.dranacger.Error.MyJsonObjectEmptyException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FavouriteTool {

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/drafens/";

    private static String getJasonTab(int searchItem){
        String tab="";
        switch (searchItem){
            case Book.ANIMATION:
                tab = "animation";
                break;
            case Book.COMIC:
                tab = "comic";
                break;
            case Book.NOVEL:
                tab = "novel";
                break;
        }
        return tab;
    }

    /**
     *
     * @return isFavourite 收藏返回position，非收藏返回-1
     */
    public static int isFavourite(String book_id,int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        int isFavourite = -1;
        try{
            String string = readFiles("files/favourite_" + getJasonTab(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            JSONObject object;
            for (int i=0;i<jsonArray.length();i++){
                object = jsonArray.getJSONObject(i);
                String id = getString(object,"id");
                if(id.equals(book_id)){
                    isFavourite=i;
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return isFavourite;
    }

    public static boolean isUpdate(String lastReadChapter,String updateChapter){
        return !lastReadChapter.equals(updateChapter);
    }

    public void write_favourite(Book book,int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            String string = readFiles("files/favourite_" + getJasonTab(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
        }catch (Exception e){
            jsonArray = new JSONArray();
            jsonObject = new JSONObject();
        }
        JSONObject object = new JSONObject();
        try {
            object.put("id",book.getId());
            object.put("name",book.getName());
            object.put("updateChapter",book.getUpdateChapter());
            object.put("updateChapter_id",book.getUpdateChapter_id());
            object.put("updateTime",book.getUpdateTime());
            object.put("author",book.getAuthor());
            object.put("type",book.getType());
            object.put("icon",book.getIcon());
            object.put("webSite",book.getWebsite());
            object.put("lastReadChapter",book.getLastReadChapter());
            object.put("lastReadChapter_id",book.getLastReadChapter_id());
            object.put("lastReadTime",book.getLastReadTime());
            object.put("briefInfo",book.getBriefInfo());
            jsonArray.put(object);
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeFiles("files/","favourite_"+getJasonTab(searchItem)+".json",jsonObject.toString());
    }

    public static void update_favourite(int i,Book book,int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            String string = readFiles("files/favourite_" + getJasonTab(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            JSONObject object = new JSONObject();
            object.put("id", book.getId());
            object.put("name", book.getName());
            object.put("updateChapter", book.getUpdateChapter());
            object.put("updateChapter_id", book.getUpdateChapter_id());
            object.put("updateTime", book.getUpdateTime());
            object.put("author", book.getAuthor());
            object.put("type", book.getType());
            object.put("icon", book.getIcon());
            object.put("webSite", book.getWebsite());
            object.put("lastReadChapter", book.getLastReadChapter());
            object.put("lastReadChapter_id", book.getLastReadChapter_id());
            object.put("lastReadTime", book.getLastReadTime());
            object.put("briefInfo", book.getBriefInfo());
            jsonArray.put(i,object);
        }catch (Exception e){
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
        }try{
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeFiles("files/","favourite_"+getJasonTab(searchItem)+".json",jsonObject.toString());
    }

    public static void delete_favourite(int i,int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        try{
            String string = readFiles("files/favourite_"+getJasonTab(searchItem)+".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            jsonArray.remove(i);
        }catch (Exception e){
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
        }
        try{
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        }catch (Exception e){
            e.printStackTrace();
        }
        writeFiles("files/","favourite_"+getJasonTab(searchItem)+".json",jsonObject.toString());
    }

    public static void writeFiles(String catalog,String fileName,String data) {
        try {
            File files = new File(PATH+catalog);
            if(!files.exists()) {
                files.mkdirs();
            }
            File file = new File(PATH+catalog+fileName);
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFiles(String fileName)throws MyJsonObjectEmptyException{
        String path = PATH + fileName;
        String string;
        try{
            FileInputStream inputStream = new FileInputStream(path);
            int length = inputStream.available();
            byte [] buffer = new byte[length];
            inputStream.read(buffer);
            string = new String(buffer, "UTF-8");
            inputStream.close();
        }
        catch(Exception e){
            throw new MyJsonObjectEmptyException();
        }
        return string;
    }

    public static List<Book> getBookList(int searchItem) throws MyJsonObjectEmptyException, MyJsonFormatException {
        JSONObject jsonObject;
        List<Book> bookList=new ArrayList<>();
        String string = readFiles("files/favourite_"+getJasonTab(searchItem)+".json");
        try{
            jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("book");
            JSONObject object;
            for (int i=0;i<jsonArray.length();i++){
                object = jsonArray.getJSONObject(i);
                Book book = new Book(getString(object,"name"),getString(object,"updateChapter"),getString(object,"updateChapter_id"),getString(object,"updateTime"),getString(object,"author"),getString(object,"type"),getString(object,"id"),getString(object,"icon"),getString(object,"webSite"),getString(object,"lastReadChapter"),getString(object,"lastReadChapter_id"),getString(object,"lastReadTime"),getString(object,"briefInfo"));
                bookList.add(book);
            }
        } catch (Exception e){
            throw new MyJsonFormatException();
        }
        return bookList;
    }

    public static int getEpisodeId(String id, List<Episode> episodeList){
        int i;
        if (id.isEmpty()){
            return 0;
        }
        for (i=0;i<episodeList.size();i++){
            if(id.equals(episodeList.get(i).getId())){
                break;
            }
        }
        if (i<episodeList.size()&&i>=0){
            return i;
        }else {
            return -1;
        }
    }

    private static String getString(JSONObject object,String Tag){
        String string;
        try{
            string = object.getString(Tag);
        }catch (Exception e){
            string = "";
        }
        return string;
    }

    private static int getInt(JSONObject object,String Tag){
        int i;
        try{
            i = object.getInt(Tag);
        }catch (Exception e){
            i = 0;
        }
        return i;
    }
}
