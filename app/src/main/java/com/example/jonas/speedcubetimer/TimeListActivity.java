package com.example.jonas.speedcubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class TimeListActivity extends Activity {

    private ListView listView;

    private TimeSession session = SpeedcubeApplication.instance().getTimeSession();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(session.getAdapter());

        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Time time = session.get(position);

                time.showPopupMenu(TimeListActivity.this, view, null);
            }
        });
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
        else if (id == R.id.action_delete){

            if(session.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete all Times?");
                builder.setNegativeButton("no", null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.clear();
                    }

                });

                builder.create().show();
            }
        }
        else if (id == R.id.action_statistic){

            String s = "";

            s += "valid: " + session.getValidTimes().size() + "\n" ;
            s += "+2: " + session.getPlus2Times().size() + "\n";
            s += "DNF: " + session.getDnfTimes().size() + "\n\n";

            if(session.getBestTime() != null){
                s += "best: " + session.getBestTime().toString() + "\n";
            }
            if(session.getWorseTime() != null){
                s += "worse: " + session.getWorseTime().toString() + "\n\n";
            }

            if(session.getAverageAll() != 0){
                s += "mean: " + Time.toStringMs(session.getAverageAll()) + "\n\n";
            }

            if(session.getBestAverageOf5Time() != null){
                s += "Average of 5\n";
                s += "best: " + session.getBestAverageOf5Time().toString() + "\n";
            }
            if(session.getWorseAverageOf5Time() != null){
                s += "worse: " + session.getWorseAverageOf5Time().toString() + "\n\n";
            }

            if(session.getBestAverageOf12Time() != null){
                s += "Average of 12\n";
                s += "best: " + session.getBestAverageOf12Time().toString() + "\n";
            }
            if(session.getWorseAverageOf12Time() != null){
                s += "worse: " + session.getWorseAverageOf12Time().toString() + "\n\n";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Time Statistics");
            builder.setPositiveButton("OK", null);
            builder.setMessage(s);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }
}
