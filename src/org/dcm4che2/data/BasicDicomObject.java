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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.dcm4che2.util.IntHashtable;
import org.dcm4che2.util.TagUtils;

public class BasicDicomObject extends AbstractDicomObject {

    private static final long serialVersionUID = 1L;

    private static final int INIT_FRAGMENT_CAPACITY = 2;

    private static final int INIT_SEQUENCE_CAPACITY = 10;

    private transient final IntHashtable<DicomElement> table;

    private transient DicomObject defaults;

    private transient DicomObject parent;

    private transient SpecificCharacterSet charset = null;

    private transient long itemOffset = -1L;

    private transient int itemPos = -1;

    private transient boolean cacheGet = true;

    private transient boolean cachePut = false;

    private transient boolean bigEndian = false;

    public BasicDicomObject() {
        this(null, 10);
    }

    public BasicDicomObject(DicomObject defaults) {
        this(defaults, 10);
    }

    public BasicDicomObject(int capacity) {
        this(null, capacity);
    }

    public BasicDicomObject(DicomObject defaults, int capacity) {
        this.defaults = defaults;
        this.table = new IntHashtable<DicomElement>(capacity);
    }

    public final DicomObject getDefaults() {
        return defaults;
    }

    public final void setDefaults(DicomObject defaults) {
        this.defaults = defaults;
    }

    public void clear() {
        table.clear();
        charset = null;
    }

    public final boolean cacheGet() {
        return cacheGet;
    }

    public final void cacheGet(final boolean cacheGet) {
        this.cacheGet = cacheGet;
        accept(new Visitor() {
            public boolean visit(DicomElement attr) {
                if (attr.vr() == VR.SQ && attr.hasItems()) {
                    for (int i = 0, n = attr.countItems(); i < n; ++i) {
                        attr.getDicomObject(i).cacheGet(cacheGet);
                    }
                }
                return true;
            }
        });
    }

    public final boolean cachePut() {
        return cachePut;
    }

    public final void cachePut(final boolean cachePut) {
        this.cachePut = cachePut;
        accept(new Visitor() {
            public boolean visit(DicomElement attr) {
                if (attr.vr() == VR.SQ && attr.hasItems()) {
                    for (int i = 0, n = attr.countItems(); i < n; ++i) {
                        attr.getDicomObject(i).cachePut(cachePut);
                    }
                }
                return true;
            }
        });
    }

    public final boolean bigEndian() {
        return parent != null ? parent.bigEndian() : bigEndian;
    }

    public final void bigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    public int resolveTag(int privateTag, String privateCreator) {
        return resolveTag(privateTag, privateCreator, false);
    }

    public void shareElements() {
        synchronized (SimpleDicomElement.shared) {
            table.accept(new IntHashtable.Visitor() {
                public boolean visit(int key, Object value) {
                    table.put(key, ((DicomElement) value).share());
                    return true;
                }
            });
        }
    }

    public Iterator<DicomElement> iterator() {
        return iterator(0, 0xffffffff);
    }

    public Iterator<DicomElement> iterator(int fromTag, int toTag) {
        if ((fromTag & 0xffffffffL) > (toTag & 0xffffffffL)) {
            throw new IllegalArgumentException("fromTag:"
                    + TagUtils.toString(fromTag) + " > toTag:"
                    + TagUtils.toString(toTag));
        }
        return table.iterator(fromTag, toTag);
    }

    public final DicomObject getParent() {
        return parent;
    }

    public final void setParent(DicomObject parent) {
        this.parent = parent;
    }

    public DicomObject getRoot() {
        return parent == null ? this : parent.getRoot();
    }

    public final int getItemPosition() {
        return itemPos;
    }

    public final void setItemPosition(int itemPos) {
        this.itemPos = itemPos;
    }

    public final long getItemOffset() {
        return itemOffset;
    }

    public final void setItemOffset(long itemOffset) {
        this.itemOffset = itemOffset;
    }

    public SpecificCharacterSet getSpecificCharacterSet() {
        if (charset != null)
            return charset;
        if (parent != null)
            return parent.getSpecificCharacterSet();
        return null;
    }

    public VR vrOf(int tag) {
        if ((tag & 0x0000ffff) == 0) // Group Length
            return VR.UL;

        VRMap vrmap;
        if ((tag & 0x00010000) != 0) { // Private Element
            if ((tag & 0x0000ff00) == 0)
                return VR.LO;

            final String privateCreatorID = getPrivateCreator(tag);
            if (privateCreatorID == null)
                return VR.UN;

            vrmap = VRMap.getPrivateVRMap(privateCreatorID);
        } else {
            vrmap = VRMap.getVRMap();
        }
        return vrmap.vrOf(tag);
    }

    public String nameOf(int tag) {
        if ((tag & 0x0000ffff) == 0) // Group Length
            return ElementDictionary.GROUP_LENGTH;

        ElementDictionary dict;
        if ((tag & 0x00010000) != 0) { // Private Element
            if ((tag & 0x0000ff00) == 0)
                return ElementDictionary.PRIVATE_CREATOR;

            final String privateCreatorID = getPrivateCreator(tag);
            if (privateCreatorID == null)
                return ElementDictionary.getUnkown();

            dict = ElementDictionary.getPrivateDictionary(privateCreatorID);
        } else {
            dict = ElementDictionary.getDictionary();
        }
        return dict.nameOf(tag);
    }

    public String getPrivateCreator(int tag) {
        if (!TagUtils.isPrivateDataElement(tag)
                || TagUtils.isPrivateCreatorDataElement(tag))
            throw new IllegalArgumentException(TagUtils.toString(tag));
        int creatorIDtag = (tag & 0xffff0000) | ((tag >> 8) & 0xff);
        return getAndCacheString(creatorIDtag);
    }

    private String getAndCacheString(int creatorIDtag) {
        DicomElement a = get(creatorIDtag);
        return a == null ? null : a.getString(getSpecificCharacterSet(), true);
    }

    public int resolveTag(int tag, String creator, boolean reserve) {
        if (creator == null || !TagUtils.isPrivateDataElement(tag))
            return tag;
        int gggg0000 = tag & 0xffff0000;
        int idTag = gggg0000 | 0x10;
        int maxIdTag = gggg0000 | 0xff;
        String id;
        while (!creator.equals(id = getAndCacheString(idTag))) {
            if (id == null) {
                if (!reserve)
                    return -1;
                addPrivateCreator(creator, idTag);
                break;
            }
            if (++idTag > maxIdTag)
                throw new IllegalStateException(
                        "No free block to reserve in group "
                                + TagUtils.toString(gggg0000));
        }
        return (tag & 0xffff00ff) | ((idTag & 0xff) << 8);
    }

    private void addPrivateCreator(String privateCreator, int idTag) {
        addInternal(new SimpleDicomElement(idTag, VR.LO, false, VR.LO.toBytes(
                privateCreator, false, getSpecificCharacterSet()),
                privateCreator));
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public int size() {
        return table.size();
    }

    public boolean contains(int tag) {
        return table.get(tag) != null
                || (defaults != null && defaults.contains(tag));
    }

    public DicomElement get(int tag) {
        DicomElement attr = table.get(tag);
        return (attr != null || defaults == null) ? attr : defaults.get(tag);
    }

    public DicomElement remove(int tag) {
        DicomElement attr = (DicomElement) table.remove(tag);
        if (attr != null) {
            if (tag == Tag.SpecificCharacterSet) {
                charset = null;
            }
        }
        return attr;
    }

    DicomElement addInternal(DicomElement a) {
        final int tag = a.tag();
        if ((tag & 0x0000ffff) == 0) {
            // do not include group length elements
            return a;
        }
        table.put(tag, a);
        if (tag == Tag.SpecificCharacterSet) {
            charset = SpecificCharacterSet.valueOf(a.getStrings(null, false));
        }
        return a;
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
    
    @Override
    public int hashCode() {
        return table.hashCode();
    }

    public boolean accept(final Visitor visitor) {
        return table.accept(new IntHashtable.Visitor() {
            public boolean visit(int key, Object value) {
                return visitor.visit((DicomElement) value);
            }
        });
    }

    public void add(DicomElement a) {
        if (a.hasItems()) {
            final int n = a.countItems();
            DicomElement t;
            if (a.vr() == VR.SQ) {
                t = putSequence(a.tag(), n);
                for (int i = 0; i < n; i++) {
                    DicomObject srcItem = a.getDicomObject(i);
                    BasicDicomObject item = new BasicDicomObject(srcItem.size());
                    item.setParent(this);
                    srcItem.copyTo(item);
                    t.addDicomObject(item);
                }
            } else {
                t = putFragments(a.tag(), a.vr(), a.bigEndian(), n);
                for (int i = 0; i < n; i++) {
                    t.addFragment(a.getFragment(i));
                }
            }
            a = t;
        }
        addInternal(a);
    }

    public DicomElement putNull(int tag, VR vr) {
        if (vr == null)
            vr = vrOf(tag);
        DicomElement e = vr == VR.SQ ? (DicomElement) new SequenceDicomElement(
                tag, vr, false, new ArrayList<Object>(0), this)
                : (DicomElement) new SimpleDicomElement(tag, vr, false, null,
                        null);
        return addInternal(e);
    }

    public DicomElement putBytes(int tag, VR vr, byte[] val, boolean bigEndian) {
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, bigEndian, val, null));
    }

    public DicomElement putNestedDicomObject(int tag, DicomObject item) {
        DicomElement a = putSequence(tag, 1);
        a.addDicomObject(item);
        return a;
    }

    public DicomElement putShorts(int tag, VR vr, short[] val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? val : null));
    }

    public DicomElement putInt(int tag, VR vr, int val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? Integer.valueOf(val) : null));
    }

    public DicomElement putInts(int tag, VR vr, int[] val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? val : null));
    }

    public DicomElement putFloat(int tag, VR vr, float val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? new Float(val) : null));
    }

    public DicomElement putFloats(int tag, VR vr, float[] val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? val : null));
    }

    public DicomElement putDouble(int tag, VR vr, double val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? new Double(val) : null));
    }

    public DicomElement putDoubles(int tag, VR vr, double[] val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be), cachePut ? val : null));
    }

    public DicomElement putString(int tag, VR vr, String val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be, getSpecificCharacterSet()),
                cachePut && vr.isSingleValue(val) ? val : null));
    }

    public DicomElement putStrings(int tag, VR vr, String[] val) {
        final boolean be = bigEndian();
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, be, vr.toBytes(val,
                be, getSpecificCharacterSet()), 
                cachePut && vr.containsSingleValues(val) ? val : null));
    }

    public DicomElement putDate(int tag, VR vr, Date val) {
        // no cache of given Date object, to avoid problems
        // with non-zero values for unsignifcant fields
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, false, vr
                .toBytes(val), null));
    }

    public DicomElement putDates(int tag, VR vr, Date[] val) {
        // no cache of given Date objects, to avoid problems
        // with non-zero values for unsignifcant fields
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, false, vr
                .toBytes(val), null));
    }

    public DicomElement putDateRange(int tag, VR vr, DateRange val) {
        // no cache of given DateRange object, to avoid problems
        // with non-zero values for unsignifcant fields
        if (vr == null)
            vr = vrOf(tag);
        return addInternal(new SimpleDicomElement(tag, vr, false, vr
                .toBytes(val), null));
    }

    public DicomElement putSequence(int tag) {
        return putSequence(tag, INIT_SEQUENCE_CAPACITY);
    }

    public DicomElement putSequence(int tag, int capacity) {
        return addInternal(new SequenceDicomElement(tag, VR.SQ, false,
                new ArrayList<Object>(capacity), this));
    }

    public DicomElement putFragments(int tag, VR vr, boolean bigEndian) {
        return putFragments(tag, vr, bigEndian, INIT_FRAGMENT_CAPACITY);
    }

    public DicomElement putFragments(int tag, VR vr, boolean bigEndian,
            int capacity) {
        if (vr == null)
            vr = vrOf(tag);
        if (!(vr instanceof VR.Fragment))
            throw new UnsupportedOperationException();
        return addInternal(new SequenceDicomElement(tag, vr, bigEndian,
                new ArrayList<Object>(capacity), this));
    }

    public void initFileMetaInformation(String tsuid) {
        final String sopClassUID = getString(Tag.SOPClassUID);
        final String sopInstanceUID = getString(Tag.SOPInstanceUID);
        initFileMetaInformation(sopClassUID, sopInstanceUID, tsuid);
    }

    public void initFileMetaInformation(String cuid, String iuid, String tsuid) {
        putBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] { 0, 1 },
                false);
        putString(Tag.MediaStorageSOPClassUID, VR.UI, cuid);
        putString(Tag.MediaStorageSOPInstanceUID, VR.UI, iuid);
        putString(Tag.TransferSyntaxUID, VR.UI, tsuid);
        putString(Tag.ImplementationClassUID, VR.UI, Implementation.classUID());
        putString(Tag.ImplementationVersionName, VR.SH, Implementation
                .versionName());
    }
}
