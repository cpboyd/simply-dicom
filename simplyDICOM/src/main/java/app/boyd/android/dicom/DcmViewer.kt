package app.boyd.android.dicom

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dcm_viewer.*
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import app.boyd.android.dicom.tasks.*
import app.boyd.android.shared.image.ColormapArrayAdapter
import java.io.File
import kotlin.collections.ArrayList

/**
 * DICOMViewer Class
 *
 * @author Christopher Boyd
 */
class DcmViewer : Activity(), CompoundButton.OnCheckedChangeListener,
        TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private var mCmapInv = false
    private var mCmapSelect = -1
    private var mImageCount = 0
    private var mInstance = intArrayOf(0, 0, 0)
    private var mMaxIndex = intArrayOf(0, 0, 0)
    private var mScaleSpacing = doubleArrayOf(1.0, 1.0, 1.0)
    private var mAxis = Axis.TRANSVERSE

    private var mMat: Mat? = null
    private var mMatList: List<Mat>? = null
    private var zList: List<Int>? = null
    private var mTask: AsyncTask<*, *, *>? = null

    private var currentFile: File? = null
    private var currentAttributes: Attributes? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dcm_viewer)

        btn_invert.setOnCheckedChangeListener(this)
        spinnerColormap.adapter = ColormapArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.colormaps_array))
        spinnerColormap.onItemSelectedListener = this
        input_idx.setOnEditorActionListener(this)

        spinnerAxis.onItemSelectedListener = this
        // Set the seek bar change index listener
        seek_idx.setOnSeekBarChangeListener(this)

        imageView.setOnContrastChangedListener(object: DcmImageView.OnContrastChangedListener {
            override fun onContrastChanged(brightness: Double, contrast: Double, colormap: Int, invertCmap: Boolean) {
                contrastView.setImageContrast(brightness, contrast, colormap, invertCmap)
            }
        })

        // If the saved instance state is not null get the file name
        // TODO: Reload state?
//        val filePath = savedInstanceState?.getString(DcmVar.DCMFILE);
//        if (filePath != null) {
//            val file = File(filePath)
//            return
//        }

        // Get the intent
        when (intent?.action) {
            Intent.ACTION_VIEW -> loadFile(intent)
            else -> {
            }
        }
    }

    /** Open file intent  */
    private val REQUEST_IMAGE_GET = 1
    fun openFile(view: View) {
        // TODO: Use ACTION_OPEN_DOCUMENT for persistable access?
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            loadFile(intent)
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
    private fun loadFile(intent: Intent) {
        // Show loading UI
        showLoading()
        IntentLoadTask(this).execute(intent)
    }

    fun cancelLoadTask(force: Boolean = false) {
        mTask?.cancel(force)
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
        if (Build.VERSION.SDK_INT >= 19) {
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
    override fun onSaveInstanceState(outState: Bundle) {
        // If anything is null, don't save the state.
        val filePath = currentFile ?: return

        // Otherwise, save the current file name
        outState.putString(DcmVar.DCMFILE, filePath.absolutePath)

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
            if (fromUser) currentInstance = progress
            val matList = mMatList
            if (matList != null) {
                val newMat = when (mAxis) {
                    Axis.TRANSVERSE -> matList[currentInstance]
                    Axis.CORONAL -> {
                        val listY = ArrayList<Mat>()
                        for (z in matList) {
                            listY.add(z.row(currentInstance))
                        }
                        val mat = Mat()
                        Core.vconcat(listY, mat)
                        mat
                    }
                    Axis.SAGITTAL -> {
                        val listX = ArrayList<Mat>()
                        for (z in matList) {
                            listX.add(z.col(currentInstance))
                        }
                        val mat = Mat()
                        Core.hconcat(listX, mat)
                        mat
                    }
                }
                imageView.mat = newMat
            }

            // Update the UI
            input_idx.setText((currentInstance + 1).toString())

            // Set the visibility of the previous button
            currentInstance = currentInstance.coerceIn(0, currentMax)
            when (currentInstance) {
                 0 -> {
                    btn_prev_idx.visibility = View.INVISIBLE
                    btn_next_idx.visibility = View.VISIBLE

                }
                currentMax -> {
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
        currentInstance--
        // Changing the progress bar will set the image
        seek_idx.progress = currentInstance
    }

    /**
     * Handle touch on next button.
     * @param view
     */
    @Synchronized
    fun nextImage(view: View) {
        clearFocus()
        currentInstance++
        // Changing the progress bar will set the image
        seek_idx.progress = currentInstance
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

    fun loadResult(result: IntentLoadTaskResult) {
        currentAttributes = result.attributes
        val mat = result.mat
        imageView.mat = mat
        mImageCount++

        // Eliminate the loading symbol
        hideLoading()

        val uris = result.uris
        if (uris.isNullOrEmpty()) {
            navigationToolbar.visibility = View.INVISIBLE
            // TODO: Handle multiple URI load
            progressContainer2.visibility = View.INVISIBLE
            return
        }

        loadOtherFiles(result, uris)
    }

    fun loadOtherFiles(result: IntentLoadTaskResult, uris: List<Uri>) {
        mTask = UrisLoadTask(this).execute(UrisLoadTaskInput(result, uris))
    }

    fun setSpacing(spacing: DoubleArray, spacingZ: Double = 1.0) {
        // mPixelSpacing{X, Y, Z}
//        mPixelSpacing = doubleArrayOf(spacing[1], spacing[0], spacingZ)
        // mScaleY2X = mScaleSpacing[mAxis]
        mScaleSpacing = doubleArrayOf(spacing[1] / spacing[0], spacing[1] / spacingZ, spacingZ / spacing[0])
    }

    fun loadResult(result: Pair<List<Mat>, List<Int>>?) {
        progressContainer2.visibility = View.INVISIBLE
        val mats = result?.first ?: return
        if (mats.isNullOrEmpty()) {
            return
        }

        mMatList = mats
        val rows = mats[0].rows()
        val cols = mats[0].cols()
        mMaxIndex = intArrayOf(mats.size - 1, rows - 1, cols - 1)
        if (mats.size <= 1) {
            return
        }

        // If there's more than one image, display the navbar
        navigationToolbar.visibility = View.VISIBLE
        // Display the current file index
        //input_idx.setText(String.valueOf(mInstance + 1))

        val attributes = currentAttributes ?: return
        val instanceNum = attributes.getInt(Tag.InstanceNumber, -1)

        val zs = result.second
        val z = zs.indexOf(instanceNum).coerceAtLeast(0)
        zList = zs
        mInstance = intArrayOf(z, rows / 2, cols / 2)
        updateAxis()
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

    private fun updateAxis(axis: Int = 0) {
        if (spinnerAxis.selectedItemPosition != axis) {
            spinnerAxis.setSelection(axis)
            return
        }

        if (mMatList == null)
            return
        val axes = Axis.values()
        mAxis = axes[axis.coerceIn(0, axes.size - 1)]
        seek_idx.max = currentMax
        val idx = currentInstance
        seek_idx.progress = idx
        imageView.scaleY2X = currentScale

        // Set the visibility of the previous button
        if (idx == 0) {
            btn_prev_idx.visibility = View.INVISIBLE
        } else if (idx == seek_idx.max) {
            btn_next_idx.visibility = View.INVISIBLE
        }
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
            R.id.spinnerColormap -> {
                mCmapSelect = position - 1
                Log.i("cpb", "Colormap: $mCmapSelect id: $id")
                imageView.setColormap(mCmapSelect, mCmapInv)
            }
            R.id.spinnerAxis -> {
                updateAxis(position)
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

    private var currentInstance: Int
    get() = mInstance[mAxis.ordinal]
    set(value) {
        mInstance[mAxis.ordinal] = value
    }

    private val currentMax: Int
        get() = mMaxIndex[mAxis.ordinal]

    private val currentScale: Double
        get() = mScaleSpacing[mAxis.ordinal]

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val userInput = Integer.parseInt(v.text.toString())
//            if (userInput < 0)
//                input_idx.setError("< 0");
//            else if (userInput > currentMax)
//                input_idx.setError("> " + currentMax);
            currentInstance = userInput.coerceIn(0, currentMax)
            v.text = (currentInstance + 1).toString()
            // Changing the progress bar will set the image
            seek_idx.progress = currentInstance
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
                (spinnerColormap.adapter as ColormapArrayAdapter).invertColormap(mCmapInv)
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