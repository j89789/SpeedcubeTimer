package com.speedcubeapp.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class TimeListAdapter extends BaseAdapter {

    List<Time> sourceList;
    TimeSession session;

    boolean isUseMilliseconds = false;

    public void setIsUseMilliseconds(boolean useMilliseconds) {
        isUseMilliseconds = useMilliseconds;
    }

    public TimeListAdapter(TimeSession session) {
        this.session = session;
        this.sourceList = session.getTimes();
    }

    @Override
    public int getCount() {
        return sourceList.size();
    }

    @Override
    public Object getItem(int position) {
        return sourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) SpeedcubeApplication.instance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_time, parent, false);
        }

        TextView textViewPos = (TextView) convertView.findViewById(R.id.textView1);
        TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
        TextView textViewAo5 = (TextView) convertView.findViewById(R.id.textViewAo5);
        TextView textViewAo12 = (TextView) convertView.findViewById(R.id.textViewAo12);


        textViewPos.setText("" + (position + 1) + ".");

        Time time = sourceList.get(position);
        Time.Type type = time.getType();

        String timeString = Time.toString(time.getTimeMs(), isUseMilliseconds ? 3 : 2);

        if (type == Time.Type.plus2) {
            timeString += "+";
        } else if (type == Time.Type.DNF) {
            timeString = SpeedcubeApplication.instance().getString(R.string.time_type_DNF);
        }

        textViewTime.setText(timeString);

        if (time.getAverageOf5() > 0) {
            textViewAo5.setText(Time.toString(time.getAverageOf5(), isUseMilliseconds ? 3 : 2));
            convertView.findViewById(R.id.rowAo5).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.rowAo5).setVisibility(View.GONE);
        }

        if (time.getAverageOf12() > 0) {
            textViewAo12.setText(Time.toString(time.getAverageOf12(), isUseMilliseconds ? 3 : 2));
            convertView.findViewById(R.id.rowAo12).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.rowAo12).setVisibility(View.GONE);
        }

        if (time == session.getBestTime()) {
            textViewPos.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.green));
        } else if (time == session.getWorseTime()) {
            textViewPos.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.red));
        } else {
            textViewPos.setTextColor(SpeedcubeApplication.instance().defaultTextColor);
        }

        if (time == session.getBestAverageOf5Time()) {
            textViewAo5.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.green));
        } else if (time == session.getWorseAverageOf5Time()) {
            textViewAo5.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.red));
        } else {
            textViewAo5.setTextColor(SpeedcubeApplication.instance().defaultTextColor);
        }

        if (time == session.getBestAverageOf12Time()) {
            textViewAo12.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.green));
        } else if (time == session.getWorseAverageOf12Time()) {
            textViewAo12.setTextColor(SpeedcubeApplication.instance().getResources().getColor(R.color.red));
        } else {
            textViewAo12.setTextColor(SpeedcubeApplication.instance().defaultTextColor);
        }

        return convertView;
    }
}
