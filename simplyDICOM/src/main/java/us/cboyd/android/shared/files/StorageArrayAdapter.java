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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import us.cboyd.android.dicom.R;
import us.cboyd.android.shared.list.RefreshArrayAdapter;

/**
 * Created by Christopher on 3/11/2015.
 */
public class StorageArrayAdapter extends RefreshArrayAdapter<File> {
    private static final int NOT_SELECTED = -1;
    private int selectedPos = NOT_SELECTED;

    // if called with the same position multiple lines it works as toggle
    public void setSelection(int position) {
        if (selectedPos == position) {
            selectedPos = NOT_SELECTED;
        } else {
            selectedPos = position;
        }
        notifyDataSetChanged();
    }

    private List<StorageUtils.StorageInfo> mStorage;
    private int             mResource;
    private LayoutInflater  mInflater;

    public StorageArrayAdapter(Context context, int resource)
    {
        super(context, resource);
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        onRefresh();
    }

    // Refresh the list of storage options.
    @Override
    public void onRefresh() {
        mStorage = StorageUtils.getStorageList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mStorage.size();
    }

    @Override
    public int getPosition(File item) {
        if (item == null)
            return -1;
        //TODO: Implement?
        return 0;
    }

    @Override
    public File getItem(int position) {
        return mStorage.get(position).getFile();
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
            holder = (FileViewHolder)view.getTag();
        }

        // Display the storage list
        holder.fileName.setText(mStorage.get(position).getDisplayName());
        holder.icon.setImageResource(mStorage.get(position).getIcon());
        holder.secondLine.setText(mStorage.get(position).path);
        return view;
    }
}
