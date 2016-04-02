package us.cboyd.android.dicom;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;

/**
 * Created by chboyd on 3/26/2016.
 */
public class DcmUtils {
    /*
     * Check if this is a DICOM image that we can display.
     * Returns resource ID for an error message or 0 if OK to display.
     * (0 is not a valid resource ID.)
     */
    public static int checkDcmImage(Attributes attributes) {
        String sopClass = attributes.getString(Tag.MediaStorageSOPClassUID);
        String transferSyntax = attributes.getString(Tag.TransferSyntaxUID);
        if ((sopClass != null) && sopClass.equals(UID.MediaStorageDirectoryStorage)) {
            // TODO: DICOMDIR support
            return R.string.err_dicomdir;
            // Null transfer syntax (e.g. "raw" DICOM)
        } else if (transferSyntax == null) {
            return R.string.err_null_transfersyntax;
        } else if (transferSyntax.startsWith("1.2.840.10008.1.2.4.")) {
            // TODO: JPEG support
            return R.string.err_jpeg;
        } else if (transferSyntax.startsWith("1.2.840.10008.1.2")) {
            return 0; // 0 is not a valid resource ID.
        } else {
            return R.string.err_unknown_transfersyntax;
        }
    }
}
