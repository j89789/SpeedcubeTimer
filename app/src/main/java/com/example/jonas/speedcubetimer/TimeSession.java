package com.example.jonas.speedcubetimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Time list
 * <p/>
 * Provides best, worst and calcAverage Times.
 */
public class TimeSession {

    Time bestAverageOf5Time;
    Time worseAverageOf5Time;
    Time bestAverageOf12Time;
    Time worseAverageOf12Time;
    Time bestTime;
    Time worseTime;
    /**
     * List of solving Times
     */
    private List<Time> times = new ArrayList<>();
    private List<Time> validTimes = new ArrayList<>();
    private List<Time> plus2Times = new ArrayList<>();
    private List<Time> dnfTimes = new ArrayList<>();
    /**
     * Adapter vor ListView
     */
    private TimeListAdapter adapter = new TimeListAdapter(this);

    /**
     * Reviver for change events
     */
    private OnChangListener onChangListener = null;

    /**
     * Average of the last 5 Times. 0 if there a DNF time.
     */
    private long average5;

    /**
     * Average of the last 12 Times. 0 if there a DNF time.
     */
    private long average12;

    /**
     * Average of all Times. Excluding DNF time.
     */
    private long averageAll;


    /**
     * Listen to all time changes in the session
     */
    private Time.OnChangeListener onTimeChangeLister = new OnTimeChangeListener();

    public TimeSession() {

        Random random = new Random();

        // For test insert 10 elements
        for (int i = 0; i < 20; i++) {
            Time time = new Time();
            time.setTimeMs(random.nextInt(30000));

            addNewTime(time);
        }
    }

    public Time getBestAverageOf12Time() {
        return bestAverageOf12Time;
    }

    public Time getBestAverageOf5Time() {
        return bestAverageOf5Time;
    }

    public Time getWorseAverageOf12Time() {
        return worseAverageOf12Time;
    }

    public Time getWorseAverageOf5Time() {
        return worseAverageOf5Time;
    }

    public Time getBestTime() {
        return bestTime;
    }

    public Time getWorseTime() {
        return worseTime;
    }

    public void setOnChangListener(OnChangListener listener) {
        onChangListener = listener;
    }

    public long getAverage12() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf5() : 0;
    }

    public long getAverage5() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf12() : 0;
    }


    public TimeListAdapter getAdapter() {
        return adapter;
    }

    public void addNewTime(Time time) {

        times.add(time);

        time.setAverageOf5(calcAverage(times.size() - 1, 5));
        time.setAverageOf12(calcAverage(times.size() - 1, 12));

        if (time.getType() == Time.Type.valid) {
            validTimes.add(time);
        } else if (time.getType() == Time.Type.plus2) {
            plus2Times.add(time);
        } else if (time.getType() == Time.Type.DNF) {
            dnfTimes.add(time);
        }

        updateExtremeValues(time);
        updateAverageOf5(time);
        updateAverageOf12(time);

        time.setOnChangeLister(onTimeChangeLister);

        if (onChangListener != null) {
            onChangListener.onSizeChanged();
        }
    }

    private void updateExtremeValues(Time time) {
        long timeMs = time.getTimeMs();

        boolean isExtremeValueChanged = false;

        if (bestTime == null || timeMs < bestTime.getTimeMs()) {
            bestTime = time;
            isExtremeValueChanged = true;
        }

        if (worseTime == null || timeMs > worseTime.getTimeMs()) {
            worseTime = time;
            isExtremeValueChanged = true;
        }


        if (onChangListener != null) {
            if (isExtremeValueChanged) {
                onChangListener.onExtremeValuesChange();
            }
        }
    }

    private void updateAverageOf5(Time time) {
        long averageOf5 = time.getAverageOf5();

        if(averageOf5 != 0) {
            boolean isAverageChanged = false;

            if (bestAverageOf5Time == null || averageOf5 < bestAverageOf5Time.getAverageOf5()) {
                bestAverageOf5Time = time;
                isAverageChanged = true;
            }

            if (worseAverageOf5Time == null || averageOf5 > worseAverageOf5Time.getAverageOf5()) {
                worseAverageOf5Time = time;
                isAverageChanged = true;
            }

            if (onChangListener != null) {
                if (isAverageChanged) {
                    onChangListener.onAverageChanged();
                }
            }
        }
    }

    private void updateAverageOf12(Time time) {
        long averageOf12 = time.getAverageOf12();

        if(averageOf12 != 0) {
            boolean isAverageChanged = false;

            if (bestAverageOf12Time == null || averageOf12 < bestAverageOf12Time.getAverageOf12()) {
                bestAverageOf12Time = time;
                isAverageChanged = true;
            }

            if (worseAverageOf12Time == null || averageOf12 > worseAverageOf12Time.getAverageOf12()) {
                worseAverageOf12Time = time;
                isAverageChanged = true;
            }

            if (onChangListener != null) {
                if (isAverageChanged) {
                    onChangListener.onAverageChanged();
                }
            }
        }
    }


    private void updateExtremeValues() {

        Collections.sort(validTimes, new Comparator<Time>() {
            @Override
            public int compare(Time lhs, Time rhs) {
                if (lhs.getTimeMs() < rhs.getTimeMs()) {
                    return -1;
                } else if (rhs.getTimeMs() > lhs.getTimeMs()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        bestTime = validTimes.get(0);
        worseTime = validTimes.get(validTimes.size() - 1);

        if (onChangListener != null) {
            onChangListener.onExtremeValuesChange();
        }
    }

    private void updateAverageOf5() {

        Collections.sort(validTimes, new Comparator<Time>() {
            @Override
            public int compare(Time lhs, Time rhs) {
                if (lhs.getAverageOf5() < rhs.getAverageOf5()) {
                    return -1;
                } else if (rhs.getAverageOf5() > lhs.getAverageOf5()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < validTimes.size(); i++) {
            long averageOf5 = validTimes.get(i).getAverageOf5();

            if(averageOf5 != 0) {
                bestAverageOf5Time = validTimes.get(i);
                break;
            }
        }

        worseAverageOf5Time = validTimes.get(validTimes.size() - 1);

        if (onChangListener != null) {
            onChangListener.onAverageChanged();
        }
    }

    private void updateAverageOf12() {

        Collections.sort(validTimes, new Comparator<Time>() {
            @Override
            public int compare(Time lhs, Time rhs) {
                if (lhs.getAverageOf12() < rhs.getAverageOf12()) {
                    return -1;
                } else if (rhs.getAverageOf12() > lhs.getAverageOf12()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < validTimes.size(); i++) {
            long averageOf12 = validTimes.get(i).getAverageOf12();

            if(averageOf12 != 0) {
                bestAverageOf12Time = validTimes.get(i);
                break;
            }
        }

        worseAverageOf12Time = validTimes.get(validTimes.size() - 1);

        if (onChangListener != null) {
            onChangListener.onAverageChanged();
        }
    }


    public void removeTime(Time time) {
        int index = times.indexOf(time);

        if (index != -1) {
            times.remove(index);
            validTimes.remove(time);
            dnfTimes.remove(time);
            plus2Times.remove(time);

            time.setOnChangeLister(null);
            adapter.notifyDataSetChanged();

            updateAvarageAt(index);

            if (time == bestTime || time == worseTime) {
                updateExtremeValues();
            } else if (time == bestAverageOf5Time || time == worseAverageOf5Time) {
                updateAverageOf5();
            } else if (time == bestAverageOf12Time || time == worseAverageOf12Time) {
                updateAverageOf12();
            }

            if (onChangListener != null) {
                onChangListener.onSizeChanged();
            }
        }
    }


    public Time get(int position) {
        return times.get(position);
    }

    public int size() {
        return times.size();
    }

    public void clear() {
        times.clear();
        validTimes.clear();
        dnfTimes.clear();
        plus2Times.clear();
        worseTime = null;
        bestTime = null;
        averageAll = 0;

        adapter.notifyDataSetChanged();


        if (onChangListener != null) {
            onChangListener.onAverageChanged();
            onChangListener.onExtremeValuesChange();
            onChangListener.onSizeChanged();
        }
    }

    public int getSize() {
        return times.size();
    }

    public long getMean() {
        return averageAll;
    }

    public List<Time> getTimes() {
        return times;
    }

    private void updateAvarageAt(int i) {
        for (int count = 0; i < times.size(); i++, count++) {
            Time outdatedTime = times.get(i);

            if (count < 5) {
                outdatedTime.setAverageOf5(calcAverage(i, 5));
            }

            outdatedTime.setAverageOf12(calcAverage(i, 12));
        }
    }

    private long calcAverage(int endIndex, int count) {
        long timeMs = 0;
        int beginIndex = endIndex - count + 1;
        if (endIndex < times.size() && beginIndex >= 0) {
            for (int i = beginIndex; i <= endIndex; i++) {
                timeMs += times.get(i).getTimeMs();
            }
            timeMs /= count;
        }
        return timeMs;
    }

    interface OnChangListener {

        void onAverageChanged();

        void onExtremeValuesChange();

        void onSizeChanged();
    }

    private class OnTimeChangeListener implements Time.OnChangeListener {
        @Override
        public void onChanged(Time time) {

            int i = times.indexOf(time);

            updateAvarageAt(i);

            updateExtremeValues();
            updateAverageOf5();
            updateAverageOf12();

            adapter.notifyDataSetChanged();
        }
    }
}
