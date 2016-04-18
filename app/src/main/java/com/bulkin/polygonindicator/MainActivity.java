package com.bulkin.polygonindicator;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Button btn_gps, btn_runTask;

    private TextView tvLat,tvLong;
    private AsyncTaskGetElements asyncTask;
    private GPSTracker gps;
    private Point point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat= (TextView)findViewById(R.id.textView_lat);
        tvLong= (TextView)findViewById(R.id.textView_long);
        btn_runTask= (Button)findViewById(R.id.button_runTask);
        btn_gps=(Button)findViewById(R.id.button_location);

        //this button starts the AsyncTask to compare the data betwen the user location
        //and the polygon
        btn_runTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask = new AsyncTaskGetElements(MainActivity.this);
                asyncTask.execute();

            }
        });

        //this button starts a method to receive data of user location from GPS and to give the user
        //feedback about his Lat and Long
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });
    }

    //a method to receive  the Lat and Long data from the location of the user
    public void getMyLocation(){

        gps = new GPSTracker(MainActivity.this);
        point= new Point();
        final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps.showSettingsAlert();

            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                tvLat.setText(String.valueOf(latitude));
                tvLong.setText(String.valueOf(longitude));
                point.setLatitude(latitude);
                point.setlongitude(longitude);

            } else {
                gps.showSettingsAlert();
            }

        } else {

            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                tvLat.setText(String.valueOf(latitude));
                tvLong.setText(String.valueOf(longitude));
                point.setLatitude(latitude);
                point.setlongitude(longitude);

            } else {
                //start a methode to alert the user that his GPS is turned off and
                // suggest him to turn it on from the setting menu
                gps.showSettingsAlert();
            }
        }
    }
}
