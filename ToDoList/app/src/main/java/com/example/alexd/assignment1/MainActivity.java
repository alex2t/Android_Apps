package com.example.alexd.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void listTask(View view){
        Intent tasklist = new Intent(this, tasklist.class);
        startActivity(tasklist);
    }

    public void addTask(View view){
        Intent newtask = new Intent(this, addtask.class);
        startActivity(newtask);
    }
}
