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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Reversion$ $Date: 2012-12-13 03:22:07 +0100 (Thu, 13 Dec 2012) $
 * @since Sep 3, 2005
 */
class SequenceDicomElement extends AbstractDicomElement {

	private static final long serialVersionUID = 3690757302122656054L;

	private transient List<Object> items;
	private transient DicomObject parent;

	public SequenceDicomElement(int tag, VR vr, boolean bigEndian, List<Object> items,
			DicomObject parent) {
		super(tag, vr, bigEndian);
		if (items == null)
			throw new NullPointerException();
		this.items = items;
		this.parent = parent;
	}

	// used by ElementSerializer 
	void setParentDicomObject(DicomObject parent) {
		this.parent = parent;
		for (int i = 0, n = items.size(); i < n; ++i) {
			Object item = items.get(i);
			if (item instanceof DicomObject) {
				((DicomObject) item).setParent(parent);
			}            
		}
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeInt(tag);
		s.writeShort(vr.code());
		s.writeBoolean(bigEndian);
		int size = items.size();
		s.writeInt(size);
		for (int i = 0; i < size; ++i) {
			Object item = items.get(i);
			if (item instanceof DicomObject) {
				s.writeObject(new ElementSerializer((DicomObject) item));
			} else {
				s.writeObject(item);
			}
		}
	}

	private void readObject(ObjectInputStream s) throws IOException,
	ClassNotFoundException {
		s.defaultReadObject();
		tag = s.readInt();
		vr = VR.valueOf(s.readUnsignedShort());
		bigEndian = s.readBoolean();
		int n = s.readInt();
		items = new ArrayList<Object>(n);
		for (int i = 0; i < n; ++i) {
			items.add(s.readObject());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SequenceDicomElement)) {
			return false;
		}
		SequenceDicomElement other = (SequenceDicomElement) o;

		if (items == other.items) {
			return true;
		}

		if (tag() != other.tag() ||  vr() != other.vr() || items.size() != other.items.size()){
			return false;
		}
		
		for (int i = 0; i < items.size(); i++) {
			// quick memory address check
			if (items.get(i) == other.items.get(i)) {
				continue;
			} 
			VR vr = vr();
			if (vr == VR.OB || vr == VR.OW){							
				byte[] item = (byte[])items.get(i);
				byte[] otherItem = (byte[]) other.items.get(i);

				if (!Arrays.equals(item, otherItem)) {
					return false;
				}
			} else if (!items.get(i).equals(other.items.get(i))) {				
				// VR was not OB or OW, and comparator otherwise did not succeed					
				return false;				
			}
		}        
		return true;
	}

	public DicomElement share() {
		if (hasDicomObjects()) {
			for (int i = 0, n = items.size(); i < n; ++i) {
				((DicomObject) items.get(i)).shareElements();
			}
		}
		return this;
	}

	@Override
	protected void appendValue(StringBuffer sb, int maxValLen) {
		final int size = items.size();
		if (size != 0) {
			if (size == 1)
				sb.append("1 item");
			else {
				sb.append(size).append(" items");
			}
		}
	}

	@Override
	protected void toggleEndian() {
		if (!hasDicomObjects()) {
			for (int i = 0, n = items.size(); i < n; ++i) {
				vr.toggleEndian((byte[]) items.get(i));
			}
		}
	}

	public final int length() {
		return items.isEmpty() ? 0 : -1;
	}

	public final boolean isEmpty() {
		return items.isEmpty();
	}

	public int vm(SpecificCharacterSet cs) {
		return items.isEmpty() ? 0 : 1;
	}

	public byte[] getBytes() {
		throw new UnsupportedOperationException();
	}

	public short getShort(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public short[] getShorts(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public int getInt(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public int[] getInts(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public float getFloat(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public float[] getFloats(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public double getDouble(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public double[] getDoubles(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public String getString(SpecificCharacterSet cs, boolean cache) {
		throw new UnsupportedOperationException();
	}

	public String[] getStrings(SpecificCharacterSet cs, boolean cache) {
		throw new UnsupportedOperationException();
	}

	public Date getDate(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public Date[] getDates(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public DateRange getDateRange(boolean cache) {
		throw new UnsupportedOperationException();
	}

	public Pattern getPattern(SpecificCharacterSet cs, boolean ignoreCase,
			boolean cache) {
		throw new UnsupportedOperationException();
	}

	public final boolean hasItems() {
		return true;
	}

	public final int countItems() {
		return items.size();
	}

	public final boolean hasDicomObjects() {
		return vr == VR.SQ;
	}

	public final boolean hasFragments() {
		return vr != VR.SQ;
	}

	public DicomObject getDicomObject() {
		return (DicomObject) (!items.isEmpty() ? items.get(0) : null);
	}

	public DicomObject getDicomObject(int index) {
		return (DicomObject) items.get(index);
	}

	public DicomObject removeDicomObject(int index) {
		DicomObject ret = (DicomObject) items.remove(index);
		ret.setParent(null);
		updateItemPositions(index);
		return ret;
	}

	public boolean removeDicomObject(DicomObject item) {
		if (!items.remove(item)) {
			return false;
		}
		item.setParent(null);
		updateItemPositions(item.getItemPosition()-1);
		return true;
	}

	private void updateItemPositions(int index) {
		for (int i = index, n = countItems(); i < n; ++i) {
			getDicomObject(i).setItemPosition(i + 1);
		}
	}

	public DicomObject addDicomObject(DicomObject item) {
		if (vr != VR.SQ)
			throw new UnsupportedOperationException();
		if (item == null)
			throw new NullPointerException();
		items.add(item);
		item.setParent(parent);
		item.setItemPosition(countItems());
		return item;
	}

	public DicomObject addDicomObject(int index, DicomObject item) {
		if (vr != VR.SQ)
			throw new UnsupportedOperationException();
		if (item == null)
			throw new NullPointerException();
		items.add(index, item);
		item.setParent(parent);
		updateItemPositions(index);
		return item;
	}

	public DicomObject setDicomObject(int index, DicomObject item) {
		if (vr != VR.SQ)
			throw new UnsupportedOperationException();
		if (item == null)
			throw new NullPointerException();
		items.set(index, item);
		item.setParent(parent);
		item.setItemPosition(index + 1);
		return item;
	}

	public byte[] getFragment(int index) {
		return (byte[]) items.get(index);
	}

	public byte[] removeFragment(int index) {
		return (byte[]) items.remove(index);
	}

	public boolean removeFragment(byte[] b) {
		return items.remove(b);
	}

	public byte[] addFragment(byte[] b) {
		if (hasDicomObjects())
			throw new UnsupportedOperationException();
		if (b == null)
			throw new NullPointerException();
		items.add(b);
		return b;
	}

	public byte[] addFragment(int index, byte[] b) {
		if (hasDicomObjects())
			throw new UnsupportedOperationException();
		if (b == null)
			throw new NullPointerException();
		items.add(index, b);
		return b;
	}

	public byte[] setFragment(int index, byte[] b) {
		if (hasDicomObjects())
			throw new UnsupportedOperationException();
		if (b == null)
			throw new NullPointerException();
		items.set(index, b);
		return b;
	}

	public DicomElement filterItems(DicomObject filter) {
		if (!hasDicomObjects())
			throw new UnsupportedOperationException();
		if (filter == null)
			return this;
		int count = countItems();
		List<Object> tmp = new ArrayList<Object>(count);
		for (int i = 0; i < count; i++) {
			tmp.add(getDicomObject(i).subSet(filter));
		}
		return new SequenceDicomElement(tag, vr, bigEndian, tmp, parent);
	}

	public String getValueAsString(SpecificCharacterSet cs, int truncate) {
		throw new UnsupportedOperationException();
	}

}
