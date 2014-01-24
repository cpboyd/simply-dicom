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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 16544 $ $Date: 2012-01-13 15:55:22 +0100 (Fri, 13 Jan 2012) $
 * @since Aug, 2005
 * 
 */
public interface DicomObject extends Serializable {
    /**
     * Returns number of elements in this Dicom Object.
     * 
     * @return number of elements in this Dicom Object.
     */
    int size();

    /**
     * Returns <tt>true</tt> if this Dicom Object contains no elements.
     * 
     * @return <tt>true</tt> if this Dicom Object contains no elements.
     */
    boolean isEmpty();

    /**
     * Removes all elements from this Dicom Object.
     */
    void clear();

    /**
     * @return the root Data Set, if this Data Set is contained within a
     *         Sequence Element of another Data Set, otherwise <tt>this</tt>.
     */
    DicomObject getRoot();

    /**
     * Returns <tt>true</tt> if this is not a nested Data Set.
     * 
     * @return <tt>true</tt> if this is not a nested Data Set.
     */
    boolean isRoot();

    /**
     * Returns the Data Set containing this Data Set in a Sequence Element, or
     * <tt>null</tt> if this is not a nested Data Set.
     * 
     * @return the Data Set containing this Data Set in a Sequence Element, or
     *         <tt>null</tt> if this is not a nested Data Set.
     */
    DicomObject getParent();

    void setParent(DicomObject parent);
    
    /**
     * Returns the Specific Character Set defined by Attribute <i>Specific
     * Character Set (0008,0005)</i> of this or the root Data Set, if this is a
     * Nested Data Set containing in a Sequence Element. Returns <tt>null</tt>
     * if Attribute Specific Character Set (0008,0005) is not present.
     * 
     * @return the Specific Character Set defined by Attribute <i>Specific
     *         Character Set (0008,0005)</i> or <tt>null</tt> if the
     *         Attribute is not present.
     */
    SpecificCharacterSet getSpecificCharacterSet();

    /**
     * Returns an iterator over all elements in this Dicom Object.
     * 
     * @return an <tt>Iterator</tt> over all elements in this Dicom Object.
     */
    Iterator<DicomElement> iterator();

    /**
     * Returns an iterator over elements in the given range in this Dicom
     * Object.
     * 
     * @param fromTag
     *            minimal (group, element) as 8 byte integer: ggggeeee, of first
     *            element included.
     * @param toTag
     *            maximal (group, element) as 8 byte integer: ggggeeee, of last
     *            element included.
     * @return an <tt>Iterator</tt> over elements in this Dicom Object.
     */
    Iterator<DicomElement> iterator(int fromTag, int toTag);

    /**
     * Returns an iterator over Command elements (0000, eeee) in this Dicom
     * Object.
     * 
     * @return an <tt>Iterator</tt> over Command elements (0000, eeee) in this
     *         Dicom Object.
     */
    Iterator<DicomElement> commandIterator();

    /**
     * Returns an iterator over File Meta Information elements (0002, eeee) in
     * this Dicom Object.
     * 
     * @return an <tt>Iterator</tt> over File Meta Information elements (0002,
     *         eeee) in this Dicom Object.
     */
    Iterator<DicomElement> fileMetaInfoIterator();

    /**
     * Returns an iterator over Data elements (group tag > 2) in this Dicom
     * Object.
     * 
     * @return an <tt>Iterator</tt> over Data elements in this Dicom Object.
     */
    Iterator<DicomElement> datasetIterator();

    /**
     * Returns zero-based position of this Nested Data in the Sequence Element,
     * or <tt>-1</tt> if this is not a Nested Data Set.
     * 
     * @return zero-based position of this Nested Data in the Sequence Element
     *         Set, or <tt>-1</tt> if this is not a Nested Data Set.
     */
    int getItemPosition();

    /**
     * Sets zero-based position of this Nested Data in the Sequence Element.
     * Typically only internally used.
     * 
     * @pos zero-based position of this Nested Data in the Sequence Element.
     */
    void setItemPosition(int pos);

    /**
     * Returns the offset of the Sequence Item containing this Nested Data Set
     * in the Dicom Stream from/to which the Data Set was read/written, or
     * <tt>-1</tt> if this is not a Nested Data Set or it was not (yet) read
     * from/written to a Dicom Stream.
     * 
     * @return the offset of the Sequence Item containing this Nested Data Set
     *         in the Dicom Stream, or <tt>-1</tt>.
     */
    long getItemOffset();

    /**
     * Sets the offset of the Sequence Item encoding this Nested Data Set in a
     * Dicom Stream. Typically only internally used.
     * 
     * @param offset
     *            of the Sequence Item containing this Nested Data Set in a
     *            Dicom Stream.
     */
    void setItemOffset(long offset);

    /**
     * Returns Value Representation of Attribute with specified (private) tag
     * or @link{VR#UN} if no entry was found in configured @link{VRMap}s.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return Value Representation of Attribute with specified (private) tag.
     */
    VR vrOf(int tag);

    /**
     * Returns name of Attribute with specified (private) tag or 
     * @link{ElementDictionary#getUnkown()} if no entry was found in configured 
     * @link{ElementDictionary}s.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return name of Attribute with specified (private) tag.
     */
    String nameOf(int tag);

    /**
     * Resolve existing private tag. Invokes
     * 
     * @link{#resolveTag(int, String, boolean) resolveTag}(tag, privateCreator,
     *                       <tt>false</tt>).
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param privateCreator
     *            private creator identifier
     * @return resolved tag or <tt>-1</tt>, if no tags are reserved for
     *         privateCreator.
     */
    int resolveTag(int tag, String privateCreator);

    /**
     * Resolves private tag. If the group number of the specified tag is odd (=
     * private tag) and privateCreator != null, searches for the first private
     * creator data element in (gggg,0010-00FF) which matches privateCreator,
     * and returns <i>ggggEEee</i> with <i>EE</i> the element number of the
     * matching private creator data element and <i>ee</i> the two lower bytes
     * of the element number of the specified tag.> If no matching private
     * creator data element in (gggg,0010-00FF) is found, and reserve=<tt>true</tt>,
     * the specified privateCreator is inserted in the first unused private
     * creator data element, and <i>ggggEEee</i> with <i>EE</i> the element
     * number the new inserted private creator data element is returned. If
     * reserve=<tt>false</tt>, <tt>-1</tt> is returned.<br>
     * If the group number of the specified tag is even (= standard tag) or
     * privateCreator == null, tag is returned unmodified.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param privateCreator
     *            private creator identifier
     * @return resolved tag or <tt>-1</tt>, if no tags are reserved for
     *         privateCreator and reserve=<tt>false</tt>.
     */
    int resolveTag(int tag, String privateCreator, boolean reserve);

    /**
     * Returns private creator identifier, for given private tag.
     * 
     * @param tag
     *            (group, element) of private tag as 8 byte integer: ggggeeee
     * @return Returns private creator identifier, for given private tag.
     * @throws IllegalArgumentExcepion
     *             if tag is not a private tag or if itself a Private Creator
     *             Data Element (gggg,00EE).
     */
    String getPrivateCreator(int tag);

    /**
     * Returns Number of Values of the specified Element or <tt>-1</tt> if
     * there is no such Element in this Dicom Object.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return Number of Values of the specified Element or <tt>-1</tt> if
     *         there is no such Element in this Dicom Object.
     */
    int vm(int tag);

    /**
     * Returns <tt>true</tt>, if this Dicom Object contains the specified
     * Element.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return <tt>true</tt>, if this DicomObject contains the specified
     *         Element.
     */
    boolean contains(int tag);

    /**
     * Returns <tt>true</tt>, if this Dicom Object contains the specified
     * Element with a value length > 0.
     * 
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return <tt>true</tt>, if this DicomObject contains the specified
     *         Element with a value length > 0.
     */
    boolean containsValue(int tag);

    /**
     * Returns <tt>true</tt> if this Dicom Object contains all of the elements
     * in the specified Dicom Object.
     *
     * @param  keys Dicom Object to be checked for containment in this Dicom Object.
     * @return <tt>true</tt> if this Dicom Object contains all of the elements
     *         in the specified collection
     */
    boolean containsAll(DicomObject keys);

    /**
     * Calls @link{Visitor#visit} for each element in this Dataset. Returns
     * <tt>false</tt>, if @link{Visitor#visit} returns <tt>false</tt> for any
     * element.
     * 
     * @param visitor
     *            <i>Visitor</i> object, which method @link{Visitor#visit} is
     *            called for each element in this Dataset.
     * @return <tt>true</tt> if @link{Visitor#visit} returns <tt>true</tt> for 
     * all elements of this dataset, <tt>false</tt> if @link{Visitor#visit}
     * returns <tt>false</tt> for any element.
     */
    boolean accept(Visitor visitor);

    /**
     * Visitor object passed to @link{#accept}.
     */
    public interface Visitor {
        /**
         * Called for each element in the visited DicomObject. If it returns
         * <tt>false</tt>, no further element is visited and
         * 
         * @link{DicomObject#accept} returns also <tt>false</tt>.
         * 
         * 
         * @param e
         *            Dicom Element to visit
         * @return <tt>true</tt> to continue, <tt>false</tt> to terminate
         *         traversal by
         * @link{DicomObject#accept}.
         */
        boolean visit(DicomElement e);
    }

    /**
     * @param attr
     */
    void add(DicomElement attr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DicomElement remove(int tag);

    /**
     * @param tagPath
     * @return
     */
    DicomElement remove(int[] tagPath);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DicomElement get(int tag);

    DicomElement get(int tag, VR vr);

    /**
     * @param tagPath
     * @return
     */
    DicomElement get(int[] tagPath);

    DicomElement get(int[] tagPath, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param bigEndian
     * @return
     */
    byte[] getBytes(int tag, boolean bigEndian);

    byte[] getBytes(int tag);
    
    /**
     * @param tagPath
     * @param bigEndian
     * @return
     */
    byte[] getBytes(int[] tagPath, boolean bigEndian);

    byte[] getBytes(int[] tagPath);
    
    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DicomObject getNestedDicomObject(int tag);

    /**
     * @param itemPath
     * @return
     */
    DicomObject getNestedDicomObject(int[] itemPath);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    int getInt(int tag);

    int getInt(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param defVal
     *            TODO
     * @return
     */
    int getInt(int tag, int defVal);

    int getInt(int tag, VR vr, int defVal);

    /**
     * @param tagPath
     * @return
     */
    int getInt(int[] tagPath);

    int getInt(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    int getInt(int[] tagPath, int defVal);

    int getInt(int[] tagPath, VR vr, int defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    int[] getInts(int tag);

    int[] getInts(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    int[] getInts(int tag, int[] defVal);

    int[] getInts(int tag, VR vr, int[] defVal);

    /**
     * @param tagPath
     * @return
     */
    int[] getInts(int[] tagPath);

    int[] getInts(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    int[] getInts(int[] tagPath, int[] defVal);

    int[] getInts(int[] tagPath, VR vr, int[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    short[] getShorts(int tag);

    short[] getShorts(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    short[] getShorts(int tag, short[] defVal);

    short[] getShorts(int tag, VR vr, short[] defVal);

    /**
     * @param tagPath
     * @return
     */
    short[] getShorts(int[] tagPath);

    short[] getShorts(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    short[] getShorts(int[] tagPath, short[] defVal);

    short[] getShorts(int[] tagPath, VR vr, short[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    float getFloat(int tag);

    float getFloat(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    float getFloat(int tag, float defVal);

    float getFloat(int tag, VR vr, float defVal);

    /**
     * @param tagPath
     * @return
     */
    float getFloat(int[] tagPath);

    float getFloat(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    float getFloat(int[] tagPath, float defVal);

    float getFloat(int[] tagPath, VR vr, float defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    float[] getFloats(int tag);

    float[] getFloats(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    float[] getFloats(int tag, float[] defVal);

    float[] getFloats(int tag, VR vr, float[] defVal);

    /**
     * @param tagPath
     * @return
     */
    float[] getFloats(int[] tagPath);

    float[] getFloats(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    float[] getFloats(int[] tagPath, float[] defVal);

    float[] getFloats(int[] tagPath, VR vr, float[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    double getDouble(int tag);

    double getDouble(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    double getDouble(int tag, double defVal);

    double getDouble(int tag, VR vr, double defVal);

    /**
     * @param tagPath
     * @return
     */
    double getDouble(int[] tagPath);

    double getDouble(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    double getDouble(int[] tagPath, double defVal);

    double getDouble(int[] tagPath, VR vr, double defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    double[] getDoubles(int tag);

    double[] getDoubles(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    double[] getDoubles(int tag, double[] defVal);

    double[] getDoubles(int tag, VR vr, double[] defVal);

    /**
     * @param tagPath
     * @return
     */
    double[] getDoubles(int[] tagPath);

    double[] getDoubles(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    double[] getDoubles(int[] tagPath, double[] defVal);

    double[] getDoubles(int[] tagPath, VR vr, double[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    String getString(int tag);

    String getString(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    String getString(int tag, String defVal);

    String getString(int tag, VR vr, String defVal);

    /**
     * @param tagPath
     * @return
     */
    String getString(int[] tagPath);

    String getString(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    String getString(int[] tagPath, String defVal);

    String getString(int[] tagPath, VR vr, String defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    String[] getStrings(int tag);

    String[] getStrings(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    String[] getStrings(int tag, String[] defVal);

    String[] getStrings(int tag, VR vr, String[] defVal);

    /**
     * @param tagPath
     * @return
     */
    String[] getStrings(int[] tagPath);

    String[] getStrings(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    String[] getStrings(int[] tagPath, String[] defVal);

    String[] getStrings(int[] tagPath, VR vr, String[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    Date getDate(int tag);

    Date getDate(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    Date getDate(int tag, Date defVal);

    Date getDate(int tag, VR vr, Date defVal);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    Date getDate(int daTag, int tmTag);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    Date getDate(int daTag, int tmTag, Date defVal);

    /**
     * @param tagPath
     * @return
     */
    Date getDate(int[] tagPath);

    Date getDate(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    Date getDate(int[] tagPath, Date defVal);

    Date getDate(int[] tagPath, VR vr, Date defVal);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    Date getDate(int[] itemPath, int daTag, int tmTag);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    Date getDate(int[] itemPath, int daTag, int tmTag, Date defVal);
    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    Date[] getDates(int tag);

    Date[] getDates(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    Date[] getDates(int tag, Date[] defVal);

    Date[] getDates(int tag, VR vr, Date[] defVal);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    Date[] getDates(int daTag, int tmTag);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    Date[] getDates(int daTag, int tmTag, Date[] defVal);

    /**
     * @param tagPath
     * @return
     */
    Date[] getDates(int[] tagPath);

    Date[] getDates(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    Date[] getDates(int[] tagPath, Date[] defVal);

    Date[] getDates(int[] tagPath, VR vr, Date[] defVal);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    Date[] getDates(int[] itemPath, int daTag, int tmTag);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    Date[] getDates(int[] itemPath, int daTag, int tmTag, Date[] defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DateRange getDateRange(int tag);

    DateRange getDateRange(int tag, VR vr);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DateRange getDateRange(int tag, DateRange defVal);

    DateRange getDateRange(int tag, VR vr, DateRange defVal);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    DateRange getDateRange(int daTag, int tmTag);

    /**
     * @param daTag
     * @param tmTag
     * @return
     */
    DateRange getDateRange(int daTag, int tmTag, DateRange defVal);

    /**
     * @param tagPath
     * @return
     */
    DateRange getDateRange(int[] tagPath);

    DateRange getDateRange(int[] tagPath, VR vr);

    /**
     * @param tagPath
     * @return
     */
    DateRange getDateRange(int[] tagPath, DateRange defVal);

    DateRange getDateRange(int[] tagPath, VR vr, DateRange defVal);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    DateRange getDateRange(int[] itemPath, int daTag, int tmTag);

    /**
     * @param itemPath
     * @param daTag
     * @param tmTag
     * @return
     */
    DateRange getDateRange(int[] itemPath, int daTag, int tmTag,
            DateRange defVal);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @return
     */
    DicomElement putNull(int tag, VR vr);

    DicomElement putBytes(int tag, VR vr, byte[] val);
    
    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @param bigEndian
     * @return
     */
    DicomElement putBytes(int tag, VR vr, byte[] val, boolean bigEndian);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param item
     * @return
     */
    DicomElement putNestedDicomObject(int tag, DicomObject item);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putInt(int tag, VR vr, int val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putInts(int tag, VR vr, int[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putShorts(int tag, VR vr, short[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putFloat(int tag, VR vr, float val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putFloats(int tag, VR vr, float[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDouble(int tag, VR vr, double val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDoubles(int tag, VR vr, double[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putString(int tag, VR vr, String val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putStrings(int tag, VR vr, String[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDate(int tag, VR vr, Date val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDates(int tag, VR vr, Date[] val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDateRange(int tag, VR vr, DateRange val);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @return
     */
    DicomElement putSequence(int tag);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param capacity
     * @return
     */
    DicomElement putSequence(int tag, int capacity);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param bigEndian
     * @return
     */
    DicomElement putFragments(int tag, VR vr, boolean bigEndian);

    /**
     * @param tag
     *            (group, element) as 8 byte integer: ggggeeee.
     * @param vr
     * @param bigEndian
     * @param capacity
     * @return
     */
    DicomElement putFragments(int tag, VR vr, boolean bigEndian, int capacity);

    /**
     * @param tagPath
     * @param vr
     * @return
     */
    DicomElement putNull(int[] tagPath, VR vr);


    DicomElement putBytes(int[] tagPath, VR vr, byte[] val);
    
    /**
     * @param tagPath
     * @param vr
     * @param val
     * @param bigEndian
     * @return
     */
    DicomElement putBytes(int[] tagPath, VR vr, byte[] val, boolean bigEndian);

    /**
     * @param tagPath
     * @param item
     * @return
     */
    DicomElement putNestedDicomObject(int[] tagPath, DicomObject item);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putInt(int[] tagPath, VR vr, int val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putInts(int[] tagPath, VR vr, int[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putShorts(int[] tagPath, VR vr, short[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putFloat(int[] tagPath, VR vr, float val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putFloats(int[] tagPath, VR vr, float[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDouble(int[] tagPath, VR vr, double val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDoubles(int[] tagPath, VR vr, double[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putString(int[] tagPath, VR vr, String val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putStrings(int[] tagPath, VR vr, String[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDate(int[] tagPath, VR vr, Date val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDates(int[] tagPath, VR vr, Date[] val);

    /**
     * @param tagPath
     * @param vr
     * @param val
     * @return
     */
    DicomElement putDateRange(int[] tagPath, VR vr, DateRange val);

    /**
     * @param tagPath
     * @return
     */
    DicomElement putSequence(int[] tagPath);

    /**
     * @param tagPath
     * @param capacity
     * @return
     */
    DicomElement putSequence(int[] tagPath, int capacity);

    /**
     * @param tagPath
     * @param vr
     * @param bigEndian
     * @return
     */
    DicomElement putFragments(int[] tagPath, VR vr, boolean bigEndian);

    /**
     * @param tagPath
     * @param vr
     * @param bigEndian
     * @param capacity
     * @return
     */
    DicomElement putFragments(int[] tagPath, VR vr, boolean bigEndian, int capacity);

    /**
     * 
     */
    void shareElements();

    /**
     * @param oos
     * @throws IOException
     */
    void serializeElements(ObjectOutputStream oos) throws IOException;

    /**
     * @param destination
     */
    void copyTo(DicomObject destination);

    /**
     * @param destination
	 * @param resolveDestinationPrivateTags
     */
    void copyTo(DicomObject destination, boolean resolveDestinationPrivateTags);

    /**
     * @param keys
     * @param ignoreCaseOfPN
     * @return
     */
    boolean matches(DicomObject keys, boolean ignoreCaseOfPN);

    /**
     * @return
     */
    boolean cacheGet();

    /**
     * @param cacheGet
     */
    void cacheGet(boolean cacheGet);

    /**
     * @return
     */
    boolean cachePut();

    /**
     * @param cachePut
     */
    void cachePut(boolean cachePut);

    /**
     * @return
     */
    boolean bigEndian();

    /**
     * @param bigEndian
     */
    void bigEndian(boolean bigEndian);

    /**
     * @return
     */
    DicomObject command();

    /**
     * @return
     */
    DicomObject dataset();

    /**
     * @return
     */
    DicomObject fileMetaInfo();

    /**
     * @param filter
     * @return
     */
    DicomObject subSet(DicomObject filter);

    /**
     * @param fromTag
     * @param toTag
     * @return
     */
    DicomObject subSet(int fromTag, int toTag);

    /**
     * @param tags
     * @return
     */
    DicomObject subSet(int[] tags);

    /**
     * @param tags
     * @return
     */
    DicomObject exclude(int[] tags);

    /**
     * Note that this method does not remove private attributes from
     * <strong>this</strong> DicomObject, rather, it returns a view of the
     * DicomObject without private attributes. You you may store this view to a
     * file.
     * 
     * @return a new DicomObject without private attributes.
     */
    DicomObject excludePrivate();

    /**
     * @param tsuid
     */
    void initFileMetaInformation(String tsuid);

    /**
     * @param cuid
     * @param iuid
     * @param tsuid
     */
    void initFileMetaInformation(String cuid, String iuid, String tsuid);

    /**
     * @param sb
     * @param param
     * @return
     */
    int toStringBuffer(StringBuffer sb, DicomObjectToStringParam param);
}
