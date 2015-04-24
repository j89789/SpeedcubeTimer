package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private List<Time> times = new ArrayList<>();
    private List<Time> validTimes = new ArrayList<>();
    private List<Time> plus2Times = new ArrayList<>();
    private List<Time> dnfTimes = new ArrayList<>();
    Time bestTime;
    Time worseTime;

    /**
     * Adapter vor ListView
     */
    private TimeListAdapter adapter = new TimeListAdapter(this);

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
     * Average of all Times. Excluding DNF time.
     */
    private long averageAll;


    /**
     * Listen to all time changes in the session
     */
    private Time.OnChangeListener onTimeChangeLister = new OnTimeChangeListener();

    public TimeSession() {

        // For test insert 10 elements
        for (int i = 0; i < 10; i++) {
            Time time = new Time();
            time.setTimeMs(245 * i);

            addNewTime(time);
        }
    }

    public Time getBestTime() {
        return bestTime;
    }

    public Time getWorseTime() {
        return worseTime;
    }

    public void setOnChangListener(OnChangListener listener) {
        onChangListener = listener;
    }

    public long getAverage12() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf5() : 0;
    }

    public long getAverage5() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf12() : 0;
    }


    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {
        times.add(time);

        if(time.getType() == Time.Type.valid){
            validTimes.add(time);
        }else if(time.getType() == Time.Type.plus2){
            plus2Times.add(time);
        }else if(time.getType() == Time.Type.DNF){
            dnfTimes.add(time);
        }

        time.setOnChangeLister(onTimeChangeLister);

        boolean extremeValuesChanged = false;

        long timeMs = time.getTimeMs();

        if (bestTime == null || timeMs < bestTime.getTimeMs()) {
            bestTime = time;
            extremeValuesChanged = true;
        }

        if (worseTime == null || timeMs > worseTime.getTimeMs()) {
            worseTime = time;
            extremeValuesChanged = true;
        }

        time.setAverageOf5(average(times.size() - 1, 5));
        time.setAverageOf12(average(times.size() - 1, 12));

        if (onChangListener != null) {
            onChangListener.onSizeChanged();
            if (extremeValuesChanged) {
                onChangListener.onExtremeValuesChange();
            }
        }
    }


    private void updateExtremeValues() {

        Collections.sort(validTimes, new Comparator<Time>() {
            @Override
            public int compare(Time lhs, Time rhs) {
                if (lhs.getTimeMs() < rhs.getTimeMs()) {
                    return -1;
                } else if (rhs.getTimeMs() > lhs.getTimeMs()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        bestTime = validTimes.get(0);
        worseTime = validTimes.get(validTimes.size() - 1);

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
        int index = times.indexOf(time);

        if (index != -1) {
            times.remove(index);
            validTimes.remove(time);
            dnfTimes.remove(time);
            plus2Times.remove(time);

            time.setOnChangeLister(null);
            adapter.notifyDataSetChanged();

            updateAvarageAt(index);

            if(time == bestTime || time == worseTime){
                updateExtremeValues();
            }

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
        validTimes.clear();
        dnfTimes.clear();
        plus2Times.clear();
        worseTime = null;
        bestTime = null;
        averageAll = 0;

        adapter.notifyDataSetChanged();


        if (onChangListener != null) {
            onChangListener.onAverageChanged();
            onChangListener.onExtremeValuesChange();
            onChangListener.onSizeChanged();
        }
    }

    public int getSize() {
        return times.size();
    }

    public long getMean() {
        return averageAll;
    }

    public List<Time> getTimes() {
        return times;
    }

    interface OnChangListener {

        void onAverageChanged();

        void onExtremeValuesChange();

        void onSizeChanged();
    }

    private class OnTimeChangeListener implements Time.OnChangeListener {
        @Override
        public void onChanged(Time time) {

            int i = times.indexOf(time);

            updateAvarageAt(i);

            boolean extremeValuesChanged = false;

            if(time == bestTime || time == worseTime){
                updateExtremeValues();
            }else {
                if (time.getTimeMs() < bestTime.getTimeMs()) {
                    bestTime = time;
                    extremeValuesChanged = true;
                }

                if (time.getTimeMs() > worseTime.getTimeMs()) {
                    worseTime = time;
                    extremeValuesChanged = true;
                }

                if (onChangListener != null && extremeValuesChanged) {
                    onChangListener.onExtremeValuesChange();
                }
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void updateAvarageAt(int i) {
        for (int count = 0; i < times.size(); i++, count++) {
            Time outdatedTime = times.get(i);

            if(count < 5) {
                outdatedTime.setAverageOf5(average(i, 5));
            }

            outdatedTime.setAverageOf12(average(i, 12));
        }
    }

    private long average(int endIndex, int count){
        long timeMs = 0;
        int beginIndex = endIndex - count + 1;
        if (endIndex < times.size() && beginIndex >= 0){
            for (int i = beginIndex; i <= endIndex ; i++) {
                timeMs += times.get(i).getTimeMs();
            }
            timeMs /= count;
        }
        return timeMs;
    }
}
