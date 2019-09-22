package com.example.alexd.assignment1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class tasklist extends AppCompatActivity {

    String username;
    String taskstr;
    //generate list
    public ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        taskstr = "";
        list = new ArrayList<String>();
        datasqlite dbObject2 = new datasqlite(this);

        dbObject2.open();
        String taskarry= dbObject2.getData();
        dbObject2.close();
        if(!taskarry.equals("")) {
            String[] tasks = taskarry.split(",");

            for (String item : tasks
                    ) {
                list.add(item);
            }
        }



        //instantiate custom adapter
        TaskListAdapter adapter = new TaskListAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listOfTask);
        lView.setAdapter(adapter);
    }
    public void updateTask (String str){

        Intent update =new Intent(this,updatetask.class);
        update.putExtra("taskName",str);
        startActivity(update);
    }

    public void deleteTask(String str){
        taskstr = str;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        datasqlite obj = new datasqlite(tasklist.this);
                        obj.open();
                        obj.deleteEntry(taskstr);
                        recreate();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    public void gotoadd(View view){
        Intent i = new Intent(this,addtask.class);
        startActivity(i);

    }
}
