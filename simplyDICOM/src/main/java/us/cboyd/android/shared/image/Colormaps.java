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

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/// N.B. Kotlin interprets hex as a signed number, so this file is intentionally left in Java.
/**
 * Created by Christopher on 3/24/2015.
 */
public class Colormaps {
    /**
     * Attempt to replicate the OpenCV (aka GNU Octave) colormaps with Android drawables.
     * OpenCV uses 64-color arrays.
     * @param position
     * @return
     */
    public static Drawable getColormapDrawable(int position) {
        return getColormapDrawable(position, false);
    }

    public static Drawable getColormapDrawable(int position, boolean invert) {
        switch (position) {
            // Gray
            case 0:
                return getDrawable(new int[] {0xFF000000, 0xFFFFFFFF}, new float[] {0f, 1f}, invert);
            // Autumn
            case 1:
                return getDrawable(new int[] {0xFFFF0000, 0xFFFFFF00}, new float[] {0f, 1f}, invert);
            // Bone
            case 2:
                return getDrawable(new int[] {0xFF000000, 0xFF545474, 0xFFA7C7C7, 0xFFFFFFFF}, new float[] {0f, 3/8f, 6/8f, 1f}, invert);
            // Jet
            case 3:
                return getDrawable(new int[] {0xFF000080, 0xFF0000FF, 0xFF00FFFF, 0xFFFFFF00, 0xFFFF0000, 0xFF800000}, new float[] {0f, 1/8f, 3/8f, 5/8f, 7/8f, 1f}, invert);
            // Winter
            case 4:
                return getDrawable(new int[] {0xFF0000FF, 0xFF00FF80}, new float[] {0f, 1f}, invert);
            // Rainbow
            case 5:
                return getDrawable(new int[] {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF0000FF, 0xFFAA00FF}, new float[] {0f, 2/5f, 3/5f, 4/5f, 1f}, invert);
            // Ocean
            case 6:
                return getDrawable(new int[] {0xFF000000, 0xFF000055, 0xFF0080AA, 0xFFFFFFFF}, new float[] {0f, 1/3f, 2/3f, 1f}, invert);
            // Summer
            case 7:
                return getDrawable(new int[] {0xFF008066, 0xFFFFFF66}, new float[] {0f, 1f}, invert);
            // Spring
            case 8:
                return getDrawable(new int[] {0xFFFF00FF, 0xFFFFFF00}, new float[] {0f, 1f}, invert);
            // Cool
            case 9:
                return getDrawable(new int[] {0xFF00FFFF, 0xFFFF00FF}, new float[] {0f, 1f}, invert);
            // HSV
            case 10:
                // HSV has 6 piece-wise segments
                return getHSVDrawable(7, invert);
            // Pink
            case 11:
                return getPinkDrawable(getPinkPositions(17), invert);
            // Hot
            case 12:
                // New octave:
//                return getDrawable(new int[] {0xFF000000, 0xFFFF0000, 0xFFFFFF00, 0xFFFFFFFF}, new float[] {0f, 3/8f, 6/8f, 1f});
                // Current OCV:
                return getDrawable(new int[] {0xFF000000, 0xFFFF0000, 0xFFFFFF00, 0xFFFFFFFF}, new float[] {0f, 2/5f, 4/5f, 1f}, invert);
            // Undefined
            default:
                return getDrawable(new int[] {Color.TRANSPARENT, Color.TRANSPARENT}, new float[] {0f, 1f}, invert);
        }
    }

    public static float[] getPinkPositions(int num) {
        float[] positions;
        if (num < 4) {
            positions = new float[num];
            for (int i = 1; i < num; i++) {
                positions[i] = (float) i / (num - 1f);
                positions[i] *= positions[i];
            }
        // Since this is a sqrt(), heavily weight the smaller values.
        } else {
            positions = new float[num];
            int alloc = num - 1;
            float[] seg = new float[3];
            // Try, at minimum, to have 0, 3/8, 6/8, and 1.
            seg[0] = (float) Math.min(Math.ceil(alloc * 2f / 3f), alloc-2);
            seg[1] = (float) Math.min(Math.ceil((alloc-seg[0]) * 2f / 3f), alloc-seg[0]-1);
            seg[2] = alloc - seg[0] - seg[1];
            int offset = 0;
            float slope = 3f / 8f;
            for (int j = 0; j < seg.length; j++) {
                if (j > 1)
                    slope = 2f / 8f;
                for (int i = 0; i < seg[j]; i++) {
                    float top = i * i;
                    float bottom = seg[j] * seg[j];
                    positions[i + offset] = top / bottom * slope + j * 3f / 8f;
                }
                offset += seg[j];
            }
            positions[alloc] = 1;
        }
        return positions;
    }

    /**
     * Replicate the OpenCV (aka GNU Octave) Pink colormap with an Android drawable.
     * @param positions Array of floating point positions
     * @return
     */
    public static Drawable getPinkDrawable(float[] positions, boolean invert) {
        // R2: 0 -> 7/12 @ 3/8 -> 1
        // G2: 0 -> 1/4 @ 3/8 -> 5/6 @ 6/8 -> 1
        // B2: 0 -> 1/2 @ 6/8 -> 1
        int num = positions.length;
        int[] colors = new int[num];
        for (int i = 0; i < num; i++) {
            float pos = positions[i];
            float r, g, b;
            if (pos < 3/8f) {
                r = 14/9f * pos;
                g = 2/3f * pos;
                b = 2/3f * pos;
            } else if (pos < 6/8f) {
                r = 1/3f + 2/3f * pos;
                g = 1/4f + 14/9f * (pos - 3/8f);
                b = 2/3f * pos;
            } else {
                r = 1/3f + 2/3f * pos;
                g = 1/3f + 2/3f * pos;
                b = 1/2f + 2f * (pos - 3/4f);
            }
            r = (float) Math.sqrt(r);
            g = (float) Math.sqrt(g);
            b = (float) Math.sqrt(b);

            colors[i] = Color.rgb(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
        }
        return getDrawable(colors, positions, invert);
    }

    /**
     * Replicate the OpenCV (aka GNU Octave) HSV colormap with an Android drawable.
     * @param num Number of values
     * @return
     */
    public static Drawable getHSVDrawable(int num, boolean invert) {
        float[] positions = new float[num];
        int[] colors = new int[num];
        for (int i = 0; i < num; i++) {
            positions[i] = (float) i / (num-1f);
            colors[i] = Color.HSVToColor(new float[] {positions[i]*360f, 1f, 1f});
        }
        return getDrawable(colors, positions, invert);
    }

    public static Drawable getDrawable(final int[] colors, final float[] positions, final boolean invert) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                if (invert)
                    return new LinearGradient(width, 0, 0, 0,
                            colors, positions, Shader.TileMode.REPEAT);
                return new LinearGradient(0, 0, width, 0,
                        colors, positions, Shader.TileMode.REPEAT);
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        return paint;
    }
}
