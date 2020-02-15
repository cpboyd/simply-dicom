package app.boyd.android.dicom

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
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
import androidx.annotation.StringRes
import app.boyd.android.dicom.tasks.*
import app.boyd.android.shared.image.ColormapArrayAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dcm_viewer.*
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat

/**
 * DICOMViewer Class
 *
 * @author Christopher Boyd
 */
class DcmViewer : Activity(), CompoundButton.OnCheckedChangeListener,
        TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
    private var mInstance = intArrayOf(0, 0, 0)
    private var mMaxIndex = intArrayOf(0, 0, 0)
    private var mScaleSpacing = doubleArrayOf(1.0, 1.0, 1.0)
    private var mAxis = Axis.TRANSVERSE

    private var mMatList: List<Mat>? = null
    private var zList: List<Int>? = null
    private var mTask: AsyncTask<*, *, *>? = null

    private var currentInstance: Int
        get() = mInstance[mAxis.ordinal]
        set(value) {
            mInstance[mAxis.ordinal] = value.coerceIn(0, currentMax)
        }

    private val currentMax: Int
        get() = mMaxIndex[mAxis.ordinal]

    private val currentScale: Double
        get() = mScaleSpacing[mAxis.ordinal]

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

        // TODO: Reload state?

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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        intent ?: return
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
        // TODO: Add prompt before clearing current data?
        cancelLoadTask()
        // Show loading UI
        showLoading()
        IntentLoadTask(this).execute(intent)
    }

    private fun cancelLoadTask(force: Boolean = false) {
        mTask?.cancel(force)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (!hasFocus) {
            return
        }

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelLoadTask(true)
        mMatList = null

        // Free the drawable callback
        val drawable = imageView?.drawable

        if (drawable != null)
            drawable.callback = null
    }

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
        } catch (ex: OutOfMemoryError) {
            System.gc()
            showMemoryDialog()
        }

        // Update the UI
        input_idx.setText((currentInstance + 1).toString())

        // Set the visibility of the previous button
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
        loadProgress.progress = (currentIndex / totalFiles.toDouble() * 100).toInt()
        progressText.text = "$currentIndex/$totalFiles"
    }

    fun showLoadError(errorMsg: String?) {
        showSnackbar(errorMsg ?: "Unable to read file.")
        hideLoading()
    }

    fun loadResult(result: IntentLoadTaskResult) {
        val mat = result.mat
        imageView.mat = mat

        // Eliminate the loading symbol
        hideLoading()

        val uris = result.uris
        if (uris.isNullOrEmpty()) {
            navigationToolbar.visibility = View.INVISIBLE
            // TODO: Handle multiple URI load
            progressContainer2.visibility = View.INVISIBLE
            return
        }

        mTask = UrisLoadTask(this).execute(UrisLoadTaskInput(result, uris))
    }

    fun setSpacing(spacing: DoubleArray, spacingZ: Double = 1.0) {
        // mPixelSpacing{X, Y, Z}
//        mPixelSpacing = doubleArrayOf(spacing[1], spacing[0], spacingZ)
        // mScaleY2X = mScaleSpacing[mAxis]
        mScaleSpacing = doubleArrayOf(spacing[1] / spacing[0], spacing[1] / spacingZ, spacingZ / spacing[0])
    }

    fun loadResult(result: UrisLoadTaskResult?) {
        progressContainer2.visibility = View.INVISIBLE
        val mats = result?.matList ?: return
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

        val instanceNum = result.currentInstance

        val zs = result.zList
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
    private fun showExitAlertDialog(@StringRes title: Int, @StringRes message: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.err_close) { dialog, id -> this@DcmViewer.finish() }

        val alertDialog = builder.create()
        alertDialog.show()
    }
    internal fun showMemoryDialog() {
        return showExitAlertDialog(R.string.err_title_oom, R.string.err_mesg_oom)
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
    override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                position: Int, id: Long) {
        clearFocus()
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(position)
        when (parent?.id) {
            R.id.spinnerColormap -> {
                val cmapSelect = position - 1
                Log.i("cpb", "Colormap: $cmapSelect id: $id")
                imageView.setColormap(cmapSelect)
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
    override fun onNothingSelected(parent: AdapterView<*>?) {
        clearFocus()
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        val text = view?.text ?: return false
        if (actionId != EditorInfo.IME_ACTION_DONE) {
            return false
        }

        val userInput = Integer.parseInt(text.toString())
//            if (userInput < 0)
//                input_idx.setError("< 0");
//            else if (userInput > currentMax)
//                input_idx.setError("> " + currentMax);
        currentInstance = userInput
        view.text = (currentInstance + 1).toString()
        // Changing the progress bar will set the image
        seek_idx.progress = currentInstance
        // Hide the keyboard (clearing focus keeps it open)
        hideKeyboard(view)
        // Clear focus from EditText
        input_idx.clearFocus()
        return true
    }

    /**
     * Called when a CompoundButton is checked
     * @param buttonView
     * @param isChecked
     */
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        clearFocus()
        // Check which toggle button was changed
        when (buttonView?.id) {
            R.id.btn_invert -> {
                (spinnerColormap.adapter as ColormapArrayAdapter).invertColormap(isChecked)
                imageView.invertColormap(isChecked)
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
                Log.d("cpb", "ERROR: Unable to load OpenCV")
            }
        }

        fun hideKeyboard(focusView: View) {
            (focusView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                    InputMethodManager)?.hideSoftInputFromWindow(focusView.windowToken, 0)
        }
    }
}