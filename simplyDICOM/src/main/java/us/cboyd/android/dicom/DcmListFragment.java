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
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import java.io.File;

import us.cboyd.android.shared.files.FileAdapterOptions;
import us.cboyd.android.shared.files.FileArrayAdapter;
import us.cboyd.android.shared.files.StorageArrayAdapter;
import us.cboyd.android.shared.list.RefreshArrayAdapter;
import us.cboyd.android.shared.list.SwipeRefreshListFragment;

/**
 * DICOM ListFragment
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmListFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener {
    OnFileSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFileSelectedListener {
        /** Called by DcmListFragment when a list item is selected */
        void onFileSelected(File currFile);
        void resetTitleAndSubtitle();
        void setTitleAndSubtitle(String displayName, File currDir);
    }
	
	/** Current directory. */
	private File 	mCurrDir, mRootDir;
	private boolean mIsRoot, mHideStorage, mIsStorage = true;
    private int     mSortSettings;

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
	
	/** onStart makes the fragment visible to the user 
	 * (based on its containing activity being started). */
	@Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setOnRefreshListener(this);
    }
	
	/** onListItemClick is called when an item in the list is selected. */
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
        File file = (File) listView.getAdapter().getItem(position);
        // If temp is null, display the storage list.
        if (file == null) {
            mCallback.resetTitleAndSubtitle();
            return;
        }
        // Set the new root directory to the selected storage option.
        if (mIsStorage) {
            if(setRoot(file))
                mCallback.setTitleAndSubtitle(null, mRootDir);
        // Otherwise, navigate through the folders as usual
        } else {
            // Notify the parent activity of selected item
            mCallback.onFileSelected(file);
        }
        
        // Set the item as checked to be highlighted when in two-pane layout
        //getListView().setItemChecked(position, true);
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

    /** Set the current root (used when loading app.) */
    public boolean setRoot(File directory) {
        if (!directory.isDirectory() || !directory.canRead())
            return false;
        mIsStorage = false;
        mRootDir = directory;
        return true;
    }

    /** Go up a directory (if not at root or on storage list). */
    public boolean navigateUp() {
        if (mIsRoot || mIsStorage)
            return false;
        mCallback.onFileSelected(mCurrDir.getParentFile());
        return true;
    }
	
	/** Set the current directory and update the list. */
	public void setDir(File directory) {
        mIsStorage = (directory == null);
        mCurrDir = directory;
	}
	
	public void refresh() {
        // If on the storage list, list the storage names.
        if (mIsStorage) {
            setListAdapter(new StorageArrayAdapter(getActivity(), R.layout.item_file));
        // Otherwise, list the files & folders within the current directory.
        } else {
            mIsRoot = mCurrDir.equals(mRootDir);

            // Create an array adapter for the list view, using the files array
            setListAdapter(new FileArrayAdapter(getActivity(), R.layout.item_file, mCurrDir, getFileAdapterOptions()));
        }
	}

    public int getFileAdapterOptions() {
        int options = mSortSettings;
        if (mIsRoot)
            options |= FileAdapterOptions.DIRECTORY_IS_ROOT;
        if (mHideStorage)
            options |= FileAdapterOptions.HIDE_STORAGE_LIST;
        return options;
    }
	
	public void setSortOptions(int settings) {
		mSortSettings = settings & FileAdapterOptions.USER_OPTIONS_MASK;
        RefreshArrayAdapter<File> adapter = getRefreshAdapter();
        if (adapter != null)
            adapter.setOptions(getFileAdapterOptions());
	}

    @Override
    public void onRefresh() {
        RefreshArrayAdapter<File> adapter = getRefreshAdapter();
        if (adapter != null)
            adapter.onRefresh();
        setRefreshing(false);
    }

    public RefreshArrayAdapter<File> getRefreshAdapter() {
        return (RefreshArrayAdapter<File>) getListAdapter();
    }
}