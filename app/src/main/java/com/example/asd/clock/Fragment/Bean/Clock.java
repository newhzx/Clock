package com.example.asd.clock.Fragment.Bean;

import java.io.Serializable;

//Clock载体
public class Clock implements Serializable{
    private int id;
    private int lunchSelect;
    private int hourSelect;
    private int minuteSelect;
    private int second;
    private String tags;
    private String json;
    private boolean bool;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLunchSelect(int lunchSelect) {
        this.lunchSelect = lunchSelect;
    }

    public int getLunchSelect() {
        return lunchSelect;
    }

    public void setHourSelect(int hourSelect) {
        this.hourSelect = hourSelect;
    }

    public int getHourSelect() {
        return hourSelect;
    }

    public void setMinuteSelect(int minuteSelect) {
        this.minuteSelect = minuteSelect;
    }

    public int getMinuteSelect() {
        return minuteSelect;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getSecond() {
        return second;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

    public boolean getBoolean() {
        return bool;
    }

}