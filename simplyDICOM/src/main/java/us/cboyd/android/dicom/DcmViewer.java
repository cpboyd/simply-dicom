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

package us.cboyd.android.dicom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import us.cboyd.android.shared.files.ExternalIO;
import us.cboyd.android.shared.MultiGestureDetector;
import us.cboyd.android.shared.files.FileUtils;
import us.cboyd.android.shared.image.ColormapArrayAdapter;
import us.cboyd.android.shared.image.ImageContrastView;
import us.cboyd.shared.Geometry;

/**
 * DICOMViewer Class
 * 
 * @author Christopher Boyd
 * @version 0.6
 * 
 */
public class DcmViewer extends Activity implements OnTouchListener, CompoundButton.OnCheckedChangeListener,
        TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private boolean mNoFilter, mShowFiles;
	private Matrix 	mMatrix;
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
    private Spinner mCmapSpiner, mAxisBox;
    private View    mCmapBox;
    private int 	mCmapSelect 	= -1;
    private Mat 	mMat			= null;
    private int		mImageCount 	= 0;
    private int[] 	mInstance, mMaxIndex;
    private double[] mPixelSpacing 	= new double[]{1.0d, 1.0d, 1.0d};
    private double[] mScaleSpacing 	= new double[]{1.0d, 1.0d, 1.0d};
    private int 	mAxis 			= DcmVar.TRANSVERSE;

    private Attributes          mAttributes     = null;
    private File 				mFilePath;
    private List<File> 	        mFileList 		= null;
    private List<Mat> 			mMatList 		= null;
    private ImageView 			mImageView;
    private ImageContrastView 	mCmapView;
    private File                mCurrFile;
    private EditText            mCurrentSlide;
    private AsyncTask           mTask;
    private View                mMultiProgress;
    private ProgressBar         mLoadProgress;
    private TextView            mLoadText;
    private ImageButton         mPreviousButton, mNextButton;

    // File Load
    private String              mInitialPath, mErrorMessage;
    private Uri                 mInitialUri;
    /// OLD:
    private SeekBar 		    mIndexSeekBar;
    private View 	            mNavigationBar;
    
    private MultiGestureDetector mMultiDetector;
    public final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int PERMISSION_REQUEST_CODE = 200;
    final boolean isMarshmallow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    // Static initialization of OpenCV
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.d("cpb", "No openCV");
        }
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dcm_viewer);

		mImageView 	= (ImageView) findViewById(R.id.imageView);
		mCmapView 	= (ImageContrastView) findViewById(R.id.contrastView);
        mCmapBox    = findViewById(R.id.contrastSelect);
        ((CompoundButton) findViewById(R.id.btn_invert)).setOnCheckedChangeListener(this);
		mCmapSpiner = (Spinner) findViewById(R.id.spinner_colormap);
		mAxisBox 	= (Spinner) findViewById(R.id.spinner_plane);
		mImageView.setOnTouchListener(this);
        mCmapSpiner.setAdapter(new ColormapArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.colormaps_array)));
		mCmapSpiner.setOnItemSelectedListener(this);
        mCurrentSlide   = (EditText) findViewById(R.id.input_idx);
        mCurrentSlide.setOnEditorActionListener(this);
        mPreviousButton = (ImageButton) findViewById(R.id.btn_prev_idx);
        mNextButton     = (ImageButton) findViewById(R.id.btn_next_idx);
        mMultiProgress  = findViewById(R.id.progressContainer2);
        mLoadProgress   = (ProgressBar) findViewById(R.id.loadProgress);
        mLoadText       = (TextView) findViewById(R.id.progressText);
		
		/// OLD:
		mIndexSeekBar 	= (SeekBar) findViewById(R.id.seek_idx);
		mNavigationBar 	= findViewById(R.id.navigationToolbar);

        mAxisBox.setOnItemSelectedListener(this);
        // Set the seek bar change index listener
        mIndexSeekBar.setOnSeekBarChangeListener(this);

        // If the saved instance state is not null get the file name
        if (savedInstanceState != null) {
            mFilePath = new File(savedInstanceState.getString(DcmVar.CURRDIR));
            mFileList = getFileList(mFilePath);
            return;
        }

        // Get the intent
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (intent.getAction()){
            case Intent.ACTION_VIEW:
                loadFile(intent);
                break;
            default:
                break;
        }
	}

    static final int REQUEST_IMAGE_GET = 1;

    /** Open file intent */
    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            loadFile(data);
        }
    }

    private void showLoading() {
        // Show loading circle
        findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
        // Make sure the navbar is gone and the progress bar is visible.
        mNavigationBar.setVisibility(View.INVISIBLE);
        mMultiProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
    }

    private void loadFile(Intent data) {
        // Check for multiple files
        ClipData files = data.getClipData();
        mInitialUri = (files == null) ? data.getData() : files.getItemAt(0).getUri();
        if (mInitialUri == null) {
            showSnackbar("File not found.");
        }

        // Show loading UI
        showLoading();

        // Attempt to get the path for local files.
        // FIXME: Should FileUtils catch this?
        try {
            mInitialPath = FileUtils.getPath(this, mInitialUri);
        } catch (Exception ex) {
            mInitialPath = null;
        }
        Log.i("cpb", "Uri: " + mInitialUri);
        Log.i("cpb", "Path: " + mInitialPath);
        // If we couldn't find a path, load the file from URI
        if (mInitialPath == null) {
            new UriLoadTask().execute(mInitialUri);
        } else {
            // Check if we need permissions to load the series.
            if (needPermission(PERMISSIONS[0])) {
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                // Load the file
                new InitialLoadTask().execute(mInitialPath);
            }
        }
    }

    private boolean needPermission(String permission) {
        return isMarshmallow &&
                checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
     public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Load the file
                    new InitialLoadTask().execute(mInitialPath);
                // If the URI was a file, tell the user we need permission.
                } else if ("file".equalsIgnoreCase(mInitialUri.getScheme())) {
                    showSnackbar("Please \"Allow\" permission to read files.");
                // Otherwise, try to load from the URI.
                } else {
                    new UriLoadTask().execute(mInitialUri);
                    // TODO: Notify user that we need permissions to load the entire series.
                    showSnackbar("Read permission denied.  Loading single file.");
                }
                break;
        }
    }

    public List<File> getFileList(File currDir) {
        // If not a directory, get the file's parent directory.
        if (!currDir.isDirectory())
            currDir = currDir.getParentFile();
        // If we don't have permission to read the current directory, return an empty list.
        if (!currDir.canRead())
            return new ArrayList<>();

        // Return a list of the DICOM files
        return Arrays.asList(currDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File path) {
                // Reject directories
                if (path.isDirectory())
                    return false;
                // If we're not showing hidden files, don't accept them.
                if (!mShowFiles && path.isHidden())
                    return false;
                // If there's no file extension filter, accept all files.
                if (mNoFilter)
                    return true;

                // Otherwise, find where the extension starts (i.e. the last '.')
                String filename = path.getName();
                int ext = filename.lastIndexOf(".");

                // No extension found.  May or may not be a DICOM file.
                if (ext == -1)
                    return true;

                // Get the file's extension.
                String extension = filename.substring(ext + 1).toLowerCase(Locale.US);

                // Check if the file has a DICOM (or DCM) extension.
                return extension.equals("dic") || extension.equals("dicom") || extension.equals("dcm");
            }
        }));
    }

    private String loadAttributes(InputStream in) {
        // Read in the DicomObject
        try {
            DicomInputStream dis;
            dis = new DicomInputStream(in);
            mAttributes = dis.getFileMetaInformation();
            if (mAttributes == null)
                return "Missing DICOM file meta information.";
            dis.readAttributes(mAttributes, -1, -1);
            mAttributes.trimToSize();
            dis.close();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("cpb", "Error: " + e.toString());
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("cpb", "Error: " + e.toString());
            return null;
        }
    }

    private class UriLoadTask extends AsyncTask<Uri, Integer, Mat> {
        protected Mat doInBackground(Uri... files) {
            System.gc();
            try {
                mErrorMessage = loadAttributes(getContentResolver().openInputStream(files[0]));
            } catch (FileNotFoundException e) {
                if (e.getMessage().contains("download_unavailable")) {
                    mErrorMessage = "Unable to download file.  Please check your connection.";
                } else {
                    mErrorMessage = "File not found.";
                }
            }

            if (mErrorMessage != null || mAttributes == null) {
                return null;
            }

            int error = DcmUtils.checkDcmImage(mAttributes);
            if (error != 0) {
                // TODO: Return string for snackbar.
                mErrorMessage = getResources().getString(error);
                return null;
            }

            Mat temp = new Mat(mAttributes.getInt(Tag.Rows, 1),
                    mAttributes.getInt(Tag.Columns, 1), CvType.CV_32S);
            temp.put(0, 0, mAttributes.getInts(Tag.PixelData));

            return temp;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        // After loading, adjust display.
        protected void onPostExecute(Mat result) {
            if (result == null) {
                showSnackbar(mErrorMessage != null ? mErrorMessage : "Unable to read file.");
                hideLoading();
                return;
            }

            mNavigationBar.setVisibility(View.INVISIBLE);

            // If this is the first time displaying an image, center it.
            int height 	= result.rows();
            int width 	= result.cols();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            float displayCenterX = displaymetrics.widthPixels/2.0f;
            float displayCenterY = displaymetrics.heightPixels/2.0f;
            mScaleY = Math.min( displaymetrics.widthPixels / (float) width,
                    displaymetrics.heightPixels / (float) height);
            Log.i("cpb", "mScaleY2X: " + mScaleY2X + " mScaleY: " + mScaleY);
            float scaledImageCenterX = (width*mScaleY*mScaleY2X)/2.0f;
            float scaledImageCenterY = (height*mScaleY)/2.0f;
            mFocusX = displayCenterX;
            mFocusY = displayCenterY;
            mMatrix = new Matrix();
            mMatrix.postScale(mScaleY * mScaleY2X, mScaleY);
            mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
            mImageView.setImageMatrix(mMatrix);
            mMat = result;
            updateImage();
            mImageCount++;

            mMultiDetector = new MultiGestureDetector(getApplicationContext(), new MultiListener());
            mMultiDetector.setHorizontalMargin(25.0f * displaymetrics.density);

            // Eliminate the loading symbol
            hideLoading();
            // TODO: Handle multiple URI load
            mMultiProgress.setVisibility(View.INVISIBLE);

        }
    }

    private class InitialLoadTask extends AsyncTask<String, Integer, Mat> {
        protected Mat doInBackground(String... files) {
            // Get the File object for the current file
            mCurrFile = new File(files[0]);
            mFileList = getFileList(mCurrFile);
            Log.i("cpb", "Loading file...");
            System.gc();
            try {
                mErrorMessage = loadAttributes(new FileInputStream(mCurrFile));
            } catch (FileNotFoundException e) {
                mErrorMessage = "File not found.";
            }

            if (mErrorMessage != null || mAttributes == null) {
                return null;
            }

            int error = DcmUtils.checkDcmImage(mAttributes);
            if (error != 0) {
                // TODO: Return string for snackbar.
                mErrorMessage = getResources().getString(error);
                return null;
            }

            Mat temp = new Mat(mAttributes.getInt(Tag.Rows, 1),
                    mAttributes.getInt(Tag.Columns, 1), CvType.CV_32S);
            temp.put(0, 0, mAttributes.getInts(Tag.PixelData));

            // Get the files array = get the files contained
            // in the parent of the current file
            mFilePath = mCurrFile.getParentFile();
            return temp;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        // After loading, adjust display.
        protected void onPostExecute(Mat result) {
            if (result == null) {
                showSnackbar(mErrorMessage != null ? mErrorMessage : "Unable to read file.");
                hideLoading();
                return;
            }

            // If the files array is null or its length is less than 1,
            // there is an error because it must at least contain 1 file:
            // the current file
            if (mFileList == null || mFileList.size() < 1) {
                showSnackbar("This directory contains no DICOM files.");
                return;
            }

            // Get the file index in the array
            int currFileIndex = mFileList.indexOf(mCurrFile);

            // If the current file index is negative
            // or greater or equal to the files array
            // length there is an error
            if (currFileIndex < 0 || currFileIndex >= mFileList.size()) {
                showSnackbar("The image file could not be found.");
                return;
            }

            // Initialize views and navigation bar
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

            // If this is the first time displaying an image, center it.
            int height 	= result.rows();
            int width 	= result.cols();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            float displayCenterX = displaymetrics.widthPixels/2.0f;
            float displayCenterY = displaymetrics.heightPixels/2.0f;
            mScaleY = Math.min( displaymetrics.widthPixels / (float) width,
                    displaymetrics.heightPixels / (float) height);
            Log.i("cpb", "mScaleY2X: " + mScaleY2X + " mScaleY: " + mScaleY);
            float scaledImageCenterX = (width*mScaleY*mScaleY2X)/2.0f;
            float scaledImageCenterY = (height*mScaleY)/2.0f;
            mFocusX = displayCenterX;
            mFocusY = displayCenterY;
            mMatrix = new Matrix();
            mMatrix.postScale(mScaleY * mScaleY2X, mScaleY);
            mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
            mImageView.setImageMatrix(mMatrix);
            mMat = result;
            updateImage();
            mImageCount++;

            mMultiDetector = new MultiGestureDetector(getApplicationContext(), new MultiListener());
            mMultiDetector.setHorizontalMargin(25.0f * displaymetrics.density);

            // Eliminate the loading symbol
            hideLoading();

            if (mMatList == null) {
                mTask = new LoadFilesTask().execute();
            }
        }
    }

    private class LoadFilesTask extends AsyncTask<Void, Integer, List> {
        protected List doInBackground(Void... params) {
            int 	rows 		= mAttributes.getInt(Tag.Rows, 1);
            int 	cols 		= mAttributes.getInt(Tag.Columns, 1);
            String 	studyUID 	= mAttributes.getString(Tag.StudyInstanceUID);
            String 	seriesUID 	= mAttributes.getString(Tag.SeriesInstanceUID);
            int 	instanceNum = mAttributes.getInt(Tag.InstanceNumber, -1);
            double[] spacing 	= mAttributes.getDoubles(Tag.PixelSpacing);
            double[] startPos 	= mAttributes.getDoubles(Tag.ImagePositionPatient);
            int     totalFiles  = mFileList.size();
            // If this is the only file, or this has an invalid instance number... Just return.
            if ((instanceNum < 1) || (totalFiles == 1)) {
                return null;
            }

            mInstance 	= new int[] {Math.max(instanceNum - 1, 0), rows/2, cols/2};
            List<Mat> temp = new ArrayList<>();

            for(int i = 1; i < instanceNum; i++) {
                temp.add(new Mat(rows, cols, CvType.CV_32S));
            }
            temp.add(mMat);
            Attributes currDcm;
            mAttributes = null;
            DicomInputStream dis;
            for(int i = 0; i < totalFiles; i++) {
                publishProgress(i, totalFiles);
                File currFile = mFileList.get(i);
                if (!currFile.equals(mCurrFile)) {
                    currDcm = null;
                    Log.i("cpb", "Attempting GC");
                    System.gc();
                    // Read in the DicomObject
                    try {
                        dis = new DicomInputStream(currFile);
                        currDcm = dis.getFileMetaInformation();
                        dis.readAttributes(currDcm, -1, -1);
                        currDcm.trimToSize();
                        dis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // If the DICOM file was empty, continue.
                    if (currDcm == null)
                        continue;

                    // Check the instance number
                    instanceNum = currDcm.getInt(Tag.InstanceNumber, -1);
                    // Spacing definition moved up
                    if ((spacing != null) &&
                            ((mInstance[0] == 0 && (instanceNum - 1) == 1) || (instanceNum == mInstance[0]))) {
                        double[] nextPos = currDcm.getDoubles(Tag.ImagePositionPatient);
                        // mPixelSpacing{X, Y, Z}
                        mPixelSpacing = new double[] {spacing[1], spacing[0],
                                Math.abs(startPos[2] - nextPos[2])};
                        // mScaleY2X = mScaleSpacing[mAxis]
                        mScaleSpacing = new double[] {spacing[1] / spacing[0],
                                spacing[1] / mPixelSpacing[2], mPixelSpacing[2] / spacing[0]};
                    }
                    // If it's less than 1, continue to the next image.
                    if (instanceNum < 1) {
                        Log.i("cpb", "Skipping file: no valid instance number");
                        continue;
                    }

                    int rows2 = currDcm.getInt(Tag.Rows, 1);
                    int cols2 = currDcm.getInt(Tag.Columns, 1);
                    if (rows != rows2 || cols != cols2) {
                        Log.i("cpb", "Skipping file: row/col mismatch");
                        continue;
                    }

                    if (studyUID.equals(currDcm.getString(Tag.StudyInstanceUID)) &&
                            seriesUID.equals(currDcm.getString(Tag.SeriesInstanceUID))) {

                        // If there isn't enough space in the list, allocate more.
                        while (temp.size() < instanceNum) {
                            temp.add(new Mat(rows, cols, CvType.CV_32S));
                        }

                        temp.get(instanceNum - 1).put(0, 0, currDcm.getInts(Tag.PixelData));
                        mImageCount++;
                    }
                }
            }
            // Display 100% (if only briefly)
            publishProgress(totalFiles, totalFiles);
            mMaxIndex = new int[]{temp.size(), rows, cols};
            return temp;
        }

        protected void onProgressUpdate(Integer... progress) {
            mLoadProgress.setProgress((int) ((progress[0] / (float) progress[1]) * 100));
            mLoadText.setText(progress[0] + "/" + progress[1]);
        }

        // After loading, adjust display.
        protected void onPostExecute(List result) {
            mMultiProgress.setVisibility(View.INVISIBLE);
            if ((result != null) && !result.isEmpty()) {
                mMatList = result;
                // If there's more than one image, display the navbar
                if (mImageCount > 1) {
                    mNavigationBar.setVisibility(View.VISIBLE);
                    // Display the current file index
                    //mCurrentSlide.setText(String.valueOf(mInstance + 1));
                    mIndexSeekBar.setMax(mMatList.size() - 1);
                    mIndexSeekBar.setProgress(mInstance[mAxis]);
                }
            }
        }
    }

    public void cancelLoadTask(boolean force) {
        if (mTask != null)
            mTask.cancel(force);
    }

    /** Called just before activity runs (after onStart). */
    @Override
    protected void onResume() {
        // FIXME: Handle URI or files
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

        super.onResume();
    }

    @Override
     public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            return;
        }

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }
	
	public boolean onTouch(View v, MotionEvent event) {
        // If we haven't loaded the image yet, don't process any touch events
        if (mMat == null)
            return false;
        mMultiDetector.onTouchEvent(event);

        float scaledImageCenterX = (mMat.cols()*mScaleY*mScaleY2X)/2.0f;
        float scaledImageCenterY = (mMat.rows()*mScaleY)/2.0f;
        
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

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        cancelLoadTask(true);
		mFileList 	= null;
		mMat 		= null;
		mMatList 	= null;
		mAttributes = null;
		
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
        // If anything is null, don't save the state.
        if (mFilePath == null) {
            outState = null;
            return;
        }
		// Otherwise, save the current file name
		outState.putString(DcmVar.CURRDIR, mFilePath.getAbsolutePath());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
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
	public synchronized void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
						List<Mat> ListY = new ArrayList<>();
						for (int i = 0; i < mMatList.size(); i++) {
							ListY.add(mMatList.get(i).row(mInstance[mAxis]));
							mMat = new Mat();
						}
						Core.vconcat(ListY, mMat);
						break;
					case DcmVar.SAGGITAL:
						List<Mat> ListX = new ArrayList<>();
						for (int i = 0; i < mMatList.size(); i++) {
							ListX.add(mMatList.get(i).col(mInstance[mAxis]));
							mMat = new Mat();
						}
						Core.hconcat(ListX, mMat);
						break;
					default:
						mAxis = DcmVar.TRANSVERSE;
				    	mIndexSeekBar.setMax(mMaxIndex[mAxis] - 1);
				    	mIndexSeekBar.setProgress(mInstance[mAxis]);
						break;
				}
                updateImage();
			}
			
			// Update the UI
			mCurrentSlide.setText(String.valueOf(mInstance[mAxis] + 1));
			
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
        clearFocus();
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
        clearFocus();
		mInstance[mAxis]--;
		// Changing the progress bar will set the image
		mIndexSeekBar.setProgress(mInstance[mAxis]);
	}
	
	/**
	 * Handle touch on next button.
	 * @param view
	 */
	public synchronized void nextImage(View view) {
        clearFocus();
		mInstance[mAxis]++;
		// Changing the progress bar will set the image
		mIndexSeekBar.setProgress(mInstance[mAxis]);
	}
	
	/**
	 * Update the current image
	 *
	 */
	private void updateImage() {
        if (mMat == null)
            return;
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
			Imgproc.applyColorMap(temp, temp, mCmapSelect);
            //applyColorMap returns a BGR image, but createBitmap expects RGB
            //do a conversion to swap blue and red channels:
			Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGR);
		}
		
		// Set the image
		Bitmap imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(temp, imageBitmap, true);
		mImageView.setImageBitmap(imageBitmap);
	}
    /**
     * Show a snackbar to inform
     * the user about an error.
     * @param message Message of the Snackbar.
     */
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.dcmViewer), message, Snackbar.LENGTH_LONG).show();
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
	
	/**
	 * Spinner's onItemSelected
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
    public void onItemSelected(AdapterView<?> parent, View view, 
            int position, long id) {
        clearFocus();
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(position)
    	switch(parent.getId()) {
    		case R.id.spinner_colormap:
		    	mCmapSelect = position - 1;
		    	Log.i("cpb", "Colormap: " + mCmapSelect + " id: " + id);
		        mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
                //TODO: Remove testing comparison
//                mCmapBox.setBackground(Colormaps.getColormapDrawable(position, mCmapInv));
		        updateImage();
		        break;
    		case R.id.spinner_plane:
                if (mMatList == null || mMaxIndex == null || mMatrix == null)
                    return;
    			mAxis = position;
		    	mScaleY2X = (float) mScaleSpacing[mAxis];
		    	mIndexSeekBar.setMax(mMaxIndex[mAxis] - 1);
		    	mIndexSeekBar.setProgress(mInstance[mAxis]);
		    	Log.i("cpb", "Axis: " + mAxis + " id: " + id + " Scale: " + mScaleY2X);
		    	float 	height 	= mMat.rows();
				float 	width 	= mMat.cols();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				float displayCenterX = displaymetrics.widthPixels/2.0f;
				float displayCenterY = displaymetrics.heightPixels/2.0f;
				mScaleY = Math.min(displaymetrics.widthPixels / (mScaleY2X*width),
						displaymetrics.heightPixels / height);
				mFocusX = displayCenterX;
				mFocusY = displayCenterY;
		    	float scaledImageCenterX = (mMat.cols()*mScaleY*mScaleY2X)/2.0f;
		        float scaledImageCenterY = (mMat.rows()*mScaleY)/2.0f;
		        
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
        clearFocus();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId== EditorInfo.IME_ACTION_DONE){
            int userInput = Integer.parseInt(v.getText().toString());
//            if (userInput < 0)
//                mCurrentSlide.setError("< 0");
//            else if (userInput > mMaxIndex[mAxis])
//                mCurrentSlide.setError("> " + mMaxIndex[mAxis]);
            mInstance[mAxis] = Math.max(0, Math.min(mMaxIndex[mAxis], userInput) - 1);
            v.setText(String.valueOf(mInstance[mAxis] + 1));
            // Changing the progress bar will set the image
            mIndexSeekBar.setProgress(mInstance[mAxis]);
            // Hide the keyboard (clearing focus keeps it open)
            hideKeyboard(v);
            // Clear focus from EditText
            mCurrentSlide.clearFocus();
        }
        return false;
    }

    static public void hideKeyboard(View focusView) {
        InputMethodManager imm = (InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
    }

    /**
     * Called when a CompoundButton is checked
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        clearFocus();
        // Check which toggle button was changed
        switch(buttonView.getId()) {
            case R.id.btn_invert:
                mCmapInv = isChecked;
                ((ColormapArrayAdapter)mCmapSpiner.getAdapter()).invertColormap(mCmapInv);
                mCmapView.setImageContrast(mBrightness, mContrast, mCmapSelect, mCmapInv);
                //TODO: Remove testing comparison
//                mCmapBox.setBackground(Colormaps.getColormapDrawable(mCmapSelect + 1, mCmapInv));
                updateImage();
                break;
        }
    }

    public boolean clearFocus() {
        if (mCurrentSlide.hasFocus()) {
            // Hide the keyboard (clearing focus keeps it open)
            hideKeyboard(mCurrentSlide);
            // Clear focus from EditText
            mCurrentSlide.clearFocus();
            return true;
        }
        return false;
    }

    /**
	 * MultiListener Class
	 * 
	 * @author Christopher Boyd
	 *
	 */
	private class MultiListener extends MultiGestureDetector.SimpleMultiGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return clearFocus();
        }

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// Center the ball on the display:
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			float displayCenterX = displaymetrics.widthPixels/2.0f;
			float displayCenterY = displaymetrics.heightPixels/2.0f;
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