package us.cboyd.android.shared;

import java.lang.reflect.Field;

import android.view.View;

public class Internals {
	public static int getResId(String variableName, Class<?> c) {

	    try {
	        Field idField = c.getDeclaredField(variableName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } 
	}
	
	public static String getString(String variableName, View view) {
	    return view.getResources().getString(getResId(variableName, String.class));
	}
}
