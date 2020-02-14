package app.boyd.android.shared.gesture

import android.content.Context
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import app.boyd.shared.Geometry
import kotlin.math.abs
import kotlin.math.atan2

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
        get() = abs(mCurrentX - mInitialX) > abs(mCurrentY - mInitialY)

    /** Returns whether travel is along the Y axis  */
    val isTravelY: Boolean
        get() = !isTravelX

    /** Returns the current scroll mode  */
    val scrollMode: ScrollMode
        get() = mScrollMode

    fun setTopMargin(margin: Double) {
        mMarginTop = margin
    }

    fun setBottomMargin(margin: Double) {
        mMarginBottom = margin
    }

    fun setHorizontalMargin(marginTop: Double, marginBottom: Double = marginTop) {
        mMarginTop = marginTop
        mMarginBottom = marginBottom
    }

    /** Reset the scroll mode to NO_SCROLL  */
    fun resetScrollMode() {
        mScrollMode = ScrollMode.NO_SCROLL
    }

    /**
     * SimpleMultiGestureListener features an onScroll that splits
     * movement-based gestures into "move" (onMove) and "scale" (onScale).
     *
     * @author Christopher Boyd
     */
    open class SimpleMultiGestureListener : OnGestureListener, GestureDetector.OnDoubleTapListener {

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {}

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?,
                              distanceX: Float, distanceY: Float): Boolean {
            e2 ?: return false
            val numPointers = e2.pointerCount
            val avg = e2.calcAvgPosition()
            mCurrentX = avg.first
            mCurrentY = avg.second
            // Calculate span and angle
            if (numPointers == 2) {
                //mPrevSpanX = mCurrSpanX;
                //mPrevSpanY = mCurrSpanY;
                mCurrSpanX = e2.getX(1) - e2.getX(0)
                mCurrSpanY = e2.getY(1) - e2.getY(0)
                mPrevAngle = mCurrAngle
                mCurrAngle = atan2(mCurrSpanY.toDouble(), mCurrSpanX.toDouble())
                mPrevSpan = mCurrSpan
                mCurrSpan = Geometry.dist2(mCurrSpanX, mCurrSpanY)
            }
            if (mScrollMode == ScrollMode.NO_SCROLL) {
                // Store initial positions
                mInitialX = mCurrentX
                mInitialY = mCurrentY
                // Set ScrollMode
                if (numPointers == 1 || mCurrAngle.isAngleSmall()) {
                    mScrollMode = ScrollMode.MOVE
                } else {
                    mScrollMode = ScrollMode.SCALE
                    // Return false to prevent onScale with an incorrect PreviousAngle.
                    return false
                }
            }

            // If only one pointer, ignore any specified margins:
            if (numPointers == 1) {
                if (mInitialY < mMarginTop)
                    return false
            }

            return when (mScrollMode) {
                ScrollMode.MOVE -> onMove(e1, e2, distanceX, distanceY, numPointers)
                ScrollMode.SCALE -> {
                    val scaleFactor = if (mPrevSpan > 0) {
                        mCurrSpan / mPrevSpan
                    } else {
                        1.0
                    }
                    onScale(e1, e2, scaleFactor, mCurrAngle - mPrevAngle)
                }
                else -> {
                    false
                }
            }
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                             velocityY: Float): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) {}

        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return false
        }

        open fun onMove(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                        distanceY: Float, numPointers: Int): Boolean {
            return false
        }

        open fun onScale(e1: MotionEvent?, e2: MotionEvent?, scaleFactor: Double, angle: Double): Boolean {
            return false
        }
    }

    constructor(context: Context, listener: OnGestureListener) : super(context, listener, null)

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
    constructor(context: Context, listener: OnGestureListener,
                handler: Handler) : super(context, listener, handler)

    companion object {
        /** Internal Variables  */
        private var mScrollMode = ScrollMode.NO_SCROLL
        private var mPrevAngle = 0.0
        private var mCurrAngle = 0.0

        /** X & Y difference between pointers  */
        private var mInitialX = 0.0
        private var mInitialY = 0.0
        private var mCurrentX = 0.0
        private var mCurrentY = 0.0
        //private var mPrevSpanX = 0.0
        //private var mPrevSpanY = 0.0
        private var mCurrSpanX = 0.0f
        private var mCurrSpanY = 0.0f
        private var mPrevSpan = 0.0
        private var mCurrSpan = 0.0

        private var mMarginTop = 0.0
        private var mMarginBottom = 0.0
    }
}
