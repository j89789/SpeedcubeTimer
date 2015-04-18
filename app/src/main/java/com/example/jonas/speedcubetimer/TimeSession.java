package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.List;


public class TimeSession {

    private List<Time> timeList = new ArrayList<Time>();
    private TimeListAdapter adapter = new TimeListAdapter(timeList);
    private OnAverageChangedListener onAverageChangedListener = null;

    public void setOnChangeLister(OnAverageChangedListener listener) {
        onAverageChangedListener = listener;

        if (onAverageChangedListener != null) {
            updateAverage();
            onAverageChangedListener.onAverageChanged();
        }
    }

    private long average5;
    private long average12;

    public long getAverage12() {
        return average12;
    }

    public long getAverage5() {
        return average5;
    }

    private Time.OnChangeLister onChangeLister = new Time.OnChangeLister() {
        @Override
        public void onChanged(Time time) {
            adapter.notifyDataSetChanged();

            if(onAverageChangedListener != null) {
                updateAverage();
            }
        }
    };

    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {
        timeList.add(time);
        time.setOnChangeLister(onChangeLister);

        updateAverage();
    }

    private void updateAverage() {
        average5 = calcAverage(5);
        average12 = calcAverage(12);

        if (onAverageChangedListener != null) {
            onAverageChangedListener.onAverageChanged();
        }
    }

    private long calcAverage(int count) {

        long average = 0;

        if(timeList.size() >= count)
        {
            for (int i = timeList.size() - (count) ; i < timeList.size() ; i++) {
                Time time = timeList.get(i);

                if (time.getType() == Time.Type.DNF){
                    return 0;
                }

                average += time.getTimeMs();
            }
            average /= count;
        }

        return average;
    }

    public void removeTime(Time time) {
        timeList.remove(time);
        time.setOnChangeLister(null);
        adapter.notifyDataSetChanged();
        updateAverage();
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
        updateAverage();
    }

    interface OnAverageChangedListener {
        void onAverageChanged();
    }
}
