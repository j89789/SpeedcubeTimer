package com.speedcubeapp.timer;


import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class Time{

    private long timeMs;
    private long averageOf5;
    private long averageOf12;
    private long timestamp;
    private Type type = Type.valid;

    private OnChangeListener lister;

    public String toStringMs() {
        return Time.toString(timeMs, 3);
    }

    static public String toStringMs(long timeMs) {
        return Time.toString(timeMs, 3);
    }

    static public String toString(long timeMs, boolean isMilliseconds) {
        return Time.toString(timeMs, isMilliseconds ? 3 : 2);
    }

    /**
     * Convert a millisecond time in to a String e.g 3:02.642.
     *
     * @param timeMs   Time in milliseconds
     * @param decimals Count of seconds decimals
     */
    static public String toString(long timeMs, int decimals) {
        long milliseconds = Math.abs(timeMs);
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        milliseconds %= 1000;
        seconds %= 60;
        minutes %= 60;

        String millisecondsString = "";
        String secondsString = "";
        String minutesString = "";

        if (decimals == 2) {
            millisecondsString = String.format("%02d", (milliseconds / 10) % 100);
        } else if (decimals == 1) {
            millisecondsString = "" + ((milliseconds / 100) % 10);
        } else if (decimals == 0) {
        } else {
            millisecondsString = String.format("%03d", milliseconds);
        }


        if (minutes != 0) {
            secondsString = String.format("%02d", seconds);
            minutesString = "" + minutes;
        } else {
            secondsString = "" + seconds;
        }

        String s = timeMs < 0 ? "- " : "";

        if (!minutesString.isEmpty()) {
            s += minutesString + ":";
        }

        s += secondsString;

        if (!millisecondsString.isEmpty()) {
            s += "." + millisecondsString;
        }

        return s;
    }

    public void setOnChangeLister(OnChangeListener lister) {
        this.lister = lister;
    }

    /**
     * May including +2000 ms
     */
    public long getTimeMs() {

        return timeMs + (type == Type.plus2 ? 2000 : 0);
    }

    public void setTimeMs(long timeMs) {
        this.timeMs = timeMs;
        if (lister != null) {
            lister.onChanged(this);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        if (lister != null) {
            lister.onChanged(this);
        }
    }

    public void showPopupMenu(Context context, View view, final PopupMenuLister popupMenuLister) {

        PopupMenu popup = new PopupMenu(context, view);

        popup.inflate(R.menu.menu_time_type);

        if (type == Time.Type.valid) {
            popup.getMenu().findItem(R.id.ok).setChecked(true);
        } else if (type == Time.Type.plus2) {
            popup.getMenu().findItem(R.id.plus2).setChecked(true);
        } else if (type == Time.Type.DNF) {
            popup.getMenu().findItem(R.id.DNF).setChecked(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ok) {
                    setType(Time.Type.valid);
                } else if (id == R.id.plus2) {
                    setType(Time.Type.plus2);
                } else if (id == R.id.DNF) {
                    setType(Time.Type.DNF);
                } else if (id == R.id.delete) {
                    SpeedcubeApplication.instance().getTimeSession().removeTime(Time.this);
                }

                if (popupMenuLister != null) {
                    popupMenuLister.onAction(id);
                }

                return true;
            }
        });

        popup.show();
    }

    public long getOriginTimeMs() {
        return timeMs;
    }

    public long getAverageOf5() {
        return averageOf5;
    }

    public void setAverageOf5(long averageOf5) {
        this.averageOf5 = averageOf5;
    }

    public long getAverageOf12() {
        return averageOf12;
    }

    public void setAverageOf12(long averageOf12) {
        this.averageOf12 = averageOf12;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestampToNow() {
        this.timestamp = System.currentTimeMillis();
    }

    enum Type {valid, plus2, DNF}

    interface OnChangeListener {

        /**
         * Called if any data changed
         */
        void onChanged(Time time);
    }

    interface PopupMenuLister {

        /**
         * Called by action form Popup menu
         *
         * @param id Action id
         */
        void onAction(int id);
    }
}
