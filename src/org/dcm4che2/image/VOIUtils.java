/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See listed authors below.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che2.image;

///CPB Edit: 12/11/2013

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.util.TagUtils;

import android.util.Log;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @version $Revision$ $Date$
 * @since Aug 18, 2007
 */
public class VOIUtils {
	private static final String TAG = "dcm4che2.image.VOIUtils";

    /**
     * Return true if the specified object contains some type of VOI attributes
     * at the current level (ie window level or VOI LUT sequence)
     */
    public static boolean containsVOIAttributes(DicomObject dobj) {
        return dobj.containsValue(Tag.WindowCenter)
                && dobj.containsValue(Tag.WindowWidth)
                || getLUT(dobj, Tag.VOILUTSequence) != null;
    }

    public static DicomObject getLUT(DicomObject dobj, int sqTag) {
        DicomObject lut = dobj.getNestedDicomObject(sqTag);
        if (lut != null) {
            if (!lut.containsValue(Tag.LUTData)) {
                Log.i(TAG, "Ignore " + TagUtils.toString(sqTag)
                        + " with missing LUT Data (0028,3006)");
                return null;
            }
            if (!lut.containsValue(Tag.LUTDescriptor)) {
                Log.i(TAG, "Ignore " + TagUtils.toString(sqTag)
                        + " with missing LUT Descriptor (0028,3002)");
                return null;
            }
        }
        return lut;
    }

    /**
     * Return true if the specified image object contains a pixel intensity
     * relationship LUT, based on SOP class
     */
    public static boolean isModalityLUTcontainsPixelIntensityRelationshipLUT(
            DicomObject img) {
        return isModalityLUTcontainsPixelIntensityRelationshipLUT(img
                .getString(Tag.SOPClassUID));
    }

    /**
     * Return true if the specified SOP class contains a pixel intensity
     * relationship LUT
     */
    public static boolean isModalityLUTcontainsPixelIntensityRelationshipLUT(
            String uid) {
        return UID.XRayAngiographicImageStorage.equals(uid)
                || UID.XRayAngiographicBiPlaneImageStorageRetired.equals(uid)
                || UID.XRayRadiofluoroscopicImageStorage.equals(uid);
    }

    /**
     * Finds the applicable DicomObject containing the Modality LUT information
     * for the given frame within the image. Preference is to use the GSPS pr
     * object first if it is found, then the dicom object enhanced multi-frame,
     * and if those aren't found, then the image itself. This will always return
     * a non-null value, although if no Modality LUT information is found
     * whatsoever, this can contain an empty set of information.
     * 
     * @param img
     * @param pr
     * @param frame
     * @return The DicomObject containing the correct modality LUT information
     *         for this frame.
     */
    public static DicomObject selectModalityLUTObject(DicomObject img,
            DicomObject pr, int frame) {
        if (pr != null)
            return pr;
        
        if (isModalityLUTcontainsPixelIntensityRelationshipLUT(img)) {
            return new BasicDicomObject();
        }
        
        DicomElement framed = img.get(Tag.PerFrameFunctionalGroupsSequence);
        if (framed != null) {
            int size = framed.countItems();
            Log.d(TAG, "Looking in enhanced multi-frame Perframe object for VOI lut information, frames="
                            + size);
            if (frame >= 1 && frame <= size) {
                DicomObject frameObj = framed.getDicomObject(frame - 1);
                if (frameObj != null) {
                    DicomObject mlutObj = frameObj
                            .getNestedDicomObject(Tag.PixelValueTransformationSequence);
                    if (mlutObj != null) {
                        Log.d(TAG, "Found a per-frame mlut info.");
                        return mlutObj;
                    }
                }
            }
        }
        try {
            DicomObject shared = img.getNestedDicomObject(Tag.SharedFunctionalGroupsSequence);
            if (shared != null) {
                DicomObject mlutObj = shared.getNestedDicomObject(Tag.PixelValueTransformationSequence);
                if (mlutObj != null) {
                    Log.d(TAG, "Found a shared mLut information object ");
                    return mlutObj;
                }
            }
        } catch (UnsupportedOperationException e) {
            Log.w(TAG, "Shared functional groups is the wrong VR type:" + e);
        }
        return img;
    }

    /**
     * Finds the applicable DicomObject containing the VOI LUT information for
     * the given frame within the image. Uses the GSPS first, if a match is
     * found for the givem SOP/frame, then SOP only, then it uses the image
     * enahanced multi-frame, and finally the regular image information. Always
     * returns some object to use, the img level itself if nothing else is
     * found.
     * 
     * @param img
     * @param db
     * @return DicomObject containing the VOI LUT to use for this frame.
     */
    public static DicomObject selectVoiObject(DicomObject img, DicomObject pr, int frame) {
        String iuid = img.getString(Tag.SOPInstanceUID);
        DicomObject voi = selectVoiItemFromPr(iuid, pr, frame);
        if (voi != null) {
            return voi;
        }
        if (pr != null) {
            return pr;
        }

        DicomElement framed = img.get(Tag.PerFrameFunctionalGroupsSequence);
        if (framed != null) {
            int size = framed.countItems();
            if (frame >= 1 && frame <= size) {
                DicomObject frameObj = framed.getDicomObject(frame - 1);
                if (frameObj != null) {
                    DicomObject voiObj = frameObj.getNestedDicomObject(Tag.FrameVOILUTSequence);
                    if (voiObj != null && containsVOIAttributes(voiObj)) {
                        return voiObj;
                    }
                }
            }
        }
        DicomObject shared = null;
        try {
            shared = img.getNestedDicomObject(Tag.SharedFunctionalGroupsSequence);
        } catch(UnsupportedOperationException e) {
            Log.w(TAG, "Image contains binary object in shared functional groups sequence - invalid dicom");
        }
        if (shared != null) {
            DicomObject voiObj = shared.getNestedDicomObject(Tag.FrameVOILUTSequence);
            if (voiObj != null && containsVOIAttributes(voiObj)) {
                return voiObj;
            }
        }
        if (containsVOIAttributes(img))
            return img;
        return null;
    }

    /**
     * Searches for a Softcopy VOI LUT Sequence for the given frame, or one for
     * just the SOP instance.
     * 
     * @param iuid
     *            Image UID
     * @param pr
     *            GSPS object to select from.
     * @param frame
     *            number. Use 0 to select any VOI applying to all frames of the
     *            UID (that is, no referenced frame number sub-object is
     *            present.)
     */
    public static DicomObject selectVoiItemFromPr(String iuid, DicomObject pr,
            int frame) {
        if (pr == null)
            return null;
        DicomElement voisq = pr.get(Tag.SoftcopyVOILUTSequence);
        if (voisq == null) {
            // Imply no VOI functionality
            return pr;
        }
        for (int i = 0, n = voisq.countItems(); i < n; i++) {
            DicomObject item = voisq.getDicomObject(i);
            DicomElement refImgs = item.get(Tag.ReferencedImageSequence);
            if (refImgs == null) {
                return item;
            }
            for (int j = 0, m = refImgs.countItems(); j < m; j++) {
                DicomObject refImage = refImgs.getDicomObject(j);
                if (iuid.equals(refImage
                        .getString(Tag.ReferencedSOPInstanceUID))) {
                    int[] frames = refImage.getInts(Tag.ReferencedFrameNumber);
                    if (frames == null || frames.length == 0)
                        return item;
                    if (frame == 0)
                        return null; // Can't have a all-frame VOI once you see
                                     // a per-frame VOI for the SOP
                    for (int k = 0; k < frames.length; k++) {
                        if (frames[k] == frame)
                            return item;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method returns the minimum and maximum window center widths, based
     * on the Modality LUT and image information. If the databuffer is missing,
     * then no image scanning will occur for min/max pixel information, but
     * instead the minimum/maximum possible values will be used instead.
     * 
     * @param img
     * @param pr
     *            Is the GSPS to apply to the object.
     * @param frame
     *            is the frame number
     * @param raster
     * @return
     */
    /*public static float[] getMinMaxWindowCenterWidth(DicomObject img,
            DicomObject pr, int frame, Raster raster) {
        int[] minMax;
        float slope;
        float intercept;
        DicomObject mObj = selectModalityLUTObject(img, pr, frame);
        DicomObject mLut = VOIUtils.getLUT(mObj, Tag.ModalityLUTSequence);
        if (mLut != null) {
            slope = 1;
            intercept = 0;
            minMax = calcMinMax(mLut);
        } else {
            slope = mObj.getFloat(Tag.RescaleSlope, 1.f);
            intercept = mObj.getFloat(Tag.RescaleIntercept, 0.f);
            int minPixel = img.getInt(Tag.SmallestImagePixelValue);
            int maxPixel = img.getInt(Tag.LargestImagePixelValue);
            
            if(img.containsValue(Tag.SmallestImagePixelValue)
                    && img.containsValue(Tag.LargestImagePixelValue)
                    && minPixel != maxPixel ) {
                minMax = new int[] { minPixel, maxPixel };
            } else if (raster == null || !(raster.getSampleModel() instanceof ComponentSampleModel) ) {
                Log.d(TAG, "Using min/max possible values to compute WL range, as we don't have data buffer to use.");
                int stored = img.getInt(Tag.BitsStored);
                boolean signed = img.getInt(Tag.PixelRepresentation) == 1;
                if (stored == 16) {
                    String rescaleType = mObj.getString(Tag.RescaleType);
                    if ("HU".equalsIgnoreCase(rescaleType)) {
                        stored = 12;
                    }
                }
                minMax = new int[] { (signed ? -(1 << (stored - 1)) : 0),
                        (signed ? (1 << (stored - 1)) - 1 : (1 << stored) - 1) };
            } else {
                minMax = calcMinMax(img, raster);
            }
        }
        // Handle all single value images
        if( minMax[0]==minMax[1] ) {
            if( minMax[0]>0 ) minMax[0] = 0;
            else minMax[1] = minMax[0]+1;
        }
        return new float[] {
                ((minMax[1] + minMax[0]) / 2.f) * slope + intercept + 0.5f,
                Math.abs((minMax[1] - minMax[0]) * slope) + 1 };
    }*/

    /** Gets the min/max value from a data buffer, in the raw pixel data */
    /*public static int[] calcMinMax(DicomObject img, Raster raster) {
        int allocated = img.getInt(Tag.BitsAllocated, 8);
        int stored = img.getInt(Tag.BitsStored, allocated);
        boolean signed = img.getInt(Tag.PixelRepresentation) != 0;
        int range = 1 << stored;
        int mask = range - 1;
        int signbit = signed ? 1 << (stored - 1) : 0;
        int w = raster.getWidth();
        int h = raster.getHeight();
        int scanlineStride = ((ComponentSampleModel) raster.getSampleModel())
                .getScanlineStride();

        Integer pixelPaddingValue = LookupTable.getIntPixelValue(img,Tag.PixelPaddingValue,signed,stored); 
        Integer pixelPaddingRangeLimit = LookupTable.getIntPixelValue(img,Tag.PixelPaddingRangeLimit,signed,stored); 
                
        int paddingMin = Integer.MIN_VALUE;
        int paddingMax = Integer.MIN_VALUE;
        
        if (pixelPaddingValue != null){
            if( (pixelPaddingValue & signbit) != 0 ) {
                pixelPaddingValue |= ~mask;
            }
            if( pixelPaddingRangeLimit != null && (pixelPaddingRangeLimit & signbit) != 0) {
                pixelPaddingRangeLimit |= ~mask;
            }
            Integer[] pixelPaddingMinMax = LookupTable.getMinMaxPixelPadding(pixelPaddingValue, pixelPaddingRangeLimit);    
            paddingMin = pixelPaddingMinMax[0];
            paddingMax = pixelPaddingMinMax[1];
        }
        
        DataBuffer data = raster.getDataBuffer();
        int[] ret;
        
        switch (data.getDataType()) {
        case DataBuffer.TYPE_BYTE:
            ret = calcMinMax(signbit, mask, w, h, scanlineStride,
                    ((DataBufferByte) data).getData(), paddingMin, paddingMax);
            break;
            
        case DataBuffer.TYPE_USHORT:
        	ret = calcMinMax(signbit, mask, w, h, scanlineStride,
                    ((DataBufferUShort) data).getData(), paddingMin, paddingMax);
        	break;
        	
        case DataBuffer.TYPE_SHORT:
        	ret = calcMinMax(signbit, mask, w, h, scanlineStride,
                    ((DataBufferShort) data).getData(), paddingMin, paddingMax);
        	break;
        	
        default:
            throw new IllegalArgumentException("Illegal Type of DataBuffer: "
                    + raster);
        }
        return ret;
    }*/

    static int[] calcMinMax(int signbit, int mask, int w, int h,
            int scanlineStride, short[] data, int paddingMin, int paddingMax) {
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int x = 0; x < h; x++) {
            for (int y = 0, i = x * scanlineStride; y < w; y++, i++) {
                int val = data[i] & mask;
                if ((val & signbit) != 0) {
                    val |= ~mask;
                }
                if( val >= paddingMin && val <= paddingMax ) {
            		continue;
            	}
                if (minVal > val) {
                    minVal = val;
                }
                if (maxVal < val) {
                    maxVal = val;
                }
            }
        }
        return new int[] { minVal, maxVal };
    }

    static int[] calcMinMax(int signbit, int mask, int w, int h,
            int scanlineStride, byte[] data, int paddingMin, int paddingMax) {
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int x = 0; x < h; x++) {
            for (int y = 0, i = x * scanlineStride; y < w; y++, i++) {
                int val = data[i] & mask;
                if ((val & signbit) != 0) {
                    val |= ~mask;
                }
                if( val >= paddingMin && val <= paddingMax ) {
            		continue;
            	}
                if (minVal > val) {
                    minVal = val;
                }
                if (maxVal < val) {
                    maxVal = val;
                }
            }
        }
        return new int[] { minVal, maxVal };
    }

    static int[] calcMinMax(DicomObject lut) {
        int[] desc = lut.getInts(Tag.LUTDescriptor);
        byte[] data = lut.getBytes(Tag.LUTData);
        if (desc == null) {
            throw new IllegalArgumentException("Missing LUT Descriptor!");
        }
        if (desc.length != 3) {
            throw new IllegalArgumentException(
                    "Illegal number of LUT Descriptor values: " + desc.length);
        }
        if (data == null) {
            throw new IllegalArgumentException("Missing LUT Data!");
        }
        int len = desc[0] == 0 ? 0x10000 : desc[0];
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        if (data.length == len) {
            for (int i = 0; i < len; i++) {
                int val = data[i] & 0xff;
                if (minVal > val) {
                    minVal = val;
                }
                if (maxVal < val) {
                    maxVal = val;
                }
            }
        } else if (data.length == len << 1) {
            int hibyte = lut.bigEndian() ? 0 : 1;
            int lobyte = 1 - hibyte;
            for (int i = 0, j = 0; i < len; i++, j++, j++) {
                int val = (data[j + hibyte] & 0xff) << 8
                        | (data[j + lobyte] & 0xff);
                if (minVal > val) {
                    minVal = val;
                }
                if (maxVal < val) {
                    maxVal = val;
                }
            }
        } else {
            throw new IllegalArgumentException("LUT Data length: "
                    + data.length + " mismatch entry value: " + len
                    + " in LUT Descriptor");
        }
        return new int[] { minVal, maxVal };
    }

}
