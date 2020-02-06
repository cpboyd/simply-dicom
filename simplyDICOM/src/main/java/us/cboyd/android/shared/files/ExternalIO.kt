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

        when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> {
                // We can read and write the media
                mExternalStorageWritable = true
                mExternalStorageAvailable = mExternalStorageWritable
            }
            Environment.MEDIA_MOUNTED_READ_ONLY -> {
                // We can only read the media
                mExternalStorageAvailable = true
                mExternalStorageWritable = false
            }
            else -> {
                // Something else is wrong. It may be one of many other states,
                //  but all we need to know is we can neither read nor write
                mExternalStorageWritable = false
                mExternalStorageAvailable = mExternalStorageWritable
            }
        }

        return mExternalStorageAvailable
    }

    fun isRoot(directory: File): Boolean {
        return directory.parent == null || directory == Environment.getExternalStorageDirectory()
    }
}
