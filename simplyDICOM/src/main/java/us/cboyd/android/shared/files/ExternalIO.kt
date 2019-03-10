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

package us.cboyd.android.shared.files

import android.os.Environment

import java.io.File

object ExternalIO {
    var mExternalStorageAvailable = false
    var mExternalStorageWritable = false


    // If storage isn't available, return false:
    val isWriteable: Boolean
        get() = if (checkStorage()) {
            mExternalStorageWritable
        } else false

    fun checkStorage(): Boolean {
        /* Code for checkStorage taken directly from:
		 * http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
		 */
        val state = Environment.getExternalStorageState()

        if (Environment.MEDIA_MOUNTED == state) {
            // We can read and write the media
            mExternalStorageWritable = true
            mExternalStorageAvailable = mExternalStorageWritable
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            // We can only read the media
            mExternalStorageAvailable = true
            mExternalStorageWritable = false
        } else {
            // Something else is wrong. It may be one of many other states,
            //  but all we need to know is we can neither read nor write
            mExternalStorageWritable = false
            mExternalStorageAvailable = mExternalStorageWritable
        }

        return mExternalStorageAvailable
    }

    fun isRoot(directory: File): Boolean {
        return directory.parent == null || directory == Environment.getExternalStorageDirectory()
    }
}
