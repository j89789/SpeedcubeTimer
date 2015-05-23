package com.speedcubeapp.timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class TimeListActivity extends Activity {

    private ListView listView;

    private Puzzle puzzle;
    private TimeSession session;


    @Override
    protected void onResume() {

        boolean isUseMilliseconds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useMilliseconds", true);
        session.getAdapter().setIsUseMilliseconds(isUseMilliseconds);

        super.onResume();
    }

    private void updatePuzzle() {

        puzzle = SpeedcubeApplication.instance().getCurrentPuzzle();
        session = SpeedcubeApplication.instance().getCurrentPuzzle().getSession();

        getActionBar().setIcon(puzzle.getImageResourceId());

        listView.setAdapter(session.getAdapter());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        listView = (ListView) findViewById(R.id.listView);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Time time = session.get(position);

                time.showPopupMenu(TimeListActivity.this, view, null);
            }
        });

        updatePuzzle();
        SpeedcubeApplication.instance().addListener(new SpeedcubeApplication.PuzzleChangeListener() {
            @Override
            public void onPuzzleChanged() {
                updatePuzzle();
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
        } else if (id == R.id.action_delete) {

            if (session.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete_all_times));
                builder.setNegativeButton(android.R.string.no, null);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.clear();
                    }

                });

                builder.create().show();
            }
        } else if (id == R.id.action_statistic) {

            showStatistic();
        } else if (id == R.id.action_save) {

            showSaveDialog();
        } else if (id == R.id.action_load) {

            showLoadDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoadDialog() {

        final File[] files = getFilesDir().listFiles();
        final String[] fileNames = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        FileAdapter adapter = new FileAdapter(this);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        builder.setTitle("Select file");
        builder.setView(listView);
        builder.setPositiveButton(android.R.string.cancel, null);

        final AlertDialog alertDialog = builder.create();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = fileNames[position];
                File file = new File(getFilesDir(), name);

                session.load(file);

                Toast.makeText(TimeListActivity.this, "Load  " + name, Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showSaveDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String defaultName = df.format(Calendar.getInstance().getTime());

        final EditText editText = new EditText(this);
        editText.setSingleLine();
        editText.setSelectAllOnFocus(true);
        editText.setText(defaultName);

        builder.setView(editText);

        final Runnable runnableSave = new Runnable() {
            @Override
            public void run() {
                String name = editText.getText().toString();

                if (!name.isEmpty()) {
                    final File file = new File(getFilesDir(), name);

                    if(file.exists())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TimeListActivity.this);
                        builder.setTitle("Override exiting file?");
                        builder.setNegativeButton(android.R.string.no, null);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save(file);
                            }
                        });

                        builder.show();
                    }else {
                        save(file);
                    }

                } else {
                    Toast.makeText(TimeListActivity.this, "Save failed. Name ist empty!", Toast.LENGTH_SHORT).show();
                }
            }

            private void save(File file) {
                session.save(file);
                Toast.makeText(TimeListActivity.this, "Saved as " + file.getName(), Toast.LENGTH_SHORT).show();
            }
        };

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runnableSave.run();
            }

        });

        builder.setNegativeButton("Cancel", null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    runnableSave.run();
                    alertDialog.dismiss();

                    return true;
                }

                return false;
            }
        });

        alertDialog.show();

    }

    private void showStatistic() {

        List<Time> times = session.getTimes();

        int totalSolvingTime = 0;

        for (Time time : times) {
            totalSolvingTime += time.getTimeMs();
        }

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_statistics, null);

        ((TextView) view.findViewById(R.id.textViewTotalSolvingTime))
                .setText(Time.toStringMs(totalSolvingTime));

        if (session.getBestTime() != null) {
            ((TextView) view.findViewById(R.id.textViewBest))
                    .setText(session.getBestTime().toStringMs());
        }

        if (session.getWorseTime() != null) {
            ((TextView) view.findViewById(R.id.textViewWorse))
                    .setText(session.getWorseTime().toStringMs());
        }

        if (session.getAverageAll() != 0) {
            ((TextView) view.findViewById(R.id.textViewMean))
                    .setText(Time.toStringMs(session.getAverageAll()));
        }

        if (session.getBestAverageOf5Time() != null) {
            ((TextView) view.findViewById(R.id.textViewAo5Best))
                    .setText(Time.toStringMs(session.getBestAverageOf5Time().getAverageOf5()));
        }
        if (session.getWorseAverageOf5Time() != null) {
            ((TextView) view.findViewById(R.id.textViewAo5Worse))
                    .setText(Time.toStringMs(session.getWorseAverageOf5Time().getAverageOf5()));
        }

        if (session.getBestAverageOf12Time() != null) {
            ((TextView) view.findViewById(R.id.textViewAo12Best))
                    .setText(Time.toStringMs(session.getBestAverageOf12Time().getAverageOf12()));
        }

        if (session.getWorseAverageOf12Time() != null) {
            ((TextView) view.findViewById(R.id.textViewAo12Worse))
                    .setText(Time.toStringMs(session.getWorseAverageOf12Time().getAverageOf12()));
        }

        int total = session.size();
        int valid = session.size() - session.getDnfTimes().size();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.time_statistics) + " (" + valid + "/" + total + ")");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}
