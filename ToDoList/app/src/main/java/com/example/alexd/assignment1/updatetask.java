package com.example.alexd.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class updatetask extends AppCompatActivity {

    TextView usertask;
    String oldName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatetask);
        usertask = findViewById(R.id.textupdate);
        oldName = getIntent().getStringExtra("tag");
        usertask.setText(oldName);
    }
//


    public void btnupdatecancel(View view){

        usertask.setText("");
        Intent listtasks =new Intent(this,tasklist.class);
        startActivity(listtasks);
    }

    public  void btnupdatesave(View view){

        String newName = usertask.getText().toString();
        if (newName.isEmpty()){
            usertask.setError("Task Name is required");
        }else {
            datasqlite dbObject3 = new datasqlite(this);
            dbObject3.open();
            dbObject3.updateEntry(oldName, newName);
            dbObject3.close();

            Intent listOfTasks = new Intent(this, tasklist.class);
            startActivity(listOfTasks);
        }
    }
}
