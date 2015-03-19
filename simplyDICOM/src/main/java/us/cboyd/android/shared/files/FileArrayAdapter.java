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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */
public class FileArrayAdapter extends ArrayAdapter<File> {

    private ArrayList<File> mDirList = new ArrayList<>();
    private ArrayList<File> mFileList = new ArrayList<>();
    private File            mCurrDir;
    private int             mResource;
    private LayoutInflater  mInflater;
    private boolean         mIsRoot, mFilesFirst, mShowHidden;

    public FileArrayAdapter(Context context, int resource, File currDir, int options)
    {
        super(context, resource);
        mIsRoot = (options & FileAdapterOptions.DIRECTORY_IS_ROOT) == FileAdapterOptions.DIRECTORY_IS_ROOT;
        mFilesFirst = (options & FileAdapterOptions.LIST_FILES_FIRST) == FileAdapterOptions.LIST_FILES_FIRST;
        mShowHidden = (options & FileAdapterOptions.SHOW_HIDDEN_FILES) == FileAdapterOptions.SHOW_HIDDEN_FILES;

        mCurrDir = currDir;
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        refreshList();
    }

    public ArrayList<File> getFileList() {
        return mFileList;
    }

    public void refreshList() {
        // Clear the directory and file lists
        mDirList.clear();
        mFileList.clear();

        // Check if we have permission to read the current directory...
        if (mCurrDir.canRead()) {
            // Loop on all files within the directory
            for (File path : mCurrDir.listFiles()) {
                if (!path.isHidden() || mShowHidden) {
                    // If it's a directory, add it to mDirList.
                    if (path.isDirectory()) {
                        mDirList.add(path);
                        // Otherwise, see if it's a DICOM file.
                    } else {
                        String filename = path.getName();

                        // Find where the extension starts (i.e. the last '.')
                        int ext = filename.lastIndexOf(".");

                        // No extension found.  May or may not be a DICOM file.
                        if (ext == -1) {
                            mFileList.add(path);
                            continue;
                        }

                        // Get the file's extension.
                        String extension = filename.substring(ext + 1);
                        // Check if the file has a DICOM (or DCM) extension.
                        if (extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("dcm")) {
                            mFileList.add(path);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mDirList.size() + mFileList.size() + 1;
    }

    @Override
    public int getPosition(File item) {
        if (item == null)
            return -1;
        //TODO: Fix
        if (item.isDirectory())
            return mDirList.indexOf(item);
        else
            return mFileList.indexOf(item);
    }

    @Override
    public File getItem(int position) {
        // Handle special cases at position 0.
        if (position == 0) {
            if (mIsRoot)
                return null;
            // If not root, return the parent directory.
            return mCurrDir.getParentFile();
        }
        // Directory
        if ((mFilesFirst && (position > mFileList.size())) ||
                (!mFilesFirst && (position <= mDirList.size()))){
            if (mFilesFirst)
                return mDirList.get(position - mFileList.size() - 1);
            else
                return mDirList.get(position - 1);
            // Otherwise, display info about the file.
        } else {
            if (mFilesFirst)
                return mFileList.get(position - 1);
            else
                return mFileList.get(position - mDirList.size() - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        File temp;
        // Display the files & folders
        // Display text for the ".."
        if (position == 0) {
            // If root directory, display different text.
            if (mIsRoot) {
                holder.icon.setImageResource(R.drawable.ic_action_storage);
                holder.fileName.setText("Storage List");
                holder.secondLine.setText("Display the list of storage options.");
            } else {
                holder.icon.setImageResource(R.drawable.ic_action_back);
                holder.fileName.setText("..");
                holder.secondLine.setText("Up to parent directory");
            }
        // Directory
        } else if ((mFilesFirst && (position > mFileList.size())) ||
                (!mFilesFirst && (position <= mDirList.size()))){
            holder.icon.setImageResource(R.drawable.ic_action_collection);
            if (mFilesFirst)
                temp = mDirList.get(position - mFileList.size() - 1);
            else
                temp = mDirList.get(position - 1);
            holder.fileName.setText(temp.getName());
            holder.secondLine.setText("Directory");
            // Otherwise, display info about the file.
        } else {
            holder.icon.setImageResource(R.drawable.ic_action_picture);
            if (mFilesFirst)
                temp = mFileList.get(position - 1);
            else
                temp = mFileList.get(position - mDirList.size() - 1);
            holder.fileName.setText(temp.getName());
            holder.secondLine.setText("File, " + temp.length() / 1024 + " KiB");
        }
        return view;
    }
}
