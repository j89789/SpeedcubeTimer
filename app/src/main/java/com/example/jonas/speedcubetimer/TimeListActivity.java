package com.example.jonas.speedcubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class TimeListActivity extends Activity {

    private ListView listView;
    private TimeListAdapter timeListAdapter = new TimeListAdapter(SpeedcubeApplication.instance().getTimeList());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(timeListAdapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        else if (id == R.id.delete_list){

            List<SolvingTime> timeList = SpeedcubeApplication.instance().getTimeList();

            if(timeList.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete all Times?");
                builder.setNegativeButton("no", null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SpeedcubeApplication.instance().getTimeList().clear();
                        timeListAdapter.notifyDataSetChanged();
                    }

                });

                builder.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
