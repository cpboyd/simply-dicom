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

/**
 * Created by Christopher on 3/12/2015.
 */
public class FileAdapterOptions {
    /**
     * Mask for just the user options
     */
    public static final int USER_OPTIONS_MASK   = 0xFC;
    /**
     * Mask for just the filter options
     */
    public static final int FILTER_MASK   = 0x1C;
    /**
     * Mask for just the sort options
     */
    public static final int SORT_MASK   = 0xE0;
    /**
     * Inform the FileArrayAdapter this is the root directory for the selected storage.
     */
    public static final int DIRECTORY_IS_ROOT   = 0x1;
    /**
     * Hide the option to go to a list of storage options (if root).
     */
    public static final int HIDE_STORAGE_LIST = 0x2;
    /**
     * Disable the FileArrayAdapter's DICOM file extension filter.
     */
    public static final int NO_FILE_EXT_FILTER  = 0x4;
    /**
     * Show hidden files.
     */
    public static final int SHOW_HIDDEN_FILES   = 0x8;
    /**
     * Show hidden directories.
     */
    public static final int SHOW_HIDDEN_FOLDERS = 0x10;
    /**
     * List files before directories.
     */
    public static final int LIST_FILES_FIRST    = 0x20;
    /**
     * Sort descending.
     */
    public static final int SORT_DESCENDING = 0x40;
    /**
     * Bit offset for sort method integer value.
     */
    public static final int OFFSET_SORT_METHOD  = 7;

    public static int getInt(int values, int option) {
        switch (option) {
            case DIRECTORY_IS_ROOT:
                return values & 0x1;
            case HIDE_STORAGE_LIST:
                return (values >> 1) & 0x1;
            case LIST_FILES_FIRST:
                return (values >> 2) & 0x1;
            case NO_FILE_EXT_FILTER:
                return (values >> 3) & 0x1;
            case SHOW_HIDDEN_FILES:
                return (values >> 4) & 0x1;
            case SHOW_HIDDEN_FOLDERS:
                return (values >> 5) & 0x1;
            case SORT_DESCENDING:
                return (values >> 6) & 0x1;
            default:
                return 0;
        }
    }
}
