package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.List;


public class TimeSession {

    private List<Time> timeList = new ArrayList<Time>();
    private TimeListAdapter adapter = new TimeListAdapter(timeList);

    private Time.OnChangeLister onChangeLister = new Time.OnChangeLister() {
        @Override
        public void onChanged(Time time) {
            adapter.notifyDataSetChanged();
        }
    };

    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {
        timeList.add(time);
        time.setOnChangeLister(onChangeLister);
    }

    public void removeTime(Time time) {
        timeList.remove(time);
        time.setOnChangeLister(null);
        adapter.notifyDataSetChanged();
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
    }
}
