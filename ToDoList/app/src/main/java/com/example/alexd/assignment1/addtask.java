package com.example.alexd.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class addtask extends AppCompatActivity {

    TextView taskName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
    }

    public void addNewTaskCancel(View view){
        Intent startMenu = new Intent(this, MainActivity.class);
        startActivity(startMenu);
    }
    public void addNewTaskSave(View view){

        taskName= this.findViewById(R.id.editTask);
        String text = taskName.getText().toString().trim();
        if (text.isEmpty())
        {
            taskName.setError("Task Name is required");
        }else {
            datasqlite dbObject = new datasqlite(this);
            dbObject.open();
            long result = dbObject.createEntry(taskName.getText().toString());
            if (result<1){
                Toast.makeText(this, "Task is already in List", Toast.LENGTH_LONG).show();
            }
            dbObject.close();
            Intent taskListView = new Intent(this, tasklist.class);
            startActivity(taskListView);
        }
    }
}
