
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



// C++: class TonemapDurand
//javadoc: TonemapDurand
public class TonemapDurand extends Tonemap {

    protected TonemapDurand(long addr) { super(addr); }


    //
    // C++:  float getSaturation()
    //

    //javadoc: TonemapDurand::getSaturation()
    public  float getSaturation()
    {
        
        float retVal = getSaturation_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setSaturation(float saturation)
    //

    //javadoc: TonemapDurand::setSaturation(saturation)
    public  void setSaturation(float saturation)
    {
        
        setSaturation_0(nativeObj, saturation);
        
        return;
    }


    //
    // C++:  float getContrast()
    //

    //javadoc: TonemapDurand::getContrast()
    public  float getContrast()
    {
        
        float retVal = getContrast_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setContrast(float contrast)
    //

    //javadoc: TonemapDurand::setContrast(contrast)
    public  void setContrast(float contrast)
    {
        
        setContrast_0(nativeObj, contrast);
        
        return;
    }


    //
    // C++:  float getSigmaSpace()
    //

    //javadoc: TonemapDurand::getSigmaSpace()
    public  float getSigmaSpace()
    {
        
        float retVal = getSigmaSpace_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setSigmaSpace(float sigma_space)
    //

    //javadoc: TonemapDurand::setSigmaSpace(sigma_space)
    public  void setSigmaSpace(float sigma_space)
    {
        
        setSigmaSpace_0(nativeObj, sigma_space);
        
        return;
    }


    //
    // C++:  float getSigmaColor()
    //

    //javadoc: TonemapDurand::getSigmaColor()
    public  float getSigmaColor()
    {
        
        float retVal = getSigmaColor_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setSigmaColor(float sigma_color)
    //

    //javadoc: TonemapDurand::setSigmaColor(sigma_color)
    public  void setSigmaColor(float sigma_color)
    {
        
        setSigmaColor_0(nativeObj, sigma_color);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float getSaturation()
    private static native float getSaturation_0(long nativeObj);

    // C++:  void setSaturation(float saturation)
    private static native void setSaturation_0(long nativeObj, float saturation);

    // C++:  float getContrast()
    private static native float getContrast_0(long nativeObj);

    // C++:  void setContrast(float contrast)
    private static native void setContrast_0(long nativeObj, float contrast);

    // C++:  float getSigmaSpace()
    private static native float getSigmaSpace_0(long nativeObj);

    // C++:  void setSigmaSpace(float sigma_space)
    private static native void setSigmaSpace_0(long nativeObj, float sigma_space);

    // C++:  float getSigmaColor()
    private static native float getSigmaColor_0(long nativeObj);

    // C++:  void setSigmaColor(float sigma_color)
    private static native void setSigmaColor_0(long nativeObj, float sigma_color);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
