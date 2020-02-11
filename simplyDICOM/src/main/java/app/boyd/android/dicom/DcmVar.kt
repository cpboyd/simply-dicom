package app.boyd.android.dicom

/**
 * Simply DICOM: Shared Variables
 *
 * @author Christopher Boyd
 * @version 0.3
 */

object DcmVar {
    /** Bundle Key / Intent Extra  */
    const val DCMFILE = "DCMFile"
}

/** Axis Orientation  */
enum class Axis {
    TRANSVERSE, CORONAL, SAGITTAL
}