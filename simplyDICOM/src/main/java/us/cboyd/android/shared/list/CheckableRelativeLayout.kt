package us.cboyd.android.shared.list

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.RelativeLayout

import us.cboyd.android.dicom.R

/**
 * Created by Christopher on 3/23/2015.
 */
class CheckableRelativeLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), Checkable {

    private var isChecked = false

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun setChecked(isChecked: Boolean) {
        this.isChecked = isChecked
        changeColor(isChecked)
    }

    override fun toggle() {
        setChecked(!this.isChecked)
    }

    private fun changeColor(isChecked: Boolean) {
        setBackgroundColor(resources.getColor(if (isChecked) R.color.accent else android.R.color.transparent))

    }
}
