package app.boyd.android.shared.image

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Created by Christopher on 3/11/2015.
 */


/**
 *
 * Wrapper class for an Adapter. Transforms the embedded Adapter instance
 * into a ListAdapter.
 */
class ColormapArrayAdapter : ArrayAdapter<String> {
    private var _invert: Boolean = false
    var invertColormap
        get() = _invert
        set(value) {
            _invert = value
        }

    constructor(context: Context, resource: Int, strings: Array<String>) : super(context, resource, strings)
    constructor(context: Context, resource: Int, textViewResourceId: Int, strings: Array<String>) : super(context, resource, textViewResourceId, strings)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        // TODO: Color background
        view.background = Colormaps.getColormapDrawable(position, invertColormap)
        return view
    }
}
