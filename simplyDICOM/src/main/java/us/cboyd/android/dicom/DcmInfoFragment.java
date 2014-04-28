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
import android.widget.LinearLayout;
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
    private static TextView mErrText;
    private static LinearLayout mImLayout;
    private static ImageView mImageView;
    private static Resources mRes;
    private static boolean 	mTagInfo 		= false;
    private static boolean 	mDebugMode		= false;
	
	/**  Array adapter for the tag listing. */
	//private ArrayList<String> mTags = new ArrayList<String>();
    private static String[] mTags;
	private ArrayAdapter<String> mAdapter 	= null;
	

	/** onCreate is called to do initial creation of the fragment. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  	super.onCreate(savedInstanceState);

    	// Retain this fragment across configuration changes.
    	setRetainInstance(true);
  	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	Log.i("cpb","DcmInfoFrag: creation");

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
        	Log.i("cpb","DcmInfoFrag: savedInstance");
            mPosition	= savedInstanceState.getInt(DcmVar.POSITION);
        	mFileList 	= savedInstanceState.getStringArrayList(DcmVar.FILELIST);
            mCurrDir 	= savedInstanceState.getString(DcmVar.CURRDIR);
        }
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dcm_info, container, false);
        
        mErrText 	= (TextView) view.findViewById(R.id.text_fileError);
        
        // ImLayout Elements
        mImLayout 	= (LinearLayout)	view.findViewById(R.id.linL_fileSelection);
        mImageView 	= (ImageView) 		view.findViewById(R.id.demoImage);
        mLoadButton = (Button) 			view.findViewById(R.id.bttn_load);
        mLoadButton.setEnabled(false);
        
        mTagList 	= (ListView) view.findViewById(R.id.list_tags);
        
        // Store a copy of the resources.
		mRes  		= getResources();
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
                SpecificCharacterSet cs = new SpecificCharacterSet("");
				DicomElement de = mDicomObject.get(Tag.MediaStorageSOPClassUID);
				String SOPClass = "";
				if (de != null)
					SOPClass = de.getString(cs, true);
				else
					SOPClass = "null";
				Log.i("cpb", "SOP Class: " + SOPClass);

                // Get the Transfer Syntax element
                de = mDicomObject.get(Tag.TransferSyntaxUID);
                String TransferSyntax = "";
                if (de != null)
                    TransferSyntax = de.getString(cs, true);
                else
                    TransferSyntax = "null";
                Log.i("cpb", "Transfer Syntax: " + TransferSyntax);

                showImage(false);
				if (SOPClass.equals(UID.MediaStorageDirectoryStorage)) {
                    // TODO: DICOMDIR support
		            mErrText.setText(mRes.getString(R.string.err_dicomdir));
				} else if (TransferSyntax.startsWith("1.2.840.10008.1.2.4.")) {
                    // TODO: JPEG support
                    mErrText.setText(mRes.getString(R.string.err_jpeg));
                } else if (TransferSyntax.startsWith("1.2.840.10008.1.2")) {
                    de= mDicomObject.get(Tag.PixelData);
                    if (de == null) {
                        mErrText.setText(mRes.getString(R.string.err_null_image));
                    } else {
                        showImage(true);
                        int rows = mDicomObject.getInt(Tag.Rows);
                        int cols = mDicomObject.getInt(Tag.Columns);
                        Mat temp = new Mat(rows, cols, CvType.CV_32S);
                        temp.put(0, 0, de.getInts(true));
                        // [Y, X] or [row, column]
                        double[] spacing = mDicomObject.getDoubles(Tag.PixelSpacing);
                        double scaleY2X = spacing[1] / spacing[0];

                        // Determine the minmax
                        Core.MinMaxLocResult minmax = Core.minMaxLoc(temp);
                        double diff = minmax.maxVal - minmax.minVal;
                        temp.convertTo(temp, CvType.CV_8UC1, 255.0d / diff, 0);

                        // Set the image
                        Bitmap imageBitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(temp, imageBitmap, true);
                        mImageView.setImageBitmap(imageBitmap);
                        mImageView.setScaleX((float) scaleY2X);
                    }
				}

				// TODO: Add selector for info tag listing
				mTags = mRes.getStringArray(R.array.dcmtag_default);
				refreshTagList();
				
			} catch (Exception ex) {
				showImage(false);
	            mErrText.setText(mRes.getString(R.string.err_file_read) + mFileList.get(mPosition)
						+ "\n\n" + ex.getMessage());
			}
    	} else {
    		showImage(false);
            mErrText.setText(mRes.getString(R.string.err_unknown_state));
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
				View view = super.getView(position, convertView, parent);
				TextView tag1 = (TextView) view.findViewById(R.id.tag1);
				TextView tagOpt = (TextView) view.findViewById(R.id.tagOpt);
				TextView text1 = (TextView) view.findViewById(R.id.tagName);
				TextView text2 = (TextView) view.findViewById(R.id.tagField);
				//int tag = Tag.toTag(mTags.get(position));
				
				int tag = Tag.toTag(mTags[position]);
				tag1.setText("(" + mTags[position].subSequence(0, 4) + ",\n "
                        + mTags[position].subSequence(4, 8) + ")");

				String temp = DcmRes.getTag(tag, mRes);
				String[] temp2 = temp.split(";");
				text1.setText(temp2[0]);
				DicomElement de = mDicomObject.get(tag);
                if (de != null) {
                    VR dvr = de.vr();

                    //SpecificCharacterSet for US_ASCII
                    SpecificCharacterSet cs = new SpecificCharacterSet("US-ASCII");

                    // Only display VR/VM if the option is selected
                    if (mTagInfo) {
                        tagOpt.setText("VR: " + dvr.toString() + "\nVM: " + de.vm(cs));
                    }

                    String dStr = de.getString(cs, false);

                    // If the string is null, display nothing.
                    if (dStr == null) {
                        text2.setText("");

                        // If in Debug mode, just display the string as-is without any special processing.
                    } else if (mDebugMode) {
                        text2.setText(dStr);

                        // Otherwise, make the fields easier to read.
                        // Start by formatting the Person Names.
                    } else if (dvr == VR.PN) {
                        // Family Name^Given Name^Middle Name^Prefix^Suffix
                        temp2 = dStr.split("\\^");
                        // May omit '^' for trailing null component groups.
                        // Use a switch-case statement to deal with this.
                        switch (temp2.length) {
                            // Last, First
                            case 2:
                                temp = temp2[0] + ", " + temp2[1];
                                break;
                            // Last, First Middle
                            case 3:
                                temp = temp2[0] + ", " + temp2[1] + " " + temp2[2];
                                break;
                            // Last, Prefix First Middle
                            case 4:
                                temp = temp2[0] + ", " + temp2[3] + " " + temp2[1] + " " + temp2[2];
                                break;
                            // Last, Prefix First Middle, Suffix
                            case 5:
                                temp = temp2[0] + ", " + temp2[3] + " " + temp2[1] + " " + temp2[2]
                                        + ", " + temp2[4];
                                break;
                            // All other cases, just display the unmodified string.
                            default:
                                temp = dStr;
                        }
                        text2.setText(temp);
                        // Translate the known UIDs into plain-text.
                    } else if (dvr == VR.UI) {
                        temp = DcmRes.getUID(dStr, mRes);
                        // Only want the first field containing the plain-text name.
                        temp2 = temp.split(";");
                        text2.setText(temp2[0]);
                        // Format the date according to the current locale.
                    } else if ((dvr == VR.DA) && (android.os.Build.VERSION.SDK_INT >= 18)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        try {
                            Date vDate = sdf.parse(dStr);
                            String dPat = DateFormat.getBestDateTimePattern(
                                    mRes.getConfiguration().locale, "MMMMdyyyy");
                            sdf.applyPattern(dPat);
                            text2.setText(sdf.format(vDate));
                        } catch (Exception e) {
                            // If the date string couldn't be parsed, display the unmodified string.
                            text2.setText(dStr);
                        }
                        // Format the date & time according to the current locale.
                    } else if ((dvr == VR.DT) && (android.os.Build.VERSION.SDK_INT >= 18)) {
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
                                    mRes.getConfiguration().locale, "MMMMdyyyyHHmmssSSSZZZZ");
                            sdf.applyPattern(dPat);
                            text2.setText(sdf.format(vDate));
                        } catch (Exception e) {
                            // If the date string couldn't be parsed, display the unmodified string.
                            text2.setText(dStr);
                        }
                        // Format the time according to the current locale.
                    } else if ((dvr == VR.TM) && (android.os.Build.VERSION.SDK_INT >= 18)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss.SSS");
                        try {
                            // Note: The DICOM standard allows for 6 fractional seconds,
                            // but Java can only handle 3.
                            // Therefore, we must limit the string length.
                            Date vDate = sdf.parse(dStr.substring(0, 10));
                            String dPat = DateFormat.getBestDateTimePattern(
                                    mRes.getConfiguration().locale, "HHmmssSSS");
                            sdf.applyPattern(dPat);
                            text2.setText(sdf.format(vDate));
                        } catch (Exception e) {
                            // If the time string couldn't be parsed, display the unmodified string.
                            text2.setText(dStr);
                        }
                    } else {
                        text2.setText(dStr);
                    }
                }
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
    
    public void showImage(boolean isImage) {
    	mLoadButton.setEnabled(isImage);
    	if (isImage) {
    		mImLayout.setVisibility(View.VISIBLE);
    		mErrText.setVisibility(View.GONE);
    	} else {
    		mImLayout.setVisibility(View.GONE);
    		mErrText.setVisibility(View.VISIBLE);
    	}
    }
}