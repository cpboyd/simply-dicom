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

package us.cboyd.android.shared.files;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

/**
 * Dicom FileFilter:
 * List only files that have a DICOM extension.
 * 
 * @author Christopher Boyd
 *
 */

public class DicomFilter implements FileFilter {
	/* 
	 * Indicates whether a specific file should be included in a pathname list.
	 */
	public boolean accept(File path) {
        // Only show visible files.
        if (path.isDirectory() || path.isHidden())
            return false;

        // Find where the extension starts (i.e. the last '.')
        String filename = path.getName();
        int ext = filename.lastIndexOf(".");

        // No extension found.  May or may not be a DICOM file.
        if (ext == -1)
            return true;

        // Get the file's extension.
        String extension = filename.substring(ext + 1).toLowerCase(Locale.US);
        // Check if the file has a DICOM (or DCM) extension.
        if (extension.equals("dic") || extension.equals("dicom") || extension.equals("dcm"))
            return true;

        // Otherwise, this probably isn't a DICOM file.
        return false;
	}
	
}
