package us.cboyd.android.dicom.tag

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import us.cboyd.android.dicom.R

/**
 * Created by Christopher on 3/11/2015.
 */
class TagViewHolder
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tagLeft: TextView
    var tagRight: TextView
    var text1: TextView
    var text2: TextView

    init {
        // Initialize the holder
        tagLeft = itemView.findViewById(R.id.tagLeft)
        tagRight = itemView.findViewById(R.id.tagRight)
        text1 = itemView.findViewById(R.id.text1)
        text2 = itemView.findViewById(R.id.text2)
    }
}
