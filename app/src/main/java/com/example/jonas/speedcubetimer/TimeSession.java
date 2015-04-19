package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Time list
 * <p/>
 * Provides best, worst and average Times.
 */
public class TimeSession {

    /**
     * List of solving Times
     */
    private List<Time> times = new ArrayList<Time>();

    /**
     * Adapter vor ListView
     */
    private TimeListAdapter adapter = new TimeListAdapter(times);

    /**
     * Reviver for change events
     */
    private OnChangListener onChangListener = null;

    /**
     * Average of the last 5 Times. 0 if there a DNF time.
     */
    private long average5;

    /**
     * Average of the last 12 Times. 0 if there a DNF time.
     */
    private long average12;

    /**
     * Best time in the session
     */
    private long bestTime = Long.MAX_VALUE;

    /**
     * Worst time in the session
     */
    private long worseTime;

    /**
     * Listen to all time changes in the session
     */
    private Time.OnChangeListener onTimeChangeLister = new OnTimeChangeListener();

    public long getBestTime() {
        return bestTime;
    }

    public long getWorseTime() {
        return worseTime;
    }

    public void setOnChangListener(OnChangListener listener) {
        onChangListener = listener;

        if (onChangListener != null) {
            onChangListener.onAverageChanged();
            onChangListener.onExtremeValuesChange();
            onChangListener.onSizeChanged();
        }
    }

    public long getAverage12() {
        return average12;
    }

    public long getAverage5() {
        return average5;
    }

    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {
        times.add(time);
        time.setOnChangeLister(onTimeChangeLister);

        boolean extremeValuesChanged = false;

        long timeMs = time.getTimeMs();

        if (timeMs < bestTime) {
            bestTime = timeMs;
            extremeValuesChanged = true;
        }

        if (timeMs > worseTime) {
            worseTime = timeMs;
            extremeValuesChanged = true;
        }


        updateAverage();

        if (onChangListener != null) {
            onChangListener.onSizeChanged();
            if (extremeValuesChanged) {
                onChangListener.onExtremeValuesChange();
            }
        }
    }

    private void updateAverage() {
        average5 = calcAverage(5);
        average12 = calcAverage(12);

        if (onChangListener != null) {
            onChangListener.onAverageChanged();
        }
    }

    private void updateExtremeValues() {
        bestTime = Long.MAX_VALUE;
        worseTime = 0;

        for (int i = 0; i < times.size(); i++) {
            Time time = times.get(i);
            long timeMs = time.getTimeMs();
            Time.Type type = time.getType();

            if (type != Time.Type.DNF) {
                if (timeMs < bestTime) {
                    bestTime = timeMs;
                } else if (timeMs > worseTime) {
                    worseTime = timeMs;
                }
            }
        }

        if (onChangListener != null) {
            onChangListener.onExtremeValuesChange();
        }
    }

    private long calcAverage(int count) {

        long average = 0;

        if (times.size() >= count) {
            for (int i = times.size() - (count); i < times.size(); i++) {
                Time time = times.get(i);

                if (time.getType() == Time.Type.DNF) {
                    return 0;
                }

                average += time.getTimeMs();
            }
            average /= count;
        }

        return average;
    }

    public void removeTime(Time time) {
        if (times.remove(time)) {
            time.setOnChangeLister(null);
            adapter.notifyDataSetChanged();

            updateAverage();
            updateExtremeValues();

            if (onChangListener != null) {
                onChangListener.onSizeChanged();
            }
        }
    }

    public Time get(int position) {
        return times.get(position);
    }

    public int size() {
        return times.size();
    }

    public void clear() {
        times.clear();
        adapter.notifyDataSetChanged();

        average5 = 0;
        average12 = 0;
        bestTime = Long.MAX_VALUE;
        worseTime = 0;

        if (onChangListener != null) {
            onChangListener.onAverageChanged();
            onChangListener.onExtremeValuesChange();
            onChangListener.onSizeChanged();
        }
    }

    public int getSize() {
        return times.size();
    }

    interface OnChangListener {

        void onAverageChanged();

        void onExtremeValuesChange();

        void onSizeChanged();
    }

    private class OnTimeChangeListener implements Time.OnChangeListener {
        @Override
        public void onChanged(Time time) {
            adapter.notifyDataSetChanged();

            updateAverage();
            updateExtremeValues();
        }
    }
}
