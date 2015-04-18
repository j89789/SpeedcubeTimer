package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.List;


public class TimeSession {

    private List<Time> timeList = new ArrayList<Time>();
    private TimeListAdapter adapter = new TimeListAdapter(timeList);
    private OnChangListener onChangListener = null;
    private long average5;
    private long average12;
    private long bestTime = Long.MAX_VALUE;
    private long worseTime;
    private Time.OnChangeLister onTimeChangeLister = new OnChangeLister();

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
        timeList.add(time);
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

        for (int i = 0; i < timeList.size(); i++) {
            Time time = timeList.get(i);
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

        if (timeList.size() >= count) {
            for (int i = timeList.size() - (count); i < timeList.size(); i++) {
                Time time = timeList.get(i);

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
        if (timeList.remove(time)) {
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
        return timeList.get(position);
    }

    public int size() {
        return timeList.size();
    }

    public void clear() {
        timeList.clear();
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
        return timeList.size();
    }

    interface OnChangListener {

        void onAverageChanged();

        void onExtremeValuesChange();

        void onSizeChanged();
    }

    private class OnChangeLister implements Time.OnChangeLister {
        @Override
        public void onChanged(Time time) {
            adapter.notifyDataSetChanged();

            updateAverage();
            updateExtremeValues();
        }
    }
}
