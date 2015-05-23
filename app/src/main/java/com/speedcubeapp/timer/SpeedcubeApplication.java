package com.speedcubeapp.timer;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Main Application
 */
public class SpeedcubeApplication extends Application {

    static public int versionCode;
    static public String versionName;
    static private SpeedcubeApplication globalInstance;
    public ColorStateList defaultTextColor;
    ArrayList<PuzzleChangeListener> listeners = new ArrayList<>();
    private SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer();
    private Puzzle[] puzzles = {new Cube2x2x2(), new Cube3x3x3(), new Cube4x4x4(), new Pyraminx()};
    private Puzzle currentPuzzle = null;

    static public SpeedcubeApplication instance() {
        return globalInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        globalInstance = this;

        setCurrentPuzzle(puzzles[0]);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Puzzle getCurrentPuzzle() {
        return currentPuzzle;
    }

    public SpeedcubeTimer getSpeedcubeTimer() {
        return speedcubeTimer;
    }

    void showPuzzlesDialog(Context context) {

        PuzzleAdapter adapter = new PuzzleAdapter(puzzles);

        ListView listView = new ListView(context);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select puzzle");
        builder.setView(listView);
        builder.setNegativeButton(android.R.string.cancel, null);

        final AlertDialog alertDialog = builder.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setCurrentPuzzle(puzzles[position]);

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void addListener(PuzzleChangeListener listener) {
        listeners.add(listener);
    }

    public void setCurrentPuzzle(Puzzle puzzle) {

        if (puzzle != currentPuzzle) {
            this.currentPuzzle = puzzle;

            for (PuzzleChangeListener listener : listeners) {
                listener.onPuzzleChanged();
            }
        }

    }

    interface PuzzleChangeListener {
        void onPuzzleChanged();


    }

    Puzzle getPuzzleById(int id){
        for (Puzzle puzzle : puzzles) {
            if (puzzle.getId() == id) {
                return puzzle;
            }
        }
        return null;
    }
}
