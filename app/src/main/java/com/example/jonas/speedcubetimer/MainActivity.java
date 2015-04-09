package com.example.jonas.speedcubetimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// 3014159 26535 89793 23846

public class MainActivity extends Activity {

    private final TouchPad touchPad = new TouchPad();
    private final SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer(this);
    private TextView timerView;
    private TimeViewUpdater timeViewUpdater = new TimeViewUpdater();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.timerView = (TextView) findViewById(R.id.timerView);
        this.timerView.setOnClickListener(new TimeViewOnClickListener());

        this.touchPad.setView(this.getWindow().getDecorView());

        this.speedcubeTimer.setTouchPad(this.touchPad);
        this.speedcubeTimer.setUpdateRunnable(timeViewUpdater);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (speedcubeTimer.getTimerState() != SpeedcubeTimer.TimerState.ready) {
                return true;
            } else {
                speedcubeTimer.reset();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private class TimeViewUpdater implements Runnable {
        @Override
        public void run() {
            timerView.setText(speedcubeTimer.getDisplayString());
        }
    }

    private class TimeViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            speedcubeTimer.reset();
        }
    }
}
