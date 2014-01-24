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

import java.util.Arrays;

public abstract class GenericNumericArray {
    
    private static class ByteNumericArray extends GenericNumericArray {
        private byte[] arr = null;
        
        public ByteNumericArray(byte[] arr) {
            this.arr = arr;
        }

        @Override
        public Object getArray() {
            return arr;
        }

        @Override
        public Number getArrayItem(int index) {
            return arr[index];
        }

        @Override
        public void setArrayItem(int index, Number value) {
            arr[index] = value.byteValue();
        }
        
        @Override
        public void fillRange(int fromIndex, int toIndex, Number val) {
            Arrays.fill(arr, fromIndex, toIndex, val.byteValue());
        }

        @Override
        public int length() {
            return arr.length;
        }
    }

    private static class ShortNumericArray extends GenericNumericArray {
        public ShortNumericArray(short[] arr) {
            this.arr = arr;
        }

        private short[] arr = null;

        @Override
        public Object getArray() {
            return arr;
        }

        @Override
        public Number getArrayItem(int index) {
            return arr[index];
        }

        @Override
        public void setArrayItem(int index, Number value) {
            arr[index] = value.shortValue();
        }
        
        @Override
        public void fillRange(int fromIndex, int toIndex, Number val) {
            Arrays.fill(arr, fromIndex, toIndex, val.shortValue());
        }

        @Override
        public int length() {
            return arr.length;
        }
    }

    public static GenericNumericArray create(byte[] array) {
        if (array == null)
            throw new NullPointerException("Array is NULL");
        return new ByteNumericArray(array);
    }

    public static GenericNumericArray create(short[] array) {
        if (array == null)
            throw new NullPointerException("Array is NULL");
        return new ShortNumericArray(array);
    }

    public static GenericNumericArray getByteArray(int size) {
        return create(new byte[size]);
    }
    
    public static GenericNumericArray getShortArray(int size) {
        return create(new short[size]);
    }

    public abstract Object getArray();
    
    public abstract Number getArrayItem(int index);
    
    public abstract void setArrayItem(int index, Number value);
    public abstract void fillRange(int fromIndex, int toIndex, Number val);

    public abstract int length();
}
