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

import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;

/**
 * Provides utility methods to extract overlay information from DICOM files.
 * 
 * @author bwallace
 */
public class OverlayUtils {

    private static final int BITS_PER_BYTE = 8;

    /**
     * Returns true if the given frame number references an overlay - that is,
     * is the form 0x60xx yyyy where xx is the overlay number, and yyyy is the
     * overlay frame number. xx must be even.
     * 
     * @param imageIndex
     * @return true if this is an overlay frame.
     */
    public static boolean isOverlay(int imageIndex) {
        return ((imageIndex & 0x60000000) == 0x60000000)
                && (imageIndex & 0x9F010000) == 0;
    }

    /**
     * Extra the frame number portion of the overlay number/imageIndex value.
     * 
     * @param imageIndex
     * @return
     */
    public static int extractFrameNumber(int imageIndex) {
        if (isOverlay(imageIndex))
            return imageIndex & 0xFFFF;
        throw new IllegalArgumentException(
                "Only frame numbers of overlays can be extracted.");
    }

    private static LookupTable reorderBytes;
    static {
        byte[] reorder = new byte[256];
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 8; j++) {
                int bitTest = 1 << j;
                if ((i & bitTest) != 0)
                    reorder[i] |= (0x80 >> j);
            }
        }
        reorderBytes = new ByteLookupTable(8, false, 0, 8, reorder);
    }

    private static byte[] rgbArr = new byte[] { (byte) 0xFF, 0 };

    private static byte[] aArr = new byte[] { (byte) 0x00, (byte) 0xFF };

    /**
     * Read an overlay image or region instead of a regular image. Specify the
     * overlayNumber by the 0x60xx 0000 number. This will return a bitmap
     * overlay, in the colour specified. The image reader isn't required unless
     * overlays encoded in the high bits are being read.
     * 
     * @param ds
     *            is a DicomObject to read the overlay from.
     * @param overlayNumber
     *            - of the form 0x60xx yyyy where xx is the overlay index and
     *            yyyy is the frame index
     * @param reader
     *            is the image reader used to extract the raster for the high
     *            bits
     * @param rgb
     *            is the colour to apply, can be null to use black & white
     *            overlays.
     * @return A single bit buffered image, transparent except in the given
     *         colour where bits are 1.
     * @throws IOException
     *             only if an image from the image reader is attempted and
     *             throws an exception.
     */
    /*public static BufferedImage extractOverlay(DicomObject ds,
            int overlayNumber, ImageReader reader, String rgbs)
            throws IOException {
        // We need the original overlay number.
        if (!OverlayUtils.isOverlay(overlayNumber))
            throw new IllegalArgumentException(
                    "Overlays must start with 0x60xx xxxx but it starts with "
                            + Integer.toString(overlayNumber, 16));
        int frameNumber = extractFrameNumber(overlayNumber);
        overlayNumber = overlayNumber & 0x60FE0000;

        int rows = getOverlayHeight(ds, overlayNumber);
        int cols = getOverlayWidth(ds, overlayNumber);
        if (cols == 0 || rows == 0)
            throw new IllegalArgumentException("No overlay found for "
                    + Integer.toString(overlayNumber));
        int position = ds.getInt(overlayNumber | Tag.OverlayBitPosition);
        byte[] data;
        if (position == 0) {       
            byte[] unpaddedData = ds.getBytes(overlayNumber | Tag.OverlayData);
            
            //Need to ensure that every row starts at a byte boundary
            data = padToFixRowByteBoundary(unpaddedData, rows, cols);
            
            // Extract a sub-frame IF there is a sub-frame, and one is
            // specified. There must be at least 2 frames worth
            // of data to even consider this operation.
            if (frameNumber > 0
                    && data.length >= rows * cols * 2 / BITS_PER_BYTE) {
                byte[] frameData = new byte[rows * cols / BITS_PER_BYTE];
                // TODO Replace with Array.copyOfRange once we are on 1.6
                System.arraycopy(data, (frameNumber - 1) * frameData.length,
                        frameData, 0, frameData.length);
                data = frameData;
            }
            // Don't touch the original data
            if( data==unpaddedData ) {
                data = new byte[rows * cols / BITS_PER_BYTE];
                System.arraycopy(unpaddedData, 0, data, 0, data.length);
            }
        } else {
            Raster raw = reader.readRaster(frameNumber, null);
            int rowLen = (cols + 7) / 8;
            data = new byte[rows * rowLen];
            int[] pixels = new int[cols];
            int bit = (1 << position);
            for (int y = 0; y < rows; y++) {
                pixels = raw.getPixels(0, y, cols, 1, pixels);
                for (int x = 0; x < cols; x++) {
                    if ((pixels[x] & bit) != 0) {
                        data[rowLen * y + x / 8] |= (1 << (x % 8));
                    }
                }
            }
        }
        DataBuffer db = new DataBufferByte(data, data.length);
        WritableRaster wr = Raster.createPackedRaster(db, cols, rows, 1,
                new Point());
        byte[] rArr = rgbArr;
        byte[] gArr = rgbArr;
        byte[] bArr = rgbArr;
        if (rgbs != null && rgbs.length() > 0) {
            if (rgbs.startsWith("#"))
                rgbs = rgbs.substring(1);
            int rgb = Integer.parseInt(rgbs, 16);
            rArr = new byte[] { 0, (byte) ((rgb >> 16) & 0xFF) };
            gArr = new byte[] { 0, (byte) ((rgb >> 8) & 0xFF) };
            bArr = new byte[] { 0, (byte) (rgb & 0xFF) };
        }
        ColorModel cm = new IndexColorModel(1, 2, rArr, gArr, bArr, aArr);
        BufferedImage bi = new BufferedImage(cm, wr, false, null);
        reorderBytes.lookup(bi.getRaster(), bi.getRaster());

        return bi;
    }*/

    /**
     * This method is used for soon-to-be-rasterized bit arrays that are contained within byte arrays (e.g.
     * certain overlays). This method accepts a byte array (containing the bit array) and fixes the byte array
     * so that the beginnings of rows in the bit array coincide with byte-boundaries. This method pads (with 0's)
     * and logically forward bit shifts across byte boundaries as necessary to accomplish this fix.
     * @param unpaddedData  The byte array containing the bit array to be padded as necessary
     * @param rows          The height of the image in pixels
     * @param cols          The width of the image in pixels
     * @return              The byte array fixed to have bit-level row beginnings coincide with byte array
     *                          boundaries
     */
    protected static byte[] padToFixRowByteBoundary(byte[] unpaddedData, int rows, int cols) {
        int numRowBytes = (cols+7)/8;
        int paddedLength = rows * numRowBytes;
        if( (unpaddedData.length == paddedLength ) && (cols%8)==0 ) return unpaddedData;
        
        byte[] data = new byte[paddedLength];
        
        for(int y=0; y<rows; y++) {
            int posnPad = y*numRowBytes;
            int posnUnpad = y*cols;
            // Bits from the current byte needed
            int bits = posnUnpad % 8;
            posnUnpad /= 8;
            int prevBits = 8-bits;
            if( bits==0 ) {
                // Not only an optimization for performance - also prevents an exception if the last pixel doesn't need 
                // to overflow from the next unpadded byte...
                System.arraycopy(unpaddedData,posnUnpad,data, posnPad, numRowBytes);
                continue;
            }
            int mask = (0xFF << bits) & 0xFF;
            int nextMask = (0xFF >> prevBits) & 0xFF;
            for(int x=0; x<numRowBytes; x++) {
                try {
                    byte firstByte = (byte) ((unpaddedData[posnUnpad+x] & mask)>>bits);
                    byte secondByte = 0;
                    // The very last byte can use nothing from the next byte if there are unused bits in it
                    if( posnUnpad+x+1 < unpaddedData.length ) secondByte = (byte) ((unpaddedData[posnUnpad+x+1] & nextMask) << prevBits);
                    data[posnPad+x] = (byte) (firstByte | secondByte);
                } catch (ArrayIndexOutOfBoundsException e) {
                    ArrayIndexOutOfBoundsException newEx = new ArrayIndexOutOfBoundsException(
                            "Did not find enough source data ("+unpaddedData.length+") in overlay to pad data for " 
                            + rows + " rows, " 
                            + cols + "columns");
                    newEx.initCause(e);
                    throw newEx;
                }
            }
        }
        
        return data;
    }

    /**
     * Reads the width of the overlay - needs to be done separately from the
     * primary width even though they are supposed to be identical, as a stand
     * alone overlay won't have any width/height except in the overlay tags.
     * 
     * @param overlayNumber
     * @return The width in pixels of the given overlay.
     * @throws IOException
     */
    public static int getOverlayWidth(DicomObject ds, int overlayNumber) {
        // Zero out the frame index first.
        overlayNumber &= 0x60FF0000;
        return ds.getInt(Tag.OverlayColumns | overlayNumber);
    }

    /**
     * Reads the height of the overlay - needs to be done separately from the
     * primary width even though they are supposed to be identical, as a stand
     * alone overlay won't have any width/height except in the overlay tags.
     * 
     * @param overlayNumber
     * @return
     */
    public static int getOverlayHeight(DicomObject ds, int overlayNumber) {
        overlayNumber &= 0x60FF0000;
        return ds.getInt(Tag.OverlayRows | overlayNumber);
    }

}
