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
    private final int timeUpdateInterval = 50;
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

    /**
     * True while the time updater is active. Also when the Activity is in pause.
     *
     * @see #isTimeUpdaterEnable
     */
    private boolean isTimeUpdaterActive = false;


    /**
     * True when the updater is needed e.g. the Activity is running.
     *
     * @see #isTimeUpdaterActive
     */
    private boolean isTimeUpdaterEnable = false;

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
            startTimeUpdater();
            Log.d(TAG, "Start solving...");
        } else {
            Log.d(TAG, "startSolving() failed");
        }
    }

    private void refreshTimeUpdater(){
        boolean isActive = isTimeUpdaterEnable && isTimeUpdaterActive;

        if(isActive){
            handler.postDelayed(this.timeUpdater, this.timeUpdateInterval);
        }
        else{
            handler.removeCallbacks(this.timeUpdater);
        }
    }

    private void startTimeUpdater() {
        stopTimeUpdater();
        isTimeUpdaterActive = true;
        refreshTimeUpdater();
    }

    private void stopTimeUpdater() {
        isTimeUpdaterActive = false;
        refreshTimeUpdater();
    }


    private void finishedSolving() {
        if (timerState == TimerState.solving) {
            timerState = TimerState.solved;
            solvingTimer.stop();
            stopTimeUpdater();
            Log.d(TAG, "Finished solving!");
        } else {
            Log.d(TAG, "finishedSolving() failed");
        }
    }

    private void startInspection() {
        if (timerState == TimerState.ready) {
            timerState = TimerState.inspection;

            inspectionTimer.start();
            startTimeUpdater();
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
        isTimeUpdaterEnable = listener != null;
        refreshTimeUpdater();
    }

    public long getInspectionTime() {
        return inspectionTimer.getCurrentTime();
    }

    public long getSolvingTime() {
        return solvingTimer.getCurrentTime();
    }

    public int getColorId() {
        return colorId;
    }

    public enum TimerState {ready, inspection, solving, solved}

    interface Listener {

        /**
         * Call if the status, inspection time the solving time are changed. While the
         * time is running the interval ist defined in #timeUpdateInterval.
         */
        void onUpdate();
    }

    private int colorId = R.color.normal;

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

                colorId = R.color.normal;
                sendUpdate();
            }
        }

        @Override
        public void onSensorDown() {

            if (timerState == TimerState.ready && !isUseInspectionTime ||
                    timerState == TimerState.inspection) {
                isSensorDownValid = false;
                colorId = (R.color.invalid);
                sendUpdate();

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

    private void sendUpdate() {
        if (listener != null) {
            listener.onUpdate();
        }
    }

    private class TimerUpdater implements Runnable {

        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        long lastTime = Long.MAX_VALUE;

        @Override
        public void run() {

            if (timerState == TimerState.inspection) {

                long currentTime = inspectionTimer.getCurrentTime();

                if (currentTime < -2000) {
                    stopTimeUpdater();
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
            }

           sendUpdate();

            if (isTimeUpdaterActive) {
                handler.postDelayed(this, timeUpdateInterval);
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

            colorId = R.color.valid;
            sendUpdate();
        }
    }
}
