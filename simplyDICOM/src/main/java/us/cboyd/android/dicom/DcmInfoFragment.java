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

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import us.cboyd.android.dicom.tag.TagRecyclerAdapter;

/**
 * DICOM InfoFragment
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmInfoFragment extends Fragment {
    private static String 		mCurrFile 	    = null;
    private static Attributes   mAttributes     = null;

    private static View         mHeader, mLoadButton;
    private static TextView     mErrText;
    private static Toolbar mToolbar;
    private static CollapsingToolbarLayout mToolbarLayout;
    private DrawerLayout 	mDrawerLayout;
    private ListView 		mDrawerList;
    private ListAdapter     mDrawerAdapter;
    private ListView.OnItemClickListener mDrawerListener;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private static ImageView    mImageView;
    private static boolean 	    mDebugMode		= false;
    private static LayoutInflater mInflater;
    private Toolbar.OnMenuItemClickListener mMenuListener;
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mMenuListener = (Toolbar.OnMenuItemClickListener) activity;
            mDrawerListener = (ListView.OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Toolbar.OnMenuItemClickListener and ListView.OnItemClickListener");
        }
    }

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
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrFile   = savedInstanceState.getString(DcmVar.DCMFILE);
        }

        // Inflate the layout for this fragment
        mInflater = inflater;
        View view = inflater.inflate(R.layout.dcm_info, container, false);
        // Toolbar
        mToolbarLayout 	= (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.dcm_preview);
        mToolbar.setOnMenuItemClickListener(mMenuListener);
        // Have the recycler view use a linear layout manager
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mAppBar = (AppBarLayout) view.findViewById(R.id.appBar);
        mImageView 	= (ImageView) view.findViewById(R.id.backdrop);
        updateModeIcon();
        // Drawer stuff
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) view.findViewById(R.id.left_drawer);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerOpened(View drawerView) {
//                ListAdapter adapter = mDrawerList.getAdapter();
                // Refresh the drawer list.
//                if (adapter != null)
//                        ((RefreshArrayAdapter<File>) adapter).onRefresh();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if ((mDrawerAdapter != null) && (mDrawerListener != null)) {
            mDrawerList.setAdapter(mDrawerAdapter);
            mDrawerList.setOnItemClickListener(mDrawerListener);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
        mDrawerToggle.syncState();

        //TODO: Add FAB to load image series
        mLoadButton = view.findViewById(R.id.btn_load);
        // Create the ListView header views
//        ListView list = (ListView) view.findViewById(android.R.id.list);
//        View header = inflater.inflate(R.layout.dcm_info_header, list, false);
//        mImageFrame = (FrameLayout) header.findViewById(R.id.imageFrame);
//        mImageView 	= (ImageView) header.findViewById(R.id.demoImage);
//        mErrText 	= (TextView) header.findViewById(R.id.text_fileError);
//        list.addHeaderView(header);
//        list.addFooterView(inflater.inflate(R.layout.fab_list_footer, list, false));
        return view;
    }

    /** onStart makes the fragment visible to the user
     * (based on its containing activity being started). */
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
            updateDicomInfo(args.getString(DcmVar.DCMFILE));
        } else if (mCurrFile != null) {
            // Set article based on saved instance state defined during onCreateView
            updateDicomInfo(mCurrFile);
        }
    }

    public void setDrawerList(ListAdapter adapter) {
        mDrawerAdapter = adapter;
        if (mDrawerList != null) {
            mDrawerList.setAdapter(adapter);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    public void updateDicomInfo(String currFile) {
        mCurrFile   = currFile;
        File file = new File(mCurrFile);
        mToolbarLayout.setTitle(file.getName());
        mToolbar.setSubtitle(file.getParent());
        Resources resources = getResources();

    	if (mCurrFile != null) {
	    	try {
				// Read in the DicomObject
				DicomInputStream dis = new DicomInputStream(new FileInputStream(file));
                mAttributes = dis.getFileMetaInformation();
                // Raw data set (DICOM data without a file format meta-header)
                if (mAttributes == null)
                    mAttributes = new Attributes();
                dis.readAttributes(mAttributes, -1, -1);
                mAttributes.trimToSize();
                mAttributes.internalizeStringValues(true);
				dis.close();

            } catch (IOException ex) {
                showImage(false);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                mErrText.setText(resources.getString(R.string.err_file_read) + mCurrFile
                        + "\n\nIO Exception: " + ex.getMessage() + "\n\n" + sw.toString());
                pw.close();
                return;
            }

            try {
                checkDcmImage();

				// TODO: Add selector for info tag listing
                mRecyclerView.setAdapter(new TagRecyclerAdapter(getActivity(), R.layout.item_tag, mAttributes, R.array.dcmint_default, mDebugMode));

				
			} catch (Exception ex) {
				showImage(false);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                // TODO: Re-add error text
//	            mErrText.setText(resources.getString(R.string.err_file_display) + mCurrFile
//                        + "\n\nException: " + ex.getMessage() + "\n\n" + sw.toString());
                pw.close();
			}
    	} else {
    		showImage(false);
            mErrText.setText(resources.getString(R.string.err_unknown_state));
        }
    }

    public void checkDcmImage() {
        showImage(false);
        int error = DcmUtils.checkDcmImage(mAttributes);
        if (error == 0) {
            loadDcmImage();
            return;
        }
        mErrText.setText(error);
    }

    public void loadDcmImage() {
        int[] pixels = mAttributes.getInts(Tag.PixelData);
        if (pixels == null) {
            mErrText.setText(getResources().getString(R.string.err_null_pixeldata));
        } else {
            // Set the PixelData to null to free memory.
            mAttributes.setNull(Tag.PixelData, VR.OB);
            showImage(true);
            int rows = mAttributes.getInt(Tag.Rows, 1);
            int cols = mAttributes.getInt(Tag.Columns, 1);
            Mat temp = new Mat(rows, cols, CvType.CV_32S);
            temp.put(0, 0, pixels);
            // Set the PixelData to null to free memory.
            pixels = null;
            // [Y, X] or [row, column]
            double[] spacing = mAttributes.getDoubles(Tag.PixelSpacing);
            double scaleY2X = 1.0d;
            if (spacing != null) {
                scaleY2X = spacing[1] / spacing[0];
            }

            // Determine the minmax
            Core.MinMaxLocResult minmax = Core.minMaxLoc(temp);
            double diff = minmax.maxVal - minmax.minVal;
            temp.convertTo(temp, CvType.CV_8UC1, 255.0d / diff, 0);
            // Make the demo image bluish, rather than black and white.
            Imgproc.applyColorMap(temp, temp, Imgproc.COLORMAP_BONE);
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGR);

            // Set the image
            Bitmap imageBitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(temp, imageBitmap, true);
            mImageView.setImageBitmap(imageBitmap);
            mImageView.setScaleX((float) scaleY2X);
            // Limit the height of the image view to display at least two ListView entries (and toolbar).
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            double width = displayMetrics.widthPixels;
            if (width > displayMetrics.heightPixels)
                width *= 0.5d;
            double maxHeight = displayMetrics.heightPixels - (3.0d*72.0d*displayMetrics.density);
            double height = Math.min(maxHeight, width / scaleY2X);
            mImageView.setMaxHeight((int)height); //displayMetrics.heightPixels - (int)(3*72*displayMetrics.density));
        }
    }

    public boolean getMode() { return mDebugMode; }
    
    public void setMode(boolean extraInfo) {
    	mDebugMode = extraInfo;
        updateModeIcon();
        if (mRecyclerView != null) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null)
                ((TagRecyclerAdapter) adapter).setDebugMode(mDebugMode);
        }
    }

    private void updateModeIcon() {
        if (mToolbar != null) {
            Menu menu = mToolbar.getMenu();
            if (menu != null) {
                MenuItem item = mToolbar.getMenu().findItem(R.id.debug_mode);
                if (mDebugMode) {
                    item.setIcon(R.drawable.ic_visibility_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_visibility_off_white_24dp);
                    item.getIcon().setAlpha(128);
                }
            }
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(DcmVar.DCMFILE, mCurrFile);
    }

    public void showImage(boolean isImage) {
    	if (isImage) {
            mLoadButton.setVisibility(View.VISIBLE);
//            mErrText.setVisibility(View.GONE);
        } else {
            mLoadButton.setVisibility(View.GONE);
//            mErrText.setVisibility(View.VISIBLE);
    	}
    }

    // TODO: Remove function (needed for DcmViewer)
    public String getDicomFile() {
        return mCurrFile;
    }
}