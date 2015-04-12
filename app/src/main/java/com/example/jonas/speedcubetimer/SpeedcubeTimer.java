package com.example.jonas.speedcubetimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Combine touch pad and solvingTimer to a speedcube time functions
 */
class SpeedcubeTimer {

    private final MyTouchPadListener touchPadListener = new MyTouchPadListener();
    private final Handler handler = new Handler();
    private final int viewUpdateInterval = 50;
    private String Tag = SpeedcubeTimer.class.getSimpleName();
    private Context context;
    private ViewTextUpdater viewTextUpdater = new ViewTextUpdater();
    private DownValidMaker downValidMaker = new DownValidMaker();
    private Runnable updater = new ViewTextUpdater();
    private Timer solvingTimer = new Timer();
    private CountdownTimer inspectionTimer = new CountdownTimer();
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
            solvingTimer.start();
            inspectionTimer.stop();
            inspectionTimer.reset();
            startUpdater();
            Log.d(Tag, "Start solving...");
        } else {
            Log.d(Tag, "startSolving() failed");
        }
    }

    private void startUpdater() {
        this.stopUpdater();
        handler.postDelayed(this.viewTextUpdater, this.viewUpdateInterval);
    }

    private void stopUpdater() {
        handler.removeCallbacks(this.viewTextUpdater);
    }


    private void finishedSolving() {
        if (timerState == TimerState.solving) {
            timerState = TimerState.solved;
            this.solvingTimer.stop();
            stopUpdater();
            Log.d(Tag, "Finished solving!");
        } else {
            Log.d(Tag, "finishedSolving() failed");
        }
    }

    private void startInspection() {
        if (timerState == TimerState.ready) {
            timerState = TimerState.inspection;

            inspectionTimer.start();
            startUpdater();
            Log.d(Tag, "Start inspection...");
        } else {
            Log.d(Tag, "startInspection() failed");
        }
    }

    public void reset() {
        if (timerState == TimerState.solved) {
            timerState = TimerState.ready;
            solvingTimer.reset();
            listener.onTextChanged(solvingTimer.currentTimeToMsString());
            Log.d(Tag, "Reset");
        } else {
            Log.d(Tag, "reset() failed");
        }
    }

    public void setTouchPad(TouchPad touchPad) {
        touchPad.setListener(touchPadListener);
    }

    public String getDisplayString() {
        return this.solvingTimer.currentTimeToMsString();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private boolean getIsInspectionTimeEnable() {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return myPreference.getBoolean("inspectionTimeEnable", true);
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

            if (timerState == TimerState.ready && !getIsInspectionTimeEnable() ||
                    timerState == TimerState.inspection) {
                if (isDownValid) {
                    startSolving();
                } else {
                    handler.removeCallbacks(downValidMaker);
                }

                listener.onColorChanged(R.color.normal);
            }
        }

        @Override
        public void onSensorDown() {

            if (timerState == TimerState.ready && !getIsInspectionTimeEnable() ||
                    timerState == TimerState.inspection) {
                isDownValid = false;
                listener.onColorChanged(R.color.invalid);

                handler.postDelayed(downValidMaker, 550);
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

            if (getIsInspectionTimeEnable()) {
                if (timerState == TimerState.ready) {
                    startInspection();
                }
            }
        }
    }

    private class ViewTextUpdater implements Runnable {

        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        long lastTime = Long.MAX_VALUE;

        @Override
        public void run() {
            String text = "";
            boolean recall = true;

            if (timerState == TimerState.solving) {
                text = solvingTimer.currentTimeToMsString();
            } else if (timerState == TimerState.inspection) {

                long currentTime = inspectionTimer.getCurrentTime();

                if (currentTime < -2000) {
                    text += "DNF";
                    recall = false;
                } else if (currentTime < 0) {
                    text += "+2";
                } else {
                    text = inspectionTimer.currentToTenthOfSecondsString();
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

    private class DownValidMaker implements Runnable {
        @Override
        public void run() {
            isDownValid = true;
            listener.onColorChanged(R.color.valid);
        }
    }
}
