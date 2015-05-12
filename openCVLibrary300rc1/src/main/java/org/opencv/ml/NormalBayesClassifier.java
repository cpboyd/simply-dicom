
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

import org.opencv.core.Mat;

// C++: class NormalBayesClassifier
//javadoc: NormalBayesClassifier
public class NormalBayesClassifier extends StatModel {

    protected NormalBayesClassifier(long addr) { super(addr); }


    //
    // C++:  float predictProb(Mat inputs, Mat& outputs, Mat& outputProbs, int flags = 0)
    //

    //javadoc: NormalBayesClassifier::predictProb(inputs, outputs, outputProbs, flags)
    public  float predictProb(Mat inputs, Mat outputs, Mat outputProbs, int flags)
    {
        
        float retVal = predictProb_0(nativeObj, inputs.nativeObj, outputs.nativeObj, outputProbs.nativeObj, flags);
        
        return retVal;
    }

    //javadoc: NormalBayesClassifier::predictProb(inputs, outputs, outputProbs)
    public  float predictProb(Mat inputs, Mat outputs, Mat outputProbs)
    {
        
        float retVal = predictProb_1(nativeObj, inputs.nativeObj, outputs.nativeObj, outputProbs.nativeObj);
        
        return retVal;
    }


    //
    // C++: static Ptr_NormalBayesClassifier create()
    //

    //javadoc: NormalBayesClassifier::create()
    public static NormalBayesClassifier create()
    {
        
        NormalBayesClassifier retVal = new NormalBayesClassifier(create_0());
        
        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float predictProb(Mat inputs, Mat& outputs, Mat& outputProbs, int flags = 0)
    private static native float predictProb_0(long nativeObj, long inputs_nativeObj, long outputs_nativeObj, long outputProbs_nativeObj, int flags);
    private static native float predictProb_1(long nativeObj, long inputs_nativeObj, long outputs_nativeObj, long outputProbs_nativeObj);

    // C++: static Ptr_NormalBayesClassifier create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
