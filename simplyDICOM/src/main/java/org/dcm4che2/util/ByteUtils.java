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
 * Gunter Zeilinger <gunterze@gmail.com>
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

package org.dcm4che2.util;

public class ByteUtils
{

    public static byte[] int2bytesLE(int val, byte[] b, int off)
    {
        b[off] = (byte) val;
        b[off + 1] = (byte) (val >>> 8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
        return b;
    }

    public static byte[] ints2bytesLE(int[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            int2bytesLE(val[i], b, i << 2);
        return b;
    }

    public static byte[] int2bytesBE(int val, byte[] b, int off)
    {
        b[off] = (byte) (val >>> 24);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 3] = (byte) val;
        return b;
    }

    public static byte[] ints2bytesBE(int[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            int2bytesBE(val[i], b, i << 2);
        return b;
    }

    public static int bytesLE2int(byte[] b, int off)
    {
        return ((b[off + 3] & 0xff) << 24) | ((b[off + 2] & 0xff) << 16)
                | ((b[off + 1] & 0xff) << 8) | (b[off] & 0xff);
    }

    public static int[] bytesLE2ints(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2int(b, i << 2);
        return val;
    }

    public static int bytesBE2int(byte[] b, int off)
    {
        return ((b[off] & 0xff) << 24) | ((b[off + 1] & 0xff) << 16)
                | ((b[off + 2] & 0xff) << 8) | (b[off + 3] & 0xff);
    }

    public static int[] bytesBE2ints(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2int(b, i << 2);
        return val;
    }

    public static byte[] tag2bytesLE(int tag, byte[] b, int off)
    {
        b[off] = (byte) ((tag >>> 16) & 0xff);
        b[off + 1] = (byte) ((tag >>> 24) & 0xff);
        b[off + 2] = (byte) (tag & 0xff);
        b[off + 3] = (byte) ((tag >>> 8) & 0xff);
        return b;
    }

    public static byte[] tags2bytesLE(int[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            tag2bytesLE(val[i], b, i << 2);
        return b;
    }

    public static byte[] tag2bytesBE(int tag, byte[] b, int off)
    {
        return int2bytesBE(tag, b, off);
    }

    public static byte[] tags2bytesBE(int[] val)
    {
        return ints2bytesBE(val);
    }

    public static int bytesLE2tag(byte[] b, int off)
    {
        return ((b[off + 1] & 0xff) << 24) | ((b[off] & 0xff) << 16)
                | ((b[off + 3] & 0xff) << 8) | (b[off + 2] & 0xff);
    }

    public static int[] bytesLE2tags(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2tag(b, i << 2);
        return val;
    }

    public static int bytesBE2tag(byte[] b, int off)
    {
        return bytesBE2int(b, off);
    }

    public static int[] bytesBE2tags(byte[] b)
    {
        return bytesBE2ints(b);
    }

    public static byte[] ushort2bytesLE(int val, byte[] b, int off)
    {
        b[off] = (byte) val;
        b[off + 1] = (byte) (val >>> 8);
        return b;
    }

    public static byte[] ushorts2bytesLE(int[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 1];
        for (int i = 0; i < val.length; i++)
            ushort2bytesLE(val[i], b, i << 1);
        return b;
    }

    public static byte[] ushort2bytesBE(int val, byte[] b, int off)
    {
        b[off] = (byte) (val >>> 8);
        b[off + 1] = (byte) val;
        return b;
    }

    public static byte[] ushorts2bytesBE(int[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 1];
        for (int i = 0; i < val.length; i++)
            ushort2bytesBE(val[i], b, i << 1);
        return b;
    }

    public static byte[] shorts2bytesLE(short[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 1];
        for (int i = 0; i < val.length; i++)
            ushort2bytesLE(val[i], b, i << 1);
        return b;
    }
    
    public static byte[] shorts2bytesBE(short[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 1];
        for (int i = 0; i < val.length; i++)
            ushort2bytesBE(val[i], b, i << 1);
        return b;
    }

    public static int bytesLE2ushort(byte[] b, int off)
    {
        return bytesLE2sshort(b, off) & 0xffff;
    }

    public static int[] bytesLE2ushorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2ushort(b, i << 1);
        return val;
    }

    public static int bytesBE2ushort(byte[] b, int off)
    {
        return bytesBE2sshort(b, off) & 0xffff;
    }

    public static int[] bytesBE2ushorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2ushort(b, i << 1);
        return val;
    }

    public static int bytesLE2sshort(byte[] b, int off)
    {
        return (b[off + 1] << 8) | (b[off] & 0xff);
    }

    public static int[] bytesLE2sshorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2sshort(b, i << 1);
        return val;
    }

    public static int bytesBE2sshort(byte[] b, int off)
    {
        return (b[off] << 8) | (b[off + 1] & 0xff);
    }

    public static int[] bytesBE2sshorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        int[] val = new int[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2sshort(b, i << 1);
        return val;
    }

    public static short[] bytesLE2shorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        short[] val = new short[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = (short) bytesLE2sshort(b, i << 1);
        return val;
    }
    
    public static short[] bytesBE2shorts(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x1) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        short[] val = new short[b.length >> 1];
        for (int i = 0; i < val.length; i++)
            val[i] = (short) bytesBE2sshort(b, i << 1);
        return val;
    }
    
    public static byte[] float2bytesLE(float val, byte[] b, int off)
    {
        return int2bytesLE(Float.floatToIntBits(val), b, off);
    }

    public static byte[] floats2bytesLE(float[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            float2bytesLE(val[i], b, i << 2);
        return b;
    }

    public static byte[] float2bytesBE(float val, byte[] b, int off)
    {
        return int2bytesBE(Float.floatToIntBits(val), b, off);
    }

    public static byte[] floats2bytesBE(float[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            float2bytesBE(val[i], b, i << 2);
        return b;
    }

    public static float bytesLE2float(byte[] b, int off)
    {
        return Float.intBitsToFloat(bytesLE2int(b, off));
    }

    public static float[] bytesLE2floats(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        float[] val = new float[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2float(b, i << 2);
        return val;
    }

    public static double[] bytesLE2floats2doubles(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        double[] val = new double[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2float(b, i << 2);
        return val;
    }

    public static float bytesBE2float(byte[] b, int off)
    {
        return Float.intBitsToFloat(bytesBE2int(b, off));
    }

    public static float[] bytesBE2floats(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        float[] val = new float[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2float(b, i << 2);
        return val;
    }

    public static double[] bytesBE2floats2doubles(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x3) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        double[] val = new double[b.length >> 2];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2float(b, i << 2);
        return val;
    }

    public static byte[] long2bytesLE(long val, byte[] b, int off)
    {
        b[off] = (byte) val;
        b[off + 1] = (byte) (val >>> 8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
        b[off + 4] = (byte) (val >>> 32);
        b[off + 5] = (byte) (val >>> 40);
        b[off + 6] = (byte) (val >>> 48);
        b[off + 7] = (byte) (val >>> 56);
        return b;
    }

    public static byte[] longs2bytesLE(long[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 2];
        for (int i = 0; i < val.length; i++)
            long2bytesLE(val[i], b, i << 2);
        return b;
    }

    public static byte[] long2bytesBE(long val, byte[] b, int off)
    {
        b[off] = (byte) (val >>> 56);
        b[off + 1] = (byte) (val >>> 48);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 7] = (byte) val;
        return b;
    }

    public static byte[] longs2bytesBE(long[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 3];
        for (int i = 0; i < val.length; i++)
            long2bytesBE(val[i], b, i << 3);
        return b;
    }

    public static long bytesLE2long(byte[] b, int off)
    {
        return ((b[off + 7] & 0xffL) << 56) | ((b[off + 6] & 0xffL) << 48)
                | ((b[off + 5] & 0xffL) << 40) | ((b[off + 4] & 0xffL) << 32)
                | ((b[off + 3] & 0xffL) << 24) | ((b[off + 2] & 0xffL) << 16)
                | ((b[off + 1] & 0xffL) << 8) | (b[off] & 0xffL);
    }

    public static long[] bytesLE2longs(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x7) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        long[] val = new long[b.length >> 3];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2long(b, i << 3);
        return val;
    }

    public static long bytesBE2long(byte[] b, int off)
    {
        return ((b[off] & 0xffL) << 56) | ((b[off + 1] & 0xffL) << 48)
                | ((b[off + 2] & 0xffL) << 40) | ((b[off + 3] & 0xffL) << 32)
                | ((b[off + 4] & 0xffL) << 24) | ((b[off + 5] & 0xffL) << 16)
                | ((b[off + 6] & 0xffL) << 8) | (b[off + 7] & 0xffL);
    }

    public static long[] bytesBE2longs(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x7) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        long[] val = new long[b.length >> 3];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2long(b, i << 3);
        return val;
    }

    public static byte[] double2bytesLE(double val, byte[] b, int off)
    {
        return long2bytesLE(Double.doubleToLongBits(val), b, off);
    }

    public static byte[] doubles2bytesLE(double[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 3];
        for (int i = 0; i < val.length; i++)
            double2bytesLE(val[i], b, i << 3);
        return b;
    }

    public static byte[] double2bytesBE(double val, byte[] b, int off)
    {
        return long2bytesBE(Double.doubleToLongBits(val), b, off);
    }

    public static byte[] doubles2bytesBE(double[] val)
    {
        if (val == null)
            return null;
        byte[] b = new byte[val.length << 3];
        for (int i = 0; i < val.length; i++)
            double2bytesBE(val[i], b, i << 3);
        return b;
    }

    public static double bytesLE2double(byte[] b, int off)
    {
        return Double.longBitsToDouble(bytesLE2long(b, off));
    }

    public static double[] bytesLE2doubles(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x7) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        double[] val = new double[b.length >> 3];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesLE2double(b, i << 3);
        return val;
    }

    public static double bytesBE2double(byte[] b, int off)
    {
        return Double.longBitsToDouble(bytesBE2long(b, off));
    }

    public static double[] bytesBE2doubles(byte[] b)
    {
        if (b == null)
            return null;
        if ((b.length & 0x7) != 0)
            throw new IllegalArgumentException("byte[" + b.length + "]");
        double[] val = new double[b.length >> 3];
        for (int i = 0; i < val.length; i++)
            val[i] = bytesBE2double(b, i << 3);
        return val;
    }

    public static void toggleShortEndian(byte[] b)
    {
        if (b != null)
            toggleShortEndian(b, 0, b.length);
    }

    public static void toggleShortEndian(byte[] b, int off, int len)
    {
        if (b == null || len == 0)
            return;
        int end = off + len;
        if (off < 0 || len < 0 || end > b.length)
            throw new IndexOutOfBoundsException("b.length = " + b.length
                    + ", off = " + off + ", len = " + len);
        if ((len & 1) != 0)
            throw new IllegalArgumentException("len = " + len);
        byte tmp;
        for (int i = off; i < end; i++, i++)
        {
            tmp = b[i];
            b[i] = b[i + 1];
            b[i + 1] = tmp;
        }
    }

    public static void toggleIntEndian(byte[] b)
    {
        if (b != null)
            toggleIntEndian(b, 0, b.length);
    }

    public static void toggleIntEndian(byte[] b, int off, int len)
    {
        if (b == null || len == 0)
            return;
        int end = off + len;
        if (off < 0 || len < 0 || end > b.length)
            throw new IndexOutOfBoundsException("b.length = " + b.length
                    + ", off = " + off + ", len = " + len);
        if ((len & 3) != 0)
            throw new IllegalArgumentException("len = " + len);
        byte tmp;
        for (int i = off; i < end; i++, i++, i++, i++)
        {
            tmp = b[i];
            b[i] = b[i + 3];
            b[i + 3] = tmp;
            tmp = b[i + 1];
            b[i + 1] = b[i + 2];
            b[i + 2] = tmp;
        }
    }

    public static void toggleLongEndian(byte[] b)
    {
        if (b != null)
            toggleLongEndian(b, 0, b.length);
    }

    public static void toggleLongEndian(byte[] b, int off, int len)
    {
        if (b == null || len == 0)
            return;
        int end = off + len;
        if (off < 0 || len < 0 || end > b.length)
            throw new IndexOutOfBoundsException("b.length = " + b.length
                    + ", off = " + off + ", len = " + len);
        if ((len & 7) != 0)
            throw new IllegalArgumentException("len = " + len);
        byte tmp;
        for (int i = off; i < end; i += 8)
        {
            tmp = b[i];
            b[i] = b[i + 7];
            b[i + 7] = tmp;
            tmp = b[i + 1];
            b[i + 1] = b[i + 6];
            b[i + 6] = tmp;
            tmp = b[i + 2];
            b[i + 2] = b[i + 5];
            b[i + 5] = tmp;
            tmp = b[i + 3];
            b[i + 3] = b[i + 4];
            b[i + 4] = tmp;
        }
    }
}
