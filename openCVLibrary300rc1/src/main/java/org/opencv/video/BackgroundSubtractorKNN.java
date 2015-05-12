
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
package org.opencv.video;



// C++: class BackgroundSubtractorKNN
//javadoc: BackgroundSubtractorKNN
public class BackgroundSubtractorKNN extends BackgroundSubtractor {

    protected BackgroundSubtractorKNN(long addr) { super(addr); }


    //
    // C++:  int getHistory()
    //

    //javadoc: BackgroundSubtractorKNN::getHistory()
    public  int getHistory()
    {
        
        int retVal = getHistory_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setHistory(int history)
    //

    //javadoc: BackgroundSubtractorKNN::setHistory(history)
    public  void setHistory(int history)
    {
        
        setHistory_0(nativeObj, history);
        
        return;
    }


    //
    // C++:  int getNSamples()
    //

    //javadoc: BackgroundSubtractorKNN::getNSamples()
    public  int getNSamples()
    {
        
        int retVal = getNSamples_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setNSamples(int _nN)
    //

    //javadoc: BackgroundSubtractorKNN::setNSamples(_nN)
    public  void setNSamples(int _nN)
    {
        
        setNSamples_0(nativeObj, _nN);
        
        return;
    }


    //
    // C++:  double getDist2Threshold()
    //

    //javadoc: BackgroundSubtractorKNN::getDist2Threshold()
    public  double getDist2Threshold()
    {
        
        double retVal = getDist2Threshold_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setDist2Threshold(double _dist2Threshold)
    //

    //javadoc: BackgroundSubtractorKNN::setDist2Threshold(_dist2Threshold)
    public  void setDist2Threshold(double _dist2Threshold)
    {
        
        setDist2Threshold_0(nativeObj, _dist2Threshold);
        
        return;
    }


    //
    // C++:  int getkNNSamples()
    //

    //javadoc: BackgroundSubtractorKNN::getkNNSamples()
    public  int getkNNSamples()
    {
        
        int retVal = getkNNSamples_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setkNNSamples(int _nkNN)
    //

    //javadoc: BackgroundSubtractorKNN::setkNNSamples(_nkNN)
    public  void setkNNSamples(int _nkNN)
    {
        
        setkNNSamples_0(nativeObj, _nkNN);
        
        return;
    }


    //
    // C++:  bool getDetectShadows()
    //

    //javadoc: BackgroundSubtractorKNN::getDetectShadows()
    public  boolean getDetectShadows()
    {
        
        boolean retVal = getDetectShadows_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setDetectShadows(bool detectShadows)
    //

    //javadoc: BackgroundSubtractorKNN::setDetectShadows(detectShadows)
    public  void setDetectShadows(boolean detectShadows)
    {
        
        setDetectShadows_0(nativeObj, detectShadows);
        
        return;
    }


    //
    // C++:  int getShadowValue()
    //

    //javadoc: BackgroundSubtractorKNN::getShadowValue()
    public  int getShadowValue()
    {
        
        int retVal = getShadowValue_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setShadowValue(int value)
    //

    //javadoc: BackgroundSubtractorKNN::setShadowValue(value)
    public  void setShadowValue(int value)
    {
        
        setShadowValue_0(nativeObj, value);
        
        return;
    }


    //
    // C++:  double getShadowThreshold()
    //

    //javadoc: BackgroundSubtractorKNN::getShadowThreshold()
    public  double getShadowThreshold()
    {
        
        double retVal = getShadowThreshold_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setShadowThreshold(double threshold)
    //

    //javadoc: BackgroundSubtractorKNN::setShadowThreshold(threshold)
    public  void setShadowThreshold(double threshold)
    {
        
        setShadowThreshold_0(nativeObj, threshold);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getHistory()
    private static native int getHistory_0(long nativeObj);

    // C++:  void setHistory(int history)
    private static native void setHistory_0(long nativeObj, int history);

    // C++:  int getNSamples()
    private static native int getNSamples_0(long nativeObj);

    // C++:  void setNSamples(int _nN)
    private static native void setNSamples_0(long nativeObj, int _nN);

    // C++:  double getDist2Threshold()
    private static native double getDist2Threshold_0(long nativeObj);

    // C++:  void setDist2Threshold(double _dist2Threshold)
    private static native void setDist2Threshold_0(long nativeObj, double _dist2Threshold);

    // C++:  int getkNNSamples()
    private static native int getkNNSamples_0(long nativeObj);

    // C++:  void setkNNSamples(int _nkNN)
    private static native void setkNNSamples_0(long nativeObj, int _nkNN);

    // C++:  bool getDetectShadows()
    private static native boolean getDetectShadows_0(long nativeObj);

    // C++:  void setDetectShadows(bool detectShadows)
    private static native void setDetectShadows_0(long nativeObj, boolean detectShadows);

    // C++:  int getShadowValue()
    private static native int getShadowValue_0(long nativeObj);

    // C++:  void setShadowValue(int value)
    private static native void setShadowValue_0(long nativeObj, int value);

    // C++:  double getShadowThreshold()
    private static native double getShadowThreshold_0(long nativeObj);

    // C++:  void setShadowThreshold(double threshold)
    private static native void setShadowThreshold_0(long nativeObj, double threshold);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
