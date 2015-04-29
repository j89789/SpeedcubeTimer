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

    /**
     * Over the touch sensor the timer can be started and stopped.
     */
    private final TouchSensorListener touchPadListener = new TouchSensorListener();
    /**
     * Generate the update frequency
     *
     * @see #updateInterval
     */
    private final Handler handler = new Handler();
    private final int updateInterval = 50;
    /**
     * Debug tag
     */
    private String tag = SpeedcubeTimer.class.getSimpleName();
    /**
     * Context only for showing Dialogs
     */
    private Context context;
    /**
     * Is frequently called if the inspection or solving time is running.
     */
    private TimerUpdater timeUpdater = new TimerUpdater();
    /**
     * This makes the solving timer ready to stat.
     *
     * The touch sensor must be down for a certain time before the solving time can start.
     */
    private SensorDownValidMaker sensorDownValidMaker = new SensorDownValidMaker();
    private Timer solvingTimer = new Timer();
    private CountdownTimer inspectionTimer = new CountdownTimer();
    /**
     * Receiver for change action. Null in no.
     */
    private Listener listener = null;
    private boolean isUseInspectionTime = false;
    /**
     * If true an acoustic signal will played while the incantation time.
     */
    private boolean isAcousticSignalsEnable = false;
    /**
     * This is the last Solving time.
     */
    private Time time = new Time();
    /**
     * Protect attributes which are source of Change events
     */
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

    public SensorStatus getSensorStatus(){
        return d.getSensorStatus();
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
    }

    /**
     * Check if the update is required. If no the update will stopped otherwise will start.
     *
     * Dependent of isTimeUpdaterActive and isTimeUpdaterActive
     */
    private void refreshTimeUpdater() {
        boolean isActive = isTimeUpdaterEnable && isTimeUpdaterActive;

        if (isActive) {
            handler.postDelayed(this.timeUpdater, this.updateInterval);
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
    }

    private void startInspection() {
        d.setTimerState(TimerState.inspection);

        inspectionTimer.start();
        startTimeUpdater();
    }

    public void reset() {
        if (d.getTimerState() == TimerState.solved) {
            d.setTimerState(TimerState.ready);
            d.setSensorStatus(SensorStatus.invalid);
            solvingTimer.reset();
            timeUpdater.run();
        }
        else if(d.getTimerState() == TimerState.inspection)
        {
            d.setTimerState(TimerState.ready);
            d.setSensorStatus(SensorStatus.invalid);

            inspectionTimer.stop();
            inspectionTimer.reset();

            time.setType(Time.Type.valid);
            time.setTimeMs(0);

            timeUpdater.run();
        }
    }

    public void setTouchPad(TouchSensor touchSensor) {
        touchSensor.setListener(touchPadListener);
    }


    /**
     * The listener get all update Events. If any relevant property changed e.g timerState
     * solving time or inspection time. A null listener will remove the listener and
     * the update interval while the timer is running will be stopped.
     * <p/>
     * Activity's: Remove listener on pause for low cpu.
     */
    public void setListener(Listener listener) {
        this.listener = listener;

        isTimeUpdaterEnable = listener != null;
        refreshTimeUpdater();
    }

    public long getInspectionTime() {
        return inspectionTimer.getCurrentTime();
    }

    public Time getTime() {
        return time;
    }

    public void setIsAcousticSignalsEnable(boolean isAcousticSignalsEnable) {
        this.isAcousticSignalsEnable = isAcousticSignalsEnable;
    }

    public long getCurrentSolvingTime() {
        return solvingTimer.getCurrentTime();
    }

    /**
     * @return True if inspection or solving timer running
     */
    public boolean isRunning() {
        return d.getTimerState() == TimerState.inspection || d.getTimerState() == TimerState.solving;
    }

    public enum TimerState {ready, inspection, solving, solved}

    interface Listener {

        /**
         * Call if the  inspection time or solving time are changed.
         */
        void onTimeChanged();

        /**
         * Call if the status changed.
         */
        void onStatusChanged(TimerState oldState, TimerState newState);

        /**
         * Call if the color changed.
         */
        void onSensorStatusChanged();
    }

    enum SensorStatus {waitForValidation, valid, invalid}

    /**
     * Protects the timer state variable. So you must call the set Function and the chance event
     * will be send to the listener...
     */
    private class PrivateData {

        private TimerState timerState = TimerState.ready;
        private SensorStatus sensorStatus = SensorStatus.invalid;

        public SensorStatus getSensorStatus() {
            return sensorStatus;
        }

        public void setSensorStatus(SensorStatus status) {
            if (sensorStatus != status) {
                sensorStatus = status;
                if (listener != null) {
                    listener.onSensorStatusChanged();
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

    private class TouchSensorListener implements TouchSensor.Listener {

        @Override
        public void onSensorUp() {

            if (d.getSensorStatus() == SensorStatus.valid) {
                startSolving();
            } else {
                handler.removeCallbacks(sensorDownValidMaker);
            }

            d.setSensorStatus(SensorStatus.invalid);
        }

        @Override
        public void onSensorDown() {

            if (d.getTimerState() == TimerState.ready && !isUseInspectionTime ||
                    d.getTimerState() == TimerState.inspection ||
                    d.getTimerState() == TimerState.solved && !isUseInspectionTime) {
                d.setSensorStatus(SensorStatus.waitForValidation);

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
                handler.postDelayed(this, updateInterval);
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
            d.setSensorStatus(SensorStatus.valid);
        }
    }
}
