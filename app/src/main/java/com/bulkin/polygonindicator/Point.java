package com.bulkin.polygonindicator;

public class Point {

    public double latitude;
    public double longitude;


    public Point() {
    }

    public Point(double latitude,double longitude ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double x) {
        this.latitude = x;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setlongitude(double y) {
        this.longitude = y;
    }
}
