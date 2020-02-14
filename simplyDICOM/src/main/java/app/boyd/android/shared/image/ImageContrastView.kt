package app.boyd.android.shared.image

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.RequiresApi
import app.boyd.shared.X11Color
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ImageContrastView : ImageView {
    // Paint and path effects used for drawing lines
    private val linePaint = Paint()
    private val dashPaint = Paint()
    private val dashPath = Path()
    private val dash = DashPathEffect(floatArrayOf(5f, 8f), 0f)
    // Brightness & contrast values
    private var mLevel = 0.0
    private var mMax = 0.0
    private var mMin = 0.0

    /**
     * Constructors
     *
     * @param context
     */
    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * Sets the brightness and contrast values
     * @param brightness
     * @param contrast
     */
    @JvmOverloads
    fun setImageContrast(brightness: Double = 50.0, contrast: Double = 0.0, colormap: Int = -1, invertCmap: Boolean = false) {
        val diff = width.toDouble()
        val imWidth = (1 - contrast / 100.0) * diff
        var alpha = 255.0 / imWidth
        var beta = alpha * -mMin
        mLevel = imWidth / 2.0 + (diff - imWidth) * (1.0 - brightness / 100.0)
        mMax = imWidth + (diff - imWidth) * (1.0 - brightness / 100.0)
        mMin = (diff - imWidth) * (1.0 - brightness / 100.0)

        val n = diff.toInt()
        val cmap = Mat(1, n, CvType.CV_32S)
        var i = 0
        while (i < n) {
            cmap.put(0, i, i.toDouble())
            i++
        }
        if (invertCmap) {
            alpha *= -1.0
            beta = 255.0 - beta
        }

        cmap.convertTo(cmap, CvType.CV_8UC1, alpha, beta)
        if (colormap >= 0) {
            Imgproc.applyColorMap(cmap, cmap, colormap)
            //applyColorMap returns a BGR image, but createBitmap expects RGB
            //do a conversion to swap blue and red channels:
            Imgproc.cvtColor(cmap, cmap, Imgproc.COLOR_RGB2BGR)
        }
        val cmapBitmap = Bitmap.createBitmap(n, 1, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(cmap, cmapBitmap, false)
        setImageBitmap(cmapBitmap)
    }

    /**
     * Override ImageView's onDraw(canvas) in order to add lines
     *
     * This allows for use with setImageBitmap() or setImageDrawable()
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        // Set the line color to lime green.
        linePaint.color = X11Color.LimeGreen
        dashPaint.color = X11Color.LimeGreen
        dashPaint.style = Paint.Style.STROKE
        dashPaint.pathEffect = dash
        // Dash effect doesn't always work with drawLine, use drawPath instead:
        dashPath.rewind()
        dashPath.moveTo(mLevel.toFloat(), 0.0f)
        dashPath.lineTo(mLevel.toFloat(), height.toFloat())

        // Draw the level, min, and max lines.
        canvas.drawPath(dashPath, dashPaint)
        // Draw "0" at pixel 1, or else it won't show up.
        canvas.drawLine(mMin.coerceAtLeast(1.0).toFloat(), 0.0f, mMin.coerceAtLeast(1.0).toFloat(), height.toFloat(), linePaint)
        canvas.drawLine(mMax.toFloat(), 0.0f, mMax.toFloat(), height.toFloat(), linePaint)

    }
}