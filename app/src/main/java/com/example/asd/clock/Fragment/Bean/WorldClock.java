package com.example.asd.clock.Fragment.Bean;

//WorldClock载体
public class WorldClock {
    private String city_en;
    private String city_cn;
    private String hours;
    public void setCity_en(String city_en) {
        this.city_en = city_en;
    }

    public void setCity_cn(String city_cn) {
        this.city_cn = city_cn;
    }

    public String getCity_en() {
        return city_en;
    }

    public String getCity_cn() {
        return city_cn;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getHours() {
        return hours;
    }
}
