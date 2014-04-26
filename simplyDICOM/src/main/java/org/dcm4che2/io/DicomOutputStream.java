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

package org.dcm4che2.io;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.ByteUtils;

public class DicomOutputStream extends FilterOutputStream {

    private static final int PREAMBLE_LENGTH = 128;

    private TransferSyntax ts = TransferSyntax.ExplicitVRLittleEndian;

    private boolean includeGroupLength = false;

    private boolean explicitItemLength = false;

    private boolean explicitSequenceLength = false;

    private boolean explicitItemLengthIfZero = true;

    private boolean explicitSequenceLengthIfZero = true;

    private byte[] header = new byte[8];

    private byte[] preamble = new byte[PREAMBLE_LENGTH];

    private long pos = 0;
    
    /** Causes a deflator stream to be finished automatically on writing the dataset - set to false if there
     * are more elements to the dataset to be written externally.
     */
    private boolean autoFinish = true;

    public DicomOutputStream(OutputStream out) {
        super(out);
    }

    public DicomOutputStream(File f) throws IOException {
        this(new BufferedOutputStream(new FileOutputStream(f)));
    }

    public DicomOutputStream(RandomAccessFile raf) throws IOException {
        super(new RAFOutputStreamAdapter(raf));
        pos = raf.getFilePointer();
    }
    
    /**
     * Use a DataOutput or ImageOutputStream as a destination.
     * @param dout to send the dicom data to.
     */
    public DicomOutputStream(DataOutput dout) {
    	super(new DataOutputStreamAdapter(dout));
    }

    public byte[] getPreamble() {
        return preamble;
    }

    public void setPreamble(byte[] preamble) {
        if (preamble != null && preamble.length != PREAMBLE_LENGTH) {
            throw new IllegalArgumentException(
                    "preamble length must be 128 but is " + preamble.length);
        }
        this.preamble = preamble;
    }

    public final long getStreamPosition() {
        return pos;
    }

    public final void setStreamPosition(long pos) {
        this.pos = pos;
    }

    public final TransferSyntax getTransferSyntax() {
        return ts;
    }

    public final void setTransferSyntax(TransferSyntax ts) {
        if (ts.deflated() && !(out instanceof DeflaterOutputStream))
            out = new DeflaterOutputStream(out,
                    new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        this.ts = ts;
    }

    public final void setTransferSyntax(String tsuid) {
        setTransferSyntax(TransferSyntax.valueOf(tsuid));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        pos += len;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        ++pos;
    }

    public final boolean isExplicitItemLength() {
        return explicitItemLength;
    }

    public final void setExplicitItemLength(boolean explicitItemLength) {
        this.explicitItemLength = explicitItemLength;
    }

    public final boolean isExplicitItemLengthIfZero() {
        return explicitItemLengthIfZero;
    }

    public final void setExplicitItemLengthIfZero(
            boolean explicitItemLengthIfZero) {
        this.explicitItemLengthIfZero = explicitItemLengthIfZero;
    }

    public final boolean isExplicitSequenceLength() {
        return explicitSequenceLength;
    }

    public final void setExplicitSequenceLength(boolean explicitSequenceLength) {
        this.explicitSequenceLength = explicitSequenceLength;
    }

    public final boolean isExplicitSequenceLengthIfZero() {
        return explicitSequenceLengthIfZero;
    }

    public final void setExplicitSequenceLengthIfZero(
            boolean explicitSequenceLengthIfZero) {
        this.explicitSequenceLengthIfZero = explicitSequenceLengthIfZero;
    }

    public final boolean isIncludeGroupLength() {
        return includeGroupLength;
    }

    public final void setIncludeGroupLength(boolean includeGroupLength) {
        this.includeGroupLength = includeGroupLength;
    }

    /**
     * Only for internal use by {@link org.dcm4che2.data.DicomObjectSerializer}.
     */
    public void serializeDicomObject(DicomObject attrs) throws IOException {
        this.ts = TransferSyntax.ExplicitVRLittleEndian;
        writeElements(attrs.iterator(), false, null);
        writeHeader(Tag.ItemDelimitationItem, null, 0);
    }

    public void writeCommand(DicomObject attrs) throws IOException {
        this.ts = TransferSyntax.ImplicitVRLittleEndian;
        writeElements(attrs.commandIterator(), true, new ItemInfo(attrs
                .commandIterator(), true));
    }

    private void writeGroupLength(int tag, int length) throws IOException {
        writeHeader(tag, VR.UL, 4);
        write(VR.UL.toBytes(length, ts.bigEndian()), 0, 4);
    }

    /**
     * Write a DICOM object to the output stream using the specified
     * <code>DicomObject</code> to obtain the transfer syntax UID and other
     * attributes.
     * 
     * @param attrs
     *            The <code>DicomObject</code> containing the DICOM tags to
     *            write into the file.
     * @throws IOException
     */
    public void writeDicomFile(DicomObject attrs) throws IOException {
        String tsuid = attrs.getString(Tag.TransferSyntaxUID);
        if (tsuid == null)
            throw new IllegalArgumentException(
                    "Missing (0002,0010) Transfer Syntax UID");
        writeFileMetaInformation(attrs);
        writeDataset(attrs, tsuid);
    }

    public void writeFileMetaInformation(DicomObject attrs) throws IOException {
        if (preamble != null) {
            write(preamble, 0, PREAMBLE_LENGTH);
            write('D');
            write('I');
            write('C');
            write('M');
        }
        this.ts = TransferSyntax.ExplicitVRLittleEndian;
        writeElements(attrs.fileMetaInfoIterator(), true, new ItemInfo(attrs
                .fileMetaInfoIterator(), true));
    }

    /**
     * Write a DICOM dataset to the output stream.
     * 
     * @param attrs
     *            A DicomObject containing the attributes to write.
     * @param tsuid
     *            A String containing the transfer syntax UID of the file.
     * @throws IOException
     */
    public void writeDataset(DicomObject attrs, String tsuid)
            throws IOException {
        writeDataset(attrs, TransferSyntax.valueOf(tsuid));
    }

    /**
     * Write a DICOM dataset to the output stream.
     * 
     * @param attrs
     *            A DicomObject containing the attributes to write.
     * @param transferSyntax
     *            A TransferSyntax object representing the transfer syntax of
     *            the file.
     * @throws IOException
     */
    public void writeDataset(DicomObject attrs, TransferSyntax transferSyntax)
            throws IOException {
        setTransferSyntax(transferSyntax);
        this.ts = transferSyntax;
        writeElements(attrs.datasetIterator(), includeGroupLength,
                createItemInfo(attrs));
        if (autoFinish) {
           finish();
        }
    }

    /** Indicate if the stream is finished automatically (compressed data written) when the dataset is written */
	public boolean isAutoFinish() {
		return autoFinish;
	}

	/** Set to false to not auto finish the stream on writing a data set - useful for writing a DataObject followed
	 * by some additonal DICOM that is custom written, eg images or related large data.
	 * @param autoFinish
	 */
	public void setAutoFinish(boolean autoFinish) {
		this.autoFinish = autoFinish;
	}
	
	@Override
	public void close() throws IOException {
		out.close();
		out = null;
	}

	/** Finishes writing compressed data to the output stream without closing the underlying stream.  Use this method when
     * applying multiple filters, and the transfer syntax is a deflator transfer syntax.
     *
     */
    public void finish() throws IOException {
       if( out instanceof DeflaterOutputStream ) {
          ((DeflaterOutputStream) out).finish();
       }
    }
    
    private ItemInfo createItemInfo(DicomObject attrs) {
        if (needItemInfo())
            return new ItemInfo(attrs.datasetIterator(), includeGroupLength);
        return null;
    }

    private boolean needItemInfo() {
        return includeGroupLength || explicitItemLength
                || explicitSequenceLength;
    }

    /**
     * Write an item (DicomObject) to the output stream.
     * 
     * @param item
     *            The DicomObject containing the specific item to write.
     * @param transferSyntax
     *            The <code>TransferSyntax</code> of the item.
     * @throws IOException
     */
    public void writeItem(DicomObject item, TransferSyntax transferSyntax)
            throws IOException {
        this.ts = transferSyntax;
        writeItem(item, createItemInfo(item));
    }

    private void writeItem(DicomObject item, ItemInfo itemInfo)
            throws IOException {
        item.setItemOffset(pos);
        int len;
        if (item.isEmpty()) {
            len = explicitItemLengthIfZero ? 0 : -1;
        }
        else {
            len = explicitItemLength ? itemInfo.len : -1;
        }
        writeHeader(Tag.Item, null, len);
        writeElements(item.iterator(), includeGroupLength, itemInfo);
        if (len == -1) {
            writeHeader(Tag.ItemDelimitationItem, null, 0);
        }
    }

    private void writeElements(Iterator<DicomElement> itr, boolean groupLength1,
            ItemInfo itemInfo) throws IOException {
        int gggg0 = -1;
        int gri = -1;
        int sqi = -1;
        while (itr.hasNext()) {
            DicomElement a = itr.next();
            if (groupLength1) {
                int gggg = a.tag() & 0xffff0000;
                if (gggg != gggg0) {
                    gggg0 = gggg;
                    assert itemInfo != null;
                    writeGroupLength(gggg, itemInfo.grlen[++gri]);
                }
            }
            final VR vr = a.vr();
            int len = a.length();
            if (vr == VR.SQ) {
                if (len == -1 && explicitSequenceLength) {
                	assert itemInfo != null;
                    len = itemInfo.sqlen[++sqi];
                }
                else if (len == 0 && !explicitSequenceLengthIfZero) {
                    len = -1;
                }
            }
            writeHeader(a.tag(), vr, len);
            a.bigEndian(ts.bigEndian());
            if (a.hasItems()) {
                if (vr == VR.SQ) {
                    for (int i = 0, n = a.countItems(); i < n; i++) {
                        DicomObject item = a.getDicomObject(i);
                        ItemInfo childItemInfo = itemInfo != null ? (ItemInfo) itemInfo.childs
                                .removeFirst()
                                : null;
                        writeItem(item, childItemInfo);
                    }
                }
                else {
                    for (int i = 0, n = a.countItems(); i < n; i++) {
                        byte[] val = a.getFragment(i);
                        writeHeader(Tag.Item, null, (val.length + 1) & ~1);
                        write(val);
                        if ((val.length & 1) != 0)
                            write(0);
                    }
                }
            }
            else if (len > 0) {
                byte[] val = a.getBytes();
                write(val);
                if ((val.length & 1) != 0)
                    write(vr.padding());
            }
            if (len == -1) {
                writeHeader(Tag.SequenceDelimitationItem, null, 0);
            }
        }
    }

    public void writeHeader(int tag, VR vr, int len) throws IOException {
        if (ts.bigEndian()) {
            ByteUtils.tag2bytesBE(tag, header, 0);
        }
        else {
            ByteUtils.tag2bytesLE(tag, header, 0);
        }
        int off = 0;
        if (vr != null && ts.explicitVR()) {
            ByteUtils.ushort2bytesBE(vr.code(), header, 4);
            if (vr.explicitVRHeaderLength() == 8) {
                if (ts.bigEndian()) {
                    ByteUtils.ushort2bytesBE(len, header, 6);
                }
                else {
                    ByteUtils.ushort2bytesLE(len, header, 6);
                }
                write(header, 0, 8);
                return;
            }
            header[6] = header[7] = 0;
            write(header, 0, 8);
            off = 4;
        }
        if (ts.bigEndian()) {
            ByteUtils.int2bytesBE(len, header, 4);
        }
        else {
            ByteUtils.int2bytesLE(len, header, 4);
        }
        write(header, off, 8 - off);
    }

    private class ItemInfo {
        int len = 0;

        int[] grlen = { 0 };

        int[] sqlen = {};

        LinkedList<ItemInfo> childs = null;

        ItemInfo(Iterator<DicomElement> it, boolean groupLength1) {
            int gggg0 = -1;
            int gri = -1;
            int sqi = -1;
            while (it.hasNext()) {
                DicomElement a = it.next();
                final VR vr = a.vr();
                int vlen = a.length();
                if (vlen == -1) {
                    if (a.vr() == VR.SQ) {
                        vlen = calcItemSqLen(a);
                        if (explicitSequenceLength) {
                            if (++sqi >= sqlen.length) {
                                sqlen = realloc(sqlen);
                            }
                            sqlen[sqi] = vlen;
                        }
                    }
                    else {
                        vlen = calcFragSqLen(a);
                    }
                }
                else if (a.vr() == VR.SQ) { // vlen == 0
                    if (!explicitSequenceLengthIfZero)
                        vlen = 8;
                }
                final int alen = (ts.explicitVR() ? vr.explicitVRHeaderLength()
                        : 8)
                        + vlen;
                len += alen;
                final int gggg = a.tag() & 0xffff0000;
                if (groupLength1) {
                    if (gggg != gggg0) {
                        gggg0 = gggg;
                        len += 12;
                        if (++gri >= grlen.length) {
                            grlen = realloc(grlen);
                        }
                    }
                    grlen[gri] += alen;
                }
            }
            if (!(len == 0 ? explicitItemLengthIfZero : explicitItemLength)) {
                len += 8;
            }
        }

        private int calcFragSqLen(DicomElement a) {
            int l = 8;
            for (int i = 0, n = a.countItems(); i < n; ++i) {
                byte[] b = a.getFragment(i);
                l += 8 + (b.length + 1) & ~1;
            }
            return l;
        }

        private int calcItemSqLen(DicomElement a) {
            int l = explicitSequenceLength ? 0 : 8;
            for (int i = 0, n = a.countItems(); i < n; ++i) {
                DicomObject item = a.getDicomObject(i);
                ItemInfo itemInfo = new ItemInfo(item.iterator(),
                        includeGroupLength);
                if (childs == null) // lazy allocation
                    childs = new LinkedList<ItemInfo>();
                childs.add(itemInfo);
                l += 8 + itemInfo.len;
            }
            return l;
        }
    }

    private static int[] realloc(int[] src) {
        int[] dest = new int[src.length + 10];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }


}
