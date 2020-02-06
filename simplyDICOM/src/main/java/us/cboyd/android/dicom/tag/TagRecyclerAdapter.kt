package us.cboyd.android.dicom.tag

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.SpecificCharacterSet
import org.dcm4che3.data.VR
import us.cboyd.android.dicom.DcmRes
import us.cboyd.android.dicom.DcmUid
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Christopher on 6/1/2015.
 */
class TagRecyclerAdapter(context: Context, private val mResource: Int,
                         private val mAttributes: Attributes, arrayId: Int,
                         private var mDebugMode: Boolean) : RecyclerView.Adapter<TagViewHolder>() {
    private val mRes: Resources = context.resources
    private val mTags: IntArray = mRes.getIntArray(arrayId)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(mResource, parent, false)
        // set the view's size, margins, paddings and layout parameters
        //        ...
        return TagViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        var temp = String.format("%08X", mTags[position])
        holder.tagLeft.text = "(${temp.substring(0, 4)},\n ${temp.substring(4, 8)})"

        temp = DcmRes.getTag(mTags[position], mRes)
        var temp2 = temp.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        holder.text2.text = temp2[0]
        val de = mAttributes.getValue(mTags[position])
        val dvr = mAttributes.getVR(mTags[position])
        // Clear existing data from recycled view
        holder.text1.text = ""
        holder.tagRight.text = ""

        // Only display VR/VM in Debug mode
        if (mDebugMode && dvr != null) {
            holder.tagRight.text = "VR: $dvr"// + "\nVM: ${dvr.vmOf(de)}"
        }
        if (de != null) {
            //SpecificCharacterSet for US_ASCII
            val cs = SpecificCharacterSet.ASCII

            val dStr = de.toString()

            // If in Debug mode, just display the string as-is without any special processing.
            if (mDebugMode) {
                holder.text1.text = dStr
                return
                // Otherwise, make the fields easier to read.
                // Start by formatting the Person Names.
            } else if (dvr == VR.PN) {
                // Family Name^Given Name^Middle Name^Prefix^Suffix
                temp2 = dStr.split("\\^".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                // May omit '^' for trailing null component groups.
                // Use a switch-case statement to deal with this.
                holder.text1.text = when (temp2.size) {
                    // Last, First
                    2 -> "${temp2[0]}, ${temp2[1]}"
                    // Last, First Middle
                    3 -> "${temp2[0]}, ${temp2[1]} ${temp2[2]}"
                    // Last, Prefix First Middle
                    4 -> "${temp2[0]}, ${temp2[3]} ${temp2[1]} ${temp2[2]}"
                    // Last, Prefix First Middle, Suffix
                    5 -> "${temp2[0]}, ${temp2[3]} ${temp2[1]} ${temp2[2]}, ${temp2[4]}"
                    // All other cases, just display the unmodified string.
                    else -> dStr
                }
                // Translate the known UIDs into plain-text.
            } else if (dvr == VR.UI) {
                temp = DcmUid.get(dStr, mRes)
                // Only want the first field containing the plain-text name.
                temp2 = temp.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                holder.text1.text = temp2[0]
                // Format the date according to the current locale.
            } else if (Build.VERSION.SDK_INT >= 18) {
                when (dvr) {
                    VR.DA -> {
                        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
                        try {
                            val vDate = sdf.parse(dStr)
                            val dPat = DateFormat.getBestDateTimePattern(
                                    mRes.configuration.locale, "MMMMdyyyy")
                            sdf.applyPattern(dPat)
                            holder.text1.text = sdf.format(vDate)
                        } catch (e: Exception) {
                            // If the date string couldn't be parsed, display the unmodified string.
                            holder.text1.text = dStr
                        }

                        // Format the date & time according to the current locale.
                    }
                    VR.DT -> {
                        val sdf = SimpleDateFormat("yyyyMMddHHmmss.SSSSSSZZZ", Locale.US)
                        try {
                            // Note: The DICOM standard allows for 6 fractional seconds,
                            // but Java can only handle 3.
                            //
                            // Therefore, we must limit the string length.
                            // Use concat to re-append the time-zone.
                            val vDate = sdf.parse(
                                    dStr.substring(0, 18) + dStr.substring(21, dStr.length))
                            val dPat = DateFormat.getBestDateTimePattern(
                                    mRes.configuration.locale, "MMMMdyyyyHHmmssSSSZZZZ")
                            sdf.applyPattern(dPat)
                            holder.text1.text = sdf.format(vDate)
                        } catch (e: Exception) {
                            // If the date string couldn't be parsed, display the unmodified string.
                            holder.text1.text = dStr
                        }

                        // Format the time according to the current locale.
                    }
                    VR.TM -> {
                        val sdf = SimpleDateFormat("HHmmss.SSS", Locale.US)
                        try {
                            // Note: The DICOM standard allows for 6 fractional seconds,
                            // but Java can only handle 3.
                            // Therefore, we must limit the string length.
                            val vDate = sdf.parse(dStr.substring(0, 10))
                            val dPat = DateFormat.getBestDateTimePattern(
                                    mRes.configuration.locale, "HHmmssSSS")
                            sdf.applyPattern(dPat)
                            holder.text1.text = sdf.format(vDate)
                        } catch (e: Exception) {
                            // If the time string couldn't be parsed, display the unmodified string.
                            holder.text1.text = dStr
                        }

                    }
                    else -> {
                        holder.text1.text = dStr
                    }
                }
            } else {
                holder.text1.text = dStr
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mTags.size
    }

    fun setDebugMode(debugMode: Boolean) {
        mDebugMode = debugMode
        notifyDataSetChanged()
    }
}
