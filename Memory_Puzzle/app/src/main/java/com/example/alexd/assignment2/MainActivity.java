package com.example.alexd.assignment2;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CustomView customview;
    TextView tvScore;
    TextView tvPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customview = findViewById(R.id.customView);
        tvScore = findViewById(R.id.tvScore);
        tvPlayer = findViewById(R.id.tvTurn);

        tvScore.setText("Score - [Player 1: 0][Player 2: 0]");
        tvPlayer.setText("Current Player: 1");
    }

    public void updateMainActivity(String score, String currentPlayer)
    {
        tvScore.setText(score);
        tvPlayer.setText(currentPlayer);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent i = new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }

}