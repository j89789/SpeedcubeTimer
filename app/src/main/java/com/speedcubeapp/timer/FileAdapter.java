package com.speedcubeapp.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;


public class FileAdapter extends BaseAdapter {

    private Context mContext;
    private ButtonDeleteListener buttonDeleteListener = new ButtonDeleteListener();

    public FileAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mContext.getFilesDir().listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return mContext.getFilesDir().listFiles()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        File[] files = mContext.getFilesDir().listFiles();

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) SpeedcubeApplication.instance()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_file, parent, false);

            ImageButton buttonDelete = (ImageButton) convertView.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(buttonDeleteListener);
            buttonDelete.setFocusable(false);
        }

        ImageButton buttonDelete = (ImageButton) convertView.findViewById(R.id.buttonDelete);
        buttonDelete.setTag(position);

        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        textViewName.setText(files[position].getName());

        return convertView;
    }

    class ButtonDeleteListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final int position = (Integer) v.getTag();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Delete file?");
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mContext.getFilesDir().listFiles()[position].delete();
                    notifyDataSetChanged();
                }
            });
            builder.show();


        }
    }
}
