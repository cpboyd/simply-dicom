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

package us.cboyd.android.shared.image;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import us.cboyd.android.dicom.R;

/**
 * Created by Christopher on 3/11/2015.
 */


/**
 * <p>Wrapper class for an Adapter. Transforms the embedded Adapter instance
 * into a ListAdapter.</p>
 */
public class ColormapArrayAdapter extends ArrayAdapter<String> {
    private int[] mColormapDrawables;
    private boolean mCmapInvert;

    public ColormapArrayAdapter(Context context, int resource, String[] strings)
    {
        super(context, resource, strings);
    }

    public ColormapArrayAdapter(Context context, int resource, int textViewResourceId, String[] strings)
    {
        super(context, resource, textViewResourceId, strings);
    }

    public void invertColormap(boolean invert) {
        mCmapInvert = invert;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        //TODO: Color background
        if (Build.VERSION.SDK_INT < 16)
            view.setBackgroundDrawable(Colormaps.getColormapDrawable(position, mCmapInvert));
        else
            view.setBackground(Colormaps.getColormapDrawable(position, mCmapInvert));
        return view;
    }
}
