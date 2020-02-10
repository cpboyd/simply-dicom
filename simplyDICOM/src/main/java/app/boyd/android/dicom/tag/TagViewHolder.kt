package app.boyd.android.dicom.tag

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import app.boyd.android.dicom.R

/**
 * Created by Christopher on 3/11/2015.
 */
class TagViewHolder
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tagLeft: TextView = itemView.findViewById(R.id.tagLeft)
    val tagRight: TextView = itemView.findViewById(R.id.tagRight)
    val text1: TextView = itemView.findViewById(R.id.text1)
    val text2: TextView = itemView.findViewById(R.id.text2)
}
