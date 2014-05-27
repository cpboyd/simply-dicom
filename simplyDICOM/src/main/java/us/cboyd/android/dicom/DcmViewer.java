/*
 * Copyright (C) 2013-2014 Christopher Boyd
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

package us.cboyd.android.dicom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.contrib.Contrib;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.android.shared.ImageContrastView;
import us.cboyd.android.shared.MultiGestureDetector;
import us.cboyd.shared.Geometry;

/**
 * DICOMViewer Class
 * 
 * @author Christopher Boyd
 * @version 0.2
 * 
 */
public class DcmViewer extends Activity implements OnTouchListener,
			SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
	
	private Matrix 	mMatrix 		= new Matrix();
    private float 	mScaleY2X 		= 1.0f;
    private float 	mScaleY 		= 1.0f;
    private float 	mRotDeg 		= 0.f;
    private float 	mFocusX 		= 0.f;
    private float 	mFocusY 		= 0.f;

    private float 	mContrast 		= 0.0f;
    private float	mLastContrast 	= mContrast;
    private float 	mBrightness 	= 50.0f;
    private float 	mLastBrightness = mBrightness;
    private boolean mCmapInv 		= false;
    private Spinner mCmapBox, mAxisBox;
    private int 	mCmapSelect 	= -1;
    private Mat 	mMat			= null;
    private int		mImageCount 	= 0;
    private int[] 	mInstance, mMaxIndex;
    private double[] mPixelSpacing 	= new double[]{1.0d, 1.0d, 1.0d};
    private double[] mScaleSpacing 	= new double[]{1.0d, 1.0d, 1.0d};
    private int 	mAxis 			= DcmVar.TRANSVERSE;

    private File 				mFilePath;
    private ArrayList<String> 	mFileList 		= null;
    private List<Mat> 			mMatList 		= null;
    private DicomObject 		mDicomObject 	= null;
    private ImageView 			mImageView;
    private ImageContrastView 	mCmapView;
    private String              mCurrFilename;
    /// OLD:
    private Button 			mPreviousButton, mNextButton;
    private TextView 		mIndexTextView, mCountTextView;
    private SeekBar 		mIndexSeekBar;
    private LinearLayout 	mNavigationBar;
    
    private MultiGestureDetector mMultiDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dcm_viewer);

		mImageView 	= (ImageView) findViewById(R.id.imageView);
		mCmapView 	= (ImageContrastView) findViewById(R.id.contrastView);
		mCmapBox 	= (Spinner) findViewById(R.id.spinner_colormap);
		mAxisBox 	= (Spinner) findViewById(R.id.spinner_plane);
		mImageView.setOnTouchListener(this);
		mCmapBox.setOnItemSelectedListener(this);
		mAxisBox.setOnItemSelectedListener(this);
		
		/// OLD:
		mPreviousButton = (Button) findViewById(R.id.previousImageButton);
		mNextButton 	= (Button) findViewById(R.id.nextImageButton);
		mIndexTextView 	= (TextView) findViewById(R.id.imageIndexView);
		mCountTextView 	= (TextView) findViewById(R.id.imageCountView);
		mIndexSeekBar 	= (SeekBar) findViewById(R.id.serieSeekBar);
		mNavigationBar 	= (LinearLayout) findViewById(R.id.navigationToolbar);
		
		// Get the file name from the savedInstanceState or from the intent
		String fileName = null;
		
		// If the saved instance state is not null get the file name
		if (savedInstanceState != null) {
			mFilePath = new File(savedInstanceState.getString(DcmVar.CURRDIR));
			mFileList = savedInstanceState.getStringArrayList(DcmVar.FILELIST);
			
		// Get the intent
		} else {
			
			Intent intent = getIntent();
			
			if (intent != null) {
				
				Bundle extras = intent.getExtras();
				
				fileName = extras == null ? null : extras.getString(DcmVar.DCMFILE);
				mFileList = extras == null ? null : extras.getStringArrayList(DcmVar.FILELIST);
				
			}
			
		}
		
		// If the file name is null, alert the user and close the activity
		if (fileName == null) {
			
			showExitAlertDialog("ERROR: Retrieving file",
					"The file could not be found.");
			
		// Load the file
		} else {
			
			// Get the File object for the current file
			File currentFile = new File(fileName);
			Log.i("cpb","Loading file...");
            System.gc();
			// Read in the DicomObject
			try {
				DicomInputStream dis;
				dis = new DicomInputStream(currentFile);
				mDicomObject = dis.readDicomObject();
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mMat 	= new Mat(mDicomObject.getInt(Tag.Rows),
							mDicomObject.getInt(Tag.Columns), CvType.CV_32S);
			mMat.put(0, 0, mDicomObject.getInts(Tag.PixelData));
			
			// Get the files array = get the files contained
			// in the parent of the current file
			mFilePath = currentFile.getParentFile();
            mCurrFilename = currentFile.getName();
			
			// If the files array is null or its length is less than 1,
			// there is an error because it must at least contain 1 file:
			// the current file
			if (mFileList == null || mFileList.size() < 1) {
				
				showExitAlertDialog("ERROR: Loading file",
						"This directory contains no DICOM files.");
				
			} else {
				
				// Get the file index in the array
				int currFileIndex = getIndex(currentFile);
				
				// If the current file index is negative
				// or greater or equal to the files array
				// length there is an error
				if (currFileIndex < 0
						|| currFileIndex >= mFileList.size()) {
					
					showExitAlertDialog("ERROR: Loading file",
							"This image file could not be found.");
				
				// Else initialize views and navigation bar
				} else {
					
					// Check if the seek bar must be shown or not
					if (mFileList.size() == 1) {
						
						mNavigationBar.setVisibility(View.INVISIBLE);
						
					} else {
						
						// Set the visibility of the previous button
						if (currFileIndex == 0) {
							
							mPreviousButton.setVisibility(View.INVISIBLE);
							
						} else if (currFileIndex == (mFileList.size() - 1)) {
							
							mNextButton.setVisibility(View.INVISIBLE);
							
						}
						
					}
					
				}
			}
			
		}
		
		// Set the seek bar change index listener
		mIndexSeekBar.setOnSeekBarChangeListener(this);
		
		mMatrix.postScale(mScaleY*mScaleY2X, mScaleY);
		mImageView.setImageMatrix(mMatrix);
		
		mMatrix = null;
	    
		mMultiDetector = new MultiGestureDetector(getApplicationContext(), new MultiListener());
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("cpb", "Load: setIm1");
		updateImage();
		Log.i("cpb", "Load: setIm3");
		// If this is the first time displaying an image, center it.
		if (mMatrix == null) {
			Log.i("cpb", "Load: setIm4");
			int 	height 	= mMat.rows();
			int 	width 	= mMat.cols();
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			float displayCenterX = displaymetrics.widthPixels/2;
			float displayCenterY = displaymetrics.heightPixels/2;
			mScaleY = Math.min( displaymetrics.widthPixels / (float) width,
					displaymetrics.heightPixels / (float) height);
	        Log.i("cpb", "mScaleY2X: " + mScaleY2X + " mScaleY: " + mScaleY);
	        float scaledImageCenterX = (width*mScaleY*mScaleY2X)/2;
	        float scaledImageCenterY = (height*mScaleY)/2;
	        mFocusX = displayCenterX;
	        mFocusY = displayCenterY;
			mMatrix = new Matrix();
			mMatrix.postScale(mScaleY*mScaleY2X, mScaleY);
			mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
			mImageView.setImageMatrix(mMatrix);
			mImageCount++;
		}
		
		if (mMatList == null) {
			int 	rows 		= mDicomObject.getInt(Tag.Rows);
			int 	cols 		= mDicomObject.getInt(Tag.Columns);
			String 	studyUID 	= mDicomObject.getString(Tag.StudyInstanceUID);
			String 	seriesUID 	= mDicomObject.getString(Tag.SeriesInstanceUID);
			int 	instanceNum = mDicomObject.getInt(Tag.InstanceNumber);
			double[] spacing 	= mDicomObject.getDoubles(Tag.PixelSpacing);
			double[] startPos 	= mDicomObject.getDoubles(Tag.ImagePositionPatient);
//            if ((instanceNum < 1) || (mFileList.size() == 1)) {
//                mNavigationBar.setVisibility(View.INVISIBLE);
//                return;
//            }
			mInstance 	= new int[] {instanceNum - 1, rows/2, cols/2};
			mMatList 	= new ArrayList<Mat>();

		    for(int i = 1; i < instanceNum; i++) {
		    	mMatList.add(new Mat(rows, cols, CvType.CV_32S));
		    }
		    mMatList.add(mMat);
		    Log.i("cpb", "Mat List Size: " + mMatList.size() + " Instance: " + instanceNum);
			DicomObject cdo = null;
            mDicomObject = null;
            DicomInputStream dis = null;
			for(String currFile : mFileList) {
                Log.i("cpb", "Loop: " + currFile);
				if (!currFile.equals(mCurrFilename)) {
                    cdo = null;
                    dis = null;
                    Log.i("cpb", "Attempting GC");
                    System.gc();
					// Read in the DicomObject
					try {
						dis = new DicomInputStream(new File(mFilePath, currFile));
						cdo = dis.readDicomObject();
						dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                    // Check the instance number
                    instanceNum = cdo.getInt(Tag.InstanceNumber);
                    // Spacing definition moved up
                    if ((spacing != null) &&
                            ((mInstance[0] == 0 && (instanceNum - 1) == 1) || (instanceNum == mInstance[0]))) {
                        double[] nextPos = cdo.getDoubles(Tag.ImagePositionPatient);
                        // mPixelSpacing{X, Y, Z}
                        mPixelSpacing = new double[] {spacing[1], spacing[0],
                                Math.abs(startPos[2] - nextPos[2])};
                        // mScaleY2X = mScaleSpacing[mAxis]
                        mScaleSpacing = new double[] {spacing[1] / spacing[0],
                                spacing[1] / mPixelSpacing[2], mPixelSpacing[2] / spacing[0]};
                    }
                    // If it's less than 1, continue to the next image.
                    if (instanceNum < 1) {
                        Log.i("cpb", "Skipping file because no instance number");
                        continue;
                    }

					int rows2 = cdo.getInt(Tag.Rows);
					int cols2 = cdo.getInt(Tag.Columns);
					if (studyUID.equals(cdo.getString(Tag.StudyInstanceUID)) &&
					 	seriesUID.equals(cdo.getString(Tag.SeriesInstanceUID))) {
						if (rows != rows2 || cols != cols2) {
							showExitAlertDialog("ERROR: Loading DICOM Series",
									"The number of rows and columns varies between instances/images.");
						}

                        // If there isn't enough space in the list, allocate more.
						while (mMatList.size() < instanceNum) {
                            mMatList.add(new Mat(rows, cols, CvType.CV_32S));
						}

                        mMatList.get(instanceNum - 1).put(0, 0, cdo.getInts(Tag.PixelData));

						mImageCount++;
					    Log.i("cpb", "Mat List Size: " + mMatList.size() + " Instance: " + instanceNum + " currFile: " + currFile);
					}
				}
			}
			mMaxIndex = new int[]{mMatList.size(), rows, cols};
			// Display the current file index
			//mIndexTextView.setText(String.valueOf(mInstance + 1));
			// Display the files count and set the seek bar maximum
			mCountTextView.setText(String.valueOf(mMatList.size()));
			
			if (mImageCount <= 1) {
				mNavigationBar.setVisibility(View.INVISIBLE);
			}

			mIndexSeekBar.setMax(mMatList.size() - 1);
			mIndexSeekBar.setProgress(mInstance[mAxis]);
		}
	}
	

	//// openCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                	Resources res = getResources();
                    Log.i(res.getString(R.string.tag_ocv), res.getString(R.string.ocv_load));
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	
	public boolean onTouch(View v, MotionEvent event) {
        mMultiDetector.onTouchEvent(event);

        float scaledImageCenterX = (mMat.cols()*mScaleY*mScaleY2X)/2;
        float scaledImageCenterY = (mMat.rows()*mScaleY)/2;
        
        mMatrix.reset();
        mMatrix.postScale(mScaleY*mScaleY2X, mScaleY);
        mMatrix.postRotate(mRotDeg,  scaledImageCenterX, scaledImageCenterY);
        mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
		mImageView.setImageMatrix(mMatrix);
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			// End scrolling if the user lifts fingers:
            mMultiDetector.resetScrollMode();
            // Store values in case we need them:
            mLastContrast   = mContrast;
            mLastBrightness = mBrightness;
        }
		
		return true; // indicate event was handled
	}
	
	/** OLD Functions */
	
	
	/** Called just before activity runs (after onStart). */
	@Override
	protected void onResume() {
		// If there isn't any external storage, quit the application.
		if (!ExternalIO.checkStorage()) {
            Resources res = getResources();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.err_mesg_disk))
				   .setTitle(res.getString(R.string.err_title_disk))
			       .setCancelable(false)
			       .setPositiveButton(res.getString(R.string.err_close),
			    	new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                DcmViewer.this.finish();
			           }
			       	});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFileList 	= null;
		mMat 		= null;
		mMatList 	= null;
		mDicomObject = null;
		
		// Free the drawable callback
		if (mImageView != null) {
			Drawable drawable = mImageView.getDrawable();
			
			if (drawable != null)
				drawable.setCallback(null);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the current file name
		outState.putString(DcmVar.CURRDIR, mFilePath.getAbsolutePath());
		outState.putStringArrayList(DcmVar.FILELIST, mFileList);
	}
	
	// ---------------------------------------------------------------
	// + <override> FUNCTIONS
	// ---------------------------------------------------------------
	

	@Override
	public void onLowMemory() {
		
		// Hint the garbage collector
		System.gc();
		
		// Show the exit alert dialog
		showExitAlertDialog("ERROR: Low Memory", "Low on memory");

		super.onLowMemory();
	}
	
	// ---------------------------------------------------------------
	// + <implements> FUNCTION
	// ---------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)
	 */
	public synchronized void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		try {
			// Set the current instance if specified by user
			// This prevents resetting the view if setMax changes the progress
			if (fromUser) mInstance[mAxis] = progress;
			if (mMatList != null) {
				switch(mAxis) {
					case DcmVar.TRANSVERSE:
						mMat = mMatList.get(mInstance[mAxis]);
						break;
					case DcmVar.CORONAL:
						List<Mat> ListY = new ArrayList<Mat>();
						for (int i = 0; i < mMatList.size(); i++) {
							ListY.add(mMatList.get(i).row(mInstance[mAxis]));
							mMat = new Mat();
						}
						Core.vconcat(ListY, mMat);
						break;
					case DcmVar.SAGGITAL:
						List<Mat> ListX = new ArrayList<Mat>();
						for (int i = 0; i < mMatList.size(); i++) {
							ListX.add(mMatList.get(i).col(mInstance[mAxis]));
							mMat = new Mat();
						}
						Core.hconcat(ListX, mMat);
						break;
					default:
						mAxis = DcmVar.TRANSVERSE;
				    	mIndexSeekBar.setMax(mMaxIndex[mAxis]-1);
				    	mIndexSeekBar.setProgress(mInstance[mAxis]);
				    	mCountTextView.setText(String.valueOf(mMaxIndex[mAxis]));
						break;
				}
				updateImage();
			}
			
			// Update the UI
			mIndexTextView.setText(String.valueOf(mInstance[mAxis] + 1));
			
			// Set the visibility of the previous button
			if (mInstance[mAxis] <= 0) {
				mInstance[mAxis] = 0;
				mPreviousButton.setVisibility(View.INVISIBLE);
				mNextButton.setVisibility(View.VISIBLE);
				
			} else if (mInstance[mAxis] >= (mMaxIndex[mAxis] - 1)) {
				mInstance[mAxis] = mMaxIndex[mAxis] - 1;
				mNextButton.setVisibility(View.INVISIBLE);
				mPreviousButton.setVisibility(View.VISIBLE);
				
			} else {
				
				mPreviousButton.setVisibility(View.VISIBLE);
				mNextButton.setVisibility(View.VISIBLE);
				
			}
			
		} catch (OutOfMemoryError ex) {
			
			System.gc();
			
			showExitAlertDialog("ERROR: Out of memory",
					"This DICOM series required more memory than your device could provide.");
			
		}
		
	}

	// Needed to implement the SeekBar.OnSeekBarChangeListener
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Do nothing.
	}

	// Needed to implement the SeekBar.OnSeekBarChangeListener
	public void onStopTrackingTouch(SeekBar seekBar) {
		
		System.gc(); // TODO needed ?
		// Do nothing.		
	}
	
	/**
	 * Handle touch on the previousButton.
	 * @param view
	 */
	public synchronized void previousImage(View view) {
		mInstance[mAxis]--;
		// Changing the progress bar will set the image
		mIndexSeekBar.setProgress(mInstance[mAxis]);
	}
	
	/**
	 * Handle touch on next button.
	 * @param view
	 */
	public synchronized void nextImage(View view) {
		mInstance[mAxis]++;
		// Changing the progress bar will set the image
		mIndexSeekBar.setProgress(mInstance[mAxis]);
	}
	
	// ---------------------------------------------------------------
	// - FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Get the index of the file in the files array.
	 * @param file
	 * @return Index of the file in the files array
	 * or -1 if the files is not in the list.
	 */
	private int getIndex(File file) {
		
		if (mFileList == null)
			throw new NullPointerException("The files array is null.");
		
		for (int i = 0; i < mFileList.size(); i++) {
			
			if (mFileList.get(i).equals(file.getName()))
				return i;
			
		}
		
		return -1;
		
	}
	
	/**
	 * Update the current image
	 *
	 */
	private void updateImage() {
		Core.MinMaxLocResult minmax = Core.minMaxLoc(mMat);
		double  diff 	= minmax.maxVal - minmax.minVal;
		double  ImWidth = (1 - (mContrast / 100.0d)) * diff;
		//double  ImMax 	= ImWidth + (diff - ImWidth) * (1.0d - (mBrightness / 100.0d)) + minmax.minVal;
		double  ImMin 	= (diff - ImWidth) * (1.0d - (mBrightness / 100.0d)) + minmax.minVal;
		double 	alpha 	= 255.0d / ImWidth;
		double 	beta 	= alpha*(-ImMin);
		int 	height 	= mMat.rows();
		int 	width 	= mMat.cols();
		
		if (mCmapInv) {
			alpha *= -1.0d;
			beta = 255.0d - beta;
		}
		
		Mat temp = new Mat(height, width, CvType.CV_32S);
		//Core.normalize(mMat, temp, ImMin, ImMax, Core.NORM_MINMAX);
		mMat.convertTo(temp, CvType.CV_8UC1, alpha, beta);
		if (mCmapSelect >= 0) {
			Contrib.applyColorMap(temp, temp, mCmapSelect);
			Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGR);
		}
		
		// Set the image
		Bitmap imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Log.w("cpb","test3");
		Utils.matToBitmap(temp, imageBitmap, true);
		Log.w("cpb","test4");
		mImageView.setImageBitmap(imageBitmap);
	}
	
	/**
	 * Show an alert dialog (AlertDialog) to inform
	 * the user that the activity must finish.
	 * @param title Title of the AlertDialog.
	 * @param message Message of the AlertDialog.
	 */
	private void showExitAlertDialog(String title, String message) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
			   .setTitle(title)
		       .setCancelable(false)
		       .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               DcmViewer.this.finish();
		           }
		       });
		
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
	}
	
	/** Checkbox's onCheckboxClicked
	 * 
	 * @param view
	 */
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.checkbox_inv:
	            mCmapInv = checked;
	            mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
	            updateImage();
	            break;
	    }
	}
	
	
	/**
	 * Spinner's onItemSelected
	 * @param parent
	 * @param view
	 * @param pos
	 * @param id
	 */
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	switch(parent.getId()) {
    		case R.id.spinner_colormap:
		    	mCmapSelect = pos - 1;
		    	Log.i("cpb", "Colormap: " + mCmapSelect + " id: " + id);
		        mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
		        updateImage();
		        break;
    		case R.id.spinner_plane:
    			mAxis = pos;
		    	mScaleY2X = (float) mScaleSpacing[mAxis];
		    	mIndexSeekBar.setMax(mMaxIndex[mAxis]-1);
		    	mIndexSeekBar.setProgress(mInstance[mAxis]);
		    	mCountTextView.setText(String.valueOf(mMaxIndex[mAxis]));
		    	Log.i("cpb", "Axis: " + mAxis + " id: " + id + " Scale: " + mScaleY2X);
		    	float 	height 	= mMat.rows();
				float 	width 	= mMat.cols();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				float displayCenterX = displaymetrics.widthPixels/2;
				float displayCenterY = displaymetrics.heightPixels/2;
				mScaleY = Math.min(displaymetrics.widthPixels / (mScaleY2X*width),
						displaymetrics.heightPixels / height);
				mFocusX = displayCenterX;
				mFocusY = displayCenterY;
		    	float scaledImageCenterX = (mMat.cols()*mScaleY*mScaleY2X)/2;
		        float scaledImageCenterY = (mMat.rows()*mScaleY)/2;
		        
		        mMatrix.reset();
		        mMatrix.postScale(mScaleY*mScaleY2X, mScaleY);
		        mMatrix.postRotate(mRotDeg,  scaledImageCenterX, scaledImageCenterY);
		        mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
				mImageView.setImageMatrix(mMatrix);
		    	break;
    	}
    }
    /** Spinner's onNothingSelected
     * 
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
	
	/** 
	 * MultiListener Class
	 * 
	 * @author Christopher Boyd
	 *
	 */
	private class MultiListener extends MultiGestureDetector.SimpleMultiGestureListener {
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// Center the ball on the display:
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			float displayCenterX = displaymetrics.widthPixels/2;
			float displayCenterY = displaymetrics.heightPixels/2;
	        mFocusX = displayCenterX;
	        mFocusY = displayCenterY;
			// Reset brightness (window level) and contrast (window width) to middle:
			mBrightness = 50.0f;
			mContrast = 0.0f;
			updateImage();
			mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
			return true;
		}
		@Override
		public boolean onMove(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY, int numPointers) {
			switch (numPointers) {
			case 1:
				mFocusX -= distanceX;
				mFocusY -= distanceY;
				return true;
			case 2:
				// Do different things, depending on whether the fingers are moving in X or Y.
				if (mMultiDetector.isTravelY()) {
					mContrast = mLastContrast;
					mBrightness = Math.max(0.0f, Math.min(100.0f, mBrightness - (distanceY / 5.0f)));
				} else {
					mBrightness = mLastBrightness;
					mContrast = Math.max(0.0f, Math.min(100.0f, mContrast + (distanceX / 10.0f)));
				}
				updateImage();
				mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
				return true;
			default: return false;
			}
		}
		@Override
		public boolean onScale(MotionEvent e1, MotionEvent e2, double scaleFactor, double angle) {
			mScaleY *= scaleFactor;
			// Prevent the oval from being too small:
			mScaleY = Math.max(0.1f, Math.min(mScaleY, 100.0f)); 
			
			mRotDeg += Geometry.rad2deg(angle);
			return true;
		}
	}
}