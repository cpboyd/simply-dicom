package us.cboyd.android.dicom.tasks

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.opencv.core.CvType
import org.opencv.core.Mat
import us.cboyd.android.dicom.DcmUtils
import us.cboyd.android.dicom.DcmViewer
import java.lang.ref.WeakReference

fun matFrom(attributes: Attributes): Mat {
    val rows = attributes.getInt(Tag.Rows, 1)
    val cols = attributes.getInt(Tag.Columns, 1)
    val mat = Mat(rows, cols, CvType.CV_32S)
    val pix = attributes.getInts(Tag.PixelData)
    // TODO: Show popup error message
    if (pix != null && pix.isNotEmpty()) {
        mat.put(0, 0, pix)
    }
    return mat
}

class IntentLoadTaskResult(val attributes: Attributes, val uris: List<Uri>, val mat: Mat = matFrom(attributes))

class IntentLoadTask internal constructor(context: DcmViewer) : AsyncTask<Intent, Int, IntentLoadTaskResult?>() {

    private val viewerRef: WeakReference<DcmViewer> = WeakReference(context)
    // TODO: Refactor to string resource
    private var errorMsg: String? = null

    override fun doInBackground(vararg intents: Intent): IntentLoadTaskResult? {
        val viewer = viewerRef.get()
        if (viewer == null || viewer.isFinishing) return null

        val intent = intents.firstOrNull() ?: return null
        val multi = intent.clipData

        val uris = ArrayList<Uri>()
        if (multi == null) {
            val uri = intent.data ?: return null
            val pair = DcmUtils.checkAttributes(viewer, uri)
            errorMsg = pair.second
            return pair.first?.let { IntentLoadTaskResult(it, uris) }
        }

        var attr: Attributes? = null
        for (i in 0 until multi.itemCount) {
            val uri = multi.getItemAt(i)?.uri ?: continue

            // If we've already found attributes, just add the rest to a list
            if (attr != null) {
                uris.add(uri)
                continue
            }

            val pair = DcmUtils.checkAttributes(viewer, uri)
            // TODO: Add error list for all files?
            errorMsg = pair.second
            pair.first?.let { attr = it }
        }

        return attr?.let { IntentLoadTaskResult(it, uris) }
    }

    // After loading, adjust display.
    override fun onPostExecute(result: IntentLoadTaskResult?) {
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