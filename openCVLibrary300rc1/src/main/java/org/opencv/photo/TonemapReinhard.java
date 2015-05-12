
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



// C++: class TonemapReinhard
//javadoc: TonemapReinhard
public class TonemapReinhard extends Tonemap {

    protected TonemapReinhard(long addr) { super(addr); }


    //
    // C++:  float getIntensity()
    //

    //javadoc: TonemapReinhard::getIntensity()
    public  float getIntensity()
    {
        
        float retVal = getIntensity_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setIntensity(float intensity)
    //

    //javadoc: TonemapReinhard::setIntensity(intensity)
    public  void setIntensity(float intensity)
    {
        
        setIntensity_0(nativeObj, intensity);
        
        return;
    }


    //
    // C++:  float getLightAdaptation()
    //

    //javadoc: TonemapReinhard::getLightAdaptation()
    public  float getLightAdaptation()
    {
        
        float retVal = getLightAdaptation_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setLightAdaptation(float light_adapt)
    //

    //javadoc: TonemapReinhard::setLightAdaptation(light_adapt)
    public  void setLightAdaptation(float light_adapt)
    {
        
        setLightAdaptation_0(nativeObj, light_adapt);
        
        return;
    }


    //
    // C++:  float getColorAdaptation()
    //

    //javadoc: TonemapReinhard::getColorAdaptation()
    public  float getColorAdaptation()
    {
        
        float retVal = getColorAdaptation_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setColorAdaptation(float color_adapt)
    //

    //javadoc: TonemapReinhard::setColorAdaptation(color_adapt)
    public  void setColorAdaptation(float color_adapt)
    {
        
        setColorAdaptation_0(nativeObj, color_adapt);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float getIntensity()
    private static native float getIntensity_0(long nativeObj);

    // C++:  void setIntensity(float intensity)
    private static native void setIntensity_0(long nativeObj, float intensity);

    // C++:  float getLightAdaptation()
    private static native float getLightAdaptation_0(long nativeObj);

    // C++:  void setLightAdaptation(float light_adapt)
    private static native void setLightAdaptation_0(long nativeObj, float light_adapt);

    // C++:  float getColorAdaptation()
    private static native float getColorAdaptation_0(long nativeObj);

    // C++:  void setColorAdaptation(float color_adapt)
    private static native void setColorAdaptation_0(long nativeObj, float color_adapt);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
