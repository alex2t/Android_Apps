package com.example.alexd.assignment3;


import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class summary extends AppCompatActivity {


    TextView maxSpeed;
    TextView minSpeed;
    TextView avgSpeed;

    TextView distance;

    TextView texttotalRunnerTime;

    TextView maxAltitude;
    TextView minAltitude;
    TextView altitudeGain;
    TextView altitudeLoss;

    GraphView graphView;
    LinearLayout linear;

    float totalDistance = 0;

    List<Double> listOFSpeeds = new ArrayList<>();

    List<Float> listOFAltitudes = new ArrayList<>();
    List<Integer> listOFTime = new ArrayList<>();

    List<Double> listOFAltitudeGain = new ArrayList<>();
    List<Double> listOFAltitudeLoss = new ArrayList<>();


    @Override // mesure and calculate all info total distance and gain and loss
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Getting all text fields of summary layout file to show results
        maxSpeed =findViewById(R.id.textMaxSpeed);
        minSpeed =findViewById(R.id.textMinSpeed);
        avgSpeed =findViewById(R.id.textAvgSpeed);
        distance = findViewById(R.id.textDistance);
        texttotalRunnerTime = findViewById(R.id.textTotalTime);
        maxAltitude = findViewById(R.id.textMaxAltitude);
        minAltitude = findViewById(R.id.textMinAltitude);
        altitudeGain = findViewById(R.id.textAltitudeGain);
        altitudeLoss = findViewById(R.id.textAltitudeLoss);

        linear = findViewById(R.id.linear);

        int[] graphData = {3,5,2,7,4,8,1,5,9};

        graphView = new GraphView(this);
        linear.addView(graphView);

        // getting time from the previous activity
        long tTime = getIntent().getLongExtra("totalTime",0);

        // set time to hours:min:sec
        long sec = 0;
        long min = 0;
        long hours = 0;
        if(tTime>0){
            // convert to min
            if (tTime>60)
                min = tTime/60;

            // convert min to hours
            if (min >60)
                hours = min/60;

            // remaining seconds
            sec = tTime % 60;
        }

        // variable to find gain and loss
        double value=0;

        long firstTime = MainActivity.trackingPoint.get(0).getTime();
        listOFSpeeds.add((double) MainActivity.trackingPoint.get(0).getSpeed());
        listOFAltitudes.add((float) MainActivity.trackingPoint.get(0).getAltitude());
        listOFTime.add(0);
        // get all speed and altitude measurements
        for (int i = 1; i<MainActivity.trackingPoint.size();i++){

            listOFSpeeds.add((double) MainActivity.trackingPoint.get(i).getSpeed());
            listOFAltitudes.add((float) MainActivity.trackingPoint.get(i).getAltitude());
            listOFTime.add((int)(MainActivity.trackingPoint.get(i).getTime() - firstTime)/1000);

            // Measure the gain and loss of Altitude
            value = MainActivity.trackingPoint.get(i).getAltitude()-MainActivity.trackingPoint.get(i-1).getAltitude();

            // measure the total distance covered by runner
            totalDistance += MainActivity.trackingPoint.get(i-1).distanceTo(MainActivity.trackingPoint.get(i));

            if (value >= 0){
                listOFAltitudeGain.add(value);
            }else{
                listOFAltitudeLoss.add(value *-1);
            }
        } // for loop end here

        // measuring the total loss and total gain of altitude
        double totalGain = 0;
        double totalLoss = 0;
        if(listOFAltitudeGain.size()>listOFAltitudeLoss.size()){

            for (int i = 0; i<listOFAltitudeGain.size();i++){
                totalGain += listOFAltitudeGain.get(i);
            }
        } else{

            for (int i = 0; i<listOFAltitudeLoss.size();i++){
                totalLoss += listOFAltitudeLoss.get(i);
            }
        }

        // Measuring the Average speed
        double totalSpeed = 0;
        double meanSpeed = 0;
        for (int i=0; i<listOFSpeeds.size();i++){
            totalSpeed += listOFSpeeds.get(i);
        }
        meanSpeed = totalSpeed/(listOFSpeeds.size()+1);


        maxSpeed.setText( String.format("%.1f" ,Collections.max(listOFSpeeds)));
        minSpeed.setText( String.format("%.1f",Collections.min(listOFSpeeds)));
        avgSpeed.setText(String.format("%.1f",meanSpeed));

        distance.setText(String.format("%.1f",totalDistance));
        texttotalRunnerTime.setText(hours+":"+min+":"+sec);

        maxAltitude.setText(String.format("%.1f",Collections.max(listOFAltitudes)));
        minAltitude.setText(String.format("%.1f",Collections.min(listOFAltitudes)));
        altitudeGain.setText(String.format("%.1f", totalGain));
        altitudeLoss.setText(String.format("%.1f", totalLoss));

        drawGraph();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //call this method with every new set of data
    public void drawGraph()
    {
        //call this method with every new set of data
        graphView.drawGraph(listOFTime,listOFAltitudes);
    }

}
