package com.speedcubeapp.timer;

import java.util.ArrayList;
import java.util.List;

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

    public List<Time> getValidTimes() {
        return validTimes;
    }

    public List<Time> getDnfTimes() {
        return dnfTimes;
    }

    public List<Time> getPlus2Times() {
        return plus2Times;
    }

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

        /*
        Random random = new Random();

        // For test insert 10 elements
        for (int i = 0; i < 20; i++) {
            Time time = new Time();
            time.setTimeMs(random.nextInt(30000));
            time.setTimeMs(17000 + (int)((double) 15000 * (double) random.nextInt(1000) / 1000));
            addNewTime(time);
        }
        */

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

    /**
     * @return 0 if no average exits
     */
    public long getAverage12() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf12() : 0;
    }

    /**
     * @return 0 if no average exits
     */
    public long getAverage5() {
        return times.size() > 0 ? times.get(times.size() - 1).getAverageOf5() : 0;
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

        updateExtremeValues();

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

        if (averageOf5 != 0) {
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

        if (averageOf12 != 0) {
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

        averageAll = 0;

        if (times.size() > 0) {
            Time time = times.get(0);
            bestTime = time;
            worseTime = time;
        } else{
            bestTime = null;
            worseTime = null;
        }

        bestAverageOf5Time = null;
        worseAverageOf5Time = null;
        bestAverageOf12Time = null;
        worseAverageOf12Time = null;

        for (int i = 0; i < times.size(); i++) {
            Time time = times.get(i);

            if(time.getType() == Time.Type.DNF){
                continue;
            }

            averageAll += time.getTimeMs();

            long ms = time.getTimeMs();

            if (ms < bestTime.getTimeMs()) {
                bestTime = time;
            }

            if (ms > worseTime.getTimeMs()) {
                worseTime = time;
            }

            if (time.getAverageOf5() != 0) {
                if (bestAverageOf5Time == null ||ms < bestAverageOf5Time.getTimeMs()) {
                    bestAverageOf5Time = time;
                }

                if (worseAverageOf5Time == null || ms > worseAverageOf5Time.getTimeMs()) {
                    worseAverageOf5Time = time;
                }
            }

            if (time.getAverageOf12() != 0) {
                if (bestAverageOf12Time == null || ms < bestAverageOf12Time.getTimeMs()) {
                    bestAverageOf12Time = time;
                }

                if (worseAverageOf12Time == null || ms > worseAverageOf12Time.getTimeMs()) {
                    worseAverageOf12Time = time;
                }
            }
        }

        if(validTimes.size() + plus2Times.size() > 0) {
            averageAll /= validTimes.size() + plus2Times.size();
        }


        if (onChangListener != null) {
            onChangListener.onExtremeValuesChange();
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

            updateAverageTimesAt(index);

            updateExtremeValues();

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
        bestAverageOf12Time = null;
        bestAverageOf5Time = null;
        worseAverageOf12Time = null;
        worseAverageOf5Time = null;

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

    public long getAverageAll() {
        return averageAll;
    }

    public List<Time> getTimes() {
        return times;
    }

    private void updateAverageTimesAt(int i) {
        for (int count = 0; i < times.size(); i++, count++) {
            Time outdatedTime = times.get(i);

            if (count < 5) {
                outdatedTime.setAverageOf5(calcAverage(i, 5));
            }

            outdatedTime.setAverageOf12(calcAverage(i, 12));
        }
    }

    /**
     * Calc average time from Time at Index for the given cont.
     *
     * @return 0 if not enough times are exists or a DNF time exits
     */
    private long calcAverage(int endIndex, int count) {
        long timeMs = 0;
        int beginIndex = endIndex - count + 1;
        if (endIndex < times.size() && beginIndex >= 0) {
            for (int i = beginIndex; i <= endIndex; i++) {
                if(times.get(i).getType() == Time.Type.DNF){
                    return 0;
                }
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

            validTimes.remove(time);
            plus2Times.remove(time);
            dnfTimes.remove(time);

            if (time.getType() == Time.Type.valid) {
                validTimes.add(time);
            } else if (time.getType() == Time.Type.plus2) {
                plus2Times.add(time);
            } else if (time.getType() == Time.Type.DNF) {
                dnfTimes.add(time);
            }

            int i = times.indexOf(time);

            updateAverageTimesAt(i);
            updateExtremeValues();

            adapter.notifyDataSetChanged();
        }
    }
}
