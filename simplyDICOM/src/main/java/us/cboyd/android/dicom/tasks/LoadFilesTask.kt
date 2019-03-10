package us.cboyd.android.dicom.tasks

import android.os.AsyncTask
import android.util.Log
import android.view.View
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import org.opencv.core.CvType
import org.opencv.core.Mat
import us.cboyd.android.dicom.DcmViewer
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.ArrayList

class LoadFilesTaskInput(val attributes: Attributes, val mat: Mat, val currentFile: File, val fileList: List<File>) {
    constructor(result: StreamLoadTaskResult, currentFile: File, fileList: List<File>) :
            this(result.attributes, result.mat, currentFile, fileList)
}

class LoadFilesTask internal constructor(context: DcmViewer) : AsyncTask<LoadFilesTaskInput, Pair<Int, Int>, List<Mat>>() {

    private val viewerRef: WeakReference<DcmViewer> = WeakReference(context)

    override fun doInBackground(vararg params: LoadFilesTaskInput): List<Mat>? {
        val input = params[0]
        val attributes = input.attributes
        val rows = attributes.getInt(Tag.Rows, 1)
        val cols = attributes.getInt(Tag.Columns, 1)
        val studyUID = attributes.getString(Tag.StudyInstanceUID)
        val seriesUID = attributes.getString(Tag.SeriesInstanceUID)
        val instance = attributes.getInt(Tag.InstanceNumber, -1)
        val spacing = attributes.getDoubles(Tag.PixelSpacing)
        val startPos = attributes.getDoubles(Tag.ImagePositionPatient)

        val fileList = input.fileList
        val totalFiles = fileList.size
        // If this is the only file, or this has an invalid instance number... Just return.
        if (instance < 1 || totalFiles == 1) {
            return null
        }

        val instanceZ = Math.max(instance - 1, 0)

        val temp = ArrayList<Mat?>(instance)
        temp[instanceZ] = input.mat

        for (i in 0 until totalFiles) {
            publishProgress(i, totalFiles)
            val currFile = fileList[i]
            if (currFile != input.currentFile) {
                var currDcm: Attributes? = null
                Log.i("cpb", "Attempting GC")
                System.gc()
                // Read in the DicomObject
                try {
                    val dis = DicomInputStream(currFile)
                    currDcm = dis.fileMetaInformation
                    dis.readAttributes(currDcm!!, -1, -1)
                    currDcm.trimToSize()
                    dis.close()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

                // If the DICOM file was empty, continue.
                if (currDcm == null)
                    continue

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
                    val viewer = viewerRef.get()
                    if (viewer == null || viewer.isFinishing) return null
                    viewer.setSpacing(spacing, Math.abs(startPos[2] - nextPos[2]))
                }

                val rows2 = currDcm.getInt(Tag.Rows, 1)
                val cols2 = currDcm.getInt(Tag.Columns, 1)
                if (rows != rows2 || cols != cols2) {
                    Log.i("cpb", "Skipping file: row/col mismatch")
                    continue
                }

                if (studyUID == currDcm.getString(Tag.StudyInstanceUID) && seriesUID == currDcm.getString(Tag.SeriesInstanceUID)) {
                    // If there isn't enough space in the list, allocate more.
                    while (temp.size < instanceNum) {
                        temp.add(null)
                    }

                    val mat = Mat(rows, cols, CvType.CV_32S)
                    mat.put(0, 0, currDcm.getInts(Tag.PixelData))
                    temp[instanceNum - 1] = mat
                }
            }
        }

        // Display 100% (if only briefly)
        publishProgress(totalFiles, totalFiles)
        return temp.filterNotNull()
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
    override fun onPostExecute(result: List<Mat>?) {
        // get a reference to the activity if it is still there
        val viewer = viewerRef.get()
        if (viewer == null || viewer.isFinishing) return
        viewer.loadResult(result)
    }
}