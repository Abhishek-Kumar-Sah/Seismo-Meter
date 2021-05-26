package com.avi.in.earthquakemeter;

public class QuakeDetails {

    private double magnitude;
    private String location;
    private Long date;
    private Long time;
    private String url;

    public String getUrl() {
        return url;
    }

    public Long getTime() {
        return time;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public Long getDate() {
        return date;
    }

    public QuakeDetails(double magnitude , String location , Long  date, Long time , String url){

        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.time = time;
        this.url = url;
    }

}
