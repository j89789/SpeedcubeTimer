package com.speedcubeapp.timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class TimerActivity extends Activity {

    /**
     * Input for the SpeedcubeTimer.
     */
    private final TouchSensor touchSensor = new TouchSensor();
    private boolean isUseMilliseconds = false;
    /**
     * This is the central element which will be visualise with this activity
     */
    private SpeedcubeTimer speedcubeTimer = SpeedcubeApplication.instance().getSpeedcubeTimer();
    /**
     * Information's of the current Session will be shown
     */
    private TimeSession session;
    private Puzzle puzzel;
    private TextView textViewTime;
    private TextView textViewAverage5;
    private TextView textViewAverage12;
    private TextView textViewBestTime;
    private TextView textViewWorstTime;
    private TextView textViewScramble;
    /**
     * Handel's changed form the Speedcube Timer
     */
    private SpeedcubeListener speedcubeListener = new SpeedcubeListener();

    SingleShotTimer screenOfTimer = new SingleShotTimer();

    private SharedPreferences defaultSharedPreferences;
    private boolean isShowAverage;
    /**
     * Best and worst time
     */
    private boolean isShowExtremeValues;

    private TimeSession.OnChangListener onChangListener = new TimeSession.OnChangListener() {
        @Override
        public void onAverageChanged() {
            updateAverageView();
        }

        @Override
        public void onExtremeValuesChange() {
            updateExtremeValues();
        }

        @Override
        public void onSizeChanged() {
        }
    };
    private boolean isShowScramble;

    @Override
    protected void onPause() {
        super.onPause();

        speedcubeTimer.setListener(null);
        session.setOnChangListener(null);
    }

    @Override
    protected void onResume() {

        // load settings
        isUseMilliseconds = defaultSharedPreferences.getBoolean("useMilliseconds", true);
        isShowAverage = defaultSharedPreferences.getBoolean("showAverage", true);
        isShowExtremeValues = defaultSharedPreferences.getBoolean("showExtremeValues", true);
        isShowScramble = defaultSharedPreferences.getBoolean("showScramble", true);

        if (!isShowScramble) {
            textViewScramble.setVisibility(View.GONE);
        } else {
            textViewScramble.setVisibility(View.VISIBLE);
        }

        speedcubeTimer.setIsUseInspectionTime(defaultSharedPreferences.getBoolean("inspectionTimeEnable", true));
        speedcubeTimer.setIsAcousticSignalsEnable(defaultSharedPreferences.getBoolean("inspectionAcousticSignals", true));

        speedcubeTimer.setTouchPad(touchSensor);
        speedcubeTimer.setListener(speedcubeListener);

        updatePuzzle();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopKeepScreenOn();

        super.onResume();
    }

    private void updatePuzzle() {

        session = SpeedcubeApplication.instance().getCurrentPuzzle().getSession();
        puzzel = SpeedcubeApplication.instance().getCurrentPuzzle();

        getActionBar().setIcon(puzzel.getImageResourceId());
        getActionBar().setTitle(puzzel.getNameResourceId());

        session.setOnChangListener(onChangListener);

        // Update all
        updateExtremeValues();
        updateAverageView();
        updateColorOfTime();
        updateTimeView();
        updateTypeView();
        updateScrambleView();

        invalidateScramble();
    }

    private void updateAverageView() {

        if (isShowAverage && !speedcubeTimer.isRunning()) {
            long average5 = session.getAverage5();
            long average12 = session.getAverage12();
            long mean = session.getAverageAll();

            if (average5 != 0) {
                textViewAverage5.setText(Time.toString(average5, isUseMilliseconds));
                findViewById(R.id.rowAo5).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rowAo5).setVisibility(View.GONE);
            }

            if (average12 != 0) {
                textViewAverage12.setText(Time.toString(average12, isUseMilliseconds));
                findViewById(R.id.rowAo12).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rowAo12).setVisibility(View.GONE);
            }

            if (session.getSize() > 1) {
                ((TextView) findViewById(R.id.textViewMean)).setText(Time.toString(mean, isUseMilliseconds));
                findViewById(R.id.tableRowMean).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.tableRowMean).setVisibility(View.GONE);
            }


            findViewById(R.id.tableLayoutAverage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tableLayoutAverage).setVisibility(View.GONE);
        }

    }

    private void updateExtremeValues() {

        if (isShowExtremeValues && session.size() > 1 && !speedcubeTimer.isRunning()) {
            textViewBestTime.setText(Time.toString(session.getBestTime().getTimeMs(), isUseMilliseconds));
            textViewWorstTime.setText(Time.toString(session.getWorseTime().getTimeMs(), isUseMilliseconds));
            findViewById(R.id.tableLayoutExtremeValues).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tableLayoutExtremeValues).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preference = getSharedPreferences("PREFERENCE", MODE_PRIVATE);

        preference.edit()
                .putInt("lastPuzzle", SpeedcubeApplication.instance().getCurrentPuzzle().getId())
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.speedcubeTimer.setContext(null);
    }

    private void invalidateScramble() {
        if (isShowScramble) {
            textViewScramble.setText(puzzel.generateScramble());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);

        textViewAverage12 = (TextView) findViewById(R.id.textViewAo12);
        textViewAverage5 = (TextView) findViewById(R.id.textViewAo5);
        textViewBestTime = (TextView) findViewById(R.id.textViewBestTime);
        textViewWorstTime = (TextView) findViewById(R.id.textViewWorstTime);
        textViewScramble = (TextView) findViewById(R.id.textViewScramble);

        SpeedcubeApplication.instance().defaultTextColor = textViewAverage12.getTextColors();

        textViewScramble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateScramble();
            }
        });


        textViewTime = (TextView) findViewById(R.id.timerView);
        textViewTime.setOnClickListener(new TimeViewOnClickListener());

        touchSensor.setView(this.getWindow().getDecorView());

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        speedcubeTimer.setContext(this);

        checkFirstRun();

        // Keep screen for 2 minutes on
        screenOfTimer.setInterval(120000);

        screenOfTimer.setTimeoutLister(new SingleShotTimer.TimeoutLister() {
            @Override
            public void onTimeout() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);

        SpeedcubeApplication.instance().addListener(new SpeedcubeApplication.PuzzleChangeListener() {
            @Override
            public void onPuzzleChanged() {
                updatePuzzle();
            }
        });
    }

    public void checkFirstRun() {
        SharedPreferences preference = getSharedPreferences("PREFERENCE", MODE_PRIVATE);

        boolean isFirstRun = preference.getBoolean("isFirstRun", true);

        if (isFirstRun){

            showHelpDialog();
            preference.edit().putBoolean("isFirstRun", false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_show_timeList) {
            Intent intent = new Intent(this, TimeListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
        }
        else if (id == android.R.id.home) {
            SpeedcubeApplication.instance().showPuzzlesDialog(this);
        }


        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        String version = "";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {}

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name) + "  " + version);
        builder.setMessage(getString(R.string.help_text));
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // deactivate back button while inspection and solving
            if (speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.inspection ||
                    speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.solving) {
                return true;
            } else if (speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.solved) {
                speedcubeTimer.reset();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void updateTypeView() {
        if (speedcubeTimer.getTime().getType() == Time.Type.DNF) {
            ((TextView) findViewById(R.id.textViewType)).setText(getString(R.string.time_type_DNF));
        } else if (speedcubeTimer.getTime().getType() == Time.Type.plus2) {
            ((TextView) findViewById(R.id.textViewType)).setText(getString(R.string.time_type_plus2));
        } else {
            ((TextView) findViewById(R.id.textViewType)).setText("");
        }
    }

    private void updateTimeView() {
        String text = "";

        SpeedcubeTimer.TimerState state = speedcubeTimer.getTimerState();

        if (state == SpeedcubeTimer.TimerState.inspection) {

            long inspectionTime = speedcubeTimer.getInspectionTime();

            if (inspectionTime < -2000) {
                text += getString(R.string.time_type_DNF);
            } else if (inspectionTime < 0) {
                text += getString(R.string.time_type_plus2);
            } else {
                text = Time.toString(inspectionTime + 999, 0);
            }
        } else if (state == SpeedcubeTimer.TimerState.solving
                || state == SpeedcubeTimer.TimerState.solved
                || state == SpeedcubeTimer.TimerState.ready) {
            text = Time.toString(speedcubeTimer.getCurrentSolvingTime(), isUseMilliseconds);
        }

        if (!text.isEmpty()) {
            textViewTime.setText(text);
        }
    }

    private void updateColorOfTime() {

        SpeedcubeTimer.SensorStatus i = speedcubeTimer.getSensorStatus();
        if (i == SpeedcubeTimer.SensorStatus.waitForValidation) {
            textViewTime.setTextColor(getResources().getColor(R.color.red));
        } else if (i == SpeedcubeTimer.SensorStatus.valid) {
            textViewTime.setTextColor(getResources().getColor(R.color.green));
        } else if (i == SpeedcubeTimer.SensorStatus.invalid) {
            if (speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.ready) {
                textViewTime.setTextColor(getResources().getColor(R.color.blue));
            } else {
                textViewTime.setTextColor(SpeedcubeApplication.instance().defaultTextColor);
            }
        }


    }

    private void updateScrambleView() {
        if (isShowScramble) {
            if (speedcubeTimer.isRunning()) {
                textViewScramble.setVisibility(View.GONE);
            } else {
                textViewScramble.setVisibility(View.VISIBLE);
            }
        }

    }

    private class SpeedcubeListener implements SpeedcubeTimer.Listener {
        @Override
        public void onStatusChanged(SpeedcubeTimer.TimerState oldState, SpeedcubeTimer.TimerState newState) {
            updateColorOfTime();
            updateScrambleView();
            updateExtremeValues();
            updateTypeView();
            updateAverageView();


            if (newState == SpeedcubeTimer.TimerState.solved) {
                invalidateScramble();
            }

            if (newState == SpeedcubeTimer.TimerState.inspection ||
                    newState == SpeedcubeTimer.TimerState.solving) {
                keepScreenOn();
            }
            else{
                stopKeepScreenOn();
            }

        }

        @Override
        public void onSensorStatusChanged() {
            updateColorOfTime();
        }

        @Override
        public void onTimeChanged() {
            updateTimeView();
        }

    }

    private void stopKeepScreenOn() {
        screenOfTimer.restart();
    }

    private void keepScreenOn() {
        screenOfTimer.stop();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private class TimeViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.solved) {

                final Time time = speedcubeTimer.getTime();

                time.showPopupMenu(TimerActivity.this, textViewTime, new Time.PopupMenuLister() {
                    @Override
                    public void onAction(int id) {

                        if (id == R.id.delete) {
                            speedcubeTimer.reset();
                        }

                        updateTypeView();
                    }
                });
            } else if (speedcubeTimer.getTimerState() == SpeedcubeTimer.TimerState.inspection) {
                speedcubeTimer.reset();
            }

//            speedcubeTimer.reset();
        }
    }
}
