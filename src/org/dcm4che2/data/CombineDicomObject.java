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
 * Bill Wallace, Agfa Healthcare, 375 Hagey Blvd, Waterloo, ON, CA
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Bill Wallace, <bill.wallace@agfa.com>
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

import java.util.Date;
import java.util.Iterator;

/** 
 * Combines two DICOM objects returning and being written out as though the first
 * one had priority and the values from the second were only used as needed.  Sequences
 * completely replace sequences, so no combining of child elements is done.
 * 
 * @author AMOBE
 *
 */
public class CombineDicomObject extends AbstractDicomObject {
	DicomObject ds1, ds2;

	public CombineDicomObject(DicomObject ds1, DicomObject ds2) {
		this.ds1 = ds1;
		this.ds2 = ds2;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public DicomObject getRoot() {
		throw new UnsupportedOperationException();
	}

	public DicomObject getParent() {
		throw new UnsupportedOperationException();
	}

	public void setParent(DicomObject parent) {
		throw new UnsupportedOperationException();
	}

	public SpecificCharacterSet getSpecificCharacterSet() {
		if( ds1.contains(Tag.SpecificCharacterSet) ) return ds1.getSpecificCharacterSet();
		return ds2.getSpecificCharacterSet();
	}

	public Iterator<DicomElement> iterator() {
		return iterator(0, 0xffffffff);
	}

	public Iterator<DicomElement> iterator(int fromTag, int toTag) {
		Iterator<DicomElement> it1 = ds1.iterator(fromTag,toTag);
		Iterator<DicomElement> it2 = ds2.iterator(fromTag,toTag);
		return new DicomElementCombineIterator(it1,it2);
	}

	public int getItemPosition() {
		throw new UnsupportedOperationException();
	}

	public void setItemPosition(int pos) {
		throw new UnsupportedOperationException();
	}

	public long getItemOffset() {
		throw new UnsupportedOperationException();
	}

	public void setItemOffset(long offset) {
		throw new UnsupportedOperationException();
	}

	public VR vrOf(int tag) {
		if( ds1.contains(tag) ) return ds1.vrOf(tag);
		return ds2.vrOf(tag);
	}

	public String nameOf(int tag) {
		if( ds1.contains(tag) ) return ds1.nameOf(tag);
		return ds2.nameOf(tag);
	}

	public int resolveTag(int tag, String privateCreator) {
		return ds1.resolveTag(tag, privateCreator);
	}

	public int resolveTag(int tag, String privateCreator, boolean reserve) {
		return ds1.resolveTag(tag, privateCreator,reserve);
	}

	public String getPrivateCreator(int tag) {
		if( ds1.contains(tag) ) return ds1.getPrivateCreator(tag);
		return ds2.getPrivateCreator(tag);
	}

	public boolean contains(int tag) {
		return ds1.contains(tag) || ds2.contains(tag);
	}

	@Override
	public DicomObject subSet(DicomObject filter) {
		return new CombineDicomObject(ds1.subSet(filter), ds2.subSet(filter));
	}

	@Override
	public DicomObject subSet(int fromTag, int toTag) {
		return new CombineDicomObject(ds1.subSet(fromTag, toTag), ds2.subSet(fromTag, toTag));
	}

	@Override
	public DicomObject subSet(int[] tags) {
		return new CombineDicomObject(ds1.subSet(tags), ds2.subSet(tags));
	}

	public boolean accept(Visitor visitor) {
		Iterator<DicomElement> it = iterator();
		while(it.hasNext()) {
			DicomElement de = it.next();
			if( !visitor.visit(de)) {
				return false;
			}
		}
		return true;
	}

	public void add(DicomElement attr) {
		ds1.add(attr);
	}

	public DicomElement remove(int tag) {
		return ds1.remove(tag);
	}

	public DicomElement get(int tag) {
		if( ds1.contains(tag) ) return ds1.get(tag);
		return ds2.get(tag);
	}

	public DicomElement putNull(int tag, VR vr) {
		return ds1.putNull(tag, vr);
	}

	public DicomElement putBytes(int tag, VR vr, byte[] val, boolean bigEndian) {
		return ds1.putBytes(tag, vr, val,bigEndian);
	}

	public DicomElement putNestedDicomObject(int tag, DicomObject item) {
		return putNestedDicomObject(tag,item);
	}

	public DicomElement putInt(int tag, VR vr, int val) {
		return ds1.putInt(tag, vr, val);
	}

	public DicomElement putInts(int tag, VR vr, int[] val) {
		return ds1.putInts(tag,vr,val);
	}

	public DicomElement putShorts(int tag, VR vr, short[] val) {
		return ds1.putShorts(tag,vr,val);
	}

	public DicomElement putFloat(int tag, VR vr, float val) {
		return ds1.putFloat(tag,vr,val);
	}

	public DicomElement putFloats(int tag, VR vr, float[] val) {
		return ds1.putFloats(tag,vr,val);
	}

	public DicomElement putDouble(int tag, VR vr, double val) {
		return ds1.putDouble(tag,vr,val);
	}

	public DicomElement putDoubles(int tag, VR vr, double[] val) {
		return ds1.putDoubles(tag,vr,val);
	}

	public DicomElement putString(int tag, VR vr, String val) {
		return ds1.putString(tag,vr,val);
	}

	public DicomElement putStrings(int tag, VR vr, String[] val) {
		return ds1.putStrings(tag,vr,val);
	}

	public DicomElement putDate(int tag, VR vr, Date val) {
		return ds1.putDate(tag,vr,val);
	}

	public DicomElement putDates(int tag, VR vr, Date[] val) {
		return ds1.putDates(tag,vr,val);
	}

	public DicomElement putDateRange(int tag, VR vr, DateRange val) {
		return ds1.putDateRange(tag,vr,val);
	}

	public DicomElement putSequence(int tag) {
		return ds1.putSequence(tag);
	}

	public DicomElement putSequence(int tag, int capacity) {
		return ds1.putSequence(tag,capacity);
	}

	public DicomElement putFragments(int tag, VR vr, boolean bigEndian) {
		return ds1.putFragments(tag,vr,bigEndian);
	}

	public DicomElement putFragments(int tag, VR vr, boolean bigEndian,
			int capacity) {
		return ds1.putFragments(tag,vr,bigEndian,capacity);
	}

	public void shareElements() {
	}

	public boolean cacheGet() {
		return false;
	}

	public void cacheGet(boolean cacheGet) {
	}

	public boolean cachePut() {
		return false;
	}

	public void cachePut(boolean cachePut) {
	}

	public boolean bigEndian() {
		return ds1.bigEndian();
	}

	public void bigEndian(boolean bigEndian) {
		ds1.bigEndian(bigEndian);
	}

	public void initFileMetaInformation(String tsuid) {
		ds1.initFileMetaInformation(tsuid);
	}

	public void initFileMetaInformation(String cuid, String iuid, String tsuid) {
		ds1.initFileMetaInformation(cuid,iuid,tsuid);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DicomObject)) {
			return false;
		}
		DicomObject other = (DicomObject) o;
		Iterator<DicomElement> it = iterator();
		Iterator<DicomElement> otherIt = other.iterator();
		while (it.hasNext() && otherIt.hasNext()) {
			if (!it.next().equals(otherIt.next()))
				return false;
		}
		return !it.hasNext() && !otherIt.hasNext();
	}

}
