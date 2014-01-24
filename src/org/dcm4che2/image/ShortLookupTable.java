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
 * Java(TM), available at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2003-2007
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

public class ShortLookupTable extends LookupTable {

    private short[] data;

    public ShortLookupTable(int inBits, boolean signed, int off, int outBits,
            short[] data) {
        this(inBits, signed, off, outBits, data, false);
    }

    public ShortLookupTable(int inBits, boolean signed, int off, int outBits,
            short[] data, boolean preserve) {
        super(inBits, signed, off, outBits, preserve);
        this.data = data;
    }

    @Override
    public final int length() {
        return data.length;
    }

    @Override
    public final byte lookupByte(int in) {
        return (byte) lookupShort(in);
    }

    @Override
    public final short lookupShort(int in) {
        int tmp = ((in & signbit) != 0 ? (in | ormask) : (in & andmask)) - off;
        return tmp <= 0 ? data[0] : tmp >= data.length ? data[data.length - 1]
                : data[tmp];
    }

    /**
     * Looks up a raw value, not a pixel-encoded value.  Useful if this is being used to combine LUTs etc
     */
	public short lookupRawShort(int in) {
        int tmp = in - off;
        return tmp <= 0 ? data[0] : tmp >= data.length ? data[data.length - 1]
                : data[tmp];
	}

    @Override
    public final int lookup(int in) {
        return lookupShort(in) & 0xffff;
    }

    @Override
    public final byte[] lookup(byte[] src, int srcPos, byte[] dst, int dstPos,
            int length, int channels, int skip) {
    	int byteLength = length*(channels+skip);
        if( srcPos + byteLength >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new byte[dstPos + byteLength];
        } else if (dstPos + byteLength  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
        	for(int ch=0; ch<channels; ch++) {
        		dst[y++] = lookupByte(src[x++]);
        	}
        	y += skip;
        	x += skip;
        }
        return dst;
    }

    @Override
    public final short[] lookup(byte[] src, int srcPos, short[] dst, int dstPos,
            int length) {
        if( srcPos + length >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new short[dstPos + length];
        } else if (dstPos + length  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
            dst[y++] = lookupShort(src[x++]);
        }
        return dst;
    }

    @Override
    public final int[] lookup(byte[] src, int srcPos, int[] dst, int dstPos,
            int length, int alpha) {
        if( srcPos + length >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new int[dstPos + length];
        } else if (dstPos + length  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
            int tmp = lookupShort(src[x++]) & 0xff;
            dst[y++] = tmp | (tmp << 8) | (tmp << 16) | (alpha << 24);
        }
        return dst;
    }
        
    @Override
    public final byte[] lookup(short[] src, int srcPos, byte[] dst, int dstPos,
            int length) {
        if( srcPos + length >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new byte[dstPos + length];
        } else if (dstPos + length  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
            dst[y++] = lookupByte(src[x++]);
        }
        return dst;
    }

    @Override
    public final short[] lookup(short[] src, int srcPos, short[] dst,
            int dstPos, int length) {
        if( srcPos + length >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new short[dstPos + length];
        } else if (dstPos + length  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
            dst[y++] = lookupShort(src[x++]);
        }
        return dst;
    }

    @Override
    public final int[] lookup(short[] src, int srcPos, int[] dst, int dstPos,
            int length, int alpha) {
        if( srcPos + length >  src.length) {
            throw new IndexOutOfBoundsException(
                    "srcPos:" + srcPos + " + length:" + length
                    + " > src.length:" + src.length);
        }
        if (dst == null) {
            dst = new int[dstPos + length];
        } else if (dstPos + length  >  dst.length) {
            throw new IndexOutOfBoundsException(
                    "dstPos:" + dstPos + " + length:" + length
                    + " > dst.length:" + dst.length);
        }
        for (int x = srcPos, y = dstPos, i = length; i-- > 0;) {
            int tmp = lookupShort(src[x++]) & 0xff;
            dst[y++] = tmp | (tmp << 8) | (tmp << 16) | (alpha << 24);
        }
        return dst;
    }
        
    protected LookupTable inverse() {
        int outMax = (1 << outBits) - 1;
        short[] newData = preserve ? new short[data.length] : data;
        for (int i = 0; i < newData.length; i++) {
            newData[i] = (short) (outMax - data[i]);
        }
        return preserve ? new ShortLookupTable(inBits, signbit != 0, off,
                outBits, newData) : this;
    }

    @Override
    protected LookupTable scale(int outBits, boolean inverse,
            short[] pval2out) {
        if (outBits == this.outBits && !inverse && pval2out == null) {
            return this;
        }
        int outBits1 = pval2out == null ? outBits : inBits(pval2out);
        int shift = outBits1 - this.outBits;
        int pval2outShift = 16 - outBits;
        int outMax = (1 << outBits1) - 1;
        if (preserve && outBits <= 8) {
            byte[] newData = new byte[data.length];
            for (int i = 0; i < newData.length; i++) {
                int tmp = data[i] & 0xffff;
                if (shift < 0) {
                    tmp >>>= -shift;
                } else {
                    tmp <<= shift;
                }
                if (inverse) {
                    tmp = outMax - tmp;
                }
                if (pval2out != null) {
                    tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
                }
                newData[i] = (byte) tmp;
            }
            return new ByteLookupTable(inBits, signbit != 0, off, outBits, newData);
        }
        short[] newData = preserve ? new short[data.length] : data;
        for (int i = 0; i < newData.length; i++) {
            int tmp = data[i] & 0xffff;
            if (shift < 0) {
                tmp >>>= -shift;
            } else {
                tmp <<= shift;
            }
            if (inverse) {
                tmp = outMax - tmp;
            }
            if( tmp<0 ) {
                tmp=0;
            }
            if (pval2out != null) {
                tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
            }
            newData[i] = (short) tmp;
        }
        if (preserve) {
            return new ShortLookupTable(inBits, signbit != 0, off, outBits,
                    newData);
        }
        this.outBits = outBits;
        return this;
    }

    @Override
    protected LookupTable combine(LookupTable other, int outBits,
            boolean inverse, short[] pval2out) {
        int shift1 = other.inBits - this.outBits;
        int outBits1 = pval2out == null ? outBits : inBits(pval2out);
        int shift2 = outBits1 - other.outBits;
        int pval2outShift = 16 - outBits;
        int outMax = (1 << outBits1) - 1;
        if (outBits <= 8) {
            byte[] newData = new byte[data.length];
            for (int i = 0; i < newData.length; i++) {
                int tmp = data[i] & 0xffff;
                tmp = other.lookup(
                        shift1 < 0 ? tmp >>> -shift1 : tmp << shift1);
                if (shift2 < 0) {
                    tmp >>>= -shift2;
                } else {
                    tmp <<= shift2;
                }
                if (inverse) {
                    tmp = outMax - tmp;
                }
                if (pval2out != null) {
                    tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
                }
                newData[i] = (byte) tmp;
            }
            return new ByteLookupTable(inBits, signbit != 0, off, outBits,
                    newData);
        }
        short[] newData = new short[data.length];
        for (int i = 0; i < newData.length; i++) {
            int tmp = data[i] & 0xffff;
            tmp = other.lookup(
                    shift1 < 0 ? tmp >>> -shift1 : tmp << shift1);
            if (shift2 < 0) {
                tmp >>>= -shift2;
            } else {
                tmp <<= shift2;
            }
            if (inverse) {
                tmp = outMax - tmp;
            }
            if (pval2out != null) {
                tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
            }
            newData[i] = (short) tmp;
        }
        return new ShortLookupTable(inBits, signbit != 0, off, outBits,
                newData);
    }

    @Override
    protected LookupTable combine(LookupTable vlut, LookupTable plut,
            int outBits, boolean inverse, short[] pval2out) {
        int shift1 = plut.inBits - vlut.outBits;
        int outBits1 = pval2out == null ? outBits : inBits(pval2out);
        int shift2 = outBits - plut.outBits;
        int pval2outShift = 16 - outBits;
        int outMax = (1 << outBits1) - 1;
        if (outBits <= 8) {
            byte[] newData = new byte[data.length];
            for (int i = 0; i < newData.length; i++) {
                int tmp = vlut.lookup(data[i] & 0xffff);
                tmp = plut.lookup(shift1 < 0 ? tmp >>> -shift1 : tmp << shift1);
                if (shift2 < 0) {
                    tmp >>>= -shift2;
                } else {
                    tmp <<= shift2;
                }
                if (inverse) {
                    tmp = outMax - tmp;
                }
                if (pval2out != null) {
                    tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
                }
                newData[i] = (byte) tmp;
            }
            return new ByteLookupTable(inBits, signbit != 0, off, outBits,
                    newData);
        }
        short[] newData = new short[data.length];
        for (int i = 0; i < newData.length; i++) {
            int tmp = vlut.lookup(data[i] & 0xffff);
            tmp = plut.lookup(shift1 < 0 ? tmp >>> -shift1 : tmp << shift1);
            if (shift2 < 0) {
                tmp >>>= -shift2;
            } else {
                tmp <<= shift2;
            }
            if (inverse) {
                tmp = outMax - tmp;
            }
            if (pval2out != null) {
                tmp = (pval2out[tmp] & 0xffff) >>> pval2outShift;
            }
            newData[i] = (short) tmp;
        }
        return new ShortLookupTable(inBits, signbit != 0, off, outBits,
                newData);
    }
}