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

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import us.cboyd.android.dicom.R;
import us.cboyd.android.shared.list.RefreshArrayAdapter;

/**
 * Created by Christopher on 3/11/2015.
 */
public class FileArrayAdapter extends RefreshArrayAdapter<File> {

    private ArrayList<FilterFile> mDirList = new ArrayList<>();
    private ArrayList<FilterFile> mFileList = new ArrayList<>();
    private File            mCurrDir;
    private int             mResource, mOptions, mOffset;
    private Resources       mRes;
    private LayoutInflater  mInflater;
    private boolean         mFilesFirst;

    public FileArrayAdapter(Context context, int resource, File currDir, int options)
    {
        super(context, resource);
        mCurrDir = currDir;
        mResource = resource;
        mRes = context.getResources();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Set the options and force a refresh
        setOptions(options, true);
    }

    @Override
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

        if (sortOnly & !forceRefresh)
            sortLists();
        else
            onRefresh();
    }

    public boolean getOption(int option) {
        return (mOptions & option) == option;
    }

    @Override
    public void onRefresh() {
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

    @Override
    public int getCount() {
        return mDirList.size() + mFileList.size() + mOffset;
    }

    @Override
    public File getItem(int position) {
        // Handle special cases at position 0.
        if ((position == 0) && (mOffset != 0)) {
            // If at root directory, return null
            if (getOption(FileAdapterOptions.DIRECTORY_IS_ROOT))
                return null;
            // If not root, return the parent directory.
            return mCurrDir.getParentFile();
        }
        // Directory
        if ((mFilesFirst && (position > mFileList.size())) ||
                (!mFilesFirst && (position <= mDirList.size()))){
            if (mFilesFirst)
                return mDirList.get(position - mFileList.size() - mOffset).file;
            else
                return mDirList.get(position - mOffset).file;
            // Otherwise, display info about the file.
        } else {
            if (mFilesFirst)
                return mFileList.get(position - mOffset).file;
            else
                return mFileList.get(position - mDirList.size() - mOffset).file;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        FileViewHolder holder;
        if (view == null) {
            // This a new view, so we inflate the layout
            view = mInflater.inflate(mResource, parent, false);
            // Initialize the holder
            holder = new FileViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.fileName = (TextView) view.findViewById(R.id.text1);
            holder.secondLine = (TextView) view.findViewById(R.id.text2);
            // Store the ViewHolder for later
            view.setTag(holder);
        } else {
            // Retrieve the ViewHolder
            holder = (FileViewHolder) view.getTag();
        }

        // Display the files & folders
        if ((position == 0) && (mOffset == 1)) {
            // Display storage list if at root directory and option is enabled.
            if (getOption(FileAdapterOptions.DIRECTORY_IS_ROOT)) {
                holder.icon.setImageResource(R.drawable.ic_storage_white_36dp);
                holder.fileName.setText("Storage List");
                holder.secondLine.setText("Display the list of storage options.");
            // Display ".." to go up a directory
            } else {
                holder.icon.setImageResource(R.drawable.ic_arrow_back_white_36dp);
                holder.fileName.setText("..");
                holder.secondLine.setText("Up to parent directory");
            }
        // Directory
        } else if ((mFilesFirst && (position > mFileList.size())) ||
                (!mFilesFirst && (position <= mDirList.size()))){
            FilterFile directory;
            if (mFilesFirst)
                directory = mDirList.get(position - mFileList.size() - mOffset);
            else
                directory = mDirList.get(position - mOffset);
            // Choose which icon to use.
            if (directory.isHidden)
                holder.icon.setImageResource(R.drawable.ic_folder_visible_off_white_36dp);
            else
                holder.icon.setImageResource(R.drawable.ic_folder_open_white_36dp);
            holder.fileName.setText(directory.file.getName());
            holder.secondLine.setText(mRes.getText(R.string.directory));
        // Otherwise, display info about the file.
        } else {
            FilterFile temp;
            if (mFilesFirst)
                temp = mFileList.get(position - mOffset);
            else
                temp = mFileList.get(position - mDirList.size() - mOffset);
            // Choose which icon to use.
            if (!temp.match)
                holder.icon.setImageResource(R.drawable.ic_file_filter_off_white_36dp);
            else if (temp.isHidden)
                holder.icon.setImageResource(R.drawable.ic_file_visible_off_white_36dp);
            else
                holder.icon.setImageResource(R.drawable.ic_image_white_36dp);
            holder.fileName.setText(temp.file.getName());
            String separator = " | ";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            String text = mRes.getText(R.string.file) + separator + "\u200e" // ensure next part is LTR
                    + temp.size / 1024 + " KiB"
                    + separator + sdf.format(new Date(temp.lastModified));
            holder.secondLine.setText(text);
        }
        return view;
    }
}
