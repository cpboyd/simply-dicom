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

package org.dcm4che2.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Reversion$ $Date: 2010-06-16 13:28:59 +0200 (Wed, 16 Jun 2010) $
 * @since Sep 3, 2005
 *
 */
class SimpleDicomElement extends AbstractDicomElement {

    private static final long serialVersionUID = 4049072757025092152L;
    static final WeakHashMap<SimpleDicomElement, WeakReference<SimpleDicomElement>> shared =
        new WeakHashMap<SimpleDicomElement, WeakReference<SimpleDicomElement>>();
    private static final ThreadLocal<char[]> cbuf = new ThreadLocal<char[]>(){
        @Override
        protected char[] initialValue() {
            return new char[64];
        }
    };
    private static final byte[] NULL_VALUE = {};
    private transient byte[] value;
    private volatile transient Object cachedValue;

    public SimpleDicomElement(int tag, VR vr, boolean bigEndian,
            byte[] value, Object cachedValue) {
        super(tag, vr, bigEndian);
        this.value = value == null ? NULL_VALUE : value;
        this.cachedValue = cachedValue;
    }
    
    private void writeObject(ObjectOutputStream s)
    throws IOException {
        s.defaultWriteObject();
        s.writeInt(tag);
        s.writeShort(vr.code());
        s.writeBoolean(bigEndian);
        s.writeInt(value.length);
        if (value.length != 0) {
            s.write(value);
        }
    }

    private void readObject(ObjectInputStream s)
    throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        tag = s.readInt();
        vr = VR.valueOf(s.readUnsignedShort());
        bigEndian = s.readBoolean();
        int len = s.readInt();
        if (len != 0) {
            value = new byte[len];
            s.readFully(value);
        } else {
            value = NULL_VALUE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleDicomElement)) {
            return false;
        }
        SimpleDicomElement other = (SimpleDicomElement) o;
        return tag == other.tag 
             && vr == other.vr 
             && Arrays.equals(value, other.value);     
    }
    
    public DicomElement share() {
        synchronized (shared) {
            WeakReference<SimpleDicomElement> wr = shared.get(this);
            if (wr != null) {
                DicomElement e = wr.get();
                if (e != null) {
                    return e;
                }
            }
            shared.put(this, new WeakReference<SimpleDicomElement>(this));
        }
        return this;
    }
    
    @Override
    protected void appendValue(StringBuffer sb, int maxValLen) {
        vr.promptValue(value, bigEndian, null, cbuf.get(), maxValLen, sb);
    }
    
    @Override
    protected void toggleEndian() {
        vr.toggleEndian(value);
    }

    public final int length() {
        return (value.length + 1) & ~1;
    }

    public final boolean isEmpty() {
        return value.length == 0;
    }

    public int vm(SpecificCharacterSet cs) {
        return vr.vm(value, cs);
    }
    
    public byte[] getBytes() {
        return value;
    }

    public short[] getShorts(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof short[])
                return (short[]) tmp;
        }
        short[] val = vr.toShorts(value, bigEndian);
        if (cache)
            cachedValue = val;
        return val;
    }

    public int getInt(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Integer)
                return ((Integer) tmp).intValue();
        }
        int val = vr.toInt(value, bigEndian);
        if (cache)
            cachedValue = Integer.valueOf(val);
        return val;
    }

    public int[] getInts(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof int[])
                return (int[]) tmp;
        }
        int[] val = vr.toInts(value, bigEndian);
        if (cache)
            cachedValue = val;
        return val;
    }

    public float getFloat(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Float)
                return ((Float) tmp).floatValue();
        }
        float val = vr.toFloat(value, bigEndian);
        if (cache)
            cachedValue = new Float(val);
        return val;
    }

    public float[] getFloats(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof float[])
                return (float[]) tmp;
        }
        float[] val = vr.toFloats(value, bigEndian);
        if (cache)
            cachedValue = val;
        return val;
    }

    public double getDouble(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Double)
                return ((Double) tmp).doubleValue();
        }
        double val = vr.toDouble(value, bigEndian);
        if (cache)
            cachedValue = new Double(val);
        return val;
    }

    public double[] getDoubles(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof double[])
                return (double[]) tmp;
        }
        double[] val = vr.toDoubles(value, bigEndian);
        if (cache)
            cachedValue = val;
        return val;
    }

    public String getString(SpecificCharacterSet cs, boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof String)
                return (String) tmp;
        }
        String val = vr.toString(value, bigEndian, cs);
        if (cache)
            cachedValue = val;
        return val;
    }

    public String[] getStrings(SpecificCharacterSet cs, boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof String[])
                return (String[]) tmp;
        }
        String[] val = vr.toStrings(value, bigEndian, cs);
        if (cache)
            cachedValue = val;
        return val;
    }

    public Date getDate(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Date)
                return (Date) tmp;
        }
        Date val = vr.toDate(value);
        if (cache)
            cachedValue = val;
        return val;
    }

    public Date[] getDates(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Date[])
                return (Date[]) tmp;
        }
        Date[] val = vr.toDates(value);
        if (cache)
            cachedValue = val;
        return val;
    }
    
    public DateRange getDateRange(boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof DateRange)
                return (DateRange) tmp;
        }
        DateRange val = vr.toDateRange(value);
        if (cache)
            cachedValue = val;
        return val;
    }

    public Pattern getPattern(SpecificCharacterSet cs, boolean ignoreCase,
            boolean cache) {
        if (cache) {
            Object tmp = cachedValue;
            if (tmp instanceof Pattern
                    && ((Pattern) tmp).flags() == (ignoreCase 
                            ? (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                                    : 0))
                return (Pattern) tmp;
        }
        Pattern val = vr.toPattern(value, bigEndian, cs, ignoreCase);
        if (cache)
            cachedValue = val;
        return val;
    }

    public final boolean hasItems() {
        return false;
    }

    public final boolean hasDicomObjects() {
        return false;
    }

    public final boolean hasFragments() {
        return false;
    }
    
    public final int countItems() {
        return -1;
    }

    public DicomObject getDicomObject() {
        throw new UnsupportedOperationException();
    }

    public DicomObject getDicomObject(int index) {
        throw new UnsupportedOperationException();
    }

    public DicomObject removeDicomObject(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean removeDicomObject(DicomObject item) {
        throw new UnsupportedOperationException();
    }

    public DicomObject addDicomObject(DicomObject item) {
        throw new UnsupportedOperationException();
    }

    public DicomObject addDicomObject(int index, DicomObject item) {
        throw new UnsupportedOperationException();
    }

    public DicomObject setDicomObject(int index, DicomObject item) {
        throw new UnsupportedOperationException();
    }

    public byte[] getFragment(int index) {
        throw new UnsupportedOperationException();
    }

    public byte[] removeFragment(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean removeFragment(byte[] b) {
        throw new UnsupportedOperationException();
    }

    public byte[] addFragment(byte[] b) {
        throw new UnsupportedOperationException();
    }

    public byte[] addFragment(int index, byte[] b) {
        throw new UnsupportedOperationException();
    }

    public byte[] setFragment(int index, byte[] b) {
        throw new UnsupportedOperationException();
    }

    public DicomElement filterItems(DicomObject filter) {
        throw new UnsupportedOperationException();
    }

    public String getValueAsString(SpecificCharacterSet cs, int truncate) {
        if (value == null || value.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(64);
        vr.promptValue(value, bigEndian, cs, cbuf.get(), truncate, sb);
        return sb.toString();
    }
    
}
