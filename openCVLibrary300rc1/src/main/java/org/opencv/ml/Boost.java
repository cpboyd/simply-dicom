
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
package org.opencv.ml;



// C++: class Boost
//javadoc: Boost
public class Boost extends DTrees {

    protected Boost(long addr) { super(addr); }


    public static final int
            DISCRETE = 0,
            REAL = 1,
            LOGIT = 2,
            GENTLE = 3;


    //
    // C++:  int getBoostType()
    //

    //javadoc: Boost::getBoostType()
    public  int getBoostType()
    {
        
        int retVal = getBoostType_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setBoostType(int val)
    //

    //javadoc: Boost::setBoostType(val)
    public  void setBoostType(int val)
    {
        
        setBoostType_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  int getWeakCount()
    //

    //javadoc: Boost::getWeakCount()
    public  int getWeakCount()
    {
        
        int retVal = getWeakCount_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setWeakCount(int val)
    //

    //javadoc: Boost::setWeakCount(val)
    public  void setWeakCount(int val)
    {
        
        setWeakCount_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  double getWeightTrimRate()
    //

    //javadoc: Boost::getWeightTrimRate()
    public  double getWeightTrimRate()
    {
        
        double retVal = getWeightTrimRate_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setWeightTrimRate(double val)
    //

    //javadoc: Boost::setWeightTrimRate(val)
    public  void setWeightTrimRate(double val)
    {
        
        setWeightTrimRate_0(nativeObj, val);
        
        return;
    }


    //
    // C++: static Ptr_Boost create()
    //

    //javadoc: Boost::create()
    public static Boost create()
    {
        
        Boost retVal = new Boost(create_0());
        
        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getBoostType()
    private static native int getBoostType_0(long nativeObj);

    // C++:  void setBoostType(int val)
    private static native void setBoostType_0(long nativeObj, int val);

    // C++:  int getWeakCount()
    private static native int getWeakCount_0(long nativeObj);

    // C++:  void setWeakCount(int val)
    private static native void setWeakCount_0(long nativeObj, int val);

    // C++:  double getWeightTrimRate()
    private static native double getWeightTrimRate_0(long nativeObj);

    // C++:  void setWeightTrimRate(double val)
    private static native void setWeightTrimRate_0(long nativeObj, double val);

    // C++: static Ptr_Boost create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
