
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

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class StatModel
//javadoc: StatModel
public class StatModel extends Algorithm {

    protected StatModel(long addr) { super(addr); }


    public static final int
            UPDATE_MODEL = 1,
            RAW_OUTPUT = 1,
            COMPRESSED_INPUT = 2,
            PREPROCESSED_INPUT = 4;


    //
    // C++:  int getVarCount()
    //

    //javadoc: StatModel::getVarCount()
    public  int getVarCount()
    {
        
        int retVal = getVarCount_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool empty()
    //

    //javadoc: StatModel::empty()
    public  boolean empty()
    {
        
        boolean retVal = empty_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool isTrained()
    //

    //javadoc: StatModel::isTrained()
    public  boolean isTrained()
    {
        
        boolean retVal = isTrained_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool isClassifier()
    //

    //javadoc: StatModel::isClassifier()
    public  boolean isClassifier()
    {
        
        boolean retVal = isClassifier_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool train(Ptr_TrainData trainData, int flags = 0)
    //

    // Unknown type 'Ptr_TrainData' (I), skipping the function


    //
    // C++:  bool train(Mat samples, int layout, Mat responses)
    //

    //javadoc: StatModel::train(samples, layout, responses)
    public  boolean train(Mat samples, int layout, Mat responses)
    {
        
        boolean retVal = train_0(nativeObj, samples.nativeObj, layout, responses.nativeObj);
        
        return retVal;
    }


    //
    // C++:  float calcError(Ptr_TrainData data, bool test, Mat& resp)
    //

    // Unknown type 'Ptr_TrainData' (I), skipping the function


    //
    // C++:  float predict(Mat samples, Mat& results = Mat(), int flags = 0)
    //

    //javadoc: StatModel::predict(samples, results, flags)
    public  float predict(Mat samples, Mat results, int flags)
    {
        
        float retVal = predict_0(nativeObj, samples.nativeObj, results.nativeObj, flags);
        
        return retVal;
    }

    //javadoc: StatModel::predict(samples)
    public  float predict(Mat samples)
    {
        
        float retVal = predict_1(nativeObj, samples.nativeObj);
        
        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getVarCount()
    private static native int getVarCount_0(long nativeObj);

    // C++:  bool empty()
    private static native boolean empty_0(long nativeObj);

    // C++:  bool isTrained()
    private static native boolean isTrained_0(long nativeObj);

    // C++:  bool isClassifier()
    private static native boolean isClassifier_0(long nativeObj);

    // C++:  bool train(Mat samples, int layout, Mat responses)
    private static native boolean train_0(long nativeObj, long samples_nativeObj, int layout, long responses_nativeObj);

    // C++:  float predict(Mat samples, Mat& results = Mat(), int flags = 0)
    private static native float predict_0(long nativeObj, long samples_nativeObj, long results_nativeObj, int flags);
    private static native float predict_1(long nativeObj, long samples_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
