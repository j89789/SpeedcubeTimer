package com.example.jonas.speedcubetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

/**
 * Combine touch pad and timer to a speedcube time functions
 */
class SpeedcubeTimer {

    private final MyTouchPadListener touchPadListener = new MyTouchPadListener();
    private final Handler handler = new Handler();
    private final int viewUpdateInterval = 50;
    private String tag = "SpeedcubeTimer";
    private Context context;
    private Runnable timerRunnable = new TimerRunnable();
    private Timer timer = new Timer();
    private TimerState timerState = TimerState.ready;
    private Listener listener = null;

    public SpeedcubeTimer(Context context) {
        this.context = context;
    }

    public void cancel() {

    }

    public TimerState getTimerState() {
        return timerState;
    }

    private void startSolving() {
        if (timerState == TimerState.ready || timerState == TimerState.inspection) {
            timerState = TimerState.solving;
            this.timer.start();
            this.handler.postDelayed(this.timerRunnable, this.viewUpdateInterval);
        } else {
            Log.d(tag, "startSolving() failed");
        }
    }

    private void stopSolving() {
        if (timerState == TimerState.solving) {
            timerState = TimerState.solved;
            this.timer.stop();
            handler.removeCallbacks(timerRunnable);
        } else {
            Log.d(tag, "stopSolving() failed");
        }
    }

    private void startInspection() {

    }

    public void reset() {
        if (timerState == TimerState.solved) {
            timerState = TimerState.ready;
            timer.reset();
            listener.onTextChanged(timer.currentTimeAsString());
        } else {
            Log.d(tag, "reset() failed");
        }
    }

    public void setTouchPad(TouchPad touchPad) {
        touchPad.setListener(touchPadListener);
    }

    public String getDisplayString() {
        return this.timer.currentTimeAsString();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public enum TimerState {ready, inspection, solving, solved}

    interface Listener {

        /**
         * Text to display changed
         *
         * @param text New text
         */
        void onTextChanged(String text);

        /**
         * Color of Text changed
         *
         * @param colorId Resource id of the new color
         */
        void onColorChanged(int colorId);
    }

    private class MyTouchPadListener implements TouchPad.Listener {

        @Override
        public void onUp() {

            if (timerState == TimerState.ready) {
                startSolving();
                listener.onColorChanged(R.color.red);
            }
        }

        @Override
        public void onDown() {

            if (timerState == TimerState.solving) {
                stopSolving();
                listener.onColorChanged(R.color.normal);
            } else if (timerState == TimerState.solved) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rest Timer?");
                builder.setMessage("You can also reset the timer by click on time view or" +
                        "back button.");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reset();
                    }
                });
                builder.setNegativeButton("cancel", null);
                builder.create().show();
            }
        }

        @Override
        public void onTrigger() {

        }
    }

    private class TimerRunnable implements Runnable {
        @Override
        public void run() {
            listener.onTextChanged(timer.currentTimeAsString());
            handler.postDelayed(this, viewUpdateInterval);
        }
    }
}
