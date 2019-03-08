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

package us.cboyd.android.dicom.tag;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */
public class TagViewHolder extends RecyclerView.ViewHolder {
    public TextView tagLeft;
    public TextView tagRight;
    public TextView text1;
    public TextView text2;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public TagViewHolder(View itemView) {
        super(itemView);
        // Initialize the holder
        tagLeft    = (TextView) itemView.findViewById(R.id.tagLeft);
        tagRight   = (TextView) itemView.findViewById(R.id.tagRight);
        text1      = (TextView) itemView.findViewById(R.id.text1);
        text2      = (TextView) itemView.findViewById(R.id.text2);
    }
}
