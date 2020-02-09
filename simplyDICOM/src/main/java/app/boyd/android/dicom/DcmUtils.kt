package app.boyd.android.dicom

import android.app.Activity
import android.net.Uri
import android.util.Log
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.UID
import org.dcm4che3.io.DicomInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * DICOM helper functions
 *
 * @author Christopher Boyd
 */
object DcmUtils {
    /*
     * Check if this is a DICOM image that we can display.
     * Returns resource ID for an error message or 0 if OK to display.
     * (0 is not a valid resource ID.)
     */
    fun checkDcmImage(attributes: Attributes): Int {
        val pixels = attributes.getInts(Tag.PixelData)
        val sopClass = attributes.getString(Tag.MediaStorageSOPClassUID)
        val transferSyntax = attributes.getString(Tag.TransferSyntaxUID)
        return when {
            pixels == null || pixels.isEmpty() -> R.string.err_null_pixeldata
            // TODO: DICOMDIR support
            sopClass == UID.MediaStorageDirectoryStorage -> R.string.err_dicomdir
            // Null transfer syntax (e.g. "raw" DICOM)
            transferSyntax == null -> R.string.err_null_transfersyntax
            // TODO: JPEG support
            transferSyntax.startsWith("1.2.840.10008.1.2.4.") -> R.string.err_jpeg
            transferSyntax.startsWith("1.2.840.10008.1.2") -> 0 // 0 is not a valid resource ID.
            else -> R.string.err_unknown_transfersyntax
        }
    }

    private fun loadAttributes(input: InputStream): Attributes? {
        try {
            // Read in the DicomObject
            DicomInputStream(input).use {
                val attributes = it.fileMetaInformation ?: return null
                it.readAttributes(attributes, -1, -1)
                attributes.trimToSize()
                return attributes
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            Log.e("cpb", "IO Error loadAttributes:", e)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            Log.e("cpb", "Error loadAttributes:", e)
        }
        return null
    }

    fun checkAttributes(viewer: Activity, uri: Uri): Pair<Attributes?, String?> {
        System.gc()

        // Check for multiple files
        // TODO: add error message?
        val attributes: Attributes
        try {
            attributes = viewer.contentResolver.openInputStream(uri)?.use { loadAttributes(it) } ?: return Pair(null, "Missing DICOM file meta information.")
        } catch (e: FileNotFoundException) {
            val errorMsg = if (e.message?.contains("download_unavailable") == true) {
                "Unable to download file.  Please check your connection."
            } else {
                "File not found."
            }
            return Pair(null, errorMsg)
        }

        val error = checkDcmImage(attributes)
        if (error != 0) {
            return Pair(null, viewer.resources.getString(error))
        }
        return Pair(attributes, null)
    }
}
