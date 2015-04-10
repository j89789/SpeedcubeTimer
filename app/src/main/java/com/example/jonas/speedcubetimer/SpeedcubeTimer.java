package com.example.jonas.speedcubetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

/**
 * Combine touch pad and solvingTimer to a speedcube time functions
 */
class SpeedcubeTimer {

    private final MyTouchPadListener touchPadListener = new MyTouchPadListener();
    private final Handler handler = new Handler();
    private final int viewUpdateInterval = 50;
    private String tag = "SpeedcubeTimer";
    private Context context;
    private ViewTextUpdater viewTextUpdater = new ViewTextUpdater();
    private DownValidMaker downValidMaker = new DownValidMaker() ;
    private Runnable updater = new ViewTextUpdater();
    private Timer solvingTimer = new Timer();
    private TimerState timerState = TimerState.ready;
    private Listener listener = null;
    private boolean isDownValid = false;

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
            this.solvingTimer.start();
            this.handler.postDelayed(this.viewTextUpdater, this.viewUpdateInterval);
        } else {
            Log.d(tag, "startSolving() failed");
        }
    }

    private void stopSolving() {
        if (timerState == TimerState.solving) {
            timerState = TimerState.solved;
            this.solvingTimer.stop();
            handler.removeCallbacks(viewTextUpdater);
        } else {
            Log.d(tag, "stopSolving() failed");
        }
    }

    private void startInspection() {

    }

    public void reset() {
        if (timerState == TimerState.solved) {
            timerState = TimerState.ready;
            solvingTimer.reset();
            listener.onTextChanged(solvingTimer.currentTimeAsString());
        } else {
            Log.d(tag, "reset() failed");
        }
    }

    public void setTouchPad(TouchPad touchPad) {
        touchPad.setListener(touchPadListener);
    }

    public String getDisplayString() {
        return this.solvingTimer.currentTimeAsString();
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
        public void onSensorUp() {

            if (timerState == TimerState.ready) {
                if(isDownValid) {
                    startSolving();
                }else{
                    handler.removeCallbacks(downValidMaker);
                }

                listener.onColorChanged(R.color.normal);
            }
        }

        @Override
        public void onSensorDown() {

            if(timerState == TimerState.ready)
            {
                isDownValid = false;
                listener.onColorChanged(R.color.invalid);

                handler.postDelayed(downValidMaker, 550);
            }
            if (timerState == TimerState.solving) {
                stopSolving();
            } else if (timerState == TimerState.solved) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rest Timer?");
                builder.setMessage("You can also reset the solvingTimer by click on time view or" +
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

    private class ViewTextUpdater implements Runnable {
        @Override
        public void run() {
            listener.onTextChanged(solvingTimer.currentTimeAsString());
            handler.postDelayed(this, viewUpdateInterval);
        }
    }

    private class DownValidMaker implements Runnable {
        @Override
        public void run() {
            isDownValid = true;
            listener.onColorChanged(R.color.valid);
        }
    }
}
