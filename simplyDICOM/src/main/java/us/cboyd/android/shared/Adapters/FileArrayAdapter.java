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

package us.cboyd.android.shared.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */
public class FileArrayAdapter extends ArrayAdapter<String> {

    private Context         mContext;
    private List<String>    mFileList;
    private int             mTextViewResId;
    private LayoutInflater  mInflater;

    public FileArrayAdapter(Context context, int textViewResourceId, List<String> contacts)
    {
        super(context, textViewResourceId);
        mContext = context;
        mFileList = contacts;
        mTextViewResId = textViewResourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        FileViewHolder holder = new FileViewHolder();
        if (view == null) {
            // This a new view, so we inflate the layout
            view = mInflater.inflate(mTextViewResId, parent, false);
            // Initialize the holder
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.fileName = (TextView) view.findViewById(R.id.fileName);
            holder.secondLine = (TextView) view.findViewById(R.id.secondLine);
        } else {
            holder = (FileViewHolder)view.getTag();
        }

        holder.fileName.setText(mDirList.get(position));
        // Display the storage list
        if (mIsStorage) {
            if (position == 0) {
                holder.icon.setImageResource(R.drawable.ic_action_refresh);
                holder.secondLine.setText("Re-check the available storage options.");
            } else {
                holder.icon.setImageResource(mStorage.get(position - 1).getIcon());
                holder.secondLine.setText(mStorage.get(position - 1).path);
            }
            // Display the files & folders
        } else {
            // Display text for the ".."
            if (position == 0) {
                // If root directory, display different text.
                if (mIsRoot) {
                    holder.icon.setImageResource(R.drawable.ic_action_storage);
                    holder.secondLine.setText("Display the list of storage options.");
                } else {
                    holder.icon.setImageResource(R.drawable.ic_action_back);
                    holder.secondLine.setText("Up to parent directory");
                }
                // Directory
            } else if ((mFilesFirst && (position > mFileList.size())) ||
                    (!mFilesFirst && (position < mDirList.size()-mFileList.size()))){
                holder.icon.setImageResource(R.drawable.ic_action_collection);
                holder.secondLine.setText("Directory");
                // Otherwise, display info about the file.
            } else {
                holder.icon.setImageResource(R.drawable.ic_action_picture);
                holder.secondLine.setText("File, " + new File(mCurrDir, mDirList.get(position)).length() / 1024 + " KiB");
            }
        }
        return view;
    }

    public class FileViewHolder
    {
        ImageView icon;
        TextView fileName;
        TextView secondLine;
    }
}
