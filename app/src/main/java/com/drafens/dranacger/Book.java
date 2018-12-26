package com.drafens.dranacger;

import java.io.Serializable;

public class Book implements Serializable{
    public static final int ANIMATION = 0;
    public static final int COMIC = 1;
    public static final int NOVEL = 2;
    private String name;
    private String id;
    private String updateChapter;
    private String updateChapter_id;
    private String updateTime;
    private String author;
    private String type;
    private String icon;
    private String website;
    private String lastReadChapter;
    private String lastReadChapter_id;
    private String lastReadTime;
    private String briefInfo;
    private String readPosition;


    public Book(String name,String updateChapter,String updateChapte_id,String updateTime,String author,String type,String id,String icon,String website,String lastReadChapter,String lastReadChapter_id,String lastReadTime,String briefInfo){
        this.name=name;
        this.updateChapter=updateChapter;
        this.updateChapter_id=updateChapte_id;
        this.updateTime=updateTime;
        this.author=author;
        this.type=type;
        this.id=id;
        this.icon=icon;
        this.website=website;
        this.lastReadChapter=lastReadChapter;
        this.lastReadChapter_id=lastReadChapter_id;
        this.lastReadTime=lastReadTime;
        this.briefInfo=briefInfo;
    }

    public String getName(){
        return name;
    }
    public String getUpdateChapter(){
        return updateChapter;
    }
    public String getUpdateChapter_id() {
        return updateChapter_id;
    }
    public String getUpdateTime(){
        return updateTime;
    }
    public String getAuthor(){
        return author;
    }
    public String getType(){
        return type;
    }
    public String getId(){
        return id;
    }
    public String getIcon(){
        return icon;
    }
    public String getWebsite(){
        return website;
    }
    public String getLastReadChapter(){
        return lastReadChapter;
    }
    public String getLastReadChapter_id() {
        return lastReadChapter_id;
    }
    public String getLastReadTime(){
        return lastReadTime;
    }
    public String getBriefInfo() {
        return briefInfo;
    }
    public String getReadPosition() {
        return readPosition;
    }

    public void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }
    public void setLastReadChapter_id(String lastReadChapter_id) {
        this.lastReadChapter_id = lastReadChapter_id;
    }
    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }
    public void setUpdateChapter(String updateChapter) {
        this.updateChapter = updateChapter;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public void setUpdateChapte_id(String updateChapter_id) {
        this.updateChapter_id = updateChapter_id;
    }
    public void setReadPosition(String readPosition) {
        this.readPosition = readPosition;
    }

    public void setBriefInfo(String briefInfo) {
        this.briefInfo = briefInfo;
    }

    @Override
    public String toString() {
        String string = "name:"+name;
        string += "\r\nid:"+id;
        string += "\r\nauthor:"+author;
        string += "\r\ntype:"+type;
        string += "\r\nupdateChapter:"+updateChapter;
        string += "\r\nupdateChapter_id:"+updateChapter_id;
        string += "\r\nupdateTime:"+updateTime;
        string += "\r\nlastReadChapter:"+lastReadChapter;
        string += "\r\nlastReadTime:"+lastReadTime;
        string += "\r\nwebsite:"+website;
        string += "\r\nicon:"+icon;
        string += "\r\nbriefInfo:"+briefInfo;
        string += "\r\nreadPosition"+readPosition;
        return string;
    }
}