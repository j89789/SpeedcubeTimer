package com.example.jonas.speedcubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView timerView;
    private final SpeedcubeTimer timer = new SpeedcubeTimer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.getWindow().getDecorView().setOnTouchListener(new OnTouchListener());

        this.timerView = (TextView) findViewById(R.id.timerView);
        this.timerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.reset();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class OnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    if (timer.isNullTime()) {
                        timer.start();
                    } else {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setTitle("Reset Timer before restart.");
                        dlgAlert.setMessage("Click on time view or back button!");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                }
            }

            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!timer.isNullTime()) {
                timer.reset();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private class SpeedcubeTimer extends Timer {

        private final Handler handler = new Handler();
        private int viewUpdateInterval = 50;
        private TimeViewUpdater timeViewUpdater = new TimeViewUpdater();

        public void start() {
            super.start();

            this.handler.postDelayed(this.timeViewUpdater, this.viewUpdateInterval);
        }

        public void stop() {
            super.stop();
            handler.removeCallbacks(timeViewUpdater);
        }

        public void reset() {
            super.reset();

            this.timeViewUpdater.run();

        }

        private class TimeViewUpdater implements Runnable {
            @Override
            public void run() {
                timerView.setText(currentTimeAsString());

                if (isRunning()) {
                    handler.postDelayed(this, viewUpdateInterval);
                }
            }
        }
    }
}
