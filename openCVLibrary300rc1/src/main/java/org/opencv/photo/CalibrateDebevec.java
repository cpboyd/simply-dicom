
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



// C++: class CalibrateDebevec
//javadoc: CalibrateDebevec
public class CalibrateDebevec extends CalibrateCRF {

    protected CalibrateDebevec(long addr) { super(addr); }


    //
    // C++:  float getLambda()
    //

    //javadoc: CalibrateDebevec::getLambda()
    public  float getLambda()
    {
        
        float retVal = getLambda_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setLambda(float lambda)
    //

    //javadoc: CalibrateDebevec::setLambda(lambda)
    public  void setLambda(float lambda)
    {
        
        setLambda_0(nativeObj, lambda);
        
        return;
    }


    //
    // C++:  int getSamples()
    //

    //javadoc: CalibrateDebevec::getSamples()
    public  int getSamples()
    {
        
        int retVal = getSamples_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setSamples(int samples)
    //

    //javadoc: CalibrateDebevec::setSamples(samples)
    public  void setSamples(int samples)
    {
        
        setSamples_0(nativeObj, samples);
        
        return;
    }


    //
    // C++:  bool getRandom()
    //

    //javadoc: CalibrateDebevec::getRandom()
    public  boolean getRandom()
    {
        
        boolean retVal = getRandom_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setRandom(bool random)
    //

    //javadoc: CalibrateDebevec::setRandom(random)
    public  void setRandom(boolean random)
    {
        
        setRandom_0(nativeObj, random);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float getLambda()
    private static native float getLambda_0(long nativeObj);

    // C++:  void setLambda(float lambda)
    private static native void setLambda_0(long nativeObj, float lambda);

    // C++:  int getSamples()
    private static native int getSamples_0(long nativeObj);

    // C++:  void setSamples(int samples)
    private static native void setSamples_0(long nativeObj, int samples);

    // C++:  bool getRandom()
    private static native boolean getRandom_0(long nativeObj);

    // C++:  void setRandom(bool random)
    private static native void setRandom_0(long nativeObj, boolean random);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
