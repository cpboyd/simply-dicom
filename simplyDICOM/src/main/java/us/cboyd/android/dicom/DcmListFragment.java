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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.android.shared.StorageUtils;

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
        public void onFileSelected(int position, ArrayList<String> dirList, File currDir);
        public void onDirectorySelected(File currDir);
    }
	
	/** Current directory. */
	private File 	mCurrDir, mRootDir;
	private boolean mShowHidden = false;
    private boolean mIsStorage, mIsRoot;
	private ArrayList<String> mDirList = new ArrayList<String>();
	private ArrayList<String> mFileList = new ArrayList<String>();
    private List<StorageUtils.StorageInfo> mStorage;
	
	/**  Array adapter for the directory listing. */
	private ArrayAdapter<String> mAdapter;
	
	/** onCreate is called to do initial creation of the fragment. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Load the list of storage options.
        mStorage = StorageUtils.getStorageList();
        mIsStorage = true;

        // If mStorage is empty, do old checks.
        if (mStorage.isEmpty()) {
            mIsStorage = false;
            mRootDir = Environment.getExternalStorageDirectory();
            // Check if the external storage is available
            if (ExternalIO.checkStorage()) {
                if (savedInstanceState != null) {
                    String currDir = savedInstanceState.getString(DcmVar.CURRDIR);

                    mCurrDir = (currDir == null) ? mRootDir : new File(currDir);
                } else {
                    // Set the top directory
                    mCurrDir = mRootDir;
                }
            }
            // TODO: Add entry to mStorage?
        }
	}
	
	/** onStart makes the fragment visible to the user 
	 * (based on its containing activity being started). */
	@Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
		setSelection(position);
        
        // Set the item as checked to be highlighted when in two-pane layout
        //getListView().setItemChecked(position, true);
	}
	
	// Allows onListItemClick as well as the DcmBrowser's nav-drawer to set the current selection.
	public void setSelection(int position) {
		String itemName = mAdapter.getItem(position);

        // If on the storage list
        if (mIsStorage) {
            if (position == 0) {
                // Refresh list of storage options
                setDir();
            } else {
                Log.i("cpb", "Storage path: " + mStorage.get(position - 1).path);
                // Set the new root directory to the selected storage option.
                mRootDir = mStorage.get(position - 1).getFile();
                setDir(mRootDir);
                mCallback.onDirectorySelected(mCurrDir);
                return;
            }
        // Otherwise, navigate through the folders as usual
        } else {
            // Go up to the parent directory, if '..'
            if (position == 0) {
                if (mCurrDir.equals(mRootDir)) {
                    mIsStorage = true;
                    setDir();
                    mCallback.onDirectorySelected(null);
                } else {
                    setDir(mCurrDir.getParentFile());
                    mCallback.onDirectorySelected(mCurrDir);
                    return;
                }
            }

            // If it's a directory:
            if (position > mFileList.size()) {
                setDir(new File(mCurrDir, itemName));
                mCallback.onDirectorySelected(mCurrDir);
                // Otherwise, display info about the DICOM file selected.
            } else {
                // Notify the parent activity of selected item
                ArrayList<String> listCopy = new ArrayList<String>(mFileList);
                mCallback.onFileSelected(position, listCopy, mCurrDir);
            }
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
		mCurrDir    = directory;
        mIsStorage  = false;
		setDir();
	}
	
	public void setDir() {
		// If there isn't external storage, do nothing.
		if (!ExternalIO.checkStorage())
			return;
		// If this fragment is visible:
		if (this.isVisible()) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                // If the directory is the external storage directory or there is no parent,
                // stop showing the Home/Up button.
                if (mIsStorage) {
                    actionBar.setHomeButtonEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(false);
                } else {
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
		}
		
		// Clear the directory and file lists
		mDirList.clear();
		mFileList.clear();

        // If on the storage list, list the storage names.
        if (mIsStorage) {
            // Load the list of storage options.
            mStorage = StorageUtils.getStorageList();
            for (StorageUtils.StorageInfo info : mStorage) {
                mDirList.add(info.getDisplayName());
            }
            mDirList.add(0, "Refresh List");
        // Otherwise, list the files & folders within the current directory.
        } else {
            // Check if we have permission to read the current directory...
            if (mCurrDir.canRead()) {
                // Loop on all files within the directory
                for (File path : mCurrDir.listFiles()) {
                    if (!path.isHidden() || mShowHidden) {
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
                }

                // Sort both lists
                Collections.sort(mDirList, String.CASE_INSENSITIVE_ORDER);
                Collections.sort(mFileList, String.CASE_INSENSITIVE_ORDER);

                // Pre-pend mFileList to mDirList
                mDirList.addAll(0, mFileList);
            }

            // If not at the root directory, add the ability to go up to the parent directory.
            if (!mCurrDir.equals(mRootDir)) {
                mIsRoot = false;
                mDirList.add(0, "..");
            } else {
                mIsRoot = true;
                mDirList.add(0, "Storage List");
            }
        }
		
        // Create an array adapter for the list view, using the files array
        mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_file, R.id.fileName, mDirList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.fileName);
                TextView text2 = (TextView) view.findViewById(R.id.secondLine);

                text1.setText(mDirList.get(position));
                // Display the storage list
                if (mIsStorage) {
                    if (position == 0) {
                        icon.setImageResource(R.drawable.ic_action_refresh);
                        text2.setText("Re-check the available storage options.");
                    } else {
                        icon.setImageResource(R.drawable.ic_action_sd_storage);
                        text2.setText(mStorage.get(position - 1).path);
                    }
                // Display the files & folders
                } else {
                    // Display text for the ".."
                    if (position == 0) {
                        // If root directory, display different text.
                        if (mIsRoot) {
                            icon.setImageResource(R.drawable.ic_action_storage);
                            text2.setText("Display the list of storage options.");
                        } else {
                            icon.setImageResource(R.drawable.ic_action_back);
                            text2.setText("Up to parent directory");
                        }
                    // Directory
                    } else if (position > mFileList.size()) {
                        icon.setImageResource(R.drawable.ic_action_collection);
                        text2.setText("Directory");
                    // Otherwise, display info about the file.
                    } else {
                        icon.setImageResource(R.drawable.ic_action_picture);
                        text2.setText("File, " + new File(mCurrDir, mDirList.get(position)).length() / 1024 + " KB");
                    }
                }
        	    return view;
        	  }
        	};
		//mAdapter = new ArrayAdapter<String>(getActivity(), layout, mDirList);
		setListAdapter(mAdapter);
	}
	
	public void setHidden(boolean show) {
		mShowHidden = show;
		setDir();
	}
	

}