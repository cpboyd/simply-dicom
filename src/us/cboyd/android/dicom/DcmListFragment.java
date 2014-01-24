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

package us.cboyd.android.dicom;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.shared.NotHidFilter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * DICOM ListFragment
 * 
 * @author Christopher Boyd
 * @version 0.3
 *
 */
public class DcmListFragment extends ListFragment {
    OnFileSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFileSelectedListener {
        /** Called by DcmListFragment when a list item is selected */
        public void onFileSelected(int position, List<String> dirList, File currDir);
        public void onDirectorySelected(File currDir);
    }
	
	/** Current directory. */
	private File mCurrDir;
	private List<String> mDirList = new ArrayList<String>();
	private List<String> mFileList = new ArrayList<String>();
	
	/**  Array adapter for the directory listing. */
	private ArrayAdapter<String> mAdapter;
	
	/** onCreate is called to do initial creation of the fragment. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check if the external storage is available
		if (ExternalIO.checkStorage()) {
			
			if (savedInstanceState != null) {
				String currDir = savedInstanceState.getString(DcmVar.CURRDIR);
				
				mCurrDir = (currDir == null) ? Environment.getExternalStorageDirectory()
						: new File(savedInstanceState.getString(DcmVar.CURRDIR));
			} else {
				// Set the top directory
				mCurrDir = Environment.getExternalStorageDirectory();
			}
			
		}
		
	}
	
	/** onStart makes the fragment visible to the user 
	 * (based on its containing activity being started). */
	@Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        //if (getFragmentManager().findFragmentById(R.id.dcmlist_fragment) != null) {
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFileSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFileSelectedListener");
        }
    }
	
	/** onListItemClick is called when an item in the list is selected. */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String itemName = mAdapter.getItem(position);
		int	   offset 	= 1;		// offset is 1 if in root directory
		
		// Account for the potential '..'
		if (!mCurrDir.equals(Environment.getExternalStorageDirectory())) {
			offset = 0;
			// Go up to the parent directory, if '..'
			if (position == 0) {
				setDir(mCurrDir.getParentFile());
				mCallback.onDirectorySelected(mCurrDir);
				return;
			}
		}
		
		// If it's a directory:
		if (position > mFileList.size() - offset) {
			 setDir(new File(mCurrDir, itemName));
			 mCallback.onDirectorySelected(mCurrDir);
		// Otherwise, display info about the DICOM file selected.
		} else {
	        // Notify the parent activity of selected item
	        mCallback.onFileSelected(position, mFileList, mCurrDir);
	        
	        // Set the item as checked to be highlighted when in two-pane layout
	        getListView().setItemChecked(position, true);
		}
	}
	
	/** Save the directory for reuse later. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the top directory absolute path
		outState.putString(DcmVar.CURRDIR, mCurrDir.getAbsolutePath());
	}
	
	/** Return the current directory. */
	public File getDir() {
		return mCurrDir;
	}
	
	/** Set the current directory and update the list. */
	public void setDir(File directory) {
		mCurrDir = directory;
		setDir();
	}
	
	public void setDir() {
		// If there isn't external storage, do nothing.
		if (!ExternalIO.checkStorage())
			return;
		// If the directory is the external storage directory or there is no parent,
		// stop showing the Home/Up button.
		if (mCurrDir.getParent() == null
				|| mCurrDir.equals(Environment.getExternalStorageDirectory())) {
			ActionBar actionBar = getActivity().getActionBar();
	        actionBar.setHomeButtonEnabled(false);
	        actionBar.setDisplayHomeAsUpEnabled(false);
		} else {
			ActionBar actionBar = getActivity().getActionBar();
	        actionBar.setHomeButtonEnabled(true);
	        actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		// TODO: See if there's a more efficient way to do this.
		/*String[] temp = mCurrDir.list(new SubDirFilter());
		if (temp == null) {
			mDirList = new ArrayList<String>();
		} else {
			mDirList = new ArrayList<String>(Arrays.asList(temp));
		}
		temp = mCurrDir.list(new DcmFileFilter());
		if (temp == null) {
			mFileList = new ArrayList<String>();
		} else {
			mFileList = Arrays.asList(temp);
		}*/
		
		// Clear the directory and file lists
		mDirList.clear();
		mFileList.clear();

		// Loop on all non-hidden files
		for (File path : mCurrDir.listFiles(new NotHidFilter())) {
			// If it's a directory, add it to mDirList.
			if (path.isDirectory()) {
				mDirList.add(path.getName());
			// Otherwise, see if it's a DICOM file.
			} else {
				String filename = path.getName();
				
				// Find where the extension starts (i.e. the last '.')
				int ext = filename.lastIndexOf(".");
				
				// No extension found.  May or may not be a DICOM file.
				if (ext == -1) {
					mFileList.add(filename);
					continue;
				}
					
				// Get the file's extension.
				String extension = filename.substring(ext + 1);
				// Check if the file has a DICOM (or DCM) extension.
				if (extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("dcm")) {
					mFileList.add(filename);
				}
			}
		}
		
		// Sort both lists
		Collections.sort(mDirList, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(mFileList, String.CASE_INSENSITIVE_ORDER);
		
		// Pre-pend mFileList to mDirList
		mDirList.addAll(0, mFileList);
		
		// If not at the root directory, add the ability to go up to the parent directory.
		if (!mCurrDir.equals(Environment.getExternalStorageDirectory()))
			mDirList.add(0, "..");
		
		// We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        // Create an array adapter for the list view, using the files array
		mAdapter = new ArrayAdapter<String>(getActivity(), layout, mDirList);
		setListAdapter(mAdapter);
	}

}