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
 * Portions created by the Initial Developer are Copyright (C) 2008
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
package org.dcm4che2.data;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.dcm4che2.data.DicomObject.Visitor;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * 
 * @version $Revision$ $Date$
 * @since Aug 6, 2008
 */
public class DicomObjects {

    private DicomObjects() {
        // Suppresses default constructor, ensuring non-instantiability.
    }

    /**
     * Returns an unmodifiable view of the specified dicom object. This method
     * allows modules to provide users with "read-only" access to internal dicom
     * objects. Query operations on the returned dicom object "read through" to
     * the specified dicom object, and attempts to modify the returned dicom
     * object result in an <tt>UnsupportedOperationException</tt>. If the
     * specified dicom object is already an unmodifiable view returned by
     * a previous invocation of this method, the specified dicom object is
     * returned.
     * <p>
     * 
     * @param dcmobj
     *            the dicom object for which an unmodifiable view is to be
     *            returned.
     * @return an unmodifiable view of the specified dicom object.
     */
    public static DicomObject unmodifiableDicomObject(DicomObject dcmobj) {
        return dcmobj instanceof UnmodifiableDicomObject ? dcmobj
                : new UnmodifiableDicomObject(dcmobj);
    }

    static class UnmodifiableDicomObject implements DicomObject {

        private static final long serialVersionUID = 4384087053472506817L;

        private final DicomObject dcmobj;

        public UnmodifiableDicomObject(DicomObject dcmobj) {
            if (dcmobj == null)
                throw new NullPointerException();

            this.dcmobj = dcmobj;
        }

        @Override
        public String toString() {
            return dcmobj.toString();
        }

        @Override
        public int hashCode() {
            return dcmobj.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return dcmobj.equals(other);
        }

        public boolean accept(Visitor visitor) {
            return dcmobj.accept(new UnmodifiableVisitor(visitor));
        }

        public void add(DicomElement attr) {
            throw new UnsupportedOperationException();
        }

        public boolean bigEndian() {
            return dcmobj.bigEndian();
        }

        public void bigEndian(boolean bigEndian) {
            throw new UnsupportedOperationException();
        }

        public boolean cacheGet() {
            return dcmobj.cacheGet();
        }

        public void cacheGet(boolean cacheGet) {
            dcmobj.cacheGet(cacheGet);
        }

        public boolean cachePut() {
            return dcmobj.cachePut();
        }

        public void cachePut(boolean cachePut) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public DicomObject command() {
            return new UnmodifiableDicomObject(dcmobj.command());
        }

        public Iterator<DicomElement> commandIterator() {
            return new UnmodifiabledIterator(dcmobj.commandIterator());
        }

        public boolean contains(int tag) {
            return dcmobj.contains(tag);
        }

        public boolean containsAll(DicomObject keys) {
            return dcmobj.containsAll(keys);
        }

        public boolean containsValue(int tag) {
            return dcmobj.containsValue(tag);
        }

        public void copyTo(DicomObject destination) {
            dcmobj.copyTo(destination);
        }

        public void copyTo(DicomObject destination, boolean resolveDestinationPrivateTags) {
            dcmobj.copyTo(destination, resolveDestinationPrivateTags);
        }

        public DicomObject dataset() {
            return new UnmodifiableDicomObject(dcmobj.dataset());
        }

        public Iterator<DicomElement> datasetIterator() {
            return new UnmodifiabledIterator(dcmobj.datasetIterator());
        }

        public DicomObject exclude(int[] tags) {
            return new UnmodifiableDicomObject(dcmobj.exclude(tags));
        }

        /** 
         * @see org.dcm4che2.data.DicomObject#excludePrivate()
         */
        public DicomObject excludePrivate() {
            return new UnmodifiableDicomObject(dcmobj.excludePrivate());
        }

        public DicomObject fileMetaInfo() {
            return new UnmodifiableDicomObject(dcmobj.fileMetaInfo());
        }

        public Iterator<DicomElement> fileMetaInfoIterator() {
            return new UnmodifiabledIterator(dcmobj.fileMetaInfoIterator());
        }

        public DicomElement get(int tag) {
            DicomElement e = dcmobj.get(tag);
            return e != null ? new UnmodifiableDicomElement(e) : null;
        }

        public DicomElement get(int tag, VR vr) {
            DicomElement e = dcmobj.get(tag, vr);
            return e != null ? new UnmodifiableDicomElement(e) : null;
        }

        public DicomElement get(int[] tagPath) {
            DicomElement e = dcmobj.get(tagPath);
            return e != null ? new UnmodifiableDicomElement(e) : null;
        }

        public DicomElement get(int[] tagPath, VR vr) {
            DicomElement e = dcmobj.get(tagPath, vr);
            return e != null ? new UnmodifiableDicomElement(e) : null;
        }

        public byte[] getBytes(int tag, boolean bigEndian) {
            return dcmobj.getBytes(tag, bigEndian);
        }

        public byte[] getBytes(int tag) {
            return dcmobj.getBytes(tag);
        }

        public byte[] getBytes(int[] tagPath, boolean bigEndian) {
            return dcmobj.getBytes(tagPath, bigEndian);
        }

        public byte[] getBytes(int[] tagPath) {
            return dcmobj.getBytes(tagPath);
        }

        public Date getDate(int tag, Date defVal) {
            return dcmobj.getDate(tag, defVal);
        }

        public Date getDate(int tag, VR vr, Date defVal) {
            return dcmobj.getDate(tag, vr, defVal);
        }

        public Date getDate(int daTag, int tmTag, Date defVal) {
            return dcmobj.getDate(daTag, tmTag, defVal);
        }

        public Date getDate(int daTag, int tmTag) {
            return dcmobj.getDate(daTag, tmTag);
        }

        public Date getDate(int tag) {
            return dcmobj.getDate(tag);
        }

        public Date getDate(int tag, VR vr) {
            return dcmobj.getDate(tag, vr);
        }

        public Date getDate(int[] tagPath, Date defVal) {
            return dcmobj.getDate(tagPath, defVal);
        }

        public Date getDate(int[] tagPath, VR vr, Date defVal) {
            return dcmobj.getDate(tagPath, vr, defVal);
        }

        public Date getDate(int[] itemPath, int daTag, int tmTag, Date defVal) {
            return dcmobj.getDate(itemPath, daTag, tmTag, defVal);
        }

        public Date getDate(int[] itemPath, int daTag, int tmTag) {
            return dcmobj.getDate(itemPath, daTag, tmTag);
        }

        public Date getDate(int[] tagPath) {
            return dcmobj.getDate(tagPath);
        }

        public Date getDate(int[] tagPath, VR vr) {
            return dcmobj.getDate(tagPath, vr);
        }

        public DateRange getDateRange(int tag, DateRange defVal) {
            return dcmobj.getDateRange(tag, defVal);
        }

        public DateRange getDateRange(int tag, VR vr, DateRange defVal) {
            return dcmobj.getDateRange(tag, vr, defVal);
        }

        public DateRange getDateRange(int daTag, int tmTag, DateRange defVal) {
            return dcmobj.getDateRange(daTag, tmTag, defVal);
        }

        public DateRange getDateRange(int daTag, int tmTag) {
            return dcmobj.getDateRange(daTag, tmTag);
        }

        public DateRange getDateRange(int tag) {
            return dcmobj.getDateRange(tag);
        }

        public DateRange getDateRange(int tag, VR vr) {
            return dcmobj.getDateRange(tag, vr);
        }

        public DateRange getDateRange(int[] tagPath, DateRange defVal) {
            return dcmobj.getDateRange(tagPath, defVal);
        }

        public DateRange getDateRange(int[] tagPath, VR vr, DateRange defVal) {
            return dcmobj.getDateRange(tagPath, vr, defVal);
        }

        public DateRange getDateRange(int[] itemPath, int daTag, int tmTag,
                DateRange defVal) {
            return dcmobj.getDateRange(itemPath, daTag, tmTag, defVal);
        }

        public DateRange getDateRange(int[] itemPath, int daTag, int tmTag) {
            return dcmobj.getDateRange(itemPath, daTag, tmTag);
        }

        public DateRange getDateRange(int[] tagPath) {
            return dcmobj.getDateRange(tagPath);
        }

        public DateRange getDateRange(int[] tagPath, VR vr) {
            return dcmobj.getDateRange(tagPath, vr);
        }

        public Date[] getDates(int tag, Date[] defVal) {
            return dcmobj.getDates(tag, defVal);
        }

        public Date[] getDates(int tag, VR vr, Date[] defVal) {
            return dcmobj.getDates(tag, vr, defVal);
        }

        public Date[] getDates(int daTag, int tmTag, Date[] defVal) {
            return dcmobj.getDates(daTag, tmTag, defVal);
        }

        public Date[] getDates(int daTag, int tmTag) {
            return dcmobj.getDates(daTag, tmTag);
        }

        public Date[] getDates(int tag) {
            return dcmobj.getDates(tag);
        }

        public Date[] getDates(int tag, VR vr) {
            return dcmobj.getDates(tag, vr);
        }

        public Date[] getDates(int[] tagPath, Date[] defVal) {
            return dcmobj.getDates(tagPath, defVal);
        }

        public Date[] getDates(int[] tagPath, VR vr, Date[] defVal) {
            return dcmobj.getDates(tagPath, vr, defVal);
        }

        public Date[] getDates(int[] itemPath, int daTag, int tmTag,
                Date[] defVal) {
            return dcmobj.getDates(itemPath, daTag, tmTag, defVal);
        }

        public Date[] getDates(int[] itemPath, int daTag, int tmTag) {
            return dcmobj.getDates(itemPath, daTag, tmTag);
        }

        public Date[] getDates(int[] tagPath) {
            return dcmobj.getDates(tagPath);
        }

        public Date[] getDates(int[] tagPath, VR vr) {
            return dcmobj.getDates(tagPath, vr);
        }

        public double getDouble(int tag, double defVal) {
            return dcmobj.getDouble(tag, defVal);
        }

        public double getDouble(int tag, VR vr, double defVal) {
            return dcmobj.getDouble(tag, vr, defVal);
        }

        public double getDouble(int tag) {
            return dcmobj.getDouble(tag);
        }

        public double getDouble(int tag, VR vr) {
            return dcmobj.getDouble(tag, vr);
        }

        public double getDouble(int[] tagPath, double defVal) {
            return dcmobj.getDouble(tagPath, defVal);
        }

        public double getDouble(int[] tagPath, VR vr, double defVal) {
            return dcmobj.getDouble(tagPath, vr, defVal);
        }

        public double getDouble(int[] tagPath) {
            return dcmobj.getDouble(tagPath);
        }

        public double getDouble(int[] tagPath, VR vr) {
            return dcmobj.getDouble(tagPath, vr);
        }

        public double[] getDoubles(int tag, double[] defVal) {
            return dcmobj.getDoubles(tag, defVal);
        }

        public double[] getDoubles(int tag, VR vr, double[] defVal) {
            return dcmobj.getDoubles(tag, vr, defVal);
        }

        public double[] getDoubles(int tag) {
            return dcmobj.getDoubles(tag);
        }

        public double[] getDoubles(int tag, VR vr) {
            return dcmobj.getDoubles(tag, vr);
        }

        public double[] getDoubles(int[] tagPath, double[] defVal) {
            return dcmobj.getDoubles(tagPath, defVal);
        }

        public double[] getDoubles(int[] tagPath, VR vr, double[] defVal) {
            return dcmobj.getDoubles(tagPath, vr, defVal);
        }

        public double[] getDoubles(int[] tagPath) {
            return dcmobj.getDoubles(tagPath);
        }

        public double[] getDoubles(int[] tagPath, VR vr) {
            return dcmobj.getDoubles(tagPath, vr);
        }

        public float getFloat(int tag, float defVal) {
            return dcmobj.getFloat(tag, defVal);
        }

        public float getFloat(int tag, VR vr, float defVal) {
            return dcmobj.getFloat(tag, vr, defVal);
        }

        public float getFloat(int tag) {
            return dcmobj.getFloat(tag);
        }

        public float getFloat(int tag, VR vr) {
            return dcmobj.getFloat(tag, vr);
        }

        public float getFloat(int[] tagPath, float defVal) {
            return dcmobj.getFloat(tagPath, defVal);
        }

        public float getFloat(int[] tagPath, VR vr, float defVal) {
            return dcmobj.getFloat(tagPath, vr, defVal);
        }

        public float getFloat(int[] tagPath) {
            return dcmobj.getFloat(tagPath);
        }

        public float getFloat(int[] tagPath, VR vr) {
            return dcmobj.getFloat(tagPath, vr);
        }

        public float[] getFloats(int tag, float[] defVal) {
            return dcmobj.getFloats(tag, defVal);
        }

        public float[] getFloats(int tag, VR vr, float[] defVal) {
            return dcmobj.getFloats(tag, vr, defVal);
        }

        public float[] getFloats(int tag) {
            return dcmobj.getFloats(tag);
        }

        public float[] getFloats(int tag, VR vr) {
            return dcmobj.getFloats(tag, vr);
        }

        public float[] getFloats(int[] tagPath, float[] defVal) {
            return dcmobj.getFloats(tagPath, defVal);
        }

        public float[] getFloats(int[] tagPath, VR vr, float[] defVal) {
            return dcmobj.getFloats(tagPath, vr, defVal);
        }

        public float[] getFloats(int[] tagPath) {
            return dcmobj.getFloats(tagPath);
        }

        public float[] getFloats(int[] tagPath, VR vr) {
            return dcmobj.getFloats(tagPath, vr);
        }

        public int getInt(int tag, int defVal) {
            return dcmobj.getInt(tag, defVal);
        }

        public int getInt(int tag, VR vr, int defVal) {
            return dcmobj.getInt(tag, vr, defVal);
        }

        public int getInt(int tag) {
            return dcmobj.getInt(tag);
        }

        public int getInt(int tag, VR vr) {
            return dcmobj.getInt(tag, vr);
        }

        public int getInt(int[] tagPath, int defVal) {
            return dcmobj.getInt(tagPath, defVal);
        }

        public int getInt(int[] tagPath, VR vr, int defVal) {
            return dcmobj.getInt(tagPath, vr, defVal);
        }

        public int getInt(int[] tagPath) {
            return dcmobj.getInt(tagPath);
        }

        public int getInt(int[] tagPath, VR vr) {
            return dcmobj.getInt(tagPath, vr);
        }

        public int[] getInts(int tag, int[] defVal) {
            return dcmobj.getInts(tag, defVal);
        }

        public int[] getInts(int tag, VR vr, int[] defVal) {
            return dcmobj.getInts(tag, vr, defVal);
        }

        public int[] getInts(int tag) {
            return dcmobj.getInts(tag);
        }

        public int[] getInts(int tag, VR vr) {
            return dcmobj.getInts(tag, vr);
        }

        public int[] getInts(int[] tagPath, int[] defVal) {
            return dcmobj.getInts(tagPath, defVal);
        }

        public int[] getInts(int[] tagPath, VR vr, int[] defVal) {
            return dcmobj.getInts(tagPath, vr, defVal);
        }

        public int[] getInts(int[] tagPath) {
            return dcmobj.getInts(tagPath);
        }

        public int[] getInts(int[] tagPath, VR vr) {
            return dcmobj.getInts(tagPath, vr);
        }

        public long getItemOffset() {
            return dcmobj.getItemOffset();
        }

        public int getItemPosition() {
            return dcmobj.getItemPosition();
        }

        public DicomObject getNestedDicomObject(int tag) {
            DicomObject item = dcmobj.getNestedDicomObject(tag);
            return item != null ? new UnmodifiableDicomObject(item) : null;
        }

        public DicomObject getNestedDicomObject(int[] itemPath) {
            DicomObject item = dcmobj.getNestedDicomObject(itemPath);
            return item != null ? new UnmodifiableDicomObject(item) : null;
        }

        public DicomObject getParent() {
            DicomObject parent = dcmobj.getParent();
            return parent != null ? new UnmodifiableDicomObject(parent) : null;
        }

        public String getPrivateCreator(int tag) {
            return dcmobj.getPrivateCreator(tag);
        }

        public DicomObject getRoot() {
            return dcmobj.isRoot() ? this : new UnmodifiableDicomObject(dcmobj
                    .getRoot());
        }

        public short[] getShorts(int tag, short[] defVal) {
            return dcmobj.getShorts(tag, defVal);
        }

        public short[] getShorts(int tag, VR vr, short[] defVal) {
            return dcmobj.getShorts(tag, vr, defVal);
        }

        public short[] getShorts(int tag) {
            return dcmobj.getShorts(tag);
        }

        public short[] getShorts(int tag, VR vr) {
            return dcmobj.getShorts(tag, vr);
        }

        public short[] getShorts(int[] tagPath, short[] defVal) {
            return dcmobj.getShorts(tagPath, defVal);
        }

        public short[] getShorts(int[] tagPath, VR vr, short[] defVal) {
            return dcmobj.getShorts(tagPath, vr, defVal);
        }

        public short[] getShorts(int[] tagPath) {
            return dcmobj.getShorts(tagPath);
        }

        public short[] getShorts(int[] tagPath, VR vr) {
            return dcmobj.getShorts(tagPath, vr);
        }

        public SpecificCharacterSet getSpecificCharacterSet() {
            return dcmobj.getSpecificCharacterSet();
        }

        public String getString(int tag, String defVal) {
            return dcmobj.getString(tag, defVal);
        }

        public String getString(int tag, VR vr, String defVal) {
            return dcmobj.getString(tag, vr, defVal);
        }

        public String getString(int tag) {
            return dcmobj.getString(tag);
        }

        public String getString(int tag, VR vr) {
            return dcmobj.getString(tag, vr);
        }

        public String getString(int[] tagPath, String defVal) {
            return dcmobj.getString(tagPath, defVal);
        }

        public String getString(int[] tagPath, VR vr, String defVal) {
            return dcmobj.getString(tagPath, vr, defVal);
        }

        public String getString(int[] tagPath) {
            return dcmobj.getString(tagPath);
        }

        public String getString(int[] tagPath, VR vr) {
            return dcmobj.getString(tagPath, vr);
        }

        public String[] getStrings(int tag, String[] defVal) {
            return dcmobj.getStrings(tag, defVal);
        }

        public String[] getStrings(int tag, VR vr, String[] defVal) {
            return dcmobj.getStrings(tag, vr, defVal);
        }

        public String[] getStrings(int tag) {
            return dcmobj.getStrings(tag);
        }

        public String[] getStrings(int tag, VR vr) {
            return dcmobj.getStrings(tag, vr);
        }

        public String[] getStrings(int[] tagPath, String[] defVal) {
            return dcmobj.getStrings(tagPath, defVal);
        }

        public String[] getStrings(int[] tagPath, VR vr, String[] defVal) {
            return dcmobj.getStrings(tagPath, vr, defVal);
        }

        public String[] getStrings(int[] tagPath) {
            return dcmobj.getStrings(tagPath);
        }

        public String[] getStrings(int[] tagPath, VR vr) {
            return dcmobj.getStrings(tagPath, vr);
        }

        public void initFileMetaInformation(String cuid, String iuid,
                String tsuid) {
            throw new UnsupportedOperationException();
        }

        public void initFileMetaInformation(String tsuid) {
            throw new UnsupportedOperationException();
        }

        public boolean isEmpty() {
            return dcmobj.isEmpty();
        }

        public boolean isRoot() {
            return dcmobj.isRoot();
        }

        public Iterator<DicomElement> iterator() {
            return new UnmodifiabledIterator(dcmobj.iterator());
        }

        public Iterator<DicomElement> iterator(int fromTag, int toTag) {
            return new UnmodifiabledIterator(dcmobj.iterator(fromTag, toTag));
        }

        public boolean matches(DicomObject keys, boolean ignoreCaseOfPN) {
            return dcmobj.matches(keys, ignoreCaseOfPN);
        }

        public String nameOf(int tag) {
            return dcmobj.nameOf(tag);
        }

        public DicomElement putBytes(int tag, VR vr, byte[] val,
                boolean bigEndian) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putBytes(int tag, VR vr, byte[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putBytes(int[] tagPath, VR vr, byte[] val,
                boolean bigEndian) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putBytes(int[] tagPath, VR vr, byte[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDate(int tag, VR vr, Date val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDate(int[] tagPath, VR vr, Date val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDateRange(int tag, VR vr, DateRange val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDateRange(int[] tagPath, VR vr, DateRange val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDates(int tag, VR vr, Date[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDates(int[] tagPath, VR vr, Date[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDouble(int tag, VR vr, double val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDouble(int[] tagPath, VR vr, double val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDoubles(int tag, VR vr, double[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putDoubles(int[] tagPath, VR vr, double[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFloat(int tag, VR vr, float val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFloat(int[] tagPath, VR vr, float val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFloats(int tag, VR vr, float[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFloats(int[] tagPath, VR vr, float[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFragments(int tag, VR vr, boolean bigEndian,
                int capacity) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFragments(int tag, VR vr, boolean bigEndian) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFragments(int[] tagPath, VR vr,
                boolean bigEndian, int capacity) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putFragments(int[] tagPath, VR vr, boolean bigEndian) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putInt(int tag, VR vr, int val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putInt(int[] tagPath, VR vr, int val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putInts(int tag, VR vr, int[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putInts(int[] tagPath, VR vr, int[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putNestedDicomObject(int tag, DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putNestedDicomObject(int[] tagPath, DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putNull(int tag, VR vr) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putNull(int[] tagPath, VR vr) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putSequence(int tag, int capacity) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putSequence(int tag) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putSequence(int[] tagPath, int capacity) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putSequence(int[] tagPath) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putShorts(int tag, VR vr, short[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putShorts(int[] tagPath, VR vr, short[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putString(int tag, VR vr, String val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putString(int[] tagPath, VR vr, String val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putStrings(int tag, VR vr, String[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement putStrings(int[] tagPath, VR vr, String[] val) {
            throw new UnsupportedOperationException();
        }

        public DicomElement remove(int tag) {
            throw new UnsupportedOperationException();
        }

        public DicomElement remove(int[] tagPath) {
            throw new UnsupportedOperationException();
        }

        public int resolveTag(int tag, String privateCreator, boolean reserve) {
            if (reserve) {
                throw new UnsupportedOperationException();
            }
            return dcmobj.resolveTag(tag, privateCreator, reserve);
        }

        public int resolveTag(int tag, String privateCreator) {
            return dcmobj.resolveTag(tag, privateCreator);
        }

        public void serializeElements(ObjectOutputStream oos)
                throws IOException {
            dcmobj.serializeElements(oos);
        }

        public void setItemOffset(long offset) {
            throw new UnsupportedOperationException();
        }

        public void setItemPosition(int pos) {
            throw new UnsupportedOperationException();
        }

        public void setParent(DicomObject parent) {
            throw new UnsupportedOperationException();
        }

        public void shareElements() {
            dcmobj.shareElements();
        }

        public int size() {
            return dcmobj.size();
        }

        public DicomObject subSet(DicomObject filter) {
            return new UnmodifiableDicomObject(dcmobj.subSet(filter));
        }

        public DicomObject subSet(int fromTag, int toTag) {
            return new UnmodifiableDicomObject(dcmobj.subSet(fromTag, toTag));
        }

        public DicomObject subSet(int[] tags) {
            return new UnmodifiableDicomObject(dcmobj.subSet(tags));
        }

        public int toStringBuffer(StringBuffer sb,
                DicomObjectToStringParam param) {
            return dcmobj.toStringBuffer(sb, param);
        }

        public int vm(int tag) {
            return dcmobj.vm(tag);
        }

        public VR vrOf(int tag) {
            return dcmobj.vrOf(tag);
        }

    }

    static class UnmodifiableVisitor implements Visitor {

        private final Visitor visitor;

        public UnmodifiableVisitor(Visitor visitor) {
            if (visitor == null)
                throw new NullPointerException();

            this.visitor = visitor;
        }

        public boolean visit(DicomElement e) {
            return visitor.visit(new UnmodifiableDicomElement(e));
        }

    }

    static class UnmodifiableDicomElement implements DicomElement {

        private static final long serialVersionUID = -5205393560442114448L;

        private final DicomElement e;

        public UnmodifiableDicomElement(DicomElement e) {
            if (e == null)
                throw new NullPointerException();

            this.e = e;
        }

        public DicomObject addDicomObject(DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public DicomObject addDicomObject(int index, DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public byte[] addFragment(byte[] b) {
            throw new UnsupportedOperationException();
        }

        public byte[] addFragment(int index, byte[] b) {
            throw new UnsupportedOperationException();
        }

        public boolean bigEndian() {
            return e.bigEndian();
        }

        public DicomElement bigEndian(boolean bigEndian) {
            if (e.bigEndian() == bigEndian || e.isEmpty())
                return this;
            throw new UnsupportedOperationException();
        }

        public int countItems() {
            return e.countItems();
        }

        public DicomElement filterItems(DicomObject filter) {
            DicomElement filteredItems = e.filterItems(filter);
            return filteredItems != this ? new UnmodifiableDicomElement(
                    filteredItems) : this;
        }

        public byte[] getBytes() {
            return e.getBytes();
        }

        public Date getDate(boolean cache) {
            return e.getDate(cache);
        }

        public DateRange getDateRange(boolean cache) {
            return e.getDateRange(cache);
        }

        public Date[] getDates(boolean cache) {
            return e.getDates(cache);
        }

        public DicomObject getDicomObject() {
            DicomObject item = e.getDicomObject();
            return item != null ? new UnmodifiableDicomObject(item) : null;
        }

        public DicomObject getDicomObject(int index) {
            return new UnmodifiableDicomObject(e.getDicomObject(index));
        }

        public double getDouble(boolean cache) {
            return e.getDouble(cache);
        }

        public double[] getDoubles(boolean cache) {
            return e.getDoubles(cache);
        }

        public float getFloat(boolean cache) {
            return e.getFloat(cache);
        }

        public float[] getFloats(boolean cache) {
            return e.getFloats(cache);
        }

        public byte[] getFragment(int index) {
            return e.getFragment(index);
        }

        public int getInt(boolean cache) {
            return e.getInt(cache);
        }

        public int[] getInts(boolean cache) {
            return e.getInts(cache);
        }

        public Pattern getPattern(SpecificCharacterSet cs, boolean ignoreCase,
                boolean cache) {
            return e.getPattern(cs, ignoreCase, cache);
        }

        public short[] getShorts(boolean cache) {
            return e.getShorts(cache);
        }

        public String getString(SpecificCharacterSet cs, boolean cache) {
            return e.getString(cs, cache);
        }

        public String[] getStrings(SpecificCharacterSet cs, boolean cache) {
            return e.getStrings(cs, cache);
        }

        public boolean hasDicomObjects() {
            return e.hasDicomObjects();
        }

        public boolean hasFragments() {
            return e.hasFragments();
        }

        public boolean hasItems() {
            return e.hasItems();
        }

        public boolean isEmpty() {
            return e.isEmpty();
        }

        public int length() {
            return e.length();
        }

        public boolean removeDicomObject(DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public DicomObject removeDicomObject(int index) {
            throw new UnsupportedOperationException();
        }

        public boolean removeFragment(byte[] b) {
            throw new UnsupportedOperationException();
        }

        public byte[] removeFragment(int index) {
            throw new UnsupportedOperationException();
        }

        public DicomObject setDicomObject(int index, DicomObject item) {
            throw new UnsupportedOperationException();
        }

        public byte[] setFragment(int index, byte[] b) {
            throw new UnsupportedOperationException();
        }

        public DicomElement share() {
            return e.share();
        }

        public int tag() {
            return e.tag();
        }

        public StringBuffer toStringBuffer(StringBuffer sb, int maxValLen) {
            return e.toStringBuffer(sb, maxValLen);
        }

        public int vm(SpecificCharacterSet cs) {
            return e.vm(cs);
        }

        public VR vr() {
            return e.vr();
        }

        public String getValueAsString(SpecificCharacterSet cs, int truncate) {
            return e.getValueAsString(cs, truncate);
        }

    }

    static class UnmodifiabledIterator implements Iterator<DicomElement> {
        private final Iterator<DicomElement> itr;

        public UnmodifiabledIterator(Iterator<DicomElement> itr) {
            if (itr == null)
                throw new NullPointerException();

            this.itr = itr;
        }

        public boolean hasNext() {
            return itr.hasNext();
        }

        public DicomElement next() {
            return new UnmodifiableDicomElement(itr.next());
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
