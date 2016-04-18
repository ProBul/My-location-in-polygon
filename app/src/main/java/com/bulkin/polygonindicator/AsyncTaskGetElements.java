package com.bulkin.polygonindicator;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncTaskGetElements extends AsyncTask<String, Void, String> {
    private GPSTracker gps;
    Context context;

    public AsyncTaskGetElements(Context context) {
        this.context = context;
    }

    //this method running in the background not to overload the main thread
    // if the XML file were too big.
    //here we start the process of getting the XML file and parsing its data to String and then
    //getting the string back.
    //after receiving the String, it will be passed to onPostExecute method that contains
    // the mathematical formulas.
    @Override
    protected String doInBackground(String... params) {
        ReadFromFile rd = new ReadFromFile(context);
        String coor = null;
        try {
            try {
                coor = rd.getCoordinatesFromFile();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return coor;
    }

    //this method contains a lnk to all the mathematical formulas that will calculate the
    // relative location of the user compared to a given polygon from KML file.

    @Override
    protected void onPostExecute(String s) {
        gps = new GPSTracker(context);

        //get the user location.
        //this data will be passed to all mathematical formulas for comparison.
        double latGPS = gps.getLatitude();
        double lngGPS = gps.getLongitude();

        //show the string with all coordinates received from the KML file
        System.out.print(s);

        //start a method to break the data from the parsed string to an array list
        // of Lat and Long data
        ArrayList<Location> coorList = breakString(s);

        //start a method that will check if the location of the user is located inside
        // the given polygon from KML file or outside and. the answer will be located in a
        // boolean variable.
        boolean isInPolygon = contains(coorList, latGPS, lngGPS);

        //if the user`s location is outside the polygon, start checking the closest distance
        // between the user and the polygon- including the polygon vertex and it`s Edges.
        if (!isInPolygon) {
            //start a mathematical method to calculate the minimum distance between the location
            //of the user and the edges of the polygon. the return answer will be stored in a
            //double variable named : minDistanceToEdge
            double minDistanceToEdge = shortestDistanceToTheEdge(coorList, latGPS, lngGPS);

            //start a mathematical method to calculate the minimum distance between the location
            //of the user and the vertexes of the polygon. the return answer will be stored in a
            //double variable named: minDistanceToVertex
            double minDistanceToVertex = shortestDistanceToVertex(coorList, latGPS, lngGPS);

            //we want to get the minimum distance from the user location to the polygon, that`s why
            //we need to compare the distance between the user to the nearest edge of the polygon
            //and the distance between the user and the nearest vertex.
            //the minimum distance will be shown as a Toast on the screen and if the user is
            //closer to the edge or vertex of the polygon.
            if (minDistanceToEdge > minDistanceToVertex) {
                Toast.makeText(context, "the closest distance is to VERTEX and it`s: " + minDistanceToVertex + "KM", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "the closest distance is to the edge and it`s: " + minDistanceToEdge + "KM", Toast.LENGTH_LONG).show();
            }
        } else {
            //if the user inside the polygon, this Toast will be shown
            Toast.makeText(context, "you are in the polygon", Toast.LENGTH_LONG).show();

        }

    }

    //this method receives a string information received from KML file.
    //the string will processed to segments  of double variables.
    //this method will use the Location class to store the Lat and Long variables in a arrayList
    // of Location object.
    private ArrayList<Location> breakString(String s) {
        ArrayList<Location> list = new ArrayList<>();
        String tempCoor = "";
        double lat;
        double lon = 0;
        int inTag = 0;
        for (int i = 0; i < s.length(); i++) {

            //when the program encounter the "," sign it starts to count the number of time it
            // encountered this sign.
            // if it`s an even number of encounters that means that this is Lat phase
            // and it will be stored in lon variable
            if (s.charAt(i) == ',') {
                if (inTag % 2 == 0) {
                    lon = Double.parseDouble(tempCoor);

                    // if it`s an uneven number of encounters that means that this is Long phase
                    // and it will be stored in lon variable
                } else {
                    lat = Double.parseDouble(tempCoor);
                    Location tempLocation = new Location("");
                    tempLocation.setLatitude(lat);
                    tempLocation.setLongitude(lon);
                    list.add(tempLocation);
                }
                inTag++;
                tempCoor = "";

                //if the program encounter white space in the string, it means that it passed one
                // Lat phase and one Long phase and it needs to start a new list for the next phase.
            } else if (s.charAt(i) == ' ') {
                tempCoor = "";

                //if the program encounter "new line" it will continue the loop from top
            } else if (s.charAt(i) == '\n') {
                tempCoor = "";

                //if the program encounter "tab" it will continue the loop from top
            } else if (s.charAt(i) == '\t') {
                tempCoor = "";

            } else {
                tempCoor += s.charAt(i);
            }
        }
        return list;
    }

    //this mathematical method will check if the user location is inside the polygon or outside.
    //this formula checks how many times the user`s point cut the polygon edges on the X axis.
    // if its an even number of times then the point is outside of the polygon and if it`s
    // uneven number of cuts then the point is inside
    public boolean contains(ArrayList<Location> list, double latGPS, double lngGPS) {

        int i;
        int j;
        boolean result = false;

        //subtraction of 1 list.size so that the method will not check the first point twice
        //the return value will be stored in a boolean variable.
        int newSize = list.size() - 1;
        for (i = 0, j = newSize - 1; i < newSize; j = i++) {
            boolean ifinFOSURE = ((list.get(i).getLongitude() > lngGPS) != (list.get(j).getLongitude() > lngGPS));
            boolean ifEXIST = latGPS < (list.get(j).getLatitude() - list.get(i).getLatitude()) *
                    (latGPS - list.get(i).getLongitude()) / (list.get(j).getLongitude() - list.get(i).getLongitude())
                    + list.get(i).getLatitude();
            if (ifinFOSURE && ifEXIST) {
                result = !result;
            }
        }
        return result;
    }

    //this mathematical method will check the shortest distance between the location of the user
    //to each of the polygon vertex. then it will compare all the distances and pick the shortest one.
    //the return answer will be stored in double variable
    public double shortestDistanceToVertex(ArrayList<Location> list, double latGPS, double lngGPS) {

        //a very big number was pick here so a comparison could be made to find a smaller distances
        double shortestDistanceToVertex = 20000000.0;
        final double AVERAGE_RADIUS_OF_EARTH = 6371.0;
        double tempDistanceToVertex;

        int i;
        //int j;
        //for (i = 0, j = list.size() - 1; i < list.size(); j = i++) {
            for (i = 0; i < list.size(); i++) {
            //Point A on the polygon Edge to compare
            double latPoly = list.get(i).getLatitude();
            double longPoly = list.get(i).getLongitude();

            double latDistance = Math.toRadians(latGPS - latPoly);
            double lngDistance = Math.toRadians(lngGPS - longPoly);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(latGPS)) * Math.cos(Math.toRadians(latPoly))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            tempDistanceToVertex = AVERAGE_RADIUS_OF_EARTH * c;

            if (tempDistanceToVertex < shortestDistanceToVertex) {
                shortestDistanceToVertex = tempDistanceToVertex;
            }
        }
        return shortestDistanceToVertex;
    }

    //this mathematical method will check the shortest distance between the location of the user
    //to each of the polygon edges.
    //the return answer will be stored in double variable
    private double shortestDistanceToTheEdge(ArrayList<Location> list, double latGPS, double lngGPS) {
        final double earthRad = 6371.0;
        double min_distanceToEdge = 0.0;

        int i;
        int j;

        for (i = 0, j = list.size() - 1; i < list.size(); j = i++) {

            //Point A on the polygon Edge to compare
            double lat1 = list.get(i).getLatitude();
            double lon1 = list.get(i).getLongitude();

            //Point B on the polygon Edge to compare
            double lat2 = list.get(j).getLatitude();
            double lon2 = list.get(j).getLongitude();

            double y = Math.sin(lngGPS - lon1) * Math.cos(latGPS);
            double x = Math.cos(lat1) * Math.sin(latGPS) - Math.sin(lat1) * Math.cos(latGPS) * Math.cos(latGPS - lat1);
            double bearing1 = Math.toDegrees(Math.atan2(y, x));
            bearing1 = 360 - (bearing1 + 0);

            double y2 = Math.sin(lon2 - lon1) * Math.cos(lat2);
            double x2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lat2 - lat1);
            double bearing2 = Math.toDegrees(Math.atan2(y2, x2));
            bearing2 = 360 - (bearing2 + 0);

            double lat1Rads = Math.toRadians(lat1);
            double lat3Rads = Math.toRadians(latGPS);
            double dLon = Math.toRadians(lngGPS - lon1);

            double distanceAC = Math.acos(Math.sin(lat1Rads) * Math.sin(lat3Rads) + Math.cos(lat1Rads) * Math.cos(lat3Rads) * Math.cos(dLon)) * earthRad;
            min_distanceToEdge = Math.abs(Math.asin(Math.sin(distanceAC / earthRad) * Math.sin(Math.toRadians(bearing1) - Math.toRadians(bearing2))) * earthRad);
        }
        return min_distanceToEdge;
    }
}
