package com.example.jonas.speedcubetimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class TimeListAdapter extends BaseAdapter {

    List<Time> sourceList;

    public TimeListAdapter(List<Time> sourceList) {
        this.sourceList = sourceList;
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
        TextView textViewInfo = (TextView) convertView.findViewById(R.id.textViewInfo);

        textViewPos.setText("" + (position + 1) + ".");

        Time time = sourceList.get(position);
        Time.Type type = time.getType();

        String timeString = Time.toStringMs(time.getTimeMs());
        String infoString = "";

        if (type == Time.Type.plus2) {
            timeString += "+";
            infoString = "( " + Time.toStringMs(time.getOriginTimeMs()) + " )";
        } else if (type == Time.Type.DNF) {
            timeString = SpeedcubeApplication.instance().getString(R.string.DNF);
            infoString = "( " + Time.toStringMs(time.getOriginTimeMs()) + " )";
        }

        textViewTime.setText(timeString);
        textViewInfo.setText(infoString);

        return convertView;
    }
}
