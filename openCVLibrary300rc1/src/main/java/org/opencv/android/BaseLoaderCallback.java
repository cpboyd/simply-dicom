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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

/**
 * Basic implementation of LoaderCallbackInterface.
 */
public abstract class BaseLoaderCallback implements LoaderCallbackInterface {

    public BaseLoaderCallback(Context AppContext) {
        mAppContext = AppContext;
    }

    public void onManagerConnected(int status)
    {
        switch (status)
        {
            /** OpenCV initialization was successful. **/
            case LoaderCallbackInterface.SUCCESS:
            {
                /** Application must override this method to handle successful library initialization. **/
            } break;
            /** OpenCV loader can not start Google Play Market. **/
            case LoaderCallbackInterface.MARKET_ERROR:
            {
                Log.e(TAG, "Package installation failed!");
                AlertDialog MarketErrorMessage = new AlertDialog.Builder(mAppContext).create();
                MarketErrorMessage.setTitle("OpenCV Manager");
                MarketErrorMessage.setMessage("Package installation failed!");
                MarketErrorMessage.setCancelable(false); // This blocks the 'BACK' button
                MarketErrorMessage.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                MarketErrorMessage.show();
            } break;
            /** Package installation has been canceled. **/
            case LoaderCallbackInterface.INSTALL_CANCELED:
            {
                Log.d(TAG, "OpenCV library instalation was canceled by user");
                finish();
            } break;
            /** Application is incompatible with this version of OpenCV Manager. Possibly, a service update is required. **/
            case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
            {
                Log.d(TAG, "OpenCV Manager Service is uncompatible with this app!");
                AlertDialog IncomatibilityMessage = new AlertDialog.Builder(mAppContext).create();
                IncomatibilityMessage.setTitle("OpenCV Manager");
                IncomatibilityMessage.setMessage("OpenCV Manager service is incompatible with this app. Try to update it via Google Play.");
                IncomatibilityMessage.setCancelable(false); // This blocks the 'BACK' button
                IncomatibilityMessage.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                IncomatibilityMessage.show();
            } break;
            /** Other status, i.e. INIT_FAILED. **/
            default:
            {
                Log.e(TAG, "OpenCV loading failed!");
                AlertDialog InitFailedDialog = new AlertDialog.Builder(mAppContext).create();
                InitFailedDialog.setTitle("OpenCV error");
                InitFailedDialog.setMessage("OpenCV was not initialised correctly. Application will be shut down");
                InitFailedDialog.setCancelable(false); // This blocks the 'BACK' button
                InitFailedDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                InitFailedDialog.show();
            } break;
        }
    }

    public void onPackageInstall(final int operation, final InstallCallbackInterface callback)
    {
        switch (operation)
        {
            case InstallCallbackInterface.NEW_INSTALLATION:
            {
                AlertDialog InstallMessage = new AlertDialog.Builder(mAppContext).create();
                InstallMessage.setTitle("Package not found");
                InstallMessage.setMessage(callback.getPackageName() + " package was not found! Try to install it?");
                InstallMessage.setCancelable(false); // This blocks the 'BACK' button
                InstallMessage.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        callback.install();
                    }
                });

                InstallMessage.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        callback.cancel();
                    }
                });

                InstallMessage.show();
            } break;
            case InstallCallbackInterface.INSTALLATION_PROGRESS:
            {
                AlertDialog WaitMessage = new AlertDialog.Builder(mAppContext).create();
                WaitMessage.setTitle("OpenCV is not ready");
                WaitMessage.setMessage("Installation is in progress. Wait or exit?");
                WaitMessage.setCancelable(false); // This blocks the 'BACK' button
                WaitMessage.setButton(AlertDialog.BUTTON_POSITIVE, "Wait", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callback.wait_install();
                    }
                });
                WaitMessage.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callback.cancel();
                    }
                });

                WaitMessage.show();
            } break;
        }
    }

    void finish()
    {
        ((Activity) mAppContext).finish();
    }

    protected Context mAppContext;
    private final static String TAG = "OpenCVLoader/BaseLoaderCallback";
}
