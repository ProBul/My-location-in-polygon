package com.bulkin.polygonindicator;

public class ShortestDistanceToTheEdge {

    public ShortestDistanceToTheEdge() {
    }

    public void run(){
    double lat1 = 3.222895;
    double lon1 = 101.719751;
    double lat2 = 3.222895;
    double lon2 = 101.719751;
    double lat3 = 3.224972;
    double lon3 = 101.722932;
    final double  earthRad = 6371.0;


    double y = Math.sin(lon3 - lon1) * Math.cos(lat3);
    double x = Math.cos(lat1) * Math.sin(lat3) - Math.sin(lat1) * Math.cos(lat3) * Math.cos(lat3 - lat1);
    double bearing1 = Math.toDegrees(Math.atan2(y, x));
    double newBearing1 = 360 - (bearing1 + 0);

    double y2 = Math.sin(lon2 - lon1) * Math.cos(lat2);
    double x2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lat2 - lat1);
    double bearing2 = Math.toDegrees(Math.atan2(y2, x2));
    double newBearing2 = 360 - (bearing2 + 0);

    double lat1Rads = Math.toRadians(lat1);
    double lat3Rads =  Math.toRadians(lat3);
    double dLon =  Math.toRadians(lon3 - lon1);

    double distanceAC = Math.acos(Math.sin(lat1Rads) * Math.sin(lat3Rads) + Math.cos(lat1Rads) * Math.cos(lat3Rads) * Math.cos(dLon)) * earthRad;
    double min_distance = Math.abs(Math.asin(Math.sin(distanceAC / earthRad) * Math.sin(Math.toRadians(newBearing1) - Math.toRadians(newBearing2))) * 6371);
    }
}
