package com.example.jonas.speedcubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


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

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_statistics, null);

            if(session.getBestTime() != null){
                ((TextView) view.findViewById(R.id.textViewBest))
                        .setText(session.getBestTime().toString());
            }

            if(session.getWorseTime() != null){
                ((TextView) view.findViewById(R.id.textViewWorse))
                        .setText(session.getWorseTime().toString());
            }

            if(session.getAverageAll() != 0){
                ((TextView) view.findViewById(R.id.textViewMean))
                        .setText(Time.toStringMs(session.getAverageAll()));
            }

            if(session.getBestAverageOf5Time() != null){
                ((TextView) view.findViewById(R.id.textViewAo5Best))
                        .setText(session.getBestAverageOf5Time().toString());
            }
            if(session.getWorseAverageOf5Time() != null){
                ((TextView) view.findViewById(R.id.textViewAo5Worse))
                        .setText(session.getWorseAverageOf5Time().toString());
            }

            if(session.getBestAverageOf12Time() != null){
                ((TextView) view.findViewById(R.id.textViewAo12Best))
                        .setText(session.getBestAverageOf12Time().toString());
            }

            if(session.getWorseAverageOf12Time() != null){
                ((TextView) view.findViewById(R.id.textViewAo12Worse))
                        .setText(session.getWorseAverageOf12Time().toString());
            }

            int total = session.size();
            int valid = session.size() - session.getDnfTimes().size();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            builder.setTitle("Time Statistics" + " (" + valid + "/" + total + ")");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }
}
