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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.DateUtils;
import org.dcm4che2.util.TagUtils;

abstract class AbstractDicomObject implements DicomObject {

    protected Object writeReplace() {
        return new DicomObjectSerializer(this);
    }

    public void serializeElements(ObjectOutputStream oos) throws IOException {
        oos.writeObject(new ElementSerializer(this));
    }

    public void copyTo(final DicomObject dest, final boolean resolveDestinationPrivateTags) {
        accept(new Visitor() {
            public boolean visit(DicomElement attr) {
                int tag = attr.tag();
                VR vr = attr.vr();
                if (!TagUtils.isPrivateDataElement(tag)  || (!resolveDestinationPrivateTags && TagUtils.isPrivateCreatorDataElement(tag)))
                    dest.add(attr);
                else if (!TagUtils.isPrivateCreatorDataElement(tag)) {
                    int destTag = resolveDestinationPrivateTags 
                                    ? dest.resolveTag(tag, getPrivateCreator(tag), true)
                                    : tag;
                    if (attr.hasItems()) {
                        final int n = attr.countItems();
                        DicomElement t;
                        if (vr == VR.SQ) {
                            t = dest.putSequence(destTag, n);
                            for (int i = 0; i < n; i++) {
                                DicomObject srcItem = attr.getDicomObject(i);
                                BasicDicomObject item = new BasicDicomObject(
                                        srcItem.size());
                                item.setParent(dest);
                                srcItem.copyTo(item, resolveDestinationPrivateTags);
                                t.addDicomObject(item);
                            }
                        } else {
                            t = putFragments(destTag, vr, attr.bigEndian(), n);
                            for (int i = 0; i < n; i++) {
                                t.addFragment(attr.getFragment(i));
                            }
                        }
                    } else
                        dest.putBytes(destTag, vr, attr.getBytes(), attr.bigEndian());
                }
                return true;
            }
        });
    }

    public void copyTo(final DicomObject dest) {
        copyTo(dest, true);
    }

    public boolean containsAll(final DicomObject keys) {
        return keys.accept(new Visitor() {
            public boolean visit(DicomElement key) {
                DicomElement el = get(key.tag());
                if (el == null)
                    return false;
                if (key.hasDicomObjects()) {
                    DicomObject itemKeys = key.getDicomObject();
                    if (itemKeys != null && !itemKeys.isEmpty()) {
                        if (!el.hasDicomObjects() || el.isEmpty())
                            return false;
                        for (int i = 0, n = el.countItems(); i < n; i++) {
                            if (!el.getDicomObject(i).containsAll(itemKeys))
                                return false;
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        toStringBuffer(sb, null);
        return sb.toString();
    }

    public int toStringBuffer(StringBuffer sb, DicomObjectToStringParam param) {
        if (sb == null)
            throw new NullPointerException();
        if (param == null)
            param = DicomObjectToStringParam.getDefaultParam();
        int lines = 0;
        for (Iterator<DicomElement> it = iterator();
                lines < param.numLines && it.hasNext();) {
            DicomElement e = it.next();
            if (++lines == param.numLines) {
                sb.append("...").append(param.lineSeparator);
                break;
            }
            int maxLen = sb.length() + param.lineLength;
            sb.append(param.indent);
            e.toStringBuffer(sb, param.valueLength);
            if (param.name) {
                sb.append(" ");
                sb.append(nameOf(e.tag()));
            }
            if (sb.length() > maxLen)
                sb.setLength(maxLen);
            sb.append(param.lineSeparator);
            if (e.countItems() > 0) {
                DicomObjectToStringParam param1 = new DicomObjectToStringParam(
                        param.name, param.valueLength, param.numItems,
                        param.lineLength, param.numLines - lines,
                        param.indent + '>', param.lineSeparator);
                if (e.hasDicomObjects())
                    lines += itemsToStringBuffer(e, sb, param1);
                else
                    lines += fragsToStringBuffer(e, sb, param1);
            }
        }
        return lines;
    }

    private static int fragsToStringBuffer(DicomElement e, StringBuffer sb,
            DicomObjectToStringParam param) {
        int lines = 0;
        for (int i = 0, n = e.countItems(); i < n; ++i) {
            if (++lines >= param.numLines) {
                sb.append("...").append(param.lineSeparator);
                break;
            }
            sb.append(param.indent);
            sb.append("ITEM #");
            sb.append(i + 1);
            if (i >= param.numItems) {
                sb.append("...").append(param.lineSeparator);
                break;
            }
            sb.append(" [");
            sb.append((e.getFragment(i).length + 1) & ~1);
            sb.append(" bytes]");
            sb.append(param.lineSeparator);
        }
        return lines;
    }

    private static int itemsToStringBuffer(DicomElement e, StringBuffer sb,
            DicomObjectToStringParam param) {
        int lines = 0;
        for (int i = 0, n = e.countItems(); i < n && lines < param.numLines; i++) {
            if (++lines == param.numLines) {
                sb.append("...").append(param.lineSeparator);
                break;
            }
            DicomObject item = e.getDicomObject(i);
            long off = item.getItemOffset();
            sb.append(param.indent);
            sb.append("ITEM #");
            sb.append(i + 1);
            if (i >= param.numItems) {
                sb.append("...").append(param.lineSeparator);
                break;
            }
            if (off != -1) {
                sb.append(" @");
                sb.append(off);
            }
            sb.append(":");
            sb.append(param.lineSeparator);
            DicomObjectToStringParam param1 = new DicomObjectToStringParam(
                    param.name, param.valueLength, param.numItems,
                    param.lineLength, param.numLines - lines, param.indent,
                    param.lineSeparator);
            lines += item.toStringBuffer(sb, param1);
        }
        return lines;
    }

    public boolean matches(final DicomObject keys, final boolean ignoreCaseOfPN) {
        return keys.accept(new Visitor() {
            public boolean visit(DicomElement test) {
                if (test.isEmpty()) // Universal Matching
                    return true;
                final int tag = test.tag();
                DicomElement attr = get(tag);
                if (attr == null || attr.isEmpty())
                    return true; // Missing DicomElement (Value) match always

                final VR vr = test.vr();
                if (vr instanceof VR.Fragment)
                    return true; // ignore OB,OW,OF,UN filter attrs

                if (vr == VR.SQ)
                    return matchSQ(attr, test.getDicomObject(), ignoreCaseOfPN);

                if (vr == VR.DA) {
                    int tmTag = DA_TM.getTMTag(tag);
                    return tmTag != 0 ? matchRange(getDates(tag, tmTag), keys
                            .getDateRange(tag, tmTag)) : matchRange(attr
                            .getDates(cacheGet()), test.getDateRange(keys
                            .cacheGet()));
                }
                if (vr == VR.TM) {
                    int daTag = DA_TM.getDATag(tag);
                    return daTag != 0 && containsValue(daTag) ? true // considered
                                                                        // by
                                                                        // visit
                                                                        // of
                                                                        // daTag
                            : matchRange(attr.getDates(cacheGet()), test
                                    .getDateRange(keys.cacheGet()));
                }
                if (vr == VR.DT) {
                    return matchRange(attr.getDates(cacheGet()), test
                            .getDateRange(keys.cacheGet()));
                }
                return matchValue(attr.getStrings(getSpecificCharacterSet(),
                        cacheGet()), test.getPattern(keys
                        .getSpecificCharacterSet(),
                        vr == VR.PN ? ignoreCaseOfPN : false, keys.cacheGet()));
            }
        });

    }

    private boolean matchValue(String[] value, Pattern pattern) {
        for (int i = 0; i < value.length; i++) {
            if (pattern.matcher(value[i]).matches())
                return true;
        }
        return false;
    }

    private boolean matchRange(Date[] dates, DateRange dateRange) {
        for (int i = 0; i < dates.length; i++) {
            if (matchRange(dates[i], dateRange.getStart(), dateRange.getEnd()))
                return true;
        }
        return false;
    }

    private boolean matchRange(Date date, Date start, Date end) {
        if (start != null && start.after(date))
            return false;
        if (end != null && end.before(date))
            return false;
        return true;
    }

    private boolean matchSQ(DicomElement sq, DicomObject keys,
            boolean ignoreCaseOfPN) {
        if (keys.isEmpty())
            return true;
        for (int i = 0, n = sq.countItems(); i < n; i++) {
            if (sq.getDicomObject(i).matches(keys, ignoreCaseOfPN))
                return true;
        }
        return false;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public boolean isEmpty() {
        return accept(new Visitor() {
            public boolean visit(DicomElement attr) {
                return false;
            }
        });
    }

    public int size() {
        final int[] count = { 0 };
        accept(new Visitor() {
            public boolean visit(DicomElement attr) {
                ++count[0];
                return true;
            }
        });
        return count[0];
    }

    public Iterator<DicomElement> commandIterator() {
        return iterator(0x00000000, 0x0000ffff);
    }

    public Iterator<DicomElement> fileMetaInfoIterator() {
        return iterator(0x00020000, 0x0002ffff);
    }

    public Iterator<DicomElement> datasetIterator() {
        return iterator(0x00030000, 0xffffffff);
    }

    public DicomObject command() {
        return subSet(0x00000000, 0x0000ffff);
    }

    public DicomObject dataset() {
        return subSet(0x00030000, 0xffffffff);
    }

    public DicomObject fileMetaInfo() {
        return subSet(0x00020000, 0x0002ffff);
    }

    public DicomObject exclude(int[] tags) {
        return tags != null && tags.length > 0 ? new FilteredDicomObject.Exclude(
                this, tags)
                : this;
    }

    /** 
     * @see org.dcm4che2.data.DicomObject#excludePrivate()
     */
    public DicomObject excludePrivate() {
        return new FilteredDicomObject.ExcludePrivate(this);
    }

    public DicomObject subSet(DicomObject filter) {
        return filter != null ? new FilteredDicomObject.FilterSet(this, filter)
                : null;
    }

    public DicomObject subSet(int fromTag, int toTag) {
        return new FilteredDicomObject.Range(this, fromTag, toTag);
    }

    public DicomObject subSet(int[] tags) {
        return tags != null && tags.length > 0 ? new FilteredDicomObject.Include(
                this, tags)
                : this;
    }

    public int vm(int tag) {
        DicomElement attr = get(tag);
        return attr != null ? attr.vm(getSpecificCharacterSet()) : -1;
    }

    public boolean containsValue(int tag) {
        DicomElement attr = get(tag);
        return attr != null && !attr.isEmpty();
    }

    public byte[] getBytes(int tag) {
        return toBytes(get(tag), false);
    }
    
    public byte[] getBytes(int tag, boolean bigEndian) {
        return toBytes(get(tag), bigEndian);
    }

    private byte[] toBytes(DicomElement a, boolean bigEndian) {
        return a == null ? null : a.bigEndian(bigEndian).getBytes();
    }

    public DicomObject getNestedDicomObject(int tag) {
        DicomElement a = get(tag, VR.SQ);
        return a == null || a.isEmpty() ? null : a.getDicomObject();
    }

    public int getInt(int tag) {
        return toInt(get(tag), 0);
    }

    public int getInt(int tag, VR vr) {
        return toInt(get(tag, vr), 0);
    }

    public int getInt(int tag, int defVal) {
        return toInt(get(tag), defVal);
    }

    public int getInt(int tag, VR vr, int defVal) {
        return toInt(get(tag, vr), defVal);
    }

    private int toInt(DicomElement a, int defVal) {
        return a == null || a.isEmpty() ? defVal : a.getInt(cacheGet());
    }

    public int[] getInts(int tag) {
        return toInts(get(tag));
    }

    public int[] getInts(int tag, VR vr) {
        return toInts(get(tag, vr));
    }

    public int[] getInts(int tag, int[] defVal) {
        return toInts(get(tag), defVal);
    }

    public int[] getInts(int tag, VR vr, int[] defVal) {
        return toInts(get(tag, vr), defVal);
    }

    private int[] toInts(DicomElement a) {
        return a == null ? null : a.getInts(cacheGet());
    }

    private int[] toInts(DicomElement a, int[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getInts(cacheGet());
    }

    public short[] getShorts(int tag) {
        return toShorts(get(tag));
    }

    public short[] getShorts(int tag, VR vr) {
        return toShorts(get(tag, vr));
    }

    public short[] getShorts(int tag, short[] defVal) {
        return toShorts(get(tag), defVal);
    }

    public short[] getShorts(int tag, VR vr, short[] defVal) {
        return toShorts(get(tag, vr), defVal);
    }

    private short[] toShorts(DicomElement a) {
        return a == null ? null : a.getShorts(cacheGet());
    }

    private short[] toShorts(DicomElement a, short[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getShorts(cacheGet());
    }

    public float getFloat(int tag) {
        return toFloat(get(tag), 0.f);
    }

    public float getFloat(int tag, VR vr) {
        return toFloat(get(tag, vr), 0.f);
    }

    public float getFloat(int tag, float defVal) {
        return toFloat(get(tag), defVal);
    }

    public float getFloat(int tag, VR vr, float defVal) {
        return toFloat(get(tag, vr), defVal);
    }

    private float toFloat(DicomElement a, float defVal) {
        return a == null || a.isEmpty() ? defVal : a.getFloat(cacheGet());
    }

    public float[] getFloats(int tag) {
        return toFloats(get(tag));
    }

    public float[] getFloats(int tag, VR vr) {
        return toFloats(get(tag, vr));
    }

    public float[] getFloats(int tag, float[] defVal) {
        return toFloats(get(tag), defVal);
    }

    public float[] getFloats(int tag, VR vr, float[] defVal) {
        return toFloats(get(tag, vr), defVal);
    }

    private float[] toFloats(DicomElement a) {
        return a == null ? null : a.getFloats(cacheGet());
    }

    private float[] toFloats(DicomElement a, float[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getFloats(cacheGet());
    }

    public double getDouble(int tag) {
        return toDouble(get(tag), 0.);
    }

    public double getDouble(int tag, VR vr) {
        return toDouble(get(tag, vr), 0.);
    }

    public double getDouble(int tag, double defVal) {
        return toDouble(get(tag), defVal);
    }

    public double getDouble(int tag, VR vr, double defVal) {
        return toDouble(get(tag, vr), defVal);
    }

    private double toDouble(DicomElement a, double defVal) {
        return a == null || a.isEmpty() ? defVal : a.getDouble(cacheGet());
    }

    public double[] getDoubles(int tag) {
        return toDoubles(get(tag));
    }

    public double[] getDoubles(int tag, VR vr) {
        return toDoubles(get(tag, vr));
    }

    public double[] getDoubles(int tag, double[] defVal) {
        return toDoubles(get(tag), defVal);
    }

    public double[] getDoubles(int tag, VR vr, double[] defVal) {
        return toDoubles(get(tag, vr), defVal);
    }

    private double[] toDoubles(DicomElement a) {
        return a == null ? null : a.getDoubles(cacheGet());
    }

    private double[] toDoubles(DicomElement a, double[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getDoubles(cacheGet());
    }

    public String getString(int tag) {
        return toString(get(tag), null);
    }

    public String getString(int tag, VR vr) {
        return toString(get(tag, vr), null);
    }

    public String getString(int tag, String defVal) {
        return toString(get(tag), defVal);
    }

    public String getString(int tag, VR vr, String defVal) {
        return toString(get(tag, vr), defVal);
    }

    private String toString(DicomElement a, String defVal) {
        return a == null || a.isEmpty() ? defVal : a.getString(
                getSpecificCharacterSet(), cacheGet());
    }

    public String[] getStrings(int tag) {
        return toStrings(get(tag));
    }

    public String[] getStrings(int tag, VR vr) {
        return toStrings(get(tag, vr));
    }

    public String[] getStrings(int tag, String[] defVal) {
        return toStrings(get(tag), defVal);
    }

    public String[] getStrings(int tag, VR vr, String[] defVal) {
        return toStrings(get(tag, vr), defVal);
    }

    private String[] toStrings(DicomElement a) {
        return a == null ? null : a.getStrings(getSpecificCharacterSet(),
                cacheGet());
    }

    private String[] toStrings(DicomElement a, String[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getStrings(
                getSpecificCharacterSet(), cacheGet());
    }

    public Date getDate(int tag) {
        return toDate(get(tag), null);
    }

    public Date getDate(int tag, VR vr) {
        return toDate(get(tag, vr), null);
    }

    public Date getDate(int tag, Date defVal) {
        return toDate(get(tag), defVal);
    }

    public Date getDate(int tag, VR vr, Date defVal) {
        return toDate(get(tag, vr), defVal);
    }

    private Date toDate(DicomElement a, Date defVal) {
        return a == null || a.isEmpty() ? defVal : a.getDate(cacheGet());
    }

    public Date[] getDates(int tag) {
        return toDates(get(tag));
    }

    public Date[] getDates(int tag, VR vr) {
        return toDates(get(tag, vr));
    }

    public Date[] getDates(int tag, Date[] defVal) {
        return toDates(get(tag), defVal);
    }

    public Date[] getDates(int tag, VR vr, Date[] defVal) {
        return toDates(get(tag, vr), defVal);
    }

    private Date[] toDates(DicomElement a) {
        return a == null ? null : a.getDates(cacheGet());
    }

    private Date[] toDates(DicomElement a, Date[] defVal) {
        return a == null || a.isEmpty() ? defVal : a.getDates(cacheGet());
    }

    public DateRange getDateRange(int tag) {
        return toDateRange(get(tag), null);
    }

    public DateRange getDateRange(int tag, VR vr) {
        return toDateRange(get(tag, vr), null);
    }

    public DateRange getDateRange(int tag, DateRange defVal) {
        return toDateRange(get(tag), defVal);
    }

    public DateRange getDateRange(int tag, VR vr, DateRange defVal) {
        return toDateRange(get(tag, vr), defVal);
    }

    private DateRange toDateRange(DicomElement a, DateRange defVal) {
        return a == null || a.isEmpty() ? defVal : a.getDateRange(cacheGet());
    }

    public Date getDate(int daTag, int tmTag) {
        return getDate(daTag, tmTag, null);
    }

    public Date getDate(int daTag, int tmTag, Date defVal) {
        Date da = getDate(daTag, VR.DA);
        return da == null ? defVal : DateUtils.toDateTime(da, getDate(tmTag, VR.TM));
    }

    public Date[] getDates(int daTag, int tmTag) {
        return getDates(daTag, tmTag, null);
    }

    public Date[] getDates(int daTag, int tmTag, Date[] defVal) {
        Date[] da = getDates(daTag, VR.DA);
        Date[] tm = getDates(tmTag, VR.TM);
        if (da == null)
            return defVal;
        if (tm != null) {
            for (int i = 0, n = Math.min(da.length, tm.length); i < n; ++i) {
                da[i] = DateUtils.toDateTime(da[i], tm[i]);
            }
        }
        return da;
    }

    public DateRange getDateRange(int daTag, int tmTag) {
        return getDateRange(daTag, tmTag, null);
    }

    public DateRange getDateRange(int daTag, int tmTag, DateRange defVal) {
        DateRange da = getDateRange(daTag, VR.DA);
        if (da == null)
            return defVal;
        DateRange tm = getDateRange(tmTag, VR.TM);
        if (tm == null)
            return da;
        return new DateRange(
                DateUtils.toDateTime(da.getStart(), tm.getStart()), DateUtils
                        .toDateTime(da.getEnd(), tm.getEnd()));
    }

    public DicomElement get(int[] tagPath) {
        checkTagPathLength(tagPath);
        final int last = tagPath.length - 1;
        final DicomObject item = getItem(tagPath, last, true);
        return item != null ? item.get(tagPath[last]) : null;
    }

    public DicomElement get(int[] tagPath, VR vr) {
        checkTagPathLength(tagPath);
        final int last = tagPath.length - 1;
        final DicomObject item = getItem(tagPath, last, true);
        return item != null ? item.get(tagPath[last], vr) : null;
    }

   public DicomElement remove(int[] tagPath) {
        checkTagPathLength(tagPath);
        final int last = tagPath.length - 1;
        final DicomObject item = getItem(tagPath, last, true);
        return item != null ? item.remove(tagPath[last]) : null;
    }

    public DicomObject getNestedDicomObject(int[] itemPath) {
        if ((itemPath.length & 1) != 0) {
            throw new IllegalArgumentException("itemPath.length: "
                    + itemPath.length);
        }
        return getItem(itemPath, itemPath.length, true);
    }

    private DicomObject getItem(int[] itemPath, int pathLen, boolean readonly) {
        DicomObject item = this;
        for (int i = 0; i < pathLen; ++i, ++i) {
            DicomElement sq = item.get(itemPath[i]);
            if (sq == null || !sq.hasItems()) {
                if (readonly) {
                    return null;
                }
                sq = item.putSequence(itemPath[i]);
            }
            while (sq.countItems() <= itemPath[i + 1]) {
                if (readonly) {
                    return null;
                }
                sq.addDicomObject(new BasicDicomObject());
            }
            item = sq.getDicomObject(itemPath[i + 1]);
        }
        return item;
    }

    public byte[] getBytes(int[] tagPath) {
        return toBytes(get(tagPath), false);
    }

    public byte[] getBytes(int[] tagPath, boolean bigEndian) {
        return toBytes(get(tagPath), bigEndian);
    }

    public int getInt(int[] tagPath) {
        return toInt(get(tagPath), 0);
    }

    public int getInt(int[] tagPath, VR vr) {
        return toInt(get(tagPath, vr), 0);
    }

    public int getInt(int[] tagPath, int defVal) {
        return toInt(get(tagPath), defVal);
    }

    public int getInt(int[] tagPath, VR vr, int defVal) {
        return toInt(get(tagPath, vr), defVal);
    }

    public int[] getInts(int[] tagPath) {
        return toInts(get(tagPath));
    }

    public int[] getInts(int[] tagPath, VR vr) {
        return toInts(get(tagPath, vr));
    }

    public int[] getInts(int[] tagPath, int[] defVal) {
        return toInts(get(tagPath), defVal);
    }

    public int[] getInts(int[] tagPath, VR vr, int[] defVal) {
        return toInts(get(tagPath, vr), defVal);
    }

    public short[] getShorts(int[] tagPath) {
        return toShorts(get(tagPath));
    }

    public short[] getShorts(int[] tagPath, VR vr) {
        return toShorts(get(tagPath, vr));
    }

    public short[] getShorts(int[] tagPath, short[] defVal) {
        return toShorts(get(tagPath), defVal);
    }

    public short[] getShorts(int[] tagPath, VR vr, short[] defVal) {
        return toShorts(get(tagPath, vr), defVal);
    }

    public float getFloat(int[] tagPath) {
        return toFloat(get(tagPath), 0.f);
    }

    public float getFloat(int[] tagPath, VR vr) {
        return toFloat(get(tagPath, vr), 0.f);
    }

    public float getFloat(int[] tagPath, float defVal) {
        return toFloat(get(tagPath), defVal);
    }

    public float getFloat(int[] tagPath, VR vr, float defVal) {
        return toFloat(get(tagPath, vr), defVal);
    }

    public float[] getFloats(int[] tagPath) {
        return toFloats(get(tagPath));
    }

    public float[] getFloats(int[] tagPath, VR vr) {
        return toFloats(get(tagPath, vr));
    }

    public float[] getFloats(int[] tagPath, float[] defVal) {
        return toFloats(get(tagPath), defVal);
    }

    public float[] getFloats(int[] tagPath, VR vr, float[] defVal) {
        return toFloats(get(tagPath, vr), defVal);
    }

    public double getDouble(int[] tagPath) {
        return toDouble(get(tagPath), 0.);
    }

    public double getDouble(int[] tagPath, VR vr) {
        return toDouble(get(tagPath, vr), 0.);
    }

    public double getDouble(int[] tagPath, double defVal) {
        return toDouble(get(tagPath), defVal);
    }

    public double getDouble(int[] tagPath, VR vr, double defVal) {
        return toDouble(get(tagPath, vr), defVal);
    }

    public double[] getDoubles(int[] tagPath) {
        return toDoubles(get(tagPath));
    }

    public double[] getDoubles(int[] tagPath, VR vr) {
        return toDoubles(get(tagPath, vr));
    }

    public double[] getDoubles(int[] tagPath, double[] defVal) {
        return toDoubles(get(tagPath), defVal);
    }

    public double[] getDoubles(int[] tagPath, VR vr, double[] defVal) {
        return toDoubles(get(tagPath, vr), defVal);
    }

    public String getString(int[] tagPath) {
        return toString(get(tagPath), null);
    }

    public String getString(int[] tagPath, VR vr) {
        return toString(get(tagPath, vr), null);
    }

    public String getString(int[] tagPath, String defVal) {
        return toString(get(tagPath), defVal);
    }

    public String getString(int[] tagPath, VR vr, String defVal) {
        return toString(get(tagPath, vr), defVal);
    }

    public String[] getStrings(int[] tagPath) {
        return toStrings(get(tagPath));
    }

    public String[] getStrings(int[] tagPath, VR vr) {
        return toStrings(get(tagPath, vr));
    }

    public String[] getStrings(int[] tagPath, String[] defVal) {
        return toStrings(get(tagPath), defVal);
    }

    public String[] getStrings(int[] tagPath, VR vr, String[] defVal) {
        return toStrings(get(tagPath, vr), defVal);
    }

    public Date getDate(int[] tagPath) {
        return toDate(get(tagPath), null);
    }

    public Date getDate(int[] tagPath, VR vr) {
        return toDate(get(tagPath, vr), null);
    }

    public Date getDate(int[] tagPath, Date defVal) {
        return toDate(get(tagPath), defVal);
    }

    public Date getDate(int[] tagPath, VR vr, Date defVal) {
        return toDate(get(tagPath, vr), defVal);
    }

    public Date[] getDates(int[] tagPath) {
        return toDates(get(tagPath));
    }

    public Date[] getDates(int[] tagPath, VR vr) {
        return toDates(get(tagPath, vr));
    }

    public Date[] getDates(int[] tagPath, Date[] defVal) {
        return toDates(get(tagPath), defVal);
    }

    public Date[] getDates(int[] tagPath, VR vr, Date[] defVal) {
        return toDates(get(tagPath, vr), defVal);
    }

    public DateRange getDateRange(int[] tagPath) {
        return toDateRange(get(tagPath), null);
    }

    public DateRange getDateRange(int[] tagPath, VR vr) {
        return toDateRange(get(tagPath, vr), null);
    }

    public DateRange getDateRange(int[] tagPath, DateRange defVal) {
        return toDateRange(get(tagPath), defVal);
    }

    public DateRange getDateRange(int[] tagPath, VR vr, DateRange defVal) {
        return toDateRange(get(tagPath, vr), defVal);
    }

    public Date getDate(int[] itemPath, int daTag, int tmTag) {
        return getDate(itemPath, daTag, tmTag, null);
    }

    public Date getDate(int[] itemPath, int daTag, int tmTag, Date defVal) {
        final DicomObject item = getNestedDicomObject(itemPath);
        return item != null ? item.getDate(daTag, tmTag, defVal) : null;
    }

    public Date[] getDates(int[] itemPath, int daTag, int tmTag) {
        return getDates(itemPath, daTag, tmTag);
    }

    public Date[] getDates(int[] itemPath, int daTag, int tmTag, Date[] defVal) {
        final DicomObject item = getNestedDicomObject(itemPath);
        return item != null ? item.getDates(daTag, tmTag, defVal) : null;
    }

    public DateRange getDateRange(int[] itemPath, int daTag, int tmTag) {
        return getDateRange(itemPath, daTag, tmTag, null);
    }

    public DateRange getDateRange(int[] itemPath, int daTag, int tmTag,
            DateRange defVal) {
        final DicomObject item = getNestedDicomObject(itemPath);
        return item != null ? item.getDateRange(daTag, tmTag, defVal) : null;
    }

    private void checkTagPathLength(int[] tagPath) {
        if ((tagPath.length & 1) == 0) {
            throw new IllegalArgumentException("tagPath.length: "
                    + tagPath.length);
        }
    }

    public DicomElement putBytes(int tag, VR vr, byte[] val) {
        return putBytes(tag, vr, val, false);
    }
    
    public DicomElement putBytes(int[] tagPath, VR vr, byte[] val) {
        return putBytes(tagPath, vr, val, false);
    }

    public DicomElement putBytes(int[] tagPath, VR vr, byte[] val,
            boolean bigEndian) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putBytes(tagPath[last], vr, val, bigEndian);
    }

    public DicomElement putDate(int[] tagPath, VR vr, Date val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putDate(tagPath[last], vr, val);
    }

    public DicomElement putDateRange(int[] tagPath, VR vr, DateRange val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putDateRange(tagPath[last], vr, val);
    }

    public DicomElement putDates(int[] tagPath, VR vr, Date[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putDates(tagPath[last], vr, val);
    }

    public DicomElement putDouble(int[] tagPath, VR vr, double val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putDouble(tagPath[last], vr, val);
    }

    public DicomElement putDoubles(int[] tagPath, VR vr, double[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putDoubles(tagPath[last], vr, val);
    }

    public DicomElement putFloat(int[] tagPath, VR vr, float val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putFloat(tagPath[last], vr, val);
    }

    public DicomElement putFloats(int[] tagPath, VR vr, float[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putFloats(tagPath[last], vr, val);
    }

    public DicomElement putFragments(int[] tagPath, VR vr, boolean bigEndian,
            int capacity) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putFragments(tagPath[last], vr, bigEndian, capacity);
    }

    public DicomElement putFragments(int[] tagPath, VR vr, boolean bigEndian) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putFragments(tagPath[last], vr, bigEndian);
    }

    public DicomElement putShorts(int[] tagPath, VR vr, short[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putShorts(tagPath[last], vr, val);
    }

    public DicomElement putInt(int[] tagPath, VR vr, int val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putInt(tagPath[last], vr, val);
    }

    public DicomElement putInts(int[] tagPath, VR vr, int[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putInts(tagPath[last], vr, val);
    }

    public DicomElement putNestedDicomObject(int[] tagPath, DicomObject item) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject parent = getItem(tagPath, last, false);
        return parent.putNestedDicomObject(tagPath[last], item);
    }

    public DicomElement putNull(int[] tagPath, VR vr) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putNull(tagPath[last], vr);
    }

    public DicomElement putSequence(int[] tagPath, int capacity) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putSequence(tagPath[last], capacity);
    }

    public DicomElement putSequence(int[] tagPath) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putSequence(tagPath[last]);
    }

    public DicomElement putString(int[] tagPath, VR vr, String val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putString(tagPath[last], vr, val);
    }

    public DicomElement putStrings(int[] tagPath, VR vr, String[] val) {
        checkTagPathLength(tagPath);
        int last = tagPath.length - 1;
        DicomObject item = getItem(tagPath, last, false);
        return item.putStrings(tagPath[last], vr, val);
    }

    public DicomElement get(int tag, VR vr) {
        DicomElement e = get(tag);
        if (e == null || e.vr() == vr) {
            return e;
        }
        if (e.vr() != VR.UN) {
            throw new UnsupportedOperationException("Update VR from " + e.vr()
                    + " to " + vr + " not supported");
        }
        if (vr != VR.SQ) {
            return putBytes(tag, vr, e.getBytes());
        }
        DicomElement sq = putSequence(tag);
        if (!e.isEmpty()) {
            try {
                byte[] b = e.getBytes();
                DicomInputStream in = new DicomInputStream(
                        new ByteArrayInputStream(b), UID.ImplicitVRLittleEndian);
                in.readItems(sq, b.length);
            } catch (IOException e1) {
                throw new RuntimeException("Failed to parse value of " + e
                        + " as sequence of items", e1);
            }
        }
        return sq;
    }
}
