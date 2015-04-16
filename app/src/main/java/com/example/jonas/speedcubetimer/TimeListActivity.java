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
import android.widget.PopupMenu;

import java.util.List;


public class TimeListActivity extends Activity {

    private ListView listView;
    private TimeListAdapter adapter = new TimeListAdapter(SpeedcubeApplication.instance().getTimeList());
    private List<SolvingTime> timeList = SpeedcubeApplication.instance().getTimeList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PopupMenu popup = new PopupMenu(TimeListActivity.this, view);

                popup.inflate(R.menu.menu_time_type);

                final SolvingTime solvingTime = timeList.get(position);

                if (solvingTime.getType() == SolvingTime.Type.valid) {
                    popup.getMenu().findItem(R.id.ok).setChecked(true);
                } else if (solvingTime.getType() == SolvingTime.Type.plus2) {
                    popup.getMenu().findItem(R.id.plus2).setChecked(true);
                } else if (solvingTime.getType() == SolvingTime.Type.DNF) {
                    popup.getMenu().findItem(R.id.DNF).setChecked(true);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();

                        if (i == R.id.ok) {
                            solvingTime.setType(SolvingTime.Type.valid);
                        } else if (i == R.id.plus2) {
                            solvingTime.setType(SolvingTime.Type.plus2);
                        } else if (i == R.id.DNF) {
                            solvingTime.setType(SolvingTime.Type.DNF);
                        } else if (i == R.id.delete) {
                            timeList.remove(solvingTime);
                        }

                        updateListView();

                        return true;
                    }
                });

                popup.show();
            }
        });

        updateListView();
    }

    private void updateListView() {
        setTitle(getString(R.string.title_activity_time_list) + " (" + timeList.size() + ")");
        adapter.notifyDataSetChanged();
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

            if(timeList.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete all Times?");
                builder.setNegativeButton("no", null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SpeedcubeApplication.instance().getTimeList().clear();
                        updateListView();
                    }

                });

                builder.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
