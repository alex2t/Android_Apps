package com.example.alexd.assignment3;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Textview shown on first screen
    TextView longi;
    TextView Lati;
    TextView TimerView;
    TextView textCityName;

    Button btnStop;
    Button btnStart;

    // GPS_Provider is used with object LocationManager
    LocationManager monitorMan;

    // check updates of running user after 5 seconds
    LocationListener locationListener;

    // variables required for writing .gpx files
    BufferedWriter writer;
    FileWriter writer2;

    Location loc;

    // Timer at first screen shown
    CountDownTimer countDownTimer;
    long timer = 0;

    // list of gps points
    // make list static so can be accessed in next summary activity for computation
    public static List<Location> trackingPoint = new ArrayList<>();

    // list of speed measured after each 5 seconds
    List<Double> speedList = new ArrayList<>();

    // file for writing gps points
    File myfile;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longi = findViewById(R.id.textLong);
        Lati = findViewById(R.id.textLati);
        TimerView = findViewById(R.id.textTimer);
        textCityName = findViewById(R.id.textCityName);

        btnStop = findViewById(R.id.buttonStop);
        btnStart = findViewById(R.id.buttonStart);

        btnStop.setEnabled(false);
        monitorMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (monitorMan.isProviderEnabled(monitorMan.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled!", Toast.LENGTH_SHORT).show();
        } else {
            showAlert();
        }
    }// onCreate function end here

    //Strat trackter event on Track Me button which measuring speed,Longitude and latitude
    public void startTracker(final View v) {

        //Oject of inner class MyLocationListener function used inside reqPermission() function
        locationListener = new MyLocationListener();

        //Timer shown on the main screen
        countDownTimer = new CountDownTimer(1000 * 60 * 60 * 24, 1000) {
            //40000 milli seconds is total time, 1000 milli seconds is time interval
            public void onTick(long millisUntilFinished) {

                if(timer ==0){
                    // At start get longitude and latitude
                    reqPermission();

                }
                timer +=1;
                // Timer show on the screen
                TimerView.setText(timer + "");
            }
            public void onFinish() {
            }
        }.start();
    }

    // check if permission are granted if perssions are not granted make a request for permission
    public void reqPermission(){

        // String Array containing all required permission strings for traker Application
        String [] permissions_Requested = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Check if perssions are not granted make a request for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // request for permissions
            // request code can be any number , latter this number will be used in checking either permissions are granted or not
            ActivityCompat.requestPermissions(this,permissions_Requested,786);
            return;
        }else{
            // else if Permissions are granted then check for location update of user
            monitorMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
        }

    }// reqPermission func end here

    // Check requested permission are granted ? by the user using the request code 786
    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 786:
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    monitorMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
                }
            }
        }
    }

    // On Stop button click this function is called
    // gpx file creates and move to next activity
    public void stopTracker(View v){

        // Stop time counter as stop button is clicked
        countDownTimer.cancel();

        // Call method for creaing the  ".gpx" file with 3 parameter
        // 1- file, 2- file name (for saving in gpx file ), 3- list of gps points longitude, latitude and time
        generateGfx(myfile,"trackerFile",trackingPoint);

        // stop location listener
        monitorMan.removeUpdates(locationListener);
        monitorMan = null;

        // move to next activity and show summary of race
        Intent intent = new Intent(this,summary.class);
        intent.putExtra("totalTime",timer);
        startActivity(intent);
        finish();
    }

    // At first install or start of Application check either GPS of mobile is on or not
    private void showAlert() {

        // if gps not enabled take user to direct gps settigs
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        System.exit(0);
    }


    // Inner class that check location update of user
    private class MyLocationListener implements LocationListener {

        @Override
        // toast shown after 5 seconds giving information about user SPEED
       // creating list of speed measured after every five seconds
        public void onLocationChanged(Location loc) {

            // toast shown after 5 seconds giving information about user SPEED
            Toast.makeText(getBaseContext(), "SPEED: " + loc.getSpeed(), Toast.LENGTH_SHORT).show();

            // creating list of gps points
            // list types is Location
            trackingPoint.add(loc);


            if(!btnStop.isEnabled()){
                // enablestop button when atleast one position is saved in file
                if (trackingPoint.size()>0){
                    btnStop.setEnabled(true);
                }
            }

            // creating list of speed measured after every five seconds
            // latter will be used measuring average speed, min and max speed of runner
            speedList.add((double) loc.getSpeed());

            /*-------to get City-Name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();

                // Round off Longitude and latitude to single digit element
                // shown on the first screen
                longi.setText(String.format("%.1f",loc.getLongitude()));
                Lati.setText(String.format("%.1f",loc.getLatitude()));

            } catch (IOException e) {
                e.printStackTrace();
            }
            textCityName.setText(cityName);
        }





        @Override // These methods are essential to declare although not being used and defined.
        public void onProviderDisabled (String provider){
        }

        @Override  // These methods are essential to declare although not being used and defined.
        public void onProviderEnabled (String provider){
        }

        @Override  // These methods are essential to declare although not being used and defined.
        public void onStatusChanged (String provider,int status, Bundle extras){
        }

    }// inner class MyLocationListener

    // Method that generate .gpx file with proper formating
    public void generateGfx(File file, String name, List<Location> points) {

        // setting dateformat
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        // Setting header of gpx file
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String metaData = "<metadata>" +    "<link href=\"http://www.tracker.com\">"+ "<text>Tracker International</text></link><time>"+df.format(trackingPoint.get(0).getTime()) +"</time></metadata>";
        name = "<name>" + name + "</name><trkseg>\n";
        String segments = "";

        // setting data in segment come from list( trackerPoint list of types Location)
        for (Location location : points) {
            segments += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><ele>"+String.format("%.1f",location.getAltitude())+"</ele><time>" + df.format(new Date(location.getTime())) + "</time></trkpt>\n";
        }

        // setting footer of .gpx file
        String footer = "</trkseg></trk></gpx>";

        try{

            // creating folder with name " GPStracker"
            file = new File(Environment.getExternalStorageDirectory() + "/" + "GPStracks");

            // if doesn't exist then creade folder
            if(!file.exists()) {
                file.mkdir();
            }

            // getting the current date and time for file name
            Date date = Calendar.getInstance().getTime();
            df = new SimpleDateFormat("yyyy-MM-dd_HH mm ssZ");

            // set formatted date time as name of gpx file & writing to file
            file = new File(file.getPath() , df.format(date)+".gpx");
            file.createNewFile();

            // open file in append mode
            writer2 =new FileWriter(file,true);
            writer = new BufferedWriter(writer2);

        }catch (Exception e){
            Toast.makeText(this,"File cann't be created",Toast.LENGTH_LONG).show();
        }

        try {
            // writing to .gpx file
            writer.append(header);
            writer.append(metaData);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            Toast.makeText(this, "File is written !", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("generateGfx", "Error Writting Path",e);
        }
    }// inner class end here
}// MainActivity class end here
