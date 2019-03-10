package us.cboyd.android.dicom

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.UID

/**
 * Created by chboyd on 3/26/2016.
 */
object DcmUtils {
    /*
     * Check if this is a DICOM image that we can display.
     * Returns resource ID for an error message or 0 if OK to display.
     * (0 is not a valid resource ID.)
     */
    fun checkDcmImage(attributes: Attributes): Int {
        val sopClass = attributes.getString(Tag.MediaStorageSOPClassUID)
        val transferSyntax = attributes.getString(Tag.TransferSyntaxUID)
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
}
