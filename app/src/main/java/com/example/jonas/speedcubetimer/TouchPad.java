package com.example.jonas.speedcubetimer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Represented the touch field as in the StackMat.
 *
 * The source view of touch events must be set with setView().
 *
 * The Listener hat the following events:
 * 1. onDown() Both hand are on the pad
 * 2. onUp() One hand leave the pad after both hands were on the pad
 * 3. onTrigger() One hand leave the pad but no both hands wer on the pad
 */
public class TouchPad {

    boolean isDown = false;
    View view = null;
    final MyOnTouchListener touchListener = new MyOnTouchListener();
    Listener listener = null;

    /**
     * True if the Listener.onUp() was call for avoid a Listener.onDown() call with the
     * same touch points.
     */
    boolean isTouchInvalid = false;


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
                            listener.onDown();
                        }
                    }
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                    if (event.getPointerCount() <= 2) {
                        if (isDown) {
                            isDown = false;
                            if (listener != null) {
                                listener.onUp();
                            }
                            isTouchInvalid = true;
                        }
                    }
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (isTouchInvalid) {
                    isTouchInvalid = false;
                }
                else
                {
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
        void onUp();

        /**
         * Called when both hands are on the touch pad
         */
        void onDown();

        /**
         * Called when one hand leave the touch pad when no both hands has touched the pad.
         */
        void onTrigger();
    }
}
