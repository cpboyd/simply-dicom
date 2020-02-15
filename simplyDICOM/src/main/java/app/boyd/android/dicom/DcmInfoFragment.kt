package app.boyd.android.dicom

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import app.boyd.android.dicom.tag.TagRecyclerAdapter
import kotlinx.android.synthetic.main.dcm_info.*
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.io.DicomInputStream
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * DICOM InfoFragment
 *
 * @author Christopher Boyd
 */
class DcmInfoFragment : Fragment() {
    private var mDrawerAdapter: ListAdapter? = null
    private var mDrawerListener: AdapterView.OnItemClickListener? = null
    private var mMenuListener: Toolbar.OnMenuItemClickListener? = null

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    private var mDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            activity, /* host Activity */
            drawer_layout, /* DrawerLayout object */
            toolbar,
            R.string.drawer_open, /* "open drawer" description for accessibility */
            R.string.drawer_close  /* "close drawer" description for accessibility */
    ) {
        override fun onDrawerOpened(drawerView: View) {
//                val adapter = left_drawer.adapter as? RefreshArrayAdapter<File> ?: return
            // Refresh the drawer list.
//                adapter.onRefresh()
        }
    }

    private var mAttributes: Attributes? = null
    private var _debugMode = false
    private var mInflater: LayoutInflater? = null
    
    var mode: Boolean
        get() = _debugMode
        set(value) {
            _debugMode = value
            updateModeIcon()
            if (recyclerView != null) {
                val adapter = recyclerView.adapter as? TagRecyclerAdapter ?: return
                adapter.debugMode = _debugMode
            }
        }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mMenuListener = activity as Toolbar.OnMenuItemClickListener
            mDrawerListener = activity as AdapterView.OnItemClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement Toolbar.OnMenuItemClickListener and ListView.OnItemClickListener")
        }

    }

    /** onCreate is called to do initial creation of the fragment.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain this fragment across configuration changes.
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mInflater = inflater
        val view = inflater.inflate(R.layout.dcm_info, container, false)
        // Toolbar
        toolbar.inflateMenu(R.menu.dcm_preview)
        toolbar.setOnMenuItemClickListener(mMenuListener)

        updateModeIcon()
        drawer_layout.setDrawerListener(mDrawerToggle)
        if (mDrawerAdapter != null && mDrawerListener != null) {
            left_drawer.adapter = mDrawerAdapter
            left_drawer.onItemClickListener = mDrawerListener
            mDrawerToggle.isDrawerIndicatorEnabled = true
        }
        mDrawerToggle.syncState()

        // Create the ListView header views
//        list.addHeaderView(header)
//        list.addFooterView(inflater.inflate(R.layout.fab_list_footer, list, false))
        return view
    }

    fun setDrawerList(adapter: ListAdapter) {
        mDrawerAdapter = adapter
        if (left_drawer != null) {
            left_drawer.adapter = adapter
            mDrawerToggle.isDrawerIndicatorEnabled = true
        }
    }

    fun updateDicomInfo(currFile: String?) {
        if (currFile == null) {
            showImage(false)
//            text_fileError.text = resources.getString(R.string.err_unknown_state)
            return
        }

        val file = File(currFile)
        collapsingToolbar.title = file.name
        toolbar.subtitle = file.parent
        try {
            // Read in the DicomObject
            DicomInputStream(FileInputStream(file)).use { dis ->
                val attributes = dis.fileMetaInformation ?: Attributes()
                mAttributes = attributes
                // Raw data set (DICOM data without a file format meta-header)
                dis.readAttributes(attributes, -1, -1)
                attributes.trimToSize()
                attributes.internalizeStringValues(true)
            }
        } catch (ex: IOException) {
            showImage(false)
            // TODO: Re-add error text
//                val sw = StringWriter()
//                PrintWriter(sw).use { pw ->
//                    ex.printStackTrace(pw)
//                    text_fileError.text = (resources.getString(R.string.err_file_read) +
//                            "$currFile\n\nIO Exception: ${ex.message}\n\n$sw")
//                }
            return
        }

        try {
            val attributes = mAttributes ?: return
            checkDcmImage(attributes)

            // TODO: Add selector for info tag listing
            val activity = activity ?: return
            recyclerView.adapter = TagRecyclerAdapter(activity, R.layout.item_tag, attributes, R.array.dcmint_default, _debugMode)

        } catch (ex: Exception) {
            showImage(false)
            // TODO: Re-add error text
//                val sw = StringWriter()
//                PrintWriter(sw).use { pw ->
//                    ex.printStackTrace(pw)
//                    text_fileError.text = (resources.getString(R.string.err_file_display) +
//                            "$currFile\n\nIO Exception: ${ex.message}\n\n$sw")
//                }
        }
    }

    fun checkDcmImage(attributes: Attributes) {
        showImage(false)
        val error = attributes.checkImage()
        if (error == 0) {
            loadDcmImage(attributes)
            return
        }
//        text_fileError.setText(error)
    }

    fun loadDcmImage(attributes: Attributes) {
        try {
            val mat = attributes.getMat() ?: return
//            text_fileError.text = resources.getString(R.string.err_null_pixeldata)

            // Set the PixelData to null to free memory.
            attributes.setNull(Tag.PixelData, VR.OB)
            showImage(true)

            // Determine the minmax
            val minMax = Core.minMaxLoc(mat)
            val diff = minMax.maxVal - minMax.minVal
            mat.convertTo(mat, CvType.CV_8UC1, 255.0 / diff, 0.0)

            // Set the image
            backdrop.setImageBitmap(mat.toBitmap())
        } catch (ex: OutOfMemoryError) {
            System.gc()
            // TODO: Display error?
            return
        }
        // [Y, X] or [row, column]
        val spacing = attributes.getDoubles(Tag.PixelSpacing)
        val scaleY2X = if (spacing != null) {
            spacing[1] / spacing[0]
        } else {
            1.0
        }
        backdrop.scaleX = scaleY2X.toFloat()
        // Limit the height of the image view to display at least two ListView entries (and toolbar).
        val displayMetrics = resources.displayMetrics
        var width = displayMetrics.widthPixels.toDouble()
        if (width > displayMetrics.heightPixels)
            width *= 0.5
        val maxHeight = displayMetrics.heightPixels - 3.0 * 72.0 * displayMetrics.density.toDouble()
        val height = (width / scaleY2X).coerceAtMost(maxHeight)
        backdrop.maxHeight = height.toInt() //displayMetrics.heightPixels - (int)(3*72*displayMetrics.density))
    }

    private fun updateModeIcon() {
        val item = toolbar?.menu?.findItem(R.id.debug_mode) ?: return

        if (_debugMode) {
            item.setIcon(R.drawable.ic_visibility_white_24dp)
        } else {
            item.setIcon(R.drawable.ic_visibility_off_white_24dp)
            item.icon.alpha = 128
        }
    }

    fun showImage(isImage: Boolean) {
        if (isImage) {
            btn_load.hide()
//            text_fileError.setVisibility(View.GONE)
        } else {
            btn_load.show()
//            text_fileError.setVisibility(View.VISIBLE)
        }
    }
}