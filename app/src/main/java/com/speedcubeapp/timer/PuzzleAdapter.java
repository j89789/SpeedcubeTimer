package com.speedcubeapp.timer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PuzzleAdapter extends BaseAdapter{

    Puzzle[] puzzles;

    public PuzzleAdapter(Puzzle[] puzzles) {
        this.puzzles = puzzles;
    }

    @Override
    public int getCount() {
        return puzzles.length;
    }

    @Override
    public Object getItem(int position) {
        return puzzles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) SpeedcubeApplication.instance()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_puzzle, parent, false);
        }

        Puzzle puzzle = puzzles[position];

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewIcon);
        imageView.setImageResource(puzzle.getImageResourceId());


        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        textViewName.setText(puzzle.getNameResourceId());


        return convertView;
    }

}
