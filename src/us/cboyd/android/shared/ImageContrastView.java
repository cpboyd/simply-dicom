/*
 * Copyright (C) 2013 Christopher Boyd
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
 * 
 */

package us.cboyd.android.shared;

import org.opencv.android.Utils;
import org.opencv.contrib.Contrib;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import us.cboyd.shared.X11Color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageContrastView extends ImageView {
	// Paint and path effects used for drawing lines
	private Paint 	linePaint 	= new Paint();
	private Paint 	dashPaint 	= new Paint();
    private Path  	dashPath  	= new Path();
	private PathEffect dash 	= new DashPathEffect(new float[] {5,8}, 0);
	// Brightness & contrast values
	private double 	mLevel, mMax, mMin;
	
	/**
	 * Constructors
	 * 
	 * @param context
	 */
    public ImageContrastView(Context context) {
		super(context);
	}
    public ImageContrastView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    public ImageContrastView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
    /**
     * Sets the brightness and contrast values
     * @param brightness
     * @param contrast
     */
	public void setImageContrast(double brightness, double contrast) {
		setImageContrast(brightness, contrast, -1, false);
	}
	
	public void setImageContrast(double brightness, double contrast, int colormap, boolean inv) {
		setImageContrastCV(brightness, contrast, colormap, inv);
	}
	
	public void setImageContrastCV(double brightness, double contrast, int colormap, boolean inv) {
        double 	diff 	= getWidth();
		double 	ImWidth = (1 - (contrast / 100.0d)) * diff;
		double 	alpha 	= 255.0d / ImWidth;
		double 	beta  	= alpha*(-mMin);
		mLevel 	= ImWidth / 2.0d + (diff - ImWidth) * (1.0d - (brightness / 100.0d));
		mMax 	= ImWidth + (diff - ImWidth) * (1.0d - (brightness / 100.0d));
		mMin 	= (diff - ImWidth) * (1.0d - (brightness / 100.0d));
		
		int i = 0;
		int n = (int) diff;
		Mat cmap = new Mat(1, n, CvType.CV_32S);
		for (i=0; i<n; i++) {
			cmap.put(0, i, i);
		}
		if (inv) {
			alpha *= -1.0d;
			beta = 255.0d - beta;
		}
		cmap.convertTo(cmap, CvType.CV_8UC1, alpha, beta);
		if (colormap >= 0) {
			Contrib.applyColorMap(cmap, cmap, colormap);
			//applyColorMap returns a BGR image, but createBitmap expects RGB
			//do a conversion to swap blue and red channels:
			Imgproc.cvtColor(cmap, cmap, Imgproc.COLOR_RGB2BGR);
		}
		Bitmap cmapBitmap = Bitmap.createBitmap(n, 1, Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(cmap, cmapBitmap, false);
		setImageBitmap(cmapBitmap);
	}

	/**
	 * Override ImageView's onDraw(canvas) in order to add lines
	 * 
	 * This allows for use with setImageBitmap() or setImageDrawable()
	 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
		// Set the line color to lime green.
        linePaint.setColor(X11Color.LimeGreen);
        dashPaint.setColor(X11Color.LimeGreen);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setPathEffect(dash);
        // Dash effect doesn't always work with drawLine, use drawPath instead:
        dashPath.rewind();
        dashPath.moveTo((float) mLevel, 0.0f);
        dashPath.lineTo((float) mLevel, getHeight());
        // Draw the level, min, and max lines.
        canvas.drawPath(dashPath, dashPaint);
        // Draw "0" at pixel 1, or else it won't show up.
        canvas.drawLine((float) Math.max(mMin, 1.0d), 0.0f, (float) Math.max(mMin, 1.0d), getHeight(), linePaint);
        canvas.drawLine((float) mMax, 0.0f, (float) mMax, getHeight(), linePaint);

    }
}