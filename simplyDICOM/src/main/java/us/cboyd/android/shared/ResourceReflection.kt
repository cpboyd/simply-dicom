package us.cboyd.android.shared

import android.view.View

/**
 * A class that allows you to retrieve resources via reflection.
 * N.B. This is drastically slower than creating and using a HashMap.
 */
object ResourceReflection {
    fun getResId(variableName: String, c: Class<*>): Int {
        return try {
            val idField = c.getDeclaredField(variableName)
            idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun getString(variableName: String, view: View): String {
        return view.resources.getString(getResId(variableName, String::class.java))
    }
}
