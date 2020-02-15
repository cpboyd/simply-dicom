package app.boyd.android.dicom.tag

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.boyd.android.dicom.DcmRes
import app.boyd.android.dicom.DcmUid
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.VR
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Christopher on 6/1/2015.
 */
class TagRecyclerAdapter(context: Context, private val mResource: Int,
                         private val mAttributes: Attributes, arrayId: Int,
                         private var _debugMode: Boolean) : RecyclerView.Adapter<TagViewHolder>() {
    private val mRes: Resources = context.resources
    private val mTags: IntArray = mRes.getIntArray(arrayId)

    var debugMode: Boolean
        get() = _debugMode
        set (value) {
            _debugMode = value
            notifyDataSetChanged()
        }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(mResource, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return TagViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val temp = String.format("%08X", mTags[position])
        holder.tagLeft.text = "(${temp.substring(0, 4)},\n ${temp.substring(4, 8)})"

        val tag = DcmRes.getTag(mTags[position], mRes).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        holder.text2.text = tag[0]

        // Clear existing data from recycled view
        val dvr = mAttributes.getVR(mTags[position])
        holder.tagRight.text = if (debugMode && dvr != null) {
            // Only display VR/VM in Debug mode
            "VR: $dvr"// + "\nVM: ${dvr.vmOf(de)}"
        } else {
            ""
        }

        val dStr = mAttributes.getValue(mTags[position])?.toString()
        holder.text1.text = dStr

        // If in Debug mode, just display the string as-is without any special processing.
        if (debugMode || dStr == null) {
            return
        }

        holder.text1.text = when (dvr) {
            // Otherwise, make the fields easier to read.
            // Start by formatting the Person Names.
            VR.PN -> {
                // Family Name^Given Name^Middle Name^Prefix^Suffix
                val names = dStr.split("\\^".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                // May omit '^' for trailing null component groups.
                // Use a switch-case statement to deal with this.
                when (names.size) {
                    // Last, First
                    2 -> "${names[0]}, ${names[1]}"
                    // Last, First Middle
                    3 -> "${names[0]}, ${names[1]} ${names[2]}"
                    // Last, Prefix First Middle
                    4 -> "${names[0]}, ${names[3]} ${names[1]} ${names[2]}"
                    // Last, Prefix First Middle, Suffix
                    5 -> "${names[0]}, ${names[3]} ${names[1]} ${names[2]}, ${names[4]}"
                    // All other cases, just display the unmodified string.
                    else -> return
                }
            }
            // Translate the known UIDs into plain-text:
            VR.UI -> {
                // Only want the first field containing the plain-text name.
                DcmUid.get(dStr, mRes).split(";".toRegex()).firstOrNull() ?: return
            }
            // Format the date according to the current locale.
            VR.DA -> {
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
                try {
                    val vDate = sdf.parse(dStr) ?: return
                    val dPat = DateFormat.getBestDateTimePattern(
                            mRes.configuration.locale, "MMMMdyyyy")
                    sdf.applyPattern(dPat)
                    sdf.format(vDate)
                } catch (e: Exception) {
                    // If the date string couldn't be parsed, display the unmodified string.
                    return
                }
            }
            // Format the date & time according to the current locale.
            VR.DT -> {
                val sdf = SimpleDateFormat("yyyyMMddHHmmss.SSSSSSZZZ", Locale.US)
                try {
                    // Note: The DICOM standard allows for 6 fractional seconds,
                    // but Java can only handle 3.
                    //
                    // Therefore, we must limit the string length.
                    // Use concat to re-append the time-zone.
                    val vDate = sdf.parse(
                            dStr.substring(0, 18) + dStr.substring(21, dStr.length)) ?: return
                    val dPat = DateFormat.getBestDateTimePattern(
                            mRes.configuration.locale, "MMMMdyyyyHHmmssSSSZZZZ")
                    sdf.applyPattern(dPat)
                    sdf.format(vDate)
                } catch (e: Exception) {
                    // If the date string couldn't be parsed, display the unmodified string.
                    return
                }
            }
            // Format the time according to the current locale.
            VR.TM -> {
                val sdf = SimpleDateFormat("HHmmss.SSS", Locale.US)
                try {
                    // Note: The DICOM standard allows for 6 fractional seconds,
                    // but Java can only handle 3.
                    // Therefore, we must limit the string length.
                    val vDate = sdf.parse(dStr.substring(0, 10)) ?: return
                    val dPat = DateFormat.getBestDateTimePattern(
                            mRes.configuration.locale, "HHmmssSSS")
                    sdf.applyPattern(dPat)
                    sdf.format(vDate)
                } catch (e: Exception) {
                    // If the time string couldn't be parsed, display the unmodified string.
                    return
                }
            }
            else -> {
                return
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mTags.size
    }
}
