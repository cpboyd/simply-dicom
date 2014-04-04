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

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * DICOM InfoFragment
 * 
 * @author Christopher Boyd
 * @version 0.3
 *
 */
public class DcmInfoFragment extends Fragment {
    private static String 		mCurrDir 	 = null;
    private static DicomObject 	mDicomObject = null;
    private static int 			mPosition 	 = 0;
    private static ArrayList<String> mFileList 	 = null;
    private static Button 	mLoadButton;
    private static ListView mTagList;
    private static ImageView mImageView;
    private static Resources mRes;
    private static boolean 	mTagInfo 		= false;
    private static boolean 	mDebugMode		= false;
	
	/**  Array adapter for the tag listing. */
	//private ArrayList<String> mTags = new ArrayList<String>();
    private String[] mTags;
	private ArrayAdapter<String> mAdapter 	= null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	Log.i("cpb","DcmInfoFrag: creation");
    	///Causes issue with landscape view
    	/*if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.

        	Log.i("cpb","DcmInfoFrag: null");
            return null;
        }*/

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
        	Log.i("cpb","DcmInfoFrag: savedInstance");
            mPosition	= savedInstanceState.getInt(DcmVar.POSITION);
        	mFileList 	= savedInstanceState.getStringArrayList(DcmVar.FILELIST);
            mCurrDir 	= savedInstanceState.getString(DcmVar.CURRDIR);
        }

    	Log.i("cpb","DcmInfoFrag: view");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dcm_info, container, false);

    	Log.i("cpb","DcmInfoFrag: buttons");
        mLoadButton = (Button) view.findViewById(R.id.bttn_load);
        mLoadButton.setEnabled(false);
        mImageView 	= (ImageView) view.findViewById(R.id.demoImage);
        mTagList 	= (ListView) view.findViewById(R.id.list_tags);

    	Log.i("cpb","DcmInfoFrag: return");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateDicomInfo(args.getInt(DcmVar.POSITION),
            		args.getStringArrayList(DcmVar.FILELIST), args.getString(DcmVar.CURRDIR));
        } else if ((mCurrDir != null) && (mFileList != null) && (mPosition != 0)) {
            // Set article based on saved instance state defined during onCreateView
            updateDicomInfo(mPosition, mFileList, mCurrDir);
        }
    }
    
    public String getDicomFile() {
    	Log.i("cpb", "File DIR: " + mCurrDir);
    	Log.i("cpb", "File ID: " + mPosition + " FileList count: " + mFileList.size());
    	return mCurrDir + '/' + mFileList.get(mPosition);
    }
    
    public List<String> getFileList() {
    	return mFileList;
    }

    public void updateDicomInfo(int position, ArrayList<String> dirList, String currDir) {
    	mPosition 	= position;
    	mFileList 	= dirList;
    	mCurrDir 	= currDir;
    	updateDicomInfo();
    }

    public void updateDicomInfo() {
    	mDicomObject = null;
    	if ((mCurrDir != null) && (mFileList != null)
    			&& (mPosition >= 0) && (mPosition < mFileList.size())) {
	    	try {
				// Read in the DicomObject
				DicomInputStream dis = new DicomInputStream(new FileInputStream(getDicomFile()));
				//mDicomObject = dis.readFileMetaInformation();
				mDicomObject = dis.readDicomObject();
				dis.close();
				
				// Get the SOP Class element
				DicomElement de = mDicomObject.get(Tag.MediaStorageSOPClassUID);
				String SOPClass = "";
				if (de != null)
					SOPClass = de.getString(new SpecificCharacterSet(""), true);
				else
					SOPClass = "null";
				Log.i("cpb", "SOP Class: " + SOPClass);
				
				// TODO: DICOMDIR support
				if (SOPClass.equals(UID.MediaStorageDirectoryStorage)) {
					mLoadButton.setEnabled(false);
				} else {
					mLoadButton.setEnabled(true);
					int rows = mDicomObject.getInt(Tag.Rows);
					int cols = mDicomObject.getInt(Tag.Columns);
					Mat temp = new Mat(rows, cols, CvType.CV_32S);
					temp.put(0, 0, mDicomObject.getInts(Tag.PixelData));
					// [Y, X] or [row, column]
					double[] spacing 	= mDicomObject.getDoubles(Tag.PixelSpacing);
					double 	scaleY2X 	= spacing[1] / spacing[0];

					// Determine the minmax
					Core.MinMaxLocResult minmax = Core.minMaxLoc(temp);
					double 	diff 	= minmax.maxVal - minmax.minVal;
					temp.convertTo(temp, CvType.CV_8UC1, 255.0d / diff, 0);
					
					// Set the image
					Bitmap imageBitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888);
					Log.w("cpb","test3");
					Utils.matToBitmap(temp, imageBitmap, true);
					Log.w("cpb","test4");
					mImageView.setImageBitmap(imageBitmap);
					mImageView.setScaleX((float) scaleY2X);
				}

				// TODO: Add selector for info tag listing
				mRes  = getResources();
				mTags = mRes.getStringArray(R.array.dcmtag_default);
				refreshTagList();
				
			} catch (Exception ex) {
	            Resources res = getResources();
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(res.getString(R.string.err_mesg_read) + mFileList.get(mPosition)
						+ "\n\n" + ex.getMessage())
					   .setTitle(res.getString(R.string.err_title_read))
				       .setCancelable(false)
				       .setPositiveButton(res.getString(R.string.err_ok), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                // Do nothing
				           }
				       });
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				
			}
    	} else {
    		mLoadButton.setEnabled(false);
        }
    }
    
    public void changeMode(boolean extraInfo) {
    	mDebugMode = extraInfo;
    	
    	if (mAdapter != null) {
    		refreshTagList();
    	}
    }

    public void refreshTagList(boolean extraInfo) {
    	mTagInfo = extraInfo;
    	
    	if (mAdapter != null) {
    		refreshTagList();
    	}
    }
    
    public void refreshTagList() {
		// Create an array adapter for the list view, using the files array
        mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_tag, R.id.tagName, mTags) {
    	  	@Override
    	  	public View getView(int position, View convertView, ViewGroup parent) {
    	  		Log.i("cpb","List: 1 : " + mDicomObject.getString(Tag.MediaStorageSOPClassUID));
				View view = super.getView(position, convertView, parent);
				TextView tag1 = (TextView) view.findViewById(R.id.tag1);
				TextView tagOpt = (TextView) view.findViewById(R.id.tagOpt);
				TextView text1 = (TextView) view.findViewById(R.id.tagName);
				TextView text2 = (TextView) view.findViewById(R.id.tagField);
				//int tag = Tag.toTag(mTags.get(position));
				
				int tag = Tag.toTag(mTags[position]);
				tag1.setText("(" + mTags[position].subSequence(0, 4) + ",\n " 
								 + mTags[position].subSequence(4, 8) + ")");

    	  		Log.i("cpb","List: 2");
				String temp = DcmRes.getTag(tag, mRes);
				String[] temp2 = temp.split(";");
				text1.setText(temp2[0]);
				DicomElement de = mDicomObject.get(tag);
				VR dvr = de.vr();
				
				//SpecificCharacterSet for US_ASCII
				SpecificCharacterSet cs = new SpecificCharacterSet("US-ASCII");
				// Only display VR/VM if the option is selected
				if (mTagInfo) {
					tagOpt.setText("VR: " + dvr.toString() + "\nVM: " + de.vm(cs));
				}
				String dStr = de.getString(cs, false);
				if (mDebugMode) {
					text2.setText(dStr);
				}
				else if (dvr == VR.UI) {
					temp = DcmRes.getUID(dStr, mRes);
					temp2 = temp.split(";");
					text2.setText(temp2[0]);
				} else if (dvr == VR.DA){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					try {
						Date vDate = sdf.parse(dStr);
						String dPat = DateFormat.getBestDateTimePattern(
								getResources().getConfiguration().locale, "MMMMdyyyy");
						sdf.applyPattern(dPat);
						text2.setText(sdf.format(vDate));
					} catch (ParseException e) {
						text2.setText(dStr);
					}
				} else if (dvr == VR.DT){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSSSSSZZZ");
					try {
						// Note: The DICOM standard allows for 6 fractional seconds,
						// but Java can only handle 3.
						//
						// Therefore, we must limit the string length.
						// Use concat to re-append the time-zone.
						Date vDate = sdf.parse(
								dStr.substring(0, 18).concat(dStr.substring(21, dStr.length())));
						String dPat = DateFormat.getBestDateTimePattern(
								getResources().getConfiguration().locale, "MMMMdyyyyHHmmssSSSZZZZ");
						sdf.applyPattern(dPat);
						text2.setText(sdf.format(vDate));
					} catch (ParseException e) {
						text2.setText(dStr);
					}
				} else if (dvr == VR.TM){
					SimpleDateFormat sdf = new SimpleDateFormat("HHmmss.SSS");
					try {
						// Note: The DICOM standard allows for 6 fractional seconds,
						// but Java can only handle 3.
						// Therefore, we must limit the string length.
						Date vDate = sdf.parse(dStr.substring(0, 10));
						String dPat = DateFormat.getBestDateTimePattern(
								getResources().getConfiguration().locale, "HHmmssSSS");
						sdf.applyPattern(dPat);
						text2.setText(sdf.format(vDate));
					} catch (ParseException e) {
						text2.setText(dStr);
					}
				} else {
					text2.setText(dStr);
				}
    	  		Log.i("cpb","List: " + Integer.toHexString(tag) + " : " + temp);
				return view;
    	  	}
    	};
		//Set the TagList's ArrayAdapter
		mTagList.setAdapter(mAdapter);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(DcmVar.POSITION, mPosition);
        outState.putStringArrayList(DcmVar.FILELIST, mFileList);
        outState.putString(DcmVar.FILELIST, mCurrDir);
    }
}