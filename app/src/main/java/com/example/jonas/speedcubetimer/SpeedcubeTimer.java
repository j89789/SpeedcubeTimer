package com.example.jonas.speedcubetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

/**
 * Combine touch pad and solvingTimer to a speedcube time functions
 */
class SpeedcubeTimer {

    private final MyTouchPadListener touchPadListener = new MyTouchPadListener();
    private final Handler handler = new Handler();
    private final int viewUpdateInterval = 50;
    private String TAG = SpeedcubeTimer.class.getSimpleName();
    private Context context;
    private TimerUpdater timeUpdater = new TimerUpdater();
    private SensorDownValidMaker sensorDownValidMaker = new SensorDownValidMaker();
    private Timer solvingTimer = new Timer();
    private CountdownTimer inspectionTimer = new CountdownTimer();
    private TimerState timerState = TimerState.ready;
    private Listener listener = null;
    private boolean isSensorDownValid = false;
    private boolean isUseInspectionTime = true;
    private boolean isUseMilliseconds = true;

    public SpeedcubeTimer(Context context) {
        this.context = context;
    }

    public void setIsUseInspectionTime(boolean isUseInspectionTime) {
        this.isUseInspectionTime = isUseInspectionTime;
    }

    public void setIsUseMilliseconds(boolean isUseMilliseconds) {
        this.isUseMilliseconds = isUseMilliseconds;
        timeUpdater.run();
    }

    public void cancel() {

    }

    public TimerState getTimerState() {
        return timerState;
    }

    private void startSolving() {
        if (timerState == TimerState.ready || timerState == TimerState.inspection) {
            timerState = TimerState.solving;
            solvingTimer.start();
            inspectionTimer.stop();
            inspectionTimer.reset();
            startUpdater();
            Log.d(TAG, "Start solving...");
        } else {
            Log.d(TAG, "startSolving() failed");
        }
    }

    private void startUpdater() {
        this.stopUpdater();
        handler.postDelayed(this.timeUpdater, this.viewUpdateInterval);
    }

    private void stopUpdater() {
        handler.removeCallbacks(this.timeUpdater);
    }


    private void finishedSolving() {
        if (timerState == TimerState.solving) {
            timerState = TimerState.solved;
            this.solvingTimer.stop();
            stopUpdater();
            Log.d(TAG, "Finished solving!");
        } else {
            Log.d(TAG, "finishedSolving() failed");
        }
    }

    private void startInspection() {
        if (timerState == TimerState.ready) {
            timerState = TimerState.inspection;

            inspectionTimer.start();
            startUpdater();
            Log.d(TAG, "Start inspection...");
        } else {
            Log.d(TAG, "startInspection() failed");
        }
    }

    public void reset() {
        if (timerState == TimerState.solved) {
            timerState = TimerState.ready;
            solvingTimer.reset();
            timeUpdater.run();
            Log.d(TAG, "Reset");
        } else {
            Log.d(TAG, "reset() failed");
        }
    }

    public void setTouchPad(TouchSensor touchSensor) {
        touchSensor.setListener(touchPadListener);
    }

    public String getDisplayString() {
        return this.solvingTimer.currentTimeToMsString();
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

    private class MyTouchPadListener implements TouchSensor.Listener {

        @Override
        public void onSensorUp() {

            if (timerState == TimerState.ready && !isUseInspectionTime ||
                    timerState == TimerState.inspection) {
                if (isSensorDownValid) {
                    startSolving();
                } else {
                    handler.removeCallbacks(sensorDownValidMaker);
                }

                listener.onColorChanged(R.color.normal);
            }
        }

        @Override
        public void onSensorDown() {

            if (timerState == TimerState.ready && !isUseInspectionTime ||
                    timerState == TimerState.inspection) {
                isSensorDownValid = false;
                listener.onColorChanged(R.color.invalid);

                handler.postDelayed(sensorDownValidMaker, 550);
            }
            if (timerState == TimerState.solving) {
                finishedSolving();
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

            if (isUseInspectionTime) {
                if (timerState == TimerState.ready) {
                    startInspection();
                }
            }
        }
    }

    private class TimerUpdater implements Runnable {

        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        long lastTime = Long.MAX_VALUE;

        @Override
        public void run() {
            String text = "";
            boolean recall = true;

            if (timerState == TimerState.inspection) {

                long currentTime = inspectionTimer.getCurrentTime();

                if (currentTime < -2000) {
                    text += "DNF";
                    recall = false;
                } else if (currentTime < 0) {
                    text += "+2";
                } else {
                    text = inspectionTimer.currentToTsString();
                }

                /* Play countdown Sounds at 8, 3, and 0 seconds */
                boolean isCountdownBeep = lastTime >= 8000 && currentTime < 8000 ||
                        lastTime >= 3000 && currentTime < 3000;

                boolean isZeroBeep = lastTime >= 0 && currentTime < 0;

                if (isCountdownBeep || isZeroBeep) {
                    Beep beep = new Beep(isZeroBeep);
                    beep.start();
                }

                lastTime = currentTime;
            } else {
                text = isUseMilliseconds ? solvingTimer.currentTimeToMsString() : solvingTimer.currentToHsString();

                if(timerState == TimerState.ready){
                    recall = false;
                }
            }


            listener.onTextChanged(text);

            if (recall) {
                handler.postDelayed(this, viewUpdateInterval);
            }
        }

        class Beep extends Thread {

            private int tone = ToneGenerator.TONE_PROP_BEEP;

            Beep(boolean isZeroBeep) {
                if (isZeroBeep) {
                    tone = ToneGenerator.TONE_PROP_BEEP2;
                }
            }

            @Override
            public void run() {
                super.run();
                tg.startTone(tone);
            }
        }
    }

    private class SensorDownValidMaker implements Runnable {
        @Override
        public void run() {
            isSensorDownValid = true;
            listener.onColorChanged(R.color.valid);
        }
    }
}
