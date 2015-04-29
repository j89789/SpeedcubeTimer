package com.jonas.speedcube;

import android.view.MotionEvent;
import android.view.View;

/**
 * Represented the touch field as in the StackMat.
 * <p/>
 * The source view of touch events must be set with setView().
 * <p/>
 * The Listener hat the following events:
 * 1. onSensorDown() Both hand are on the pad
 * 2. onSensorUp() One hand leave the pad after both hands were on the pad
 * 3. onTrigger() One hand leave the pad but no both hands wer on the pad
 */
class TouchSensor {

    private boolean isDown = false;
    private View view = null;
    private final MyOnTouchListener touchListener = new MyOnTouchListener();
    private Listener listener = null;
    private String TAG = TouchSensor.class.getSimpleName();

    /**
     * True if the Listener.onSensorUp() was call for avoid a Listener.onSensorDown() call with the
     * same touch points.
     */
    private boolean isTouchInvalid = false;


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setView(View view) {

        if (this.view != null) {
            this.view.setOnTouchListener(null);
        }

        this.view = view;
        this.view.setOnTouchListener(this.touchListener);
    }

    class MyOnTouchListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (!isTouchInvalid) {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                    if (!isDown) {
                        isDown = true;
                        if (listener != null) {
                            listener.onSensorDown();
                        }
                    }
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                    if (event.getPointerCount() <= 2) {
                        if (isDown) {
                            isDown = false;
                            if (listener != null) {
                                listener.onSensorUp();
                            }
                            isTouchInvalid = true;
                        }
                    }
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (isTouchInvalid) {
                    isTouchInvalid = false;
                } else {
                    if (listener != null) {
                        listener.onTrigger();
                    }
                }
            }

            return true;
        }
    }

    public interface Listener {


        /**
         * Called when one hands leave the touch pad after both hands has touched the pad.
         */
        void onSensorUp();

        /**
         * Called when both hands are on the touch pad
         */
        void onSensorDown();

        /**
         * Called when one hand leave the touch pad when no both hands has touched the pad.
         */
        void onTrigger();
    }
}
