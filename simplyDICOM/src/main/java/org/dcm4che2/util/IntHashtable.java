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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hash table implementation, which uses integers as keys.
 * 
 * @param <T>
 *            the value type.
 */
public class IntHashtable<T> {

    private static final float HIGH_WATER_FACTOR = 0.4F;
    private static final float LOW_WATER_FACTOR = 0.0F;
    private static final int[] PRIMES = { 7, 17, 37, 67, 131, 257, 521, 1031,
            2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309,
            1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879,
            134217757, 268435459, 536870923, 1073741827, 2147483647 };

    private static int primeIndex(int size) {
        for (int i = 0; i < PRIMES.length; ++i) {
            if (size < PRIMES[i]) {
                return i;
            }
        }
        return PRIMES.length - 1;
    }

    public interface Visitor {
        boolean visit(int key, Object value);
    }

    private int primeIndex;
    private int highWaterMark;
    private int lowWaterMark;
    private int count;
    private int[] keyList;
    private T[] values;
    private T value0;
    private volatile int[] sortedKeys;

    public IntHashtable() {
        initialize(3);
    }

    public IntHashtable(int initialSize) {
        initialize(primeIndex((int) (initialSize / HIGH_WATER_FACTOR)));
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void clear() {
        count = 0;
        Arrays.fill(keyList, 0);
        Arrays.fill(values, null);
        value0 = null;
        sortedKeys = null;
    }

    public void put(int key, T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (key == 0) {
            if (value0 == null)
                ++count;
            value0 = value;
            return;
        }
        sortedKeys = null;
        int index = find(key);
        if (values[index] == null)
            count++;
        keyList[index] = key;
        values[index] = value;
        if (count > highWaterMark)
            rehash();
    }

    public T get(int key) {
        return key == 0 ? value0 : values[find(key)];
    }

    public Object remove(int key) {
        Object retval = null;
        if (key == 0) {
            if (value0 != null) {
                retval = value0;
                value0 = null;
                --count;
            }
        } else {
            final int index = find(key);
            if (values[index] != null) {
                retval = values[index];
                sortedKeys = null;
                values[index] = null;
                --count;
                if (count < lowWaterMark) {
                    rehash();
                }
            }
        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof IntHashtable))
            return false;

        IntHashtable<T> other = (IntHashtable<T>) that;
        if (other.size() != count) {
            return false;
        }
        for (int i = 0; i < values.length; ++i) {
            Object v = values[i];
            if (v != null && !v.equals(other.get(keyList[i])))
                return false;
        }
        return equals(value0, other.value0);// && equals(value_1,
                                            // other.value_1);
    }

    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o1);
    }

    @Override
    public int hashCode() {
        if (count == 0)
            return 0;

        int h = 0;
        for (int i = 0; i < values.length; i++) {
            Object v = values[i];
            if (v != null)
                h += keyList[i] ^ v.hashCode();
        }
        if (value0 != null) {
            h = 37 * h + value0.hashCode();
        }
        return h;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object result = super.clone();
        values = values.clone();
        keyList = keyList.clone();
        sortedKeys = sortedKeys != null ? (int[]) sortedKeys.clone() : null;
        return result;
    }

    public boolean accept(Visitor visitor) {
        if (value0 != null) {
            if (!visitor.visit(0, value0))
                return false;
        }
        for (int i = 0; i < keyList.length; i++) {
            Object v = values[i];
            if (v != null) {
                if (!visitor.visit(keyList[i], v))
                    return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator(int start, int end) {
        return new Itr(start, end);
    }

    @SuppressWarnings("unchecked")
    private void initialize(int primeIndex) {
        this.primeIndex = Math.min(Math.max(0, primeIndex), PRIMES.length - 1);
        int initialSize = PRIMES[primeIndex];
        values = (T[]) new Object[initialSize];
        keyList = new int[initialSize];
        sortedKeys = null;
        count = 0;
        if (value0 != null)
            ++count;
        lowWaterMark = (int) (initialSize * LOW_WATER_FACTOR);
        highWaterMark = (int) (initialSize * HIGH_WATER_FACTOR);
    }

    private void rehash() {
        T[] oldValues = values;
        int[] oldkeyList = keyList;
        int newPrimeIndex = primeIndex;
        if (count > highWaterMark) {
            ++newPrimeIndex;
        } else if (count < lowWaterMark) {
            newPrimeIndex -= 2;
        }
        initialize(newPrimeIndex);
        for (int i = oldkeyList.length - 1; i >= 0; --i) {
            T value = oldValues[i];
            if (value != null) {
                int key = oldkeyList[i];
                int index = find(key);
                keyList[index] = key;
                values[index] = value;
                ++count;
            }
        }
    }

    private int find(int key) {
        int firstDeleted = -1;
        int i = (key ^ 0x4000000) % keyList.length;
        if (i < 0)
            i = -i;
        int d = 0;
        do {
            int hash = keyList[i];
            if (hash == key) {
                return i;
            }
            if (values[i] == null) {
                if (hash == 0) {
                    return firstDeleted >= 0 ? firstDeleted : i;
                } else if (firstDeleted < 0) {
                    firstDeleted = i;
                }
            }
            if (d == 0) {
                d = (key % (keyList.length - 1));
                if (d < 0)
                    d = -d;
                ++d;
            }

            i = (i + d) % keyList.length;
        } while (i != firstDeleted);
        return i;
    }

    private final class Itr implements Iterator<T> {
        int endIndex;
        int index;
        T next;
        /** Contains the sorted keys for the iterator to use - makes it quite a bit more efficient to 
         * access each instance as no synchronization is required per-access. 
         */
        int[] itrSortedKeys;

        private Itr(int start, int end) {
            if ((start & 0xffffffffL) > (end & 0xffffffffL))
                throw new IllegalArgumentException("start:" + start + ", end:"
                        + end);
            if (isEmpty())
                return;
            if (end == 0) {
                if (start == 0)
                    next = value0;
                return;
            }
            if (sortedKeys==null) {
            	// Create a copy to operate on
            	int[] tSortedKeys = new int[keyList.length]; 
                System.arraycopy(keyList, 0, tSortedKeys, 0, keyList.length);
                Arrays.sort(tSortedKeys);
                sortedKeys = tSortedKeys;
            }
            itrSortedKeys = sortedKeys;
            endIndex = Arrays.binarySearch(itrSortedKeys, end);
            if (endIndex < 0) {
                if (endIndex == -1)
                    endIndex = itrSortedKeys.length - 1;
                else
                    endIndex = -(endIndex + 1) - 1;
            }
            index = Arrays.binarySearch(itrSortedKeys, start != 0 ? start : 1);
            if (index < 0) {
                index = -(index + 1) % itrSortedKeys.length;
            }
            if (start == 0 && value0 != null) {
                next = value0;
                --index;
            } else {
                if (index != incIndex(endIndex)) {
                    while ((next = get(itrSortedKeys[index])) == null
                            && index != endIndex)
                        index = incIndex(index);
                }
            }
        }

        private int incIndex(int index) {
            return (index + 1) % itrSortedKeys.length;
        }

        public boolean hasNext() {
            return next != null;
        }

        public T next() {
            if (next == null)
                throw new NoSuchElementException();
            T v = next;
            next = null;
            while (next == null && index != endIndex) {
                index = incIndex(index);
                next = get(itrSortedKeys[index]);
            }
            return v;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
