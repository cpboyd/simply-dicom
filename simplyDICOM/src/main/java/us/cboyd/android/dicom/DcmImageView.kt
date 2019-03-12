package us.cboyd.android.dicom

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import us.cboyd.android.shared.MultiGestureDetector
import us.cboyd.shared.Geometry

class DcmImageView: ImageView, View.OnTouchListener {
    interface OnContrastChangedListener {
        fun onContrastChanged(brightness: Double, contrast: Double, colormap: Int, invertCmap: Boolean)
    }

    private var mContrastListener: OnContrastChangedListener? = null
    private val multiDetector: MultiGestureDetector
    private var mContrast = 0.0
    private var mLastContrast = mContrast
    private var mBrightness = 50.0
    private var mLastBrightness = mBrightness
    private var mInvertCmap = false
    private var mColormap = -1
    private var mMat: Mat? = null
    private var mScaleY2X = 1.0f
    private var mScaleY = 1.0f
    private var mRotDeg = 0.0f
    private var mFocusX = 0.0f
    private var mFocusY = 0.0f

    constructor(context: Context): super(context)
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0): super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setOnTouchListener(this)
        multiDetector = MultiGestureDetector(context.applicationContext, MultiListener())
    }

    fun setOnContrastChangedListener(listener: OnContrastChangedListener) {
        mContrastListener = listener
    }

    fun setColormap(colormap: Int = -1, invertCmap: Boolean = false) {
        mColormap = colormap
        mInvertCmap = invertCmap
        redrawImage()
        mContrastListener?.onContrastChanged(mBrightness, mContrast, colormap, invertCmap)
    }

    /**
     * Sets the brightness and contrast values
     * @param brightness
     * @param contrast
     */
    fun setImageContrast(brightness: Double = 50.0, contrast: Double = 0.0, colormap: Int = -1, invertCmap: Boolean = false) {
        mBrightness = brightness
        mContrast = contrast
        setColormap(colormap, invertCmap)
    }

    /**
     * Update the current image
     *
     */
    fun updateImage(mat: Mat?, brightness: Double = 50.0, contrast: Double = 0.0, colormap: Int = -1, invertCmap: Boolean = false) {
        mMat = mat
        setImageContrast(brightness, contrast, colormap, invertCmap)
    }

    private fun redrawImage() {
        val mat = mMat
        // If mat is null, clear the image
        if (mat == null) {
            setImageDrawable(null)
            return
        }

        val minmax = Core.minMaxLoc(mat)
        val diff = minmax.maxVal - minmax.minVal
        val ImWidth = (1.0 - mContrast / 100.0) * diff
        //val  ImMax 	= ImWidth + (diff - ImWidth) * (1.0d - (mBrightness / 100.0d)) + minmax.minVal
        val ImMin = (diff - ImWidth) * (1.0 - mBrightness / 100.0) + minmax.minVal
        var alpha = 255.0 / ImWidth
        var beta = alpha * -ImMin

        if (mInvertCmap) {
            alpha *= -1.0
            beta = 255.0 - beta
        }

        val height = mat.rows()
        val width = mat.cols()
        val temp = Mat(height, width, CvType.CV_32S)
        //Core.normalize(mat, temp, ImMin, ImMax, Core.NORM_MINMAX)
        mat.convertTo(temp, CvType.CV_8UC1, alpha, beta)
        if (mColormap >= 0) {
            Imgproc.applyColorMap(temp, temp, mColormap)
            //applyColorMap returns a BGR image, but createBitmap expects RGB
            //do a conversion to swap blue and red channels:
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGR)
        }

        // Set the image
        val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(temp, imageBitmap, true)
        setImageBitmap(imageBitmap)
    }

    fun updateMat(mat: Mat?) {
        mMat = mat
        // If mat is null, clear the image
        if (mat == null) {
            setImageDrawable(null)
            return
        }

        // If this is the first time displaying an image, center it.
        setScaleY2X()
        redrawImage()
    }

    fun setScaleY2X(scaleY2X: Float = 1.0f) {
        mScaleY2X = scaleY2X
        val mat = mMat ?: return

        val height = mat.rows().toFloat()
        val width = mat.cols().toFloat()
        val windowManager = windowManager ?: return
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        multiDetector.setHorizontalMargin(25.0f * displayMetrics.density)
        val displayCenterX = displayMetrics.widthPixels / 2.0f
        val displayCenterY = displayMetrics.heightPixels / 2.0f
        mScaleY = Math.min(displayMetrics.widthPixels / (mScaleY2X * width),
                displayMetrics.heightPixels / height)
        Log.i("cpb", "mScaleY2X: $mScaleY2X mScaleY: $mScaleY")
        mFocusX = displayCenterX
        mFocusY = displayCenterY
        
        updateMatrix()
    }

    private fun updateMatrix() {
        val mat = mMat ?: return
        val scaledImageCenterX = mat.cols().toFloat() * mScaleY * mScaleY2X / 2.0f
        val scaledImageCenterY = mat.rows().toFloat() * mScaleY / 2.0f

        val matrix = Matrix()
        matrix.postScale(mScaleY * mScaleY2X, mScaleY)
        matrix.postRotate(mRotDeg, scaledImageCenterX, scaledImageCenterY)
        matrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY)
        imageMatrix = matrix
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // If we haven't loaded the image yet, don't process any touch events
        val mat = mMat ?: return false
        multiDetector.onTouchEvent(event)

        updateMatrix()

        if (event.action == MotionEvent.ACTION_UP) {
            // End scrolling if the user lifts fingers:
            multiDetector.resetScrollMode()
            // Store values in case we need them:
            mLastContrast = mContrast
            mLastBrightness = mBrightness
        }

        return true // indicate event was handled
    }

    private val activity: Activity?
        get() {
            // Gross way of unwrapping the Activity so we can get the FragmentManager
            var context = context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            return null
        }

    private val windowManager: WindowManager?
        get() {
            return context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        }

    private fun clearActivityFocus(): Boolean {
        return (activity as? DcmViewer)?.clearFocus() ?: false
    }

    /**
     * MultiListener Class
     *
     * @author Christopher Boyd
     */
    private inner class MultiListener : MultiGestureDetector.SimpleMultiGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return clearActivityFocus()
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // Center the ball on the display:
            val windowManager = windowManager ?: return false
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val displayCenterX = displayMetrics.widthPixels / 2.0f
            val displayCenterY = displayMetrics.heightPixels / 2.0f
            mFocusX = displayCenterX
            mFocusY = displayCenterY
            // Reset brightness (window level) and contrast (window width) to middle:
            mBrightness = 50.0
            mContrast = 0.0
            setImageContrast(mBrightness, mContrast, mColormap, mInvertCmap)
            return true
        }

        override fun onMove(e1: MotionEvent, e2: MotionEvent,
                            distanceX: Float, distanceY: Float, numPointers: Int): Boolean {
            when (numPointers) {
                1 -> {
                    mFocusX -= distanceX
                    mFocusY -= distanceY
                    return true
                }
                2 -> {
                    // Do different things, depending on whether the fingers are moving in X or Y.
                    if (multiDetector.isTravelY) {
                        mContrast = mLastContrast
                        mBrightness = Math.max(0.0, Math.min(100.0, mBrightness - distanceY / 5.0))
                    } else {
                        mBrightness = mLastBrightness
                        mContrast = Math.max(0.0, Math.min(100.0, mContrast + distanceX / 10.0))
                    }
                    setImageContrast(mBrightness, mContrast, mColormap, mInvertCmap)
                    return true
                }
                else -> return false
            }
        }

        override fun onScale(e1: MotionEvent, e2: MotionEvent, scaleFactor: Double, angle: Double): Boolean {
            mScaleY *= scaleFactor.toFloat()
            // Prevent the oval from being too small:
            mScaleY = Math.max(0.1f, Math.min(mScaleY, 100.0f))

            mRotDeg += Geometry.rad2deg(angle).toFloat()
            return true
        }
    }
}