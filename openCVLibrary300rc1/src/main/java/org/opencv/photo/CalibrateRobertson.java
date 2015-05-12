
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

//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.photo;

import org.opencv.core.Mat;

// C++: class CalibrateRobertson
//javadoc: CalibrateRobertson
public class CalibrateRobertson extends CalibrateCRF {

    protected CalibrateRobertson(long addr) { super(addr); }


    //
    // C++:  int getMaxIter()
    //

    //javadoc: CalibrateRobertson::getMaxIter()
    public  int getMaxIter()
    {
        
        int retVal = getMaxIter_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setMaxIter(int max_iter)
    //

    //javadoc: CalibrateRobertson::setMaxIter(max_iter)
    public  void setMaxIter(int max_iter)
    {
        
        setMaxIter_0(nativeObj, max_iter);
        
        return;
    }


    //
    // C++:  float getThreshold()
    //

    //javadoc: CalibrateRobertson::getThreshold()
    public  float getThreshold()
    {
        
        float retVal = getThreshold_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setThreshold(float threshold)
    //

    //javadoc: CalibrateRobertson::setThreshold(threshold)
    public  void setThreshold(float threshold)
    {
        
        setThreshold_0(nativeObj, threshold);
        
        return;
    }


    //
    // C++:  Mat getRadiance()
    //

    //javadoc: CalibrateRobertson::getRadiance()
    public  Mat getRadiance()
    {
        
        Mat retVal = new Mat(getRadiance_0(nativeObj));
        
        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getMaxIter()
    private static native int getMaxIter_0(long nativeObj);

    // C++:  void setMaxIter(int max_iter)
    private static native void setMaxIter_0(long nativeObj, int max_iter);

    // C++:  float getThreshold()
    private static native float getThreshold_0(long nativeObj);

    // C++:  void setThreshold(float threshold)
    private static native void setThreshold_0(long nativeObj, float threshold);

    // C++:  Mat getRadiance()
    private static native long getRadiance_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
