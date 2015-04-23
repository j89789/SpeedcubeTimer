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

    private TimeSession timeSession = SpeedcubeApplication.instance().getTimeSession();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(timeSession.getAdapter());

        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Time time = timeSession.get(position);

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

            if(timeSession.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete all Times?");
                builder.setNegativeButton("no", null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timeSession.clear();
                    }

                });

                builder.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
