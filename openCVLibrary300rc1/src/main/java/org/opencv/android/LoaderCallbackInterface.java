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

/**
 * Interface for callback object in case of asynchronous initialization of OpenCV.
 */
public interface LoaderCallbackInterface
{
    /**
     * OpenCV initialization finished successfully.
     */
    static final int SUCCESS = 0;
    /**
     * Google Play Market cannot be invoked.
     */
    static final int MARKET_ERROR = 2;
    /**
     * OpenCV library installation has been canceled by the user.
     */
    static final int INSTALL_CANCELED = 3;
    /**
     * This version of OpenCV Manager Service is incompatible with the app. Possibly, a service update is required.
     */
    static final int INCOMPATIBLE_MANAGER_VERSION = 4;
    /**
     * OpenCV library initialization has failed.
     */
    static final int INIT_FAILED = 0xff;

    /**
     * Callback method, called after OpenCV library initialization.
     * @param status status of initialization (see initialization status constants).
     */
    public void onManagerConnected(int status);

    /**
     * Callback method, called in case the package installation is needed.
     * @param callback answer object with approve and cancel methods and the package description.
     */
    public void onPackageInstall(final int operation, InstallCallbackInterface callback);
};
