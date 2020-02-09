package app.boyd.android.dicom.tasks

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.opencv.core.CvType
import org.opencv.core.Mat
import app.boyd.android.dicom.DcmUtils
import app.boyd.android.dicom.DcmViewer
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.abs

class UrisLoadTaskInput(val attributes: Attributes, val mat: Mat, val uriList: List<Uri>) {
    constructor(result: IntentLoadTaskResult, uriList: List<Uri>) :
            this(result.attributes, result.mat, uriList)
}

class UrisLoadTask internal constructor(context: DcmViewer) : AsyncTask<UrisLoadTaskInput, Pair<Int, Int>, Pair<List<Mat>, List<Int>>?>() {

    private val viewerRef: WeakReference<DcmViewer> = WeakReference(context)

    override fun doInBackground(vararg params: UrisLoadTaskInput): Pair<List<Mat>, List<Int>>? {
        val input = params[0]
        val attributes = input.attributes
        val rows = attributes.getInt(Tag.Rows, 1)
        val cols = attributes.getInt(Tag.Columns, 1)
        val studyUID = attributes.getString(Tag.StudyInstanceUID)
        val seriesUID = attributes.getString(Tag.SeriesInstanceUID)
        val instance = attributes.getInt(Tag.InstanceNumber, -1)
        val spacing = attributes.getDoubles(Tag.PixelSpacing)
        val startPos = attributes.getDoubles(Tag.ImagePositionPatient)

        val uriList = input.uriList
        val totalFiles = uriList.size
        // If this is the only file, or this has an invalid instance number... Just return.
        if (instance < 1 || totalFiles == 1) {
            return null
        }

        val instanceZ = (instance - 1).coerceAtLeast(0)
        val matList = ArrayList<Mat?>()
        val zList = ArrayList<Int?>()
        for (i in 0 until instanceZ) {
            matList.add(null)
            zList.add(null)
        }
        matList.add(input.mat)
        zList.add(instanceZ)

        for ((i, uri) in uriList.withIndex()) {
            publishProgress(i, totalFiles)

            val viewer = viewerRef.get()
            if (viewer == null || viewer.isFinishing) {
                return null
            }

            val pair = DcmUtils.checkAttributes(viewer, uri)
            val currDcm = pair.first ?: continue

            // Check the instance number
            val instanceNum = currDcm.getInt(Tag.InstanceNumber, -1)
            // If it's less than 1, continue to the next image.
            if (instanceNum < 1) {
                Log.i("cpb", "Skipping file: no valid instance number")
                continue
            }

            // Spacing definition moved up
            if (spacing != null && (instanceZ + 2 == instanceNum || instanceNum == instanceZ)) {
                val nextPos = currDcm.getDoubles(Tag.ImagePositionPatient)
                // Get currently loaded attributes
                viewer.setSpacing(spacing, abs(startPos[2] - nextPos[2]))
            }

            val rows2 = currDcm.getInt(Tag.Rows, 1)
            val cols2 = currDcm.getInt(Tag.Columns, 1)
            if (rows != rows2 || cols != cols2) {
                Log.i("cpb", "Skipping file: row/col mismatch")
                continue
            }

            if (studyUID == currDcm.getString(Tag.StudyInstanceUID) && seriesUID == currDcm.getString(Tag.SeriesInstanceUID)) {
                // If there isn't enough space in the list, allocate more.
                while (matList.size < instanceNum) {
                    matList.add(null)
                    zList.add(null)
                }

                val mat = Mat(rows, cols, CvType.CV_32S)
                mat.put(0, 0, currDcm.getInts(Tag.PixelData))

                val z = instanceNum - 1
                matList[z] = mat
                zList[z] = z
            }
        }

        // Display 100% (if only briefly)
        publishProgress(totalFiles, totalFiles)
        return Pair(matList.filterNotNull(), zList.filterNotNull())
    }

    private fun publishProgress(currentIndex: Int, totalFiles: Int) {
        publishProgress(Pair(currentIndex, totalFiles))
    }

    override fun onProgressUpdate(vararg pairs: Pair<Int, Int>?) {
        val progress = pairs.last() ?: return
        // get a reference to the activity if it is still there
        val viewer = viewerRef.get()
        if (viewer == null || viewer.isFinishing) return
        viewer.updateProgress(progress)
    }

    // After loading, adjust display.
    override fun onPostExecute(result: Pair<List<Mat>, List<Int>>?) {
        // get a reference to the activity if it is still there
        val viewer = viewerRef.get()
        if (viewer == null || viewer.isFinishing) return
        viewer.loadResult(result)
    }
}