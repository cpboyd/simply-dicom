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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Splitter;

import junit.framework.Assert;

import java.io.File;

import us.cboyd.android.shared.files.FileAdapterOptions;
import us.cboyd.android.shared.files.FileRecyclerAdapter;

/**
 * DICOM ListFragment
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmFilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener {
    OnFileSelectedListener mCallback;

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            case 0:
                mIsStorage = true;
                // Highlight for selected
                tab.getIcon().setAlpha(255);
                mRecyclerAdapter.setStorage(true);
                return;
            case 1:
                mIsRoot = true;
                tab.getIcon().setAlpha(255);
                mRecyclerAdapter.setDirectory(mRootDir, getFileAdapterOptions());
                return;
        }
        // Otherwise
        mIsStorage = false;
        mIsRoot = false;
        int dirUp = mTabLayout.getTabCount() - position - 1;
        // Use temp to avoid overwriting mCurrDir (so that we can leave the Tabs as-is)
        File temp = mCurrDir;
        for (int i = 0; i < dirUp; i++) {
            temp = temp.getParentFile();
            // This shouldn't be null:
            if (temp == null) return;
        }

        mRecyclerAdapter.setDirectory(temp, getFileAdapterOptions());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        // Dim icons for unselected
        switch (position) {
            case 0:
                tab.getIcon().setAlpha(128);
                return;
            case 1:
                tab.getIcon().setAlpha(128);
                return;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFileSelectedListener {
        /** Called by DcmListFragment when a list item is selected */
        void onFileSelected(File currFile);
        void setRoot(File currDir);
    }
	
	/** Current directory. */
	private File 	mCurrDir, mRootDir;
	private boolean mIsRoot, mHideStorage, mIsStorage = true;
    private int     mSortSettings;
    private CollapsingToolbarLayout mToolbarLayout;
    private static Toolbar      mToolbar;
    private TabLayout mTabLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FileRecyclerAdapter mRecyclerAdapter;
    private String mAppName;
    private Toolbar.OnMenuItemClickListener mMenuListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFileSelectedListener) activity;
            mMenuListener = (Toolbar.OnMenuItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFileSelectedListener and Toolbar.OnMenuItemClickListener");
        }
    }

    /**
     * The returned view hierarchy <em>must</em> have a ListView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     *
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAppName = getResources().getString(R.string.app_name);
        // Create the list fragment's content view by calling the super method
        final View root = inflater.inflate(R.layout.dcm_list, container, false);

        // Now create a SwipeRefreshLayout to wrap the fragment's content view
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        mRecyclerAdapter = new FileRecyclerAdapter(R.layout.item_file, mCurrDir, mSortSettings, mCallback);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // Toolbar
        mToolbarLayout 	= (CollapsingToolbarLayout) root.findViewById(R.id.collapsingToolbar);
        mToolbar 	= (Toolbar) root.findViewById(R.id.file_toolbar);
        mToolbar.inflateMenu(R.menu.file_list);
        mToolbar.setOnMenuItemClickListener(mMenuListener);
        mToolbar.setTitle(mAppName);

        mTabLayout = (TabLayout) root.findViewById(R.id.tabLayout);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setOnTabSelectedListener(this);
        updateTabs();

        // Now return the SwipeRefreshLayout as this fragment's content view
        return root;
    }
	
	/** onStart makes the fragment visible to the user 
	 * (based on its containing activity being started). */
	@Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
//        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
	
	/** Save the directory for reuse later. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the top directory absolute path
		outState.putString(DcmVar.CURRDIR, mCurrDir.getAbsolutePath());
	}

    public Toolbar getToolbar() { return mToolbar; }

    /** Return whether the current directory is the storage list. */
    public boolean isStorage() { return mIsStorage; }

    /** Return whether the current directory is the root directory. */
    public boolean isRoot() { return mIsRoot; }

    /** Return the current directory. */
    public File getDir() {
        return mCurrDir;
    }

    /** Set the current root (used when loading app.) */
    public boolean setRoot(File directory) {
        if ((directory == null) || !directory.isDirectory() || !directory.canRead())
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
        updateTabs();
	}

    public void updateTabs() {
        if (mTabLayout != null) {
            mTabLayout.removeAllTabs();
            Drawable icon = getResources().getDrawable(R.drawable.ic_storage_white_36dp);
            Assert.assertNotNull(icon);
            icon.setAlpha(128);
            mTabLayout.addTab(mTabLayout.newTab().setIcon(icon), false);
            if (mIsStorage || (mRootDir == null)) {
                mTabLayout.getTabAt(0).select();
                return;
            }

            icon = getResources().getDrawable(R.drawable.ic_sd_storage_white_36dp);
            Assert.assertNotNull(icon);
            icon.setAlpha(128);
            mTabLayout.addTab(mTabLayout.newTab().setIcon(icon), false);
            Iterable<String> tabs = Splitter.on('/').omitEmptyStrings().split(getFolderTitle());
            for (String tab : tabs) {
                mTabLayout.addTab(mTabLayout.newTab().setText(tab), false);
            }
            // Select the last tab
            mTabLayout.getTabAt(mTabLayout.getTabCount() - 1).select();
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
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.setOptions(getFileAdapterOptions());
	}

    public String getFolderTitle() {
        return ((mCurrDir == null) || (mRootDir == null)) ? ""
                : mCurrDir.getAbsolutePath().replaceFirst(mRootDir.getAbsolutePath(), "");
    }

    /**
     * Set whether the {@link SwipeRefreshLayout} should be displaying
     * that it is refreshing or not.
     *
     * @see SwipeRefreshLayout#setRefreshing(boolean)
     */
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void onRefresh() {
        // If on the storage list, list the storage names.
        if (mIsStorage) {
            mRecyclerAdapter.setStorage(true);
            return;
        // Otherwise, list the files & folders within the current directory.
//        } else {
//            mIsRoot = mCurrDir.equals(mRootDir);
        }

        if (mRecyclerAdapter != null)
            mRecyclerAdapter.setOptions(getFileAdapterOptions(), true);
        setRefreshing(false);
    }
}