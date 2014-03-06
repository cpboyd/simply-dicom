package us.cboyd.android.dicom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/* 
					// Use image matrix for scaling in the event that X & Y pixel spacing differ.
					DisplayMetrics displaymetrics = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
					float displayCenterX = displaymetrics.widthPixels/2;
					float displayCenterY = displaymetrics.heightPixels/2;
					mScaleY = Math.min( displaymetrics.widthPixels / (float) cols,
							displaymetrics.heightPixels / (float) rows);
					
					float scaledImageCenterX = (cols*mScaleY*scaleY2X)/2;
			        float scaledImageCenterY = (rows*mScaleY)/2;
			        mFocusX = displayCenterX;
			        mFocusY = displayCenterY;
			        
			        Matrix 	imMatrix = new Matrix();
			        imMatrix.postScale(mScaleY*scaleY2X, mScaleY);
			        imMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
					mImageView.setImageMatrix(imMatrix);
 */

public class DemoImageView extends ImageView {
	public DemoImageView(Context context) {
        super(context);
    }

    public DemoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    try {
	        Drawable drawable = getDrawable();
	
	        if (drawable == null) {
	            setMeasuredDimension(0, 0);
	        } else {
	            float imageSideRatio = (float)drawable.getIntrinsicWidth() / (float)drawable.getIntrinsicHeight();
	            float viewSideRatio = (float)MeasureSpec.getSize(widthMeasureSpec) / (float)MeasureSpec.getSize(heightMeasureSpec);
	            if (imageSideRatio >= viewSideRatio) {
	                // Image is wider than the display (ratio)
	                int width = MeasureSpec.getSize(widthMeasureSpec);
	                int height = (int)(width / imageSideRatio);
	                setMeasuredDimension(width, height);
	            } else {
	                // Image is taller than the display (ratio)
	                int height = MeasureSpec.getSize(heightMeasureSpec);
	                int width = (int)(height * imageSideRatio);
	                setMeasuredDimension(width, height);
	            }
	        }
	    } catch (Exception e) {
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }
    }
}
