package com.drafens.dranacger;

import java.io.Serializable;

public class Episode implements Serializable{
    private String name;
    private String id;

    public Episode(String name,String id){
        this.name=name;
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String string = "name:"+name;
        string += "\r\nid:"+id;
        return string;
    }
}
