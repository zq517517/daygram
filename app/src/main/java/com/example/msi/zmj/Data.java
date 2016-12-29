package com.example.msi.zmj;

import java.io.Serializable;

public class Data implements Serializable{
    private String date;
    private String year;
    private String month;
    private String week;
    private String day;
    private String detail;

    public String getDate() {
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getYear(){
        return year;
    }
    public void setYear(String year){
        this.year = year;
    }
    public String getMonth(){
        return month;
    }
    public void setMonth(String month){
        this.month = month;
    }
    public String getWeek(){
        return week;
    }
    public void setWeek(String week) {
        this.week = week;
    }
    public String getDay(){
        return day;
    }
    public void setDay(String day){
        this.day = day;
    }
    public String getDetail(){
        return detail;
    }
    public void setDetail(String detail){
        this.detail = detail;
    }
}
