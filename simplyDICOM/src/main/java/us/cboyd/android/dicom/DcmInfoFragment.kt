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
import kotlinx.android.synthetic.main.dcm_info.*
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.io.DicomInputStream
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import us.cboyd.android.dicom.tag.TagRecyclerAdapter
import java.io.*

/**
 * DICOM InfoFragment
 *
 * @author Christopher Boyd
 * @version 0.7
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

    private var mCurrFile: String? = null
    private var mAttributes: Attributes? = null
    private var mDebugMode = false
    private var mInflater: LayoutInflater? = null
    
    var mode: Boolean
        get() = mDebugMode
        set(extraInfo) {
            mDebugMode = extraInfo
            updateModeIcon()
            if (recyclerView != null) {
                val adapter = recyclerView.adapter as? TagRecyclerAdapter ?: return
                adapter.setDebugMode(mDebugMode)
            }
        }

    // TODO: Remove function (needed for DcmViewer)
    val dicomFile: String?
        get() = mCurrFile

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
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrFile = savedInstanceState.getString(DcmVar.DCMFILE)
        }

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

    /** onStart makes the fragment visible to the user
     * (based on its containing activity being started).  */
    override fun onStart() {
        super.onStart()

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        val args = arguments
        if (args != null) {
            // Set article based on argument passed in
            updateDicomInfo(args.getString(DcmVar.DCMFILE))
        } else if (mCurrFile != null) {
            // Set article based on saved instance state defined during onCreateView
            updateDicomInfo(mCurrFile)
        }
    }

    fun setDrawerList(adapter: ListAdapter) {
        mDrawerAdapter = adapter
        if (left_drawer != null) {
            left_drawer.adapter = adapter
            mDrawerToggle.isDrawerIndicatorEnabled = true
        }
    }

    fun updateDicomInfo(currFile: String?) {
        mCurrFile = currFile
        val file = File(currFile)
        collapsingToolbar.title = file.name
        toolbar.subtitle = file.parent

        if (currFile != null) {
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
                checkDcmImage()

                // TODO: Add selector for info tag listing
                recyclerView.adapter = TagRecyclerAdapter(activity!!, R.layout.item_tag, mAttributes!!, R.array.dcmint_default, mDebugMode)

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

        } else {
            showImage(false)
//            text_fileError.text = resources.getString(R.string.err_unknown_state)
        }
    }

    fun checkDcmImage() {
        showImage(false)
        val error = DcmUtils.checkDcmImage(mAttributes!!)
        if (error == 0) {
            loadDcmImage()
            return
        }
//        text_fileError.setText(error)
    }

    fun loadDcmImage() {
        val attributes = mAttributes ?: return
        val pixels: IntArray? = attributes.getInts(Tag.PixelData)
        if (pixels == null) {
//            text_fileError.text = resources.getString(R.string.err_null_pixeldata)
            return
        }

        // Set the PixelData to null to free memory.
        attributes.setNull(Tag.PixelData, VR.OB)
        showImage(true)
        val rows = attributes.getInt(Tag.Rows, 1)
        val cols = attributes.getInt(Tag.Columns, 1)
        val temp = Mat(rows, cols, CvType.CV_32S)
        temp.put(0, 0, pixels)
        // [Y, X] or [row, column]
        val spacing = attributes.getDoubles(Tag.PixelSpacing)
        var scaleY2X = 1.0
        if (spacing != null) {
            scaleY2X = spacing[1] / spacing[0]
        }

        // Determine the minmax
        val minmax = Core.minMaxLoc(temp)
        val diff = minmax.maxVal - minmax.minVal
        temp.convertTo(temp, CvType.CV_8UC1, 255.0 / diff, 0.0)
        // Make the demo image bluish, rather than black and white.
        Imgproc.applyColorMap(temp, temp, Imgproc.COLORMAP_BONE)
        Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGR)

        // Set the image
        val imageBitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(temp, imageBitmap, true)
        backdrop.setImageBitmap(imageBitmap)
        backdrop.scaleX = scaleY2X.toFloat()
        // Limit the height of the image view to display at least two ListView entries (and toolbar).
        val displayMetrics = resources.displayMetrics
        var width = displayMetrics.widthPixels.toDouble()
        if (width > displayMetrics.heightPixels)
            width *= 0.5
        val maxHeight = displayMetrics.heightPixels - 3.0 * 72.0 * displayMetrics.density.toDouble()
        val height = Math.min(maxHeight, width / scaleY2X)
        backdrop.maxHeight = height.toInt() //displayMetrics.heightPixels - (int)(3*72*displayMetrics.density))
    }

    private fun updateModeIcon() {
        if (toolbar != null) {
            val menu = toolbar.menu
            if (menu != null) {
                val item = toolbar.menu.findItem(R.id.debug_mode)
                if (mDebugMode) {
                    item.setIcon(R.drawable.ic_visibility_white_24dp)
                } else {
                    item.setIcon(R.drawable.ic_visibility_off_white_24dp)
                    item.icon.alpha = 128
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(DcmVar.DCMFILE, mCurrFile)
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