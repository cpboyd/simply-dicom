package app.boyd.android.dicom

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.UID
import org.dcm4che3.io.DicomInputStream
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * DICOM helper functions
 *
 * @author Christopher Boyd
 */

/*
 * Check if this is a DICOM image that we can display.
 * Returns resource ID for an error message or 0 if OK to display.
 * (0 is not a valid resource ID.)
 */
@StringRes fun Attributes.checkImage(): Int {
    try {
        val pixels = this.getInts(Tag.PixelData)
        if (pixels == null || pixels.isEmpty()) {
            return R.string.err_null_pixeldata
        }
    } catch (ex: OutOfMemoryError) {
        System.gc()
        return R.string.err_mesg_oom
    }
    val sopClass = this.getString(Tag.MediaStorageSOPClassUID)
    val transferSyntax = this.getString(Tag.TransferSyntaxUID)
    return when {
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

fun InputStream.loadAttributes(): Attributes? {
    try {
        // Read in the DicomObject
        DicomInputStream(this).use {
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

fun Activity.checkAttributes(uri: Uri): Pair<Attributes?, String?> {
    System.gc()

    // Check for multiple files
    // TODO: add error message?
    val attributes: Attributes
    try {
        attributes = this.contentResolver.openInputStream(uri)?.use { it.loadAttributes() } ?: return Pair(null, "Missing DICOM file meta information.")
    } catch (e: FileNotFoundException) {
        val errorMsg = if (e.message?.contains("download_unavailable") == true) {
            "Unable to download file.  Please check your connection."
        } else {
            "File not found."
        }
        return Pair(null, errorMsg)
    } catch (e: Exception) {
        // TODO: Investigate SecurityException
        // All other errors:
        Log.e("cpb", "Error checkAttributes:", e)
        return Pair(null, "Unable to load file.")
    }

    val error = attributes.checkImage()
    if (error != 0) {
        return Pair(null, this.resources.getString(error))
    }
    return Pair(attributes, null)
}

fun Attributes.getMat(): Mat? {
    // TODO: Integrate with checkImage
    val rows = this.getInt(Tag.Rows, 1)
    val cols = this.getInt(Tag.Columns, 1)
    val pix = this.getInts(Tag.PixelData)
    if (pix == null || pix.isEmpty() || cols < 1 || rows < 1) {
        return null
    }
    val mat = Mat(rows, cols, CvType.CV_32S)
    mat.put(0, 0, pix)
    return mat
}

fun Core.MinMaxLocResult.span(): Double {
    return this.maxVal - this.minVal
}

fun Mat.minMaxSpan(): Double {
    // Determine the minMax
    return Core.minMaxLoc(this).span()
}

fun Mat.adjustContrast(span: Double = this.minMaxSpan(), min: Double = 0.0, invert: Boolean = false): Mat {
    val clone = this.clone()

    var gain = 255.0 / span
    var bias = gain * -min
    if (invert) {
        gain *= -1.0
        bias = 255.0 - bias
    }
    // Core.normalize(mat, temp, ImMin, ImMax, Core.NORM_MINMAX)
    clone.convertTo(clone, CvType.CV_8UC1, gain, bias)
    return clone
}

fun Mat.toBitmap(colormap: Int = Imgproc.COLORMAP_BONE): Bitmap {
    // Grayscale is just the lack of a colormap:
    if (colormap >= 0) {
        Imgproc.applyColorMap(this, this, colormap.coerceIn(0, 20))
        // applyColorMap returns a BGR image, but createBitmap expects RGB
        // do a conversion to swap blue and red channels:
        Imgproc.cvtColor(this, this, Imgproc.COLOR_RGB2BGR)
    }

    val cols = this.cols()
    val rows = this.rows()
    val imageBitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(this, imageBitmap, true)
    return imageBitmap
}