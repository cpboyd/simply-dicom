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

package us.cboyd.android.shared.files;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import us.cboyd.android.dicom.DcmFilesFragment;
import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */
public class FileRecyclerAdapter extends RecyclerView.Adapter<FileViewHolder> implements SwipeRefreshLayout.OnRefreshListener {
    DcmFilesFragment.OnFileSelectedListener mCallback;

    private List<StorageUtils.StorageInfo> mStorage;
    private ArrayList<FilterFile> mDirList = new ArrayList<>();
    private ArrayList<FilterFile> mFileList = new ArrayList<>();
    private File            mCurrDir;
    private int             mResource, mOptions, mOffset;
    private boolean         mFilesFirst, mIsStorage;

    public FileRecyclerAdapter(int resource, File currDir, int options, DcmFilesFragment.OnFileSelectedListener callback) {
        mCurrDir = currDir;
        if (mCurrDir == null)
            mIsStorage = true;
        mResource = resource;
        mCallback = callback;

        // Set the options and force a refresh
        setOptions(options, true);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mResource, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        ...
        FileViewHolder vh = new FileViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Display the storage list
        if (mIsStorage) {
            holder.setFile(mStorage.get(position), mCallback);
            return;
        }

        // Display the files & folders
        if ((position == 0) && (mOffset == 1)) {
            // Display storage list if at root directory and option is enabled.
            if (getOption(FileAdapterOptions.DIRECTORY_IS_ROOT)) {
                holder.setFile(null, R.drawable.ic_storage_white_36dp,
                        "Storage List", "Display the list of storage options.", mCallback);
            // Display ".." to go up a directory
            } else {
                holder.setFile(mCurrDir.getParentFile(), R.drawable.ic_arrow_back_white_36dp,
                        "..", "Up to parent directory", mCallback);
            }
            // Directory
        } else if ((mFilesFirst && (position > mFileList.size())) ||
                (!mFilesFirst && (position <= mDirList.size()))){
            FilterFile directory;
            if (mFilesFirst)
                directory = mDirList.get(position - mFileList.size() - mOffset);
            else
                directory = mDirList.get(position - mOffset);
            holder.setFile(directory, mCallback);
        // Otherwise, display info about the file.
        } else {
            FilterFile temp;
            if (mFilesFirst)
                temp = mFileList.get(position - mOffset);
            else
                temp = mFileList.get(position - mDirList.size() - mOffset);
            holder.setFile(temp, mCallback);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mIsStorage) ? mStorage.size() : mDirList.size() + mFileList.size() + mOffset;
    }

    // Try to avoid having to reload the lists.
    public void setOptions(int options) {
        setOptions(options, false);
    }

    public void setOptions(int options, boolean forceRefresh) {
        // If no filter options changed, then we can just sort the existing lists.
        boolean sortOnly = ((mOptions & FileAdapterOptions.FILTER_MASK) == (options & FileAdapterOptions.FILTER_MASK));
        mOptions = options;
        // Determine if there's an offset:
        mOffset = !(getOption(FileAdapterOptions.DIRECTORY_IS_ROOT)
                && getOption(FileAdapterOptions.HIDE_STORAGE_LIST)) ? 1 : 0;
        mFilesFirst = getOption(FileAdapterOptions.LIST_FILES_FIRST);

        // If this is the storage list, sorting doesn't apply.
        if (mIsStorage)
            return;

        if (sortOnly && !forceRefresh)
            sortLists();
        else
            onRefresh();
    }

    public boolean getOption(int option) {
        return (mOptions & option) == option;
    }

    @Override
    public void onRefresh() {
        // Refresh the storage list:
        if (mIsStorage) {
            mStorage = StorageUtils.getStorageList();
            notifyDataSetChanged();
            return;
        }

        // Clear the directory and file lists
        mDirList.clear();
        mFileList.clear();

        // Check if we have permission to read the current directory...
        if (mCurrDir.canRead()) {
            boolean showFolders = getOption(FileAdapterOptions.SHOW_HIDDEN_FOLDERS);
            boolean showFiles = getOption(FileAdapterOptions.SHOW_HIDDEN_FILES);
            boolean noFilter = getOption(FileAdapterOptions.NO_FILE_EXT_FILTER);
            // Loop on all files within the directory
            for (File path : mCurrDir.listFiles()) {
                // If it's a directory, add it to mDirList. (Depending on visibility settings.)
                if (path.isDirectory() && (showFolders || !path.isHidden())) {
                    mDirList.add(new FilterFile(path));
                // Otherwise, see if it's a DICOM file. (Depending on visibility settings.)
                } else if (showFiles || !path.isHidden()) {
                    // Find where the extension starts (i.e. the last '.')
                    String filename = path.getName();
                    int ext = filename.lastIndexOf(".");

                    // No extension found.  May or may not be a DICOM file.
                    if (ext == -1) {
                        mFileList.add(new FilterFile(path));
                        continue;
                    }

                    // Get the file's extension.
                    String extension = filename.substring(ext + 1).toLowerCase(Locale.US);
                    // Check if the file has a DICOM (or DCM) extension.
                    if (extension.equals("dic") || extension.equals("dicom") || extension.equals("dcm")) {
                        mFileList.add(new FilterFile(path));
                        continue;
                    }

                    // If the extension filter is disabled, add this file.
                    if (noFilter) {
                        mFileList.add(new FilterFile(path, false));
                    }
                }
            }
        }
        sortLists();
    }

    // Sort the lists
    public void sortLists() {
        boolean reverse = getOption(FileAdapterOptions.SORT_DESCENDING);
        Collections.sort(mDirList, FileComparators.FilterFileName(reverse));
        switch (mOptions >> FileAdapterOptions.OFFSET_SORT_METHOD) {
            case 0:
                Collections.sort(mFileList, FileComparators.FilterFileName(reverse));
                break;
            case 1:
                Collections.sort(mFileList, FileComparators.FilterFileModified(reverse));
                break;
            case 2:
                Collections.sort(mFileList, FileComparators.FilterFileSize(reverse));
                break;
        }
        notifyDataSetChanged();
    }

    public void setStorage(boolean isStorage) {
        mIsStorage = isStorage;
        onRefresh();
    }

    public void setDirectory(File directory) {
        mIsStorage = false;
        mCurrDir = directory;
    }

    public void setDirectory(File directory, int options) {
        setDirectory(directory);
        setOptions(options, true);
    }
}
