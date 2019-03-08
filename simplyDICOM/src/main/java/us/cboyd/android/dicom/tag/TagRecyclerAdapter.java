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

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.SpecificCharacterSet;
import org.dcm4che3.data.VR;

import java.text.SimpleDateFormat;
import java.util.Date;

import us.cboyd.android.dicom.DcmRes;

/**
 * Created by Christopher on 6/1/2015.
 */
public class TagRecyclerAdapter extends RecyclerView.Adapter<TagViewHolder> {
    private Attributes      mAttributes;
    private int             mResource;
    private Resources       mRes;
    private int[]           mTags;
    private boolean         mDebugMode;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TagRecyclerAdapter(Context context, int resource, Attributes attributes, int arrayId, boolean debugMode) {
        mDebugMode = debugMode;

        mAttributes = attributes;
        mResource = resource;
        mRes = context.getResources();
        mTags = mRes.getIntArray(arrayId);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mResource, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        ...
        TagViewHolder vh = new TagViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String temp = String.format("%08X", mTags[position]);
        holder.tagLeft.setText("(" + temp.substring(0, 4) + ",\n " + temp.substring(4, 8) + ")");

        temp = DcmRes.getTag(mTags[position], mRes);
        String[] temp2 = temp.split(";");
        holder.text2.setText(temp2[0]);
        Object de   = mAttributes.getValue(mTags[position]);
        VR dvr      = mAttributes.getVR(mTags[position]);
        // Clear existing data from recycled view
        holder.text1.setText("");
        holder.tagRight.setText("");

        // Only display VR/VM in Debug mode
        if (mDebugMode && dvr != null) {
            holder.tagRight.setText("VR: " + dvr.toString());// + "\nVM: " + dvr.vmOf(de));
        }
        if (de != null) {
            //SpecificCharacterSet for US_ASCII
            SpecificCharacterSet cs = SpecificCharacterSet.DEFAULT;

            String dStr = de.toString();

            // If in Debug mode, just display the string as-is without any special processing.
            if (mDebugMode) {
                holder.text1.setText(dStr);
                // Otherwise, make the fields easier to read.
                // Start by formatting the Person Names.
            } else if (dvr == VR.PN) {
                // Family Name^Given Name^Middle Name^Prefix^Suffix
                temp2 = dStr.split("\\^");
                // May omit '^' for trailing null component groups.
                // Use a switch-case statement to deal with this.
                switch (temp2.length) {
                    // Last, First
                    case 2:
                        temp = temp2[0] + ", " + temp2[1];
                        break;
                    // Last, First Middle
                    case 3:
                        temp = temp2[0] + ", " + temp2[1] + " " + temp2[2];
                        break;
                    // Last, Prefix First Middle
                    case 4:
                        temp = temp2[0] + ", " + temp2[3] + " " + temp2[1] + " " + temp2[2];
                        break;
                    // Last, Prefix First Middle, Suffix
                    case 5:
                        temp = temp2[0] + ", " + temp2[3] + " " + temp2[1] + " " + temp2[2]
                                + ", " + temp2[4];
                        break;
                    // All other cases, just display the unmodified string.
                    default:
                        temp = dStr;
                }
                holder.text1.setText(temp);
                // Translate the known UIDs into plain-text.
            } else if (dvr == VR.UI) {
                temp = DcmRes.getUID(dStr, mRes);
                // Only want the first field containing the plain-text name.
                temp2 = temp.split(";");
                holder.text1.setText(temp2[0]);
                // Format the date according to the current locale.
            } else if (Build.VERSION.SDK_INT >= 18) {
                if (dvr == VR.DA) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    try {
                        Date vDate = sdf.parse(dStr);
                        String dPat = DateFormat.getBestDateTimePattern(
                                mRes.getConfiguration().locale, "MMMMdyyyy");
                        sdf.applyPattern(dPat);
                        holder.text1.setText(sdf.format(vDate));
                    } catch (Exception e) {
                        // If the date string couldn't be parsed, display the unmodified string.
                        holder.text1.setText(dStr);
                    }
                    // Format the date & time according to the current locale.
                } else if (dvr == VR.DT) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSSSSSZZZ");
                    try {
                        // Note: The DICOM standard allows for 6 fractional seconds,
                        // but Java can only handle 3.
                        //
                        // Therefore, we must limit the string length.
                        // Use concat to re-append the time-zone.
                        Date vDate = sdf.parse(
                                dStr.substring(0, 18).concat(dStr.substring(21, dStr.length())));
                        String dPat = DateFormat.getBestDateTimePattern(
                                mRes.getConfiguration().locale, "MMMMdyyyyHHmmssSSSZZZZ");
                        sdf.applyPattern(dPat);
                        holder.text1.setText(sdf.format(vDate));
                    } catch (Exception e) {
                        // If the date string couldn't be parsed, display the unmodified string.
                        holder.text1.setText(dStr);
                    }
                    // Format the time according to the current locale.
                } else if (dvr == VR.TM) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss.SSS");
                    try {
                        // Note: The DICOM standard allows for 6 fractional seconds,
                        // but Java can only handle 3.
                        // Therefore, we must limit the string length.
                        Date vDate = sdf.parse(dStr.substring(0, 10));
                        String dPat = DateFormat.getBestDateTimePattern(
                                mRes.getConfiguration().locale, "HHmmssSSS");
                        sdf.applyPattern(dPat);
                        holder.text1.setText(sdf.format(vDate));
                    } catch (Exception e) {
                        // If the time string couldn't be parsed, display the unmodified string.
                        holder.text1.setText(dStr);
                    }
                } else {
                    holder.text1.setText(dStr);
                }
            } else {
                holder.text1.setText(dStr);
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTags.length;
    }

    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
        notifyDataSetChanged();
    }
}
