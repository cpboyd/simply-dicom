/*
 * Copyright (C) 2013 - 2015. Christopher Boyd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package us.cboyd.android.dicom

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dcm_viewer.*
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import us.cboyd.android.dicom.tasks.LoadFilesTask
import us.cboyd.android.dicom.tasks.LoadFilesTaskInput
import us.cboyd.android.dicom.tasks.StreamLoadTask
import us.cboyd.android.dicom.tasks.StreamLoadTaskResult
import us.cboyd.android.shared.files.ExternalIO
import us.cboyd.android.shared.files.FileUtils
import us.cboyd.android.shared.image.ColormapArrayAdapter
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

/**
 * DICOMViewer Class
 *
 * @author Christopher Boyd
 * @version 0.6
 */
class DcmViewer : Activity(), CompoundButton.OnCheckedChangeListener,
        TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private val mNoFilter: Boolean = false
    private val mShowFiles: Boolean = false

    private var mCmapInv = false
    private var mCmapSelect = -1
    private var mImageCount = 0
    private var mInstance = intArrayOf(0, 0, 0)
    private var mMaxIndex = intArrayOf(0, 0, 0)
    private var mScaleSpacing = doubleArrayOf(1.0, 1.0, 1.0)
    private var mAxis = DcmVar.TRANSVERSE

    private var mMat: Mat? = null
    private var mMatList: List<Mat>? = null
    private var mTask: AsyncTask<*, *, *>? = null

    var currentFile: File? = null
    var currentAttributes: Attributes? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dcm_viewer)

        btn_invert.setOnCheckedChangeListener(this)
        spinner_colormap.adapter = ColormapArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.colormaps_array))
        spinner_colormap.onItemSelectedListener = this
        input_idx.setOnEditorActionListener(this)

        spinner_plane.onItemSelectedListener = this
        // Set the seek bar change index listener
        seek_idx.setOnSeekBarChangeListener(this)

        imageView.setOnContrastChangedListener(object: DcmImageView.OnContrastChangedListener {
            override fun onContrastChanged(brightness: Double, contrast: Double, colormap: Int, invertCmap: Boolean) {
                contrastView.setImageContrast(brightness, contrast, colormap, invertCmap)
            }
        })

        // If the saved instance state is not null get the file name
        if (savedInstanceState != null) {
            val file = File(savedInstanceState.getString(DcmVar.DCMFILE))
            // TODO: Reload state?
            return
        }

        // Get the intent
        val intent = intent ?: return
        val action = intent.action ?: return
        when (intent.action) {
            Intent.ACTION_VIEW -> loadFile(intent)
            else -> {
            }
        }
    }

    /** Open file intent  */
    private val REQUEST_IMAGE_GET = 1
    fun openFile(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/*"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            loadFile(data)
        }
    }

    private fun showLoading() {
        // Show loading circle
        progressContainer.visibility = View.VISIBLE
        // Make sure the navbar is gone and the progress bar is visible.
        navigationToolbar.visibility = View.INVISIBLE
        progressContainer2.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressContainer.visibility = View.GONE
    }

    // File Load
    private var mInitialPath: String? = null
    private var mInitialUri: Uri? = null
    private val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val PERMISSION_REQUEST_CODE = 200
    private fun loadFile(data: Intent) {
        // Check for multiple files
        val files = data.clipData
        val initialUri = if (files == null) data.data else files.getItemAt(0).uri
        if (initialUri == null) {
            showSnackbar("File not found.")
            return
        }

        // Show loading UI
        showLoading()

        // Attempt to get the path for local files.
        // FIXME: Should FileUtils catch this?
        val initialPath: String? = try {
            FileUtils.getPath(this, initialUri)
        } catch (ex: Exception) {
            null
        }

        Log.i("cpb", "Uri: $initialUri")
        Log.i("cpb", "Path: $initialPath")
        // If we couldn't find a path, load the file from URI
        if (initialPath == null) {
            loadUriTask(initialUri)
        } else {
            // Check if we need permissions to load the series.
            if (needPermission(PERMISSIONS[0])) {
                mInitialUri = initialUri
                mInitialPath = initialPath
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
            } else {
                // Load the file
                loadFileTask(initialPath)
            }
        }
    }

    private fun loadUriTask(uri: Uri) {
        try {
            StreamLoadTask(this).execute(contentResolver.openInputStream(uri))
        } catch (e: FileNotFoundException) {
            val errorMsg = if (e.message?.contains("download_unavailable") == true) {
                "Unable to download file.  Please check your connection."
            } else {
                "File not found."
            }
            showLoadError(errorMsg)
        }
    }

    private fun loadFileTask(pathname: String?) {
        // Get the File object for the current file
        val file = File(pathname)
        currentFile = file
        try {
            StreamLoadTask(this).execute(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            showLoadError("File not found.")
        }
    }

    private fun needPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (permsRequestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Load the file
                    loadFileTask(mInitialPath)
                    return
                }

                val initialUri = mInitialUri ?: return

                // If the URI was a file, tell the user we need permission.
                if ("file".equals(initialUri.scheme, ignoreCase = true)) {
                    showSnackbar("Please \"Allow\" permission to read files.")
                    return
                }

                // Otherwise, try to load from the URI.
                loadUriTask(initialUri)
                // TODO: Notify user that we need permissions to load the entire series.
                showSnackbar("Read permission denied.  Loading single file.")
            }
        }
    }

    // Return a list of the DICOM files
    fun getFileList(file: File): List<File> {
        // If not a directory, get the file's parent directory.
        val currDir = if (file.isDirectory) file else file.parentFile
        // If we don't have permission to read the current directory, return an empty list.
        return if (!currDir.canRead()) ArrayList() else Arrays.asList(*currDir.listFiles(FileFilter { path ->
            // Reject directories and hidden files (if we're not showing them)
            if (path.isDirectory || !mShowFiles && path.isHidden)
                return@FileFilter false
            // If there's no file extension filter, accept all files.
            if (mNoFilter)
                return@FileFilter true

            // Otherwise, find where the extension starts (i.e. the last '.')
            val filename = path.name
            val ext = filename.lastIndexOf(".")

            // No extension found.  May or may not be a DICOM file.
            if (ext == -1)
                return@FileFilter true

            // Get the file's extension.
            val extension = filename.substring(ext + 1).toLowerCase(Locale.US)

            // Check if the file has a DICOM (or DCM) extension.
            extension == "dic" || extension == "dicom" || extension == "dcm"
        }))
    }

    fun cancelLoadTask(force: Boolean = false) {
        mTask?.cancel(force)
    }

    /** Called just before activity runs (after onStart).  */
    override fun onResume() {
        // FIXME: Handle URI or files
        // If there isn't any external storage, quit the application.
        if (!ExternalIO.checkStorage()) {
            val res = resources

            val builder = AlertDialog.Builder(this)
            builder.setMessage(res.getString(R.string.err_mesg_disk))
                    .setTitle(res.getString(R.string.err_title_disk))
                    .setCancelable(false)
                    .setPositiveButton(res.getString(R.string.err_close)
                    ) { dialog, id -> this@DcmViewer.finish() }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (!hasFocus) {
            return
        }

        val decorView = window.decorView
        var uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        decorView.systemUiVisibility = uiOptions
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelLoadTask(true)
        mMat = null
        mMatList = null
        currentAttributes = null

        // Free the drawable callback
        if (imageView != null) {
            val drawable = imageView.drawable

            if (drawable != null)
                drawable.callback = null
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
    override fun onSaveInstanceState(outState: Bundle?) {
        // If anything is null, don't save the state.
        val filePath = currentFile ?: return

        // Otherwise, save the current file name
        outState?.putString(DcmVar.DCMFILE, filePath.absolutePath)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }


    override fun onLowMemory() {
        // Hint the garbage collector
        System.gc()

        // Show the exit alert dialog
        showExitAlertDialog("ERROR: Low Memory", "Low on memory")

        super.onLowMemory()
    }

    /* (non-Javadoc)
	 * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)
	 */
    @Synchronized
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        try {
            // Set the current instance if specified by user
            // This prevents resetting the view if setMax changes the progress
            if (fromUser) mInstance[mAxis] = progress
            val matList = mMatList
            if (matList != null) {
                val mat = Mat()
                when (mAxis) {
                    DcmVar.TRANSVERSE -> mMat = matList[mInstance[mAxis]]
                    DcmVar.CORONAL -> {
                        val listY = ArrayList<Mat>()
                        for (i in matList.indices) {
                            listY.add(matList[i].row(mInstance[mAxis]))
                        }
                        Core.vconcat(listY, mat)
                        imageView.updateMat(mat)
                    }
                    DcmVar.SAGGITAL -> {
                        val listX = ArrayList<Mat>()
                        for (i in matList.indices) {
                            listX.add(matList[i].col(mInstance[mAxis]))
                        }
                        Core.hconcat(listX, mat)
                        imageView.updateMat(mat)
                    }
                    else -> {
                        mAxis = DcmVar.TRANSVERSE
                        seek_idx.max = mMaxIndex[mAxis] - 1
                        seek_idx.progress = mInstance[mAxis]
                        imageView.updateMat(mat)
                    }
                }
            }

            // Update the UI
            input_idx.setText((mInstance[mAxis] + 1).toString())

            // Set the visibility of the previous button
            when {
                mInstance[mAxis] <= 0 -> {
                    mInstance[mAxis] = 0
                    btn_prev_idx.visibility = View.INVISIBLE
                    btn_next_idx.visibility = View.VISIBLE

                }
                mInstance[mAxis] >= mMaxIndex[mAxis] - 1 -> {
                    mInstance[mAxis] = mMaxIndex[mAxis] - 1
                    btn_next_idx.visibility = View.INVISIBLE
                    btn_prev_idx.visibility = View.VISIBLE

                }
                else -> {
                    btn_prev_idx.visibility = View.VISIBLE
                    btn_next_idx.visibility = View.VISIBLE
                }
            }
        } catch (ex: OutOfMemoryError) {
            System.gc()

            showExitAlertDialog("ERROR: Out of memory",
                    "This DICOM series required more memory than your device could provide.")
        }

    }

    // Needed to implement the SeekBar.OnSeekBarChangeListener
    override fun onStartTrackingTouch(seekBar: SeekBar) {
        clearFocus()
    }

    // Needed to implement the SeekBar.OnSeekBarChangeListener
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // TODO: necessary?
        System.gc()
    }

    /**
     * Handle touch on the previousButton.
     * @param view
     */
    @Synchronized
    fun previousImage(view: View) {
        clearFocus()
        mInstance[mAxis]--
        // Changing the progress bar will set the image
        seek_idx.progress = mInstance[mAxis]
    }

    /**
     * Handle touch on next button.
     * @param view
     */
    @Synchronized
    fun nextImage(view: View) {
        clearFocus()
        mInstance[mAxis]++
        // Changing the progress bar will set the image
        seek_idx.progress = mInstance[mAxis]
    }

    fun updateProgress(progress: Pair<Int, Int>) {
        val currentIndex = progress.first
        val totalFiles = progress.second
        loadProgress.progress = (currentIndex / totalFiles.toFloat() * 100).toInt()
        progressText.text = "$currentIndex/$totalFiles"
    }

    fun showLoadError(errorMsg: String?) {
        showSnackbar(errorMsg ?: "Unable to read file.")
        hideLoading()
    }

    fun loadResult(result: StreamLoadTaskResult) {
        currentAttributes = result.attributes
        val mat = result.mat
        imageView.updateMat(mat)
        mImageCount++

        // Eliminate the loading symbol
        hideLoading()

        val currentFile = currentFile
        if (currentFile != null) {
            loadOtherFiles(result, currentFile)
        } else {
            navigationToolbar.visibility = View.INVISIBLE
            // TODO: Handle multiple URI load
            progressContainer2.visibility = View.INVISIBLE
        }
    }

    fun loadOtherFiles(result: StreamLoadTaskResult, file: File) {
        val currDir = if (file.isDirectory) file else file.parentFile
        // Get the files contained in the parent directory of the current file
        val fileList = getFileList(currDir)
        // If the files array is null or its length is less than 1, there is an error because
        // it must at least contain 1 file: the current file
        if (fileList.isEmpty()) {
            showSnackbar("This directory contains no DICOM files.")
            return
        }

        // Get the file index in the array
        val currFileIndex = fileList.indexOf(file)

        // If the current file index is negative
        // or greater or equal to the files array
        // length there is an error
        if (currFileIndex < 0 || currFileIndex >= fileList.size) {
            showSnackbar("The image file could not be found.")
            return
        }

        // Initialize views and navigation bar
        // Check if the seek bar must be shown or not
        if (fileList.size == 1) {
            navigationToolbar.visibility = View.INVISIBLE
        } else {
            // Set the visibility of the previous button
            if (currFileIndex == 0) {
                btn_prev_idx.visibility = View.INVISIBLE
            } else if (currFileIndex == fileList.size - 1) {
                btn_next_idx.visibility = View.INVISIBLE
            }
        }

        mMatList ?: return
        val attributes = currentAttributes ?: return
        val rows = attributes.getInt(Tag.Rows, 1)
        val cols = attributes.getInt(Tag.Columns, 1)
        val instanceNum = attributes.getInt(Tag.InstanceNumber, -1)

        val instanceZ = Math.max(instanceNum - 1, 0)
        mInstance = intArrayOf(instanceZ, rows / 2, cols / 2)

        mTask = LoadFilesTask(this).execute(LoadFilesTaskInput(result, file, fileList))
    }

    fun setSpacing(spacing: DoubleArray, spacingZ: Double = 1.0) {
        // mPixelSpacing{X, Y, Z}
//        mPixelSpacing = doubleArrayOf(spacing[1], spacing[0], spacingZ)
        // mScaleY2X = mScaleSpacing[mAxis]
        mScaleSpacing = doubleArrayOf(spacing[1] / spacing[0], spacing[1] / spacingZ, spacingZ / spacing[0])
    }

    fun loadResult(result: List<Mat>?) {
        progressContainer2.visibility = View.INVISIBLE
        if (result?.isEmpty() != false) {
            return
        }

        mMatList = result
        mMaxIndex = intArrayOf(result.size, result[0].rows(), result[0].cols())
        // If there's more than one image, display the navbar
        if (result.count() > 1) {
            navigationToolbar.visibility = View.VISIBLE
            // Display the current file index
            //input_idx.setText(String.valueOf(mInstance + 1))
            seek_idx.max = result.size - 1
            seek_idx.progress = mInstance[mAxis]
        }
    }

    /**
     * Show a snackbar to inform
     * the user about an error.
     * @param message Message of the Snackbar.
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(dcmViewer, message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Show an alert dialog (AlertDialog) to inform
     * the user that the activity must finish.
     * @param title Title of the AlertDialog.
     * @param message Message of the AlertDialog.
     */
    private fun showExitAlertDialog(title: String, message: String) {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Exit") { dialog, id -> this@DcmViewer.finish() }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    /**
     * Spinner's onItemSelected
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                position: Int, id: Long) {
        clearFocus()
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(position)
        when (parent.id) {
            R.id.spinner_colormap -> {
                mCmapSelect = position - 1
                Log.i("cpb", "Colormap: $mCmapSelect id: $id")
                imageView.setColormap(mCmapSelect, mCmapInv)
            }
            R.id.spinner_plane -> {
                if (mMatList == null)
                    return
                mAxis = position
                seek_idx.max = mMaxIndex[mAxis] - 1
                seek_idx.progress = mInstance[mAxis]
                imageView.setScaleY2X(mScaleSpacing[mAxis].toFloat())
            }
        }
    }

    /** Spinner's onNothingSelected
     *
     * @param parent
     */
    override fun onNothingSelected(parent: AdapterView<*>) {
        clearFocus()
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val userInput = Integer.parseInt(v.text.toString())
//            if (userInput < 0)
//                input_idx.setError("< 0");
//            else if (userInput > mMaxIndex[mAxis])
//                input_idx.setError("> " + mMaxIndex[mAxis]);
            mInstance[mAxis] = Math.max(0, Math.min(mMaxIndex[mAxis], userInput) - 1)
            v.text = (mInstance[mAxis] + 1).toString()
            // Changing the progress bar will set the image
            seek_idx.progress = mInstance[mAxis]
            // Hide the keyboard (clearing focus keeps it open)
            hideKeyboard(v)
            // Clear focus from EditText
            input_idx.clearFocus()
        }
        return false
    }

    /**
     * Called when a CompoundButton is checked
     * @param buttonView
     * @param isChecked
     */
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        clearFocus()
        // Check which toggle button was changed
        when (buttonView.id) {
            R.id.btn_invert -> {
                mCmapInv = isChecked
                (spinner_colormap.adapter as ColormapArrayAdapter).invertColormap(mCmapInv)
                imageView.setColormap(mCmapSelect, mCmapInv)
            }
        }
    }

    fun clearFocus(): Boolean {
        if (input_idx.hasFocus()) {
            // Hide the keyboard (clearing focus keeps it open)
            hideKeyboard(input_idx)
            // Clear focus from EditText
            input_idx.clearFocus()
            return true
        }
        return false
    }

    companion object {
        // Static initialization of OpenCV
        init {
            if (!OpenCVLoader.initDebug()) {
                // Handle initialization error
                Log.d("cpb", "No openCV")
            }
        }

        fun hideKeyboard(focusView: View) {
            (focusView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                    InputMethodManager)?.hideSoftInputFromWindow(focusView.windowToken, 0)
        }
    }
}