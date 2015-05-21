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

import java.util.Comparator;

/**
 * Created by Christopher on 5/14/2015.
 */
public class FileComparators {
    public static Comparator<FilterFile> FilterFileName() {
        return FilterFileName(false);
    }
    public static Comparator<FilterFile> FilterFileName(final boolean reverse) {
        return new Comparator<FilterFile>(){
            public int compare(FilterFile f1, FilterFile f2)
            {
                int value = f1.file.getName().compareToIgnoreCase(f2.file.getName());
                return reverse ? -value : value;
            }
        };
    }

    public static Comparator<FilterFile> FilterFileModified() {
        return FilterFileModified(false);
    }
    public static Comparator<FilterFile> FilterFileModified(final boolean reverse) {
        return new Comparator<FilterFile>(){
            public int compare(FilterFile f1, FilterFile f2)
            {
                int value = f1.lastModified < f2.lastModified ? -1 : (f1.lastModified == f2.lastModified ? 0 : 1);
                return reverse ? -value : value;
            }
        };
    }


    public static Comparator<FilterFile> FilterFileSize() {
        return FilterFileSize(false);
    }
    public static Comparator<FilterFile> FilterFileSize(final boolean reverse) {
        return new Comparator<FilterFile>(){
            public int compare(FilterFile f1, FilterFile f2)
            {
                int value = f1.size < f2.size ? -1 : (f1.size == f2.size ? 0 : 1);
                return reverse ? -value : value;
            }
        };
    }
}
