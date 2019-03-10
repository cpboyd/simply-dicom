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

package us.cboyd.android.shared

import android.content.Context
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import us.cboyd.shared.Geometry

/**
 * Detects various gestures and events using the supplied [MotionEvent]s.
 * The [OnGestureListener] callback will notify users when a particular
 * motion event has occurred. This class should only be used with
 * [MotionEvent]s reported via touch (don't use for trackball events).
 *
 * To use this class:
 *
 *  * Create an instance of the `MultiGestureDetector` for your
 * [View]
 *  * In the [View.onTouchEvent] method ensure you call
 * [.onTouchEvent]. The methods defined in your callback will
 * be executed when the events occur.
 *
 */
class MultiGestureDetector : GestureDetector {

    /** Returns whether travel is along the X axis  */
    val isTravelX: Boolean
        get() = Math.abs(mCurrentX - mInitialX) > Math.abs(mCurrentY - mInitialY)

    /** Returns whether travel is along the Y axis  */
    val isTravelY: Boolean
        get() = !isTravelX

    /** Returns the current scroll mode  */
    val scrollMode: Short
        get() = mScrollMode

    fun setTopMargin(margin: Float) {
        mMarginTop = margin
    }

    fun setBottomMargin(margin: Float) {
        mMarginBottom = margin
    }

    fun setHorizontalMargin(margin: Float) {
        setHorizontalMargin(margin, margin)
    }

    fun setHorizontalMargin(marginTop: Float, marginBottom: Float) {
        mMarginTop = marginTop
        mMarginBottom = marginBottom
    }

    /** Reset the scroll mode to NO_SCROLL  */
    fun resetScrollMode() {
        mScrollMode = NO_SCROLL
    }

    /**
     * SimpleMultiGestureListener features an onScroll that splits
     * movement-based gestures into "move" (onMove) and "scale" (onScale).
     *
     * @author Christopher Boyd
     */
    open class SimpleMultiGestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {}

        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            val numPointers = calcAvgPosition(e2)
            // Calculate span and angle
            if (numPointers == 2) {
                //mPrevSpanX = mCurrSpanX;
                //mPrevSpanY = mCurrSpanY;
                mCurrSpanX = e2.getX(1) - e2.getX(0)
                mCurrSpanY = e2.getY(1) - e2.getY(0)
                mPrevAngle = mCurrAngle
                mCurrAngle = Math.atan2(mCurrSpanY.toDouble(), mCurrSpanX.toDouble())
                mPrevSpan = mCurrSpan
                mCurrSpan = Geometry.dist2(mCurrSpanX, mCurrSpanY)
            }
            if (mScrollMode == NO_SCROLL) {
                // Store initial positions
                mInitialX = mCurrentX
                mInitialY = mCurrentY
                // Set ScrollMode
                if (numPointers == 1 || smallAngle(mCurrAngle)) {
                    mScrollMode = MOVE
                } else {
                    mScrollMode = SCALE
                    // Return false to prevent onScale with an incorrect PreviousAngle.
                    return false
                }
            }

            // If only one pointer, ignore any specified margins:
            if (numPointers == 1) {
                if (mInitialY < mMarginTop)
                    return false
            }

            when (mScrollMode) {
                MOVE -> return onMove(e1, e2, distanceX, distanceY, numPointers)
                SCALE -> {
                    val scaleFactor: Double
                    if (mPrevSpan > 0) {
                        scaleFactor = mCurrSpan / mPrevSpan
                    } else {
                        scaleFactor = 1.0
                    }
                    return onScale(e1, e2, scaleFactor, mCurrAngle - mPrevAngle)
                }
            }
            return false
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {}

        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return false
        }

        open fun onMove(e1: MotionEvent, e2: MotionEvent, distanceX: Float,
                        distanceY: Float, numPointers: Int): Boolean {
            return false
        }

        open fun onScale(e1: MotionEvent, e2: MotionEvent, scaleFactor: Double, angle: Double): Boolean {
            return false
        }
    }

    constructor(context: Context, listener: GestureDetector.OnGestureListener) : super(context, listener, null) {}

    /**
     * Creates a GestureDetector with the supplied listener that runs deferred
     * events on the thread associated with the supplied
     * [android.os.Handler].
     *
     * @see android.os.Handler.Handler
     * @param context
     * the application's context
     * @param listener
     * the listener invoked for all the callbacks, this must not be
     * null.
     * @param handler
     * the handler to use for running deferred listener events.
     *
     * @throws NullPointerException
     * if `listener` is null.
     */
    constructor(context: Context, listener: GestureDetector.OnGestureListener,
                handler: Handler) : super(context, listener, handler) {
    }

    companion object {
        /** Scroll Mode Values  */
        val NO_SCROLL: Short = 0
        val MOVE: Short = 1
        val SCALE: Short = 2
        val MOVE2: Short = 3

        /** Internal Variables  */
        private var mScrollMode = NO_SCROLL
        private var mPrevAngle: Double = 0.toDouble()
        private var mCurrAngle: Double = 0.toDouble()

        /** X & Y difference between pointers  */
        private var mInitialX: Float = 0.toFloat()
        private var mInitialY: Float = 0.toFloat()
        private var mCurrentX: Float = 0.toFloat()
        private var mCurrentY: Float = 0.toFloat()
        //private static float mPrevSpanX = 0;
        //private static float mPrevSpanY = 0;
        private var mCurrSpanX: Float = 0.toFloat()
        private var mCurrSpanY: Float = 0.toFloat()
        private var mPrevSpan: Double = 0.toDouble()
        private var mCurrSpan: Double = 0.toDouble()

        private var mMarginTop: Float = 0.toFloat()
        private var mMarginBottom: Float = 0.toFloat()

        /** Determine if angle is small enough  */
        private fun smallAngle(angle: Double): Boolean {
            var angle = angle
            angle = Geometry.rad2deg(Math.abs(angle))
            // The sweet spot seems to be around 20 degrees:
            return 0.0 <= angle && angle < 20.0 || 160.0 < angle && angle <= 180.0
        }

        /**
         * Calculates the average X & Y positions, returns the [MotionEvent]'s
         * pointer count.
         */
        private fun calcAvgPosition(e2: MotionEvent): Int {
            val numPointers = e2.pointerCount
            // Store initial positions
            mCurrentX = 0f
            mCurrentY = 0f
            for (i in 0 until numPointers) {
                mCurrentX += e2.getX(i)
                mCurrentY += e2.getY(i)
            }
            mCurrentX /= numPointers.toFloat()
            mCurrentY /= numPointers.toFloat()
            return numPointers
        }
    }
}
