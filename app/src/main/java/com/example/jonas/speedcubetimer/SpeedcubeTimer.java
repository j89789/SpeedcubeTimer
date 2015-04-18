package com.example.jonas.speedcubetimer;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

/**
 * Combine touch pad and solvingTimer to a speedcube time functions
 */
class SpeedcubeTimer {

    private final MyTouchSensorListener touchPadListener = new MyTouchSensorListener();
    private final Handler handler = new Handler();
    private final int timeUpdateInterval = 50;
    private String TAG = SpeedcubeTimer.class.getSimpleName();
    private Context context;
    private TimerUpdater timeUpdater = new TimerUpdater();
    private SensorDownValidMaker sensorDownValidMaker = new SensorDownValidMaker();
    private Timer solvingTimer = new Timer();
    private CountdownTimer inspectionTimer = new CountdownTimer();
    private Listener listener = null;
    private boolean isSensorDownValid = false;
    private boolean isUseInspectionTime = false;
    private boolean isAcousticSignalsEnable = false;
    private Time time = new Time();


    private PrivateData d = new PrivateData();
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


    public SpeedcubeTimer() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setIsUseInspectionTime(boolean isUseInspectionTime) {
        this.isUseInspectionTime = isUseInspectionTime;
    }


    public void cancel() {

    }

    public TimerState getTimerState() {
        return d.getTimerState();
    }

    private void startSolving() {

        d.setTimerState(TimerState.solving);

        solvingTimer.reset();
        solvingTimer.start();

        inspectionTimer.stop();
        inspectionTimer.reset();

        startTimeUpdater();

        Log.d(TAG, "Start solving...");
    }

    private void refreshTimeUpdater() {
        boolean isActive = isTimeUpdaterEnable && isTimeUpdaterActive;

        if (isActive) {
            handler.postDelayed(this.timeUpdater, this.timeUpdateInterval);
        } else {
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

        d.setTimerState(TimerState.solved);

        solvingTimer.stop();
        stopTimeUpdater();

        if (listener != null) {
            listener.onTimeChanged();
        }

        time.setTimeMs(solvingTimer.getCurrentTime());
        SpeedcubeApplication.instance().getTimeSession().addNewTime(time);

        Log.d(TAG, "Finished solving!");
    }

    private void startInspection() {
        d.setTimerState(TimerState.inspection);

        inspectionTimer.start();
        startTimeUpdater();

        Log.d(TAG, "Start inspection...");
    }

    public void reset() {
        if (d.getTimerState() == TimerState.solved) {
            d.setTimerState(TimerState.ready);
            d.setColorId(R.color.ready);
            solvingTimer.reset();
            timeUpdater.run();
        }
        else if(d.getTimerState() == TimerState.inspection)
        {
            d.setTimerState(TimerState.ready);
            d.setColorId(R.color.ready);

            inspectionTimer.stop();
            inspectionTimer.reset();

            time.setType(Time.Type.valid);

            timeUpdater.run();
        }

        Log.d(TAG, "Reset");
    }

    public void setTouchPad(TouchSensor touchSensor) {
        touchSensor.setListener(touchPadListener);
    }

    public String getDisplayString() {
        return this.solvingTimer.currentTimeToMsString();
    }

    /**
     * The listener get all update Events. If any relevant property changed e.g timerState
     * solving time or inspection time. A null listener will remove the listener and
     * the update interval while the timer is running will be stopped.
     * <p/>
     * Activity's: Remove listener on pause for low cpu. The listener funktions will be called
     * in this for initial GUI update
     */
    public void setListener(Listener listener) {
        this.listener = listener;

        isTimeUpdaterEnable = listener != null;
        refreshTimeUpdater();

        if (listener != null) {
            listener.onColorChanged();
            listener.onStatusChanged(TimerState.ready, this.getTimerState());
            listener.onTimeChanged();
        }
    }

    public long getInspectionTime() {
        return inspectionTimer.getCurrentTime();
    }

    public Time getTime() {
        return time;
    }

    public int getColorId() {
        return d.getColorId();
    }

    public void setIsAcousticSignalsEnable(boolean isAcousticSignalsEnable) {
        this.isAcousticSignalsEnable = isAcousticSignalsEnable;
    }

    public long getCurrentSolvingTime() {
        return solvingTimer.getCurrentTime();
    }

    public enum TimerState {ready, inspection, solving, solved}

    interface Listener {

        /**
         * Call if the  inspection time or solving time are changed.
         */
        void onTimeChanged();

        /**
         * Call if the status changed.
         *
         * @param oldState
         * @param newState
         */
        void onStatusChanged(TimerState oldState, TimerState newState);

        /**
         * Call if the color changed.
         */
        void onColorChanged();
    }

    /**
     * Protects the timer state variable. So you must call the set Function and the update
     * will be send...
     */
    private class PrivateData {

        private TimerState timerState = TimerState.ready;
        private int colorId = R.color.ready;

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int newColorId) {

            if (colorId != newColorId) {
                colorId = newColorId;
                if (listener != null) {
                    listener.onColorChanged();
                }
            }
        }

        public TimerState getTimerState() {
            return timerState;
        }

        public void setTimerState(TimerState newTimerState) {

            if (timerState != newTimerState) {
                TimerState oldTimerState = timerState;

                if(oldTimerState == TimerState.solved){
                    time = new Time();
                }

                timerState = newTimerState;
                if (listener != null) {
                    listener.onStatusChanged(oldTimerState, newTimerState);
                }
            }
        }
    }

    private class MyTouchSensorListener implements TouchSensor.Listener {

        @Override
        public void onSensorUp() {

            if (isSensorDownValid) {
                isSensorDownValid = false;
                startSolving();
            } else {
                handler.removeCallbacks(sensorDownValidMaker);
            }

            if (d.getTimerState() == TimerState.ready) {
                d.setColorId(R.color.ready);
            } else {
                d.setColorId(R.color.normal);
            }
        }

        @Override
        public void onSensorDown() {

            if (d.getTimerState() == TimerState.ready && !isUseInspectionTime ||
                    d.getTimerState() == TimerState.inspection ||
                    d.getTimerState() == TimerState.solved && !isUseInspectionTime) {
                isSensorDownValid = false;
                d.setColorId(R.color.invalid);

                handler.postDelayed(sensorDownValidMaker, 550);
            }
            if (d.getTimerState() == TimerState.solving) {
                finishedSolving();
            }
        }

        @Override
        public void onTrigger() {

            if (isUseInspectionTime) {
                if (d.getTimerState() == TimerState.ready ||
                        d.getTimerState() == TimerState.solved) {
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

            if (d.getTimerState() == TimerState.inspection) {

                long currentTime = inspectionTimer.getCurrentTime();

                if (currentTime < -2000) {
                    stopTimeUpdater();
                    time.setType(Time.Type.DNF);
                } else if (currentTime < 0) {
                    time.setType(Time.Type.plus2);
                }

                if (isAcousticSignalsEnable) {

                    /* Play countdown Sounds at 8, 3, and 0 seconds */
                    boolean isCountdownBeep = lastTime >= 8000 && currentTime < 8000 ||
                            lastTime >= 3000 && currentTime < 3000;

                    boolean isZeroBeep = lastTime >= 0 && currentTime < 0;

                    if (isCountdownBeep || isZeroBeep) {
                        Beep beep = new Beep(isZeroBeep);
                        beep.start();
                    }
                }

                lastTime = currentTime;
            }

            if (listener != null) {
                listener.onTimeChanged();
            }

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
            d.setColorId(R.color.valid);
        }
    }
}
