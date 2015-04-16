package com.example.jonas.speedcubetimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class TimeListAdapter extends BaseAdapter {

    List<SolvingTime> sourceList;

    public TimeListAdapter(List<SolvingTime> sourceList) {
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
        TextView textViewTime = (TextView) convertView.findViewById(R.id.textView2);
        TextView textViewType = (TextView) convertView.findViewById(R.id.textView3);

        SolvingTime solvingTime = sourceList.get(position);
        textViewPos.setText("" + position + ".");
        textViewTime.setText(Time.toStringMs(solvingTime.getTimeMs()));

        SolvingTime.Type type = solvingTime.getType();

        if (type == SolvingTime.Type.plus2) {
            textViewType.setText("+2");
        }
        else if (type == SolvingTime.Type.DNF) {
            textViewType.setText("DNF");
        }
        else{
            textViewType.setText("");
        }

        return convertView;
    }
}
