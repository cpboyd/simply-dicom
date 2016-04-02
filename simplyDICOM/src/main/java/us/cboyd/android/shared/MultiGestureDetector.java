/*
 * Copyright (C) 2013 - 2015. Christopher Boyd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package us.cboyd.android.shared;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import us.cboyd.shared.Geometry;

/**
 * Detects various gestures and events using the supplied {@link MotionEvent}s.
 * The {@link OnGestureListener} callback will notify users when a particular
 * motion event has occurred. This class should only be used with
 * {@link MotionEvent}s reported via touch (don't use for trackball events).
 * 
 * To use this class:
 * <ul>
 * <li>Create an instance of the {@code MultiGestureDetector} for your
 * {@link View}
 * <li>In the {@link View#onTouchEvent(MotionEvent)} method ensure you call
 * {@link #onTouchEvent(MotionEvent)}. The methods defined in your callback will
 * be executed when the events occur.
 * </ul>
 */
public class MultiGestureDetector extends GestureDetector {
	/** Scroll Mode Values **/
	public final static short NO_SCROLL = 0;
	public final static short MOVE = 1;
	public final static short SCALE = 2;
	public final static short MOVE2 = 3;

	/** Internal Variables **/
	private static short mScrollMode = NO_SCROLL;
	private static double mPrevAngle, mCurrAngle;

	/** X & Y difference between pointers **/
	private static float mInitialX, mInitialY, mCurrentX, mCurrentY;
	//private static float mPrevSpanX = 0;
	//private static float mPrevSpanY = 0;
	private static float mCurrSpanX, mCurrSpanY;
	private static double mPrevSpan, mCurrSpan;

    private static float mMarginTop, mMarginBottom;

    public void setTopMargin(float margin) {
        mMarginTop = margin;
    }

    public void setBottomMargin(float margin) {
        mMarginBottom = margin;
    }

    public void setHorizontalMargin(float margin) {
        setHorizontalMargin(margin, margin);
    }

    public void setHorizontalMargin(float marginTop, float marginBottom) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;
    }

	/** Determine if angle is small enough **/
	private static boolean smallAngle(double angle) {
		angle = Geometry.rad2deg(Math.abs(angle));
		// The sweet spot seems to be around 20 degrees:
		return ((0.0d <= angle && angle < 20.0d) || (160.0d < angle && angle <= 180.0));
	}

	/**
	 * Calculates the average X & Y positions, returns the {@link MotionEvent}'s
	 * pointer count.
	 **/
	private static int calcAvgPosition(MotionEvent e2) {
		int numPointers = e2.getPointerCount();
		// Store initial positions
		mCurrentX = 0;
		mCurrentY = 0;
		for (int i = 0; i < numPointers; i++) {
			mCurrentX += e2.getX(i);
			mCurrentY += e2.getY(i);
		}
		mCurrentX /= numPointers;
		mCurrentY /= numPointers;
		return numPointers;
	}

	/** Returns whether travel is along the X axis **/
	public boolean isTravelX() {
		return (Math.abs(mCurrentX - mInitialX) > Math.abs(mCurrentY
				- mInitialY));
	}

	/** Returns whether travel is along the Y axis **/
	public boolean isTravelY() {
		return !isTravelX();
	}

	/** Returns the current scroll mode **/
	public short getScrollMode() {
		return mScrollMode;
	}

	/** Reset the scroll mode to NO_SCROLL **/
	public void resetScrollMode() {
		mScrollMode = NO_SCROLL;
	}

	/**
	 * SimpleMultiGestureListener features an onScroll that splits
	 * movement-based gestures into "move" (onMove) and "scale" (onScale).
	 * 
	 * @author Christopher Boyd
	 * 
	 */
	public static class SimpleMultiGestureListener implements
			OnGestureListener, OnDoubleTapListener {

        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
    			float distanceX, float distanceY) {
        	int numPointers = calcAvgPosition(e2);
        	// Calculate span and angle
        	if (numPointers == 2) {
        		//mPrevSpanX = mCurrSpanX;
        		//mPrevSpanY = mCurrSpanY;
        		mCurrSpanX = e2.getX(1) - e2.getX(0);
        		mCurrSpanY = e2.getY(1) - e2.getY(0);
        		mPrevAngle = mCurrAngle;
        		mCurrAngle = Math.atan2(mCurrSpanY, mCurrSpanX);
        		mPrevSpan = mCurrSpan;
        		mCurrSpan = Geometry.dist2(mCurrSpanX, mCurrSpanY);
        	}
    		if (mScrollMode == NO_SCROLL) {
    			// Store initial positions
    			mInitialX = mCurrentX;
    			mInitialY = mCurrentY;
    			// Set ScrollMode
    			if ((numPointers == 1) || smallAngle(mCurrAngle)) {
    				mScrollMode = MOVE;
    			} else {
    				mScrollMode = SCALE;
    				// Return false to prevent onScale with an incorrect PreviousAngle.
    				return false;
    			}
    		}

            // If only one pointer, ignore any specified margins:
            if (numPointers == 1) {
                if (mInitialY < mMarginTop)
                    return false;
            }

    		switch (mScrollMode) {
                case MOVE:
                    return onMove(e1, e2, distanceX, distanceY, numPointers);
                case SCALE:
                    double scaleFactor;
                    if (mPrevSpan > 0) {
                        scaleFactor = mCurrSpan / mPrevSpan;
                    } else {
                        scaleFactor = 1.0d;
                    }
                    return onScale(e1, e2, scaleFactor, mCurrAngle-mPrevAngle);
    		}
    		return false;
    	}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		public void onShowPress(MotionEvent e) {
		}

		public boolean onDown(MotionEvent e) {
			return false;
		}

		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		public boolean onMove(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY, int numPointers) {
			return false;
		}

		public boolean onScale(MotionEvent e1, MotionEvent e2, double scaleFactor, double angle) {
			return false;
		}
	}

	public MultiGestureDetector(Context context, OnGestureListener listener) {
		super(context, listener, null);
	}

	/**
	 * Creates a GestureDetector with the supplied listener that runs deferred
	 * events on the thread associated with the supplied
	 * {@link android.os.Handler}.
	 * 
	 * @see android.os.Handler#Handler()
	 * 
	 * @param context
	 *            the application's context
	 * @param listener
	 *            the listener invoked for all the callbacks, this must not be
	 *            null.
	 * @param handler
	 *            the handler to use for running deferred listener events.
	 * 
	 * @throws NullPointerException
	 *             if {@code listener} is null.
	 */
	public MultiGestureDetector(Context context, OnGestureListener listener,
			Handler handler) {
		super(context, listener, handler);
	}
}
