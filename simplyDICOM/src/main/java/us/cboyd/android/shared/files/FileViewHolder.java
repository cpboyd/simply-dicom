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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import us.cboyd.android.dicom.DcmFilesFragment;
import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */
public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    DcmFilesFragment.OnFileSelectedListener mCallback;
    public ImageView icon;
    public TextView fileName;
    public TextView secondLine;
    public File file;
    public boolean isStorage;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public FileViewHolder(View itemView) {
        super(itemView);
        // Initialize the holder
        icon        = (ImageView) itemView.findViewById(R.id.icon);
        fileName    = (TextView) itemView.findViewById(R.id.text1);
        secondLine  = (TextView) itemView.findViewById(R.id.text2);
        itemView.setOnClickListener(this);
    }

    public void setFile(StorageUtils.StorageInfo info, DcmFilesFragment.OnFileSelectedListener callback) {
        fileName.setText(info.getDisplayName());
        icon.setImageResource(info.getIcon());
        secondLine.setText(info.path);
        file = info.getFile();
        isStorage = true;
        mCallback = callback;
    }

    public void setFile(File currFile, int iconRes, String text1, String text2, DcmFilesFragment.OnFileSelectedListener callback) {
        icon.setImageResource(iconRes);
        fileName.setText(text1);
        secondLine.setText(text2);
        file = currFile;
        isStorage = false;
        mCallback = callback;
    }

    public void setFile(File currFile, int iconRes, int text1, int text2, DcmFilesFragment.OnFileSelectedListener callback) {
        icon.setImageResource(iconRes);
        fileName.setText(text1);
        secondLine.setText(text2);
        file = currFile;
        isStorage = false;
        mCallback = callback;
    }

    public void setFile(FilterFile filterFile, DcmFilesFragment.OnFileSelectedListener callback) {
        if (filterFile.file.isDirectory()) {
            // Choose which icon to use.
            if (filterFile.isHidden)
                icon.setImageResource(R.drawable.ic_folder_visible_off_white_36dp);
            else
                icon.setImageResource(R.drawable.ic_folder_open_white_36dp);
            secondLine.setText(R.string.directory);

            String text = "+";
            if (filterFile.file.canExecute())
                text += "x";
            if (filterFile.file.canRead())
                text += "r";
            if (filterFile.file.canWrite())
                text += "w";
            secondLine.setText(secondLine.getText() + text);
        } else {
            // Choose which icon to use.
            if (!filterFile.match)
                icon.setImageResource(R.drawable.ic_file_filter_off_white_36dp);
            else if (filterFile.isHidden)
                icon.setImageResource(R.drawable.ic_file_visible_off_white_36dp);
            else
                icon.setImageResource(R.drawable.ic_image_white_36dp);
            String separator = " | ";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            String text = separator + "\u200e" // ensure next part is LTR
                    + filterFile.size / 1024 + " KiB"
                    + separator + sdf.format(new Date(filterFile.lastModified));
            secondLine.setText(R.string.file);
            secondLine.setText(secondLine.getText() + text);
        }
        fileName.setText(filterFile.file.getName());
        file = filterFile.file;
        isStorage = false;
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        // If temp is null, display the storage list.
        // Set the new root directory to the selected storage option.
        if ((file == null) || isStorage) {
            mCallback.setRoot(file);
            return;
        }
        // Otherwise, navigate through the folders as usual
        // Notify the parent activity of selected item
        mCallback.onFileSelected(file);

    // Set the item as checked to be highlighted when in two-pane layout
    //getListView().setItemChecked(position, true);
    }
}
