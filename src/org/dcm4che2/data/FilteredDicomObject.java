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

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.dcm4che2.util.TagUtils;

abstract class FilteredDicomObject extends AbstractDicomObject
{

    private static final long serialVersionUID = -3541514478162843135L;

    static final class Include extends FilteredDicomObject
    {
        private static final long serialVersionUID = 1L;
        final int[] tags;

        public Include(DicomObject attrs, int[] tags)
        {
            super(attrs);
            this.tags = tags.clone();
            Arrays.sort(this.tags);
        }

        @Override
        protected boolean filter(int tag)
        {
            return Arrays.binarySearch(tags, tag) >= 0;
        }

        @Override
        public void clear() {
            for (int i = 0; i < tags.length; i++) {
                attrs.remove(tags[i]);
            }
        }
    }

    static final class Exclude extends FilteredDicomObject
    {
        private static final long serialVersionUID = 1L;
        final int[] tags;

        public Exclude(DicomObject attrs, int[] tags)
        {
            super(attrs);
            this.tags = tags.clone();
            Arrays.sort(this.tags);
        }

        @Override
        protected boolean filter(int tag)
        {
            return Arrays.binarySearch(tags, tag) < 0;
        }
    }

    static final class Range extends FilteredDicomObject
    {
        private static final long serialVersionUID = 1L;
        final long fromTag;
        final long toTag;

        public Range(DicomObject attrs, int fromTag, int toTag)
        {
            super(attrs);
            if ((fromTag & 0xffffffffL) > (toTag & 0xffffffffL))
            {
                throw new IllegalArgumentException("fromTag:"
                        + TagUtils.toString(fromTag) + " > toTag:"
                        + TagUtils.toString(toTag));
            }
            this.fromTag = fromTag & 0xffffffffL;
            this.toTag = toTag & 0xffffffffL;
            if (this.fromTag > this.toTag)
            {
                throw new IllegalArgumentException("fromTag:"
                        + TagUtils.toString(fromTag) + " > toTag:"
                        + TagUtils.toString(toTag));
            }
        }

        @Override
        protected boolean filter(int tag)
        {
            long ltag = tag & 0xffffffffL;
            return fromTag <= ltag && ltag <= toTag;
        }

        @Override
        public Iterator<DicomElement> iterator()
        {
            return new Itr(attrs.iterator((int) fromTag, (int) toTag));
        }

        @Override
        public Iterator<DicomElement> iterator(int fromTag, int toTag)
        {
            final long maxFromTag = Math.max(fromTag & 0xffffffff, this.fromTag);
            final long minToTag = Math.min(toTag & 0xffffffff, this.toTag);
            return new Itr(attrs.iterator((int) maxFromTag, (int) minToTag));
        }
    }

    static final class ExcludePrivate extends FilteredDicomObject
    {
        private static final long serialVersionUID = 1L;

        public ExcludePrivate(DicomObject attrs)
        {
            super(attrs);
        }

        @Override
        protected boolean filter(int tag)
        {
            return !TagUtils.isPrivateDataElement(tag);
        }

    }

    static final class FilterSet extends FilteredDicomObject
    {
        private static final long serialVersionUID = 1L;

        final class FilterItr extends Itr
        {

            public FilterItr(Iterator<DicomElement> itr)
            {
                super(itr);
            }

            @Override
            public DicomElement next()
            {
                DicomElement attr = super.next();
                if (attr.vr() == VR.SQ && attr.hasItems())
                {
                    return attr.filterItems(filter.getNestedDicomObject(attr.tag()));
                }
                return attr;
            }

        }

        final DicomObject filter;

        public FilterSet(DicomObject attrs, DicomObject filter)
        {
            super(attrs);
            this.filter = filter;
        }

        @Override
        protected boolean filter(int tag)
        {
            return filter.contains(tag);
        }

        @Override
        public DicomObject getNestedDicomObject(int tag)
        {
            DicomObject item = super.getNestedDicomObject(tag);
            if (item == null)
                return null;

            return item.subSet(filter.getNestedDicomObject(tag));
        }

        @Override
        public Iterator<DicomElement> iterator()
        {
            return new FilterItr(attrs.iterator());
        }

        @Override
        public Iterator<DicomElement> iterator(int fromTag, int toTag)
        {
            return new FilterItr(attrs.iterator(fromTag, toTag));
        }

        @Override
        public void clear() {
            filter.accept(new Visitor() {

                public boolean visit(DicomElement e) {
                    attrs.remove(e.tag());
                    return true;
                }});
        }
    }

    protected final DicomObject attrs;

    public FilteredDicomObject(DicomObject attrs)
    {
        this.attrs = attrs;
    }

    protected abstract boolean filter(int tag);

    public int getItemPosition()
    {
        return attrs.getItemPosition();
    }

    public void setItemPosition(int pos)
    {
        attrs.setItemPosition(pos);
    }

    public long getItemOffset()
    {
        return attrs.getItemOffset();
    }

    public void setItemOffset(long offset)
    {
        //NO OP
    }

    public boolean accept(final Visitor visitor)
    {
        return attrs.accept(new Visitor()
        {
            public boolean visit(DicomElement attr)
            {
                return !filter(attr.tag()) || visitor.visit(attr);
            }
        });
    }

    public Iterator<DicomElement> iterator()
    {
        return new Itr(attrs.iterator());
    }

    public Iterator<DicomElement> iterator(int fromTag, int toTag)
    {
        return new Itr(attrs.iterator(fromTag, toTag));
    }

    protected class Itr implements Iterator<DicomElement>
    {
        final Iterator<DicomElement> itr;
        DicomElement next;

        public Itr(Iterator<DicomElement> itr)
        {
            this.itr = itr;
            findNext();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return next != null;
        }

        public DicomElement next()
        {
            if (next == null)
                throw new NoSuchElementException();
            DicomElement tmp = next;
            findNext();
            return tmp;
        }

        private void findNext()
        {
            while (itr.hasNext())
            {
                next = itr.next();
                if (filter(next.tag()))
                    return;
            }
            next = null;
        }

    }

    public boolean contains(int tag)
    {
        return filter(tag) && attrs.contains(tag);
    }

    public DicomElement get(int tag)
    {
        return filter(tag) ? attrs.get(tag) : null;
    }

    @Override
    public DicomElement get(int tag, VR vr)
    {
        return filter(tag) ? attrs.get(tag, vr) : null;
    }

    public DicomObject getParent()
    {
        return attrs.getParent();
    }

    public void setParent(DicomObject parent)
    {
        throw new UnsupportedOperationException();
    }

    public String getPrivateCreator(int privateTag)
    {
        return filter(privateTag) ? attrs.getPrivateCreator(privateTag) : null;
    }

    public DicomObject getRoot()
    {
        return attrs.getRoot();
    }

    public SpecificCharacterSet getSpecificCharacterSet()
    {
        return attrs.getSpecificCharacterSet();
    }

    public boolean cacheGet()
    {
        return attrs.cacheGet();
    }

    public boolean cachePut()
    {
        return attrs.cachePut();
    }

    public boolean bigEndian()
    {
        return attrs.bigEndian();
    }

    public void add(DicomElement attr)
    {
        if (!filter(attr.tag())) {
            throw new IllegalArgumentException();
        }
        attrs.add(attr);
    }

    public DicomElement putBytes(int tag, VR vr, byte[] val, boolean bigEndian)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putBytes(tag, vr, val, bigEndian);
    }

    public DicomElement putDouble(int tag, VR vr, double val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putDouble(tag, vr, val);
    }

    public DicomElement putDoubles(int tag, VR vr, double[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putDoubles(tag, vr, val);
    }

    public DicomElement putNull(int tag, VR vr)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putNull(tag, vr);
    }

    public DicomElement putFloat(int tag, VR vr, float val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putFloat(tag, vr, val);
    }

    public DicomElement putFloats(int tag, VR vr, float[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putFloats(tag, vr, val);
    }

    public DicomElement putInt(int tag, VR vr, int val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putInt(tag, vr, val);
    }

    public DicomElement putInts(int tag, VR vr, int[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putInts(tag, vr, val);
    }

    public DicomElement putShorts(int tag, VR vr, short[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putShorts(tag, vr, val);
    }

    public DicomElement putNestedDicomObject(int tag, DicomObject item)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putNestedDicomObject(tag, item);
    }

    public DicomElement putString(int tag, VR vr, String val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putString(tag, vr, val);
    }

    public DicomElement putStrings(int tag, VR vr, String[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putStrings(tag, vr, val);
    }

    public DicomElement putDate(int tag, VR vr, Date val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putDate(tag, vr, val);
    }

    public DicomElement putDates(int tag, VR vr, Date[] val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putDates(tag, vr, val);
    }

    public DicomElement putDateRange(int tag, VR vr, DateRange val)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putDateRange(tag, vr, val);
    }

    public void clear()
    {
        final int[] toRemove = new int[size()];
        accept(new Visitor() {
            int i = 0;
            public boolean visit(DicomElement attr) {
                toRemove[i++] = attr.tag();
                return true;
            }
        });
        for (int i = 0; i < toRemove.length; i++) {
            attrs.remove(toRemove[i]);
        }
    }

    public DicomElement remove(int tag)
    {
        return filter(tag) ? attrs.remove(tag) : null;
    }

    public int resolveTag(int privateTag, String privateCreator)
    {
        return attrs.resolveTag(privateTag, privateCreator);
    }

    public int resolveTag(int privateTag, String privateCreator, boolean reserve)
    {
        return attrs.resolveTag(privateTag, privateCreator, false);
    }

    public void cacheGet(boolean cached)
    {
        attrs.cacheGet(cached);
    }

    public void cachePut(boolean cached)
    {
        attrs.cachePut(cached);
    }

    public void bigEndian(boolean bigEndian)
    {
        attrs.bigEndian(bigEndian);
    }

    public void shareElements()
    {
        throw new UnsupportedOperationException();
    }

    public VR vrOf(int tag)
    {
        return attrs.vrOf(tag);
    }

    public String nameOf(int tag)
    {
        return attrs.nameOf(tag);
    }

    public DicomElement putFragments(int tag, VR vr, boolean bigEndian,
            int capacity)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putFragments(tag, vr, bigEndian, capacity);
    }

    public DicomElement putFragments(int tag, VR vr, boolean bigEndian)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putFragments(tag, vr, bigEndian);
    }

    public DicomElement putSequence(int tag, int capacity)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putSequence(tag, capacity);
    }

    public DicomElement putSequence(int tag)
    {
        if (!filter(tag)) {
            throw new UnsupportedOperationException();
        }
        return attrs.putSequence(tag);
    }

    public void initFileMetaInformation(String tsuid)
    {
        throw new UnsupportedOperationException();
    }

    public void initFileMetaInformation(String cuid, String iuid, String tsuid)
    {
        throw new UnsupportedOperationException();
    }
}
