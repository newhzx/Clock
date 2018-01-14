package com.example.asd.clock.WorldClock.Sortlist;

/**
 * Created by asd on 2017/12/30.
 */

public class SortModel {
    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母
    private String id;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
    public String getId() {return id;}
    public void setId(String id){this.id = id;}
}
