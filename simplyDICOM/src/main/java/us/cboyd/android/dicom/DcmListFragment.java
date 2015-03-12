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
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.android.shared.StorageUtils;
import us.cboyd.android.shared.SwipeRefreshListFragment;
import us.cboyd.android.shared.adapters.FileArrayAdapter;
import us.cboyd.android.shared.adapters.StorageArrayAdapter;

/**
 * DICOM ListFragment
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmListFragment extends SwipeRefreshListFragment {
    OnFileSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFileSelectedListener {
        /** Called by DcmListFragment when a list item is selected */
        public void onFileSelected(ArrayList<String> dirList, File currDir, File currFile);
        public void onRootSelected(File currDir, String displayName);
        public void onRootSelected(File currDir);
        public void onDirectorySelected(File currDir);
    }
	
	/** Current directory. */
	private File 	mCurrDir, mRootDir;
	private boolean mShowHidden = false;
    private boolean mFilesFirst = false;
    private boolean mIsStorage, mIsRoot;
    private List<StorageUtils.StorageInfo> mStorage;
	
	/**  Array adapter for the directory listing. */
    private StorageArrayAdapter mStorageAdapter;
	private FileArrayAdapter    mAdapter;
	
	/** onCreate is called to do initial creation of the fragment. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mIsStorage = true;
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

    // Set the new root directory to the selected storage option.
    public void setStorage(int position){
//        // Refresh list of storage options
//        if (position == 0)
//            setDir();
        mRootDir = mStorageAdapter.getItem(position).getFile();
        setDir(mRootDir);
        mCallback.onRootSelected(mCurrDir, mStorageAdapter.getItem(position).getDisplayName());
    }
	
	// Allows onListItemClick as well as the DcmBrowser's nav-drawer to set the current selection.
	public void setSelection(int position) {
        // If on the storage list
        if (mIsStorage) {
            setStorage(position);
        // Otherwise, navigate through the folders as usual
        } else {
            // Handle special cases
            if (position == 0) {
                // Go back to the storage list
                if (mCurrDir.equals(mRootDir)) {
                    mIsStorage = true;
                    setDir();
                    mCallback.onRootSelected(null);
                    return;
                // Go up to the parent directory, if '..'
                } else {
                    setDir(mCurrDir.getParentFile());
                    mCallback.onDirectorySelected(mCurrDir);
                    return;
                }
            }

            File currFile = mAdapter.getItem(position);
            // If it's a directory:
            if (currFile.isDirectory()){
                setDir(currFile);
                mCallback.onDirectorySelected(mCurrDir);
                // Otherwise, display info about the DICOM file selected.
            } else {
                // Notify the parent activity of selected item
                ArrayList<String> listCopy = new ArrayList<>();
                for (File item : mAdapter.getFileList()) {
                    listCopy.add(item.getName());
                }
                mCallback.onFileSelected(listCopy, mCurrDir, currFile);
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

    /** Return whether the current directory is the storage list. */
    public boolean isStorage() { return mIsStorage; }

    /** Return whether the current directory is the root directory. */
    public boolean isRoot() { return mIsRoot; }
	
	/** Return whether the specified directory is the root directory. */
	public boolean isRoot(File temp) { return temp.equals(mRootDir); }

    /** Return the current directory. */
    public File getDir() {
        return mCurrDir;
    }

    /** Return the current root. */
    public File getRoot() {
        return mRootDir;
    }
	
	/** Set the current directory and update the list. */
	public void setDir(File directory) {
		mCurrDir = directory;
        if (directory == null) {
            mIsStorage = true;
        } else {
            mIsStorage = false;
        }
		setDir();
	}
	
	public void setDir() {
		// If there isn't external storage, do nothing.
		if (!ExternalIO.checkStorage())
			return;

        // If on the storage list, list the storage names.
        if (mIsStorage) {
            mStorageAdapter = new StorageArrayAdapter(getActivity(), R.layout.item_file);
            setListAdapter(mStorageAdapter);
        // Otherwise, list the files & folders within the current directory.
        } else {
            mIsRoot = mCurrDir.equals(mRootDir);

            // Create an array adapter for the list view, using the files array
            mAdapter = new FileArrayAdapter(getActivity(), R.layout.item_file, mCurrDir, mIsRoot, mFilesFirst, mShowHidden);
            setListAdapter(mAdapter);
        }
	}
	
	public void setHidden(boolean show) {
		mShowHidden = show;
		setDir();
	}

    public void listFilesFirst(boolean checked) {
        mFilesFirst = checked;
        setDir();
    }
}