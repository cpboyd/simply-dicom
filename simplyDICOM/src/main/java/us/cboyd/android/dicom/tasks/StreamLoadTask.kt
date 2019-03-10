package us.cboyd.android.dicom.tasks

import android.os.AsyncTask
import android.util.Log
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import org.opencv.core.CvType
import org.opencv.core.Mat
import us.cboyd.android.dicom.DcmUtils
import us.cboyd.android.dicom.DcmViewer
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

fun matFrom(attributes: Attributes): Mat {
    val mat = Mat(attributes.getInt(Tag.Rows, 1),
            attributes.getInt(Tag.Columns, 1), CvType.CV_32S)
    mat.put(0, 0, attributes.getInts(Tag.PixelData))
    return mat
}

class StreamLoadTaskResult(val attributes: Attributes, val mat: Mat = matFrom(attributes))

class StreamLoadTask internal constructor(context: DcmViewer) : AsyncTask<InputStream, Int, StreamLoadTaskResult>() {

    private val viewerRef: WeakReference<DcmViewer> = WeakReference(context)
    // TODO: Refactor to string resource
    private var errorMsg: String? = null

    private fun loadAttributes(input: InputStream?): Attributes? {
        try {
            // Read in the DicomObject
            DicomInputStream(input).use { dis ->
                val attributes = dis.fileMetaInformation ?: return null
                dis.readAttributes(attributes, -1, -1)
                attributes.trimToSize()
                return attributes
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            Log.e("cpb", "Error loadAttributes: $e")
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            Log.e("cpb", "Error loadAttributes: $e")
        }
        return null
    }

    override fun doInBackground(vararg files: InputStream): StreamLoadTaskResult? {
        System.gc()

        val attributes = loadAttributes(files[0])

        if (attributes == null) {
            errorMsg = "Missing DICOM file meta information."
            return null
        }

        val error = DcmUtils.checkDcmImage(attributes)
        if (error != 0) {
            val viewer = viewerRef.get()
            if (viewer == null || viewer.isFinishing) return null
            errorMsg = viewer.resources.getString(error)
            return null
        }

        return StreamLoadTaskResult(attributes)
    }

    // After loading, adjust display.
    override fun onPostExecute(result: StreamLoadTaskResult?) {
        // get a reference to the activity if it is still there
        val viewer = viewerRef.get()
        if (viewer == null || viewer.isFinishing) return

        if (result == null) {
            viewer.showLoadError(errorMsg)
            return
        }

        viewer.loadResult(result)
    }
}