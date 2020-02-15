package app.boyd.android.dicom.tasks

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import app.boyd.android.dicom.DcmViewer
import app.boyd.android.dicom.checkAttributes
import app.boyd.android.dicom.getMat
import org.dcm4che3.data.Attributes
import org.opencv.core.Mat
import java.lang.ref.WeakReference

class IntentLoadTaskResult(val attributes: Attributes, val uris: List<Uri>, val mat: Mat? = attributes.getMat())

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
            val pair = viewer.checkAttributes(uri)
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

            val pair = viewer.checkAttributes(uri)
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