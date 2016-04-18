package com.bulkin.polygonindicator;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ReadFromFile {

    private XmlPullParserFactory xmlFactoryObject;
    private String xmlFile = null;

    private Context context;
    private boolean in_coordinatestag = false;

    public ReadFromFile(Context context) {
        this.context = context;
    }

    //Start a method a method to extract data from XML file received from internet
    //and return it as a string
    public String getCoordinatesFromURL() {

        return fetchXML();

    }
    //Start a method to extract data from XML file received from local located file on user phone or pc
    //and return it as a string
    public String getCoordinatesFromFile() throws IOException, XmlPullParserException {

        return fetchXMLfromfile();
    }

    //this method opens a stream to XML file located on local memory and sends it to a Parser Method
    //to extract desired info
    public String fetchXMLfromfile() throws IOException, XmlPullParserException {
        InputStream stream = context.getApplicationContext().getAssets().open("Allowed.kml");
        xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser myparser =
                xmlFactoryObject.newPullParser();
        myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES
                , false);
        myparser.setInput(stream, null);

        xmlFile = parseXMLAndStoreIt(myparser);
        stream.close();


        return xmlFile;
    }

    //this method opens a stream to XML file located on remote machine and sends it to a Parser Method
    //to extract desired info
    public String fetchXML() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(xmlFile);
                    HttpURLConnection conn = (HttpURLConnection)
                            url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser =
                            xmlFactoryObject.newPullParser();
                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES
                            , false);
                    myparser.setInput(stream, null);

                    xmlFile = parseXMLAndStoreIt(myparser);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return xmlFile;
    }

    //this method reads an XML file located on local memory file and returns a string with desired information
    // in this example it contains the text located between "coordinates" Tag and returns a string with all
    //coordinates that will be used later in methods located in AsyncTaskGetElements Class
    public String parseXMLAndStoreIt(XmlPullParser myParser) throws XmlPullParserException, IOException {
        int event;
        String coordinates = null;


        event = myParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = myParser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("coordinates"))
                        this.in_coordinatestag = true;
                    break;

                case XmlPullParser.TEXT:
                    if (in_coordinatestag)
                        coordinates = myParser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (tagName.equals("coordinates"))
                        this.in_coordinatestag = false;
                    break;
            }
            event = myParser.next();
        }
        return coordinates;
    }
}
