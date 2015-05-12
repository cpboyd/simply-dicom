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

package org.opencv.android;

import android.content.Context;

/**
 * Helper class provides common initialization methods for OpenCV library.
 */
public class OpenCVLoader
{
    /**
     * OpenCV Library version 2.4.2.
     */
    public static final String OPENCV_VERSION_2_4_2 = "2.4.2";

    /**
     * OpenCV Library version 2.4.3.
     */
    public static final String OPENCV_VERSION_2_4_3 = "2.4.3";

    /**
     * OpenCV Library version 2.4.4.
     */
    public static final String OPENCV_VERSION_2_4_4 = "2.4.4";

    /**
     * OpenCV Library version 2.4.5.
     */
    public static final String OPENCV_VERSION_2_4_5 = "2.4.5";

    /**
     * OpenCV Library version 2.4.6.
     */
    public static final String OPENCV_VERSION_2_4_6 = "2.4.6";

    /**
     * OpenCV Library version 2.4.7.
     */
    public static final String OPENCV_VERSION_2_4_7 = "2.4.7";

    /**
     * OpenCV Library version 2.4.8.
     */
    public static final String OPENCV_VERSION_2_4_8 = "2.4.8";

    /**
     * OpenCV Library version 2.4.9.
     */
    public static final String OPENCV_VERSION_2_4_9 = "2.4.9";

    /**
     * OpenCV Library version 2.4.10.
     */
    public static final String OPENCV_VERSION_2_4_10 = "2.4.10";

    /**
     * OpenCV Library version 2.4.11.
     */
    public static final String OPENCV_VERSION_2_4_11 = "2.4.11";

    /**
     * OpenCV Library version 3.0.0.
     */
    public static final String OPENCV_VERSION_3_0_0 = "3.0.0";


    /**
     * Loads and initializes OpenCV library from current application package. Roughly, it's an analog of system.loadLibrary("opencv_java").
     * @return Returns true is initialization of OpenCV was successful.
     */
    public static boolean initDebug()
    {
        return StaticHelper.initOpenCV(false);
    }

    /**
     * Loads and initializes OpenCV library from current application package. Roughly, it's an analog of system.loadLibrary("opencv_java").
     * @param InitCuda load and initialize CUDA runtime libraries.
     * @return Returns true is initialization of OpenCV was successful.
     */
    public static boolean initDebug(boolean InitCuda)
    {
        return StaticHelper.initOpenCV(InitCuda);
    }

    /**
     * Loads and initializes OpenCV library using OpenCV Engine service.
     * @param Version OpenCV library version.
     * @param AppContext application context for connecting to the service.
     * @param Callback object, that implements LoaderCallbackInterface for handling the connection status.
     * @return Returns true if initialization of OpenCV is successful.
     */
    public static boolean initAsync(String Version, Context AppContext,
            LoaderCallbackInterface Callback)
    {
        return AsyncServiceHelper.initOpenCV(Version, AppContext, Callback);
    }
}
