package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.List;


public class TimeSession {

    private List<Time> timeList = new ArrayList<Time>();
    private TimeListAdapter adapter = new TimeListAdapter(timeList);

    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {
        timeList.add(time);
    }

    public void removeTime(Time time) {
        timeList.remove(time);
    }

    public Time get(int position) {
        return timeList.get(position);
    }

    public int size() {
        return timeList.size();
    }

    public void clear() {
        timeList.clear();
    }
}
