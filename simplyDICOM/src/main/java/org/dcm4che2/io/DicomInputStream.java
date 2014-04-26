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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.dcm4che2.data.VRMap;
import org.dcm4che2.util.ByteUtils;
import org.dcm4che2.util.CloseUtils;
import org.dcm4che2.util.TagUtils;

import android.util.Log;

public class DicomInputStream extends FilterInputStream implements
        DicomInputHandler {
	private static final String TAG = "dcm4che2.media.DicomInputStream";

    private static final int ZLIB_HEADER = 0x789c;

    private static final int DEF_ALLOCATE_LIMIT = 0x4000000; // 64MiB

    private static final byte[] EMPTY_BYTES = {};

    private int allocateLimit = DEF_ALLOCATE_LIMIT;

    private DicomInputHandler handler = this;

    private TransferSyntax ts;

    private DicomObject attrs;

    private ArrayList<DicomElement> sqStack;

    private long pos = 0;

    private long tagpos = 0;

    private boolean expectFmiEnd = false;

    private long fmiEndPos = 0;

    private long markedPos = 0;

    private byte[] preamble;

    private byte[] header = new byte[8];

    private int tag;

    private VR vr;

    private int vallen;

    private boolean stopAtFmiEnd;

    public DicomInputStream(RandomAccessFile raf) throws IOException {
        this(new RAFInputStreamAdapter(raf));
        pos = raf.getFilePointer();
    }

    public DicomInputStream(RandomAccessFile raf, TransferSyntax ts)
            throws IOException {
        this(new RAFInputStreamAdapter(raf), ts);
        pos = raf.getFilePointer();
    }

    public DicomInputStream(File f) throws IOException {
        super(new BufferedInputStream(new FileInputStream(f)));
        try {
            this.ts = guessTransferSyntax();
        } catch (IOException e) {
            CloseUtils.safeClose(this);
            throw e;
        }
    }

    /*public DicomInputStream(ImageInputStream iis, TransferSyntax ts)
    throws IOException {
        this(new ImageInputStreamAdapter(iis), ts);
        pos = iis.getStreamPosition();
    }

    public DicomInputStream(ImageInputStream iis) throws IOException {
        this(new ImageInputStreamAdapter(iis));
        pos = iis.getStreamPosition();
    }*/

    public DicomInputStream(InputStream in, String tsuid) throws IOException {
        this(in, TransferSyntax.valueOf(tsuid));
    }

    public DicomInputStream(InputStream in) throws IOException {
        super(in);
        this.ts = guessTransferSyntax();
    }

    public DicomInputStream(InputStream in, TransferSyntax ts)
            throws IOException {
        super(in);
        if (ts == null)
            throw new NullPointerException("ts");
        switchTransferSyntax(ts);
    }

    /** 
     * Returns the limit of initial allocated memory for element values.
     * 
     * By default, the limit is set to 67108864 (64 MiB).
     *
     * @return Limit of initial allocated memory for value or -1 for no limit
     * @see #setAllocateLimit(int)
     */
    public final int getAllocateLimit() {
        return allocateLimit;
    }

    /**
     * Sets the limit of initial allocated memory for element values. If the
     * value length exceeds the limit, a byte array with the specified size is
     * allocated. If the array can filled with bytes read from this
     * <code>DicomInputStream</code>, the byte array is reallocated with
     * twice the previous length and filled again. That continues until
     * the twice of the previous length exceeds the actual value length. Then
     * the byte array is reallocated with actual value length and filled with
     * the remaining bytes for the value from this <code>DicomInputStream</code>.
     * 
     * The rational of the incrementing allocation of byte arrays is to avoid
     * OutOfMemoryErrors on parsing corrupted DICOM streams.
     * 
     * By default, the limit is set to 67108864 (64 MiB).
     * 
     * @param allocateLimit limit of initial allocated memory or -1 for no limit
     * 
     */
    public final void setAllocateLimit(int allocateLimit) {
        this.allocateLimit = allocateLimit;
    }

    public byte[] getPreamble() {
        return (preamble == null ? null : preamble.clone());
    }

    public final long getStreamPosition() {
        return pos;
    }

    public final void setStreamPosition(long pos) {
        this.pos = pos;
    }

    public final long tagPosition() {
        return tagpos;
    }

    public final long getEndOfFileMetaInfoPosition() {
        return fmiEndPos;
    }

    public final void setEndOfFileMetaInfoPosition(long fmiEndPos) {
        this.fmiEndPos = fmiEndPos;
    }

    public final void setHandler(DicomInputHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.handler = handler;
    }

    public final int tag() {
        return tag;
    }

    public final int level() {
        return sqStack != null ? sqStack.size() : 0;
    }

    public final int valueLength() {
        return vallen;
    }

    public final VR vr() {
        return vr;
    }

    public final DicomElement sq() {
        return sqStack.get(sqStack.size() - 1);
    }

    public final TransferSyntax getTransferSyntax() {
        return ts;
    }

    public final DicomObject getDicomObject() {
        return attrs;
    }

    private TransferSyntax guessTransferSyntax() throws IOException {
        mark(132);
        byte[] b = new byte[128];
        try {
            readFully(b, 0, 128);
            readFully(header, 0, 4);
            if (header[0] == 'D' && header[1] == 'I' && header[2] == 'C'
                    && header[3] == 'M') {
                preamble = b;
                b = header;
                if (!markSupported()) {
                    expectFmiEnd = true;
                    return TransferSyntax.ExplicitVRLittleEndian;
                }
                mark(6);
                readFully(b, 0, 6);
            }
        } catch (IOException ignore) {
            // ignore read errors; we'll guess something smart
        }
        reset();
        
        final VRMap vrmap = VRMap.getVRMap();
        final int vrcode = ((b[4] & 0xff) << 8) | (b[5] & 0xff);
        VR vr;
        if ((vr = vrmap.vrOf(ByteUtils.bytesLE2tag(b, 0))) != VR.UN) {
            expectFmiEnd = b[0] == 2;
            return vrcode == vr.code() ? TransferSyntax.ExplicitVRLittleEndian
                                       : TransferSyntax.ImplicitVRLittleEndian;
        }
        if ((vr = vrmap.vrOf(ByteUtils.bytesBE2tag(b, 0))) != VR.UN) {
            expectFmiEnd = b[1] == 2;
            return vrcode == vr.code() ? TransferSyntax.ExplicitVRBigEndian
                                       : TransferSyntax.ImplicitVRBigEndian;
        }
        throw new DicomCodingException("Not a DICOM Stream");
    }

    @Override
    public int read() throws IOException {
        int ch = in.read();
        if (ch != -1) {
            ++pos;
        }
        return ch;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = in.read(b, off, len);
        if (result != -1) {
            pos += result;
        }
        return result;
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
        markedPos = pos;
    }

    @Override
    public void reset() throws IOException {
        in.reset();
        pos = markedPos;
    }

    @Override
    public long skip(long n) throws IOException {
        long result = in.skip(n);
        if (result > 0) {
            pos += result;
        }
        return result;
    }

    public final void skipFully(long len) throws IOException {
        long remaining = len;
        while (remaining > 0) {
            long count = skip(remaining);
            if (count <= 0)
                throw new EOFException();
            remaining -= count;
        }
    }

    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    public int readHeader() throws IOException {
        tagpos = pos;
        readFully(header, 0, 8);
        tag = ts.bigEndian() ? ByteUtils.bytesBE2tag(header, 0) : ByteUtils
                .bytesLE2tag(header, 0);
        if (expectFmiEnd && !TagUtils.isFileMetaInfoElement(tag)) {
            Log.w(TAG, "Missing or wrong (0002,0000) Group Length of File Meta Information");
            String tsuid = attrs.getString(Tag.TransferSyntaxUID);
            if (tsuid != null) {
                ts = TransferSyntax.valueOf(tsuid);
                tag = ts.bigEndian() ? ByteUtils.bytesBE2tag(header, 0)
                        : ByteUtils.bytesLE2tag(header, 0);
            } else {
                Log.w(TAG, "Missing (0002,0010) Transfer Synatx in File Meta Information");
            }
            expectFmiEnd = false;
        }
        vr = null;
        if (TagUtils.hasVR(tag) && ts.explicitVR()) {
            try {
                vr = VR.valueOf(((header[4] & 0xff) << 8) | (header[5] & 0xff));
            } catch (IllegalArgumentException e) {                
                vr = attrs.vrOf(tag);
                Log.w(TAG, "Catch " + e + " for attribute " 
                        + TagUtils.toString(tag) + " at pos: " + tagpos
                        + " - assume " + vr);
            }
            if (vr.explicitVRHeaderLength() == 8) {
                vallen = ts.bigEndian() ? ByteUtils.bytesBE2ushort(header, 6)
                        : ByteUtils.bytesLE2ushort(header, 6);
                if (vr == VR.UN_SIEMENS) {
                    //if (Log.isLoggable(TAG, Log.INFO)) {
                        Log.i(TAG, "Replace invalid VR '??' of "
                                + TagUtils.toString(tag) + " by 'UN'");
                    //}
                    vr = VR.UN;
                }
                return tag;
            }
            readFully(header, 4, 4);
        }
        vallen = ts.bigEndian() ? ByteUtils.bytesBE2int(header, 4) : ByteUtils
                .bytesLE2int(header, 4);
        return tag;
    }

    public void readItem(DicomObject dest) throws IOException {
        dest.setItemOffset(pos);
        if (readHeader() != Tag.Item)
            throw new DicomCodingException("Expected (FFFE,E000) but read "
                    + TagUtils.toString(tag));
        readDicomObject(dest, vallen);
    }

    public void readDicomObject(DicomObject dest, int len) throws IOException {
        DicomObject oldAttrs = attrs;
        this.attrs = dest;
        try {
            parse(len, Tag.ItemDelimitationItem);
        } finally {
            this.attrs = oldAttrs;
        }
    }

    public DicomObject readDicomObject() throws IOException {
        DicomObject dest = new BasicDicomObject();
        readDicomObject(dest, -1);
        return dest;
    }
    
    /**
     * Read File Meta Information from this stream into <code>DicomObject</code>.
     * <p>
     * If there is no File Meta Information on current stream position, the
     * method returns without changing the stream position. Otherwise the stream
     * will be parsed until the end of the File Meta Information is detected and
     * File Meta Information elements are put into <code>dest</code>.
     * 
     * @param dest
     *            <code>DicomObject</code> into which File Meta Information is
     *            read.
     * @throws EOFException
     *             if this stream reaches the end before the end of the File
     *             Meta Information is detected.
     * @throws IOException
     *             if an I/O error occurs.
     */
     public void readFileMetaInformation(DicomObject dest) throws IOException {
        if (!expectFmiEnd) {
            return;
        }
        stopAtFmiEnd = true;
        try {
            readDicomObject(dest, -1);           
        } finally {
            stopAtFmiEnd = false;            
        }
        
    }

    /**
     * Read File Meta Information from this stream.
     * <p>
     * If there is no File Meta Information on current stream position,
     * <code>null</code> is returned. Otherwise the stream will be parsed
     * until the end of the File Meta Information is detected and a
     * <code>DicomObject</code> containing the File Meta Information is
     * returned.
     * 
     * @return File Meta Information or <code>null</code>
     * @throws EOFException
     *             if this stream reaches the end before the end of the File
     *             Meta Information is detected.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public DicomObject readFileMetaInformation() throws IOException {
        if (!expectFmiEnd) {
            return null;
        }
        DicomObject dest = new BasicDicomObject();
        readFileMetaInformation(dest);
        return dest;
    }

    private void parse(int len, int endTag) throws IOException {
        long endPos = len == -1 ? Long.MAX_VALUE : pos + (len & 0xffffffffL);
        boolean quit = false;
        int tag0 = 0;
        while (!quit && tag0 != endTag && pos < endPos) {
            mark(12);
            try {
                tag0 = readHeader();
            } catch (EOFException e) {
                if (len != -1)
                    throw e;
                if (endTag == Tag.SequenceDelimitationItem) {
                    Log.w(TAG, "Unexpected EOF - treat as (FFFE,E0DD) Sequence Delimitation Item");
                    tag0 = tag = Tag.SequenceDelimitationItem;
                } else {
                    // treat EOF like read of ItemDelimitationItem                
                    tag0 = tag = Tag.ItemDelimitationItem;
                }
                vr = null;
                vallen = 0;
            }
            if (stopAtFmiEnd && !expectFmiEnd) {
                reset();
                return;
            }
            TransferSyntax prevTs = ts;
            if (TagUtils.hasVR(tag) && (vr == null || vr == VR.UN)) {
                // switch to ImplicitVRLittleEndian, because Datasets in
                // items of sequences encoded with VR=UN are itself encoded
                // in DICOM default Transfer Syntax
                ts = TransferSyntax.ImplicitVRLittleEndian;
                vr = attrs.vrOf(tag);
            }
            quit = !handler.readValue(this);
            ts = prevTs;
            if (expectFmiEnd && pos == fmiEndPos) {
                String tsuid = attrs.getString(Tag.TransferSyntaxUID);
                if (tsuid != null)
                    switchTransferSyntax(TransferSyntax.valueOf(tsuid));
                else
                    Log.w(TAG, "Missing (0002,0010) Transfer Syntax in " +
                                "File Meta Information");
                this.expectFmiEnd = false;
                if (stopAtFmiEnd) {
                    return;
                }
            }
        }
    }

    private void switchTransferSyntax(TransferSyntax ts) throws IOException {
        if (this.ts != null && this.ts.deflated())
            throw new IllegalStateException(
                    "Cannot switch back from Deflated TS");
        if (ts.deflated()) {
            if (hasZLIBHeader()) {
                Log.w(TAG, "Deflated DICOM Stream with ZLIB Header");
                super.in = new InflaterInputStream(super.in);
            } else
                super.in = new InflaterInputStream(super.in,
                        new Inflater(true));
        }
        this.ts = ts;
    }

    private boolean hasZLIBHeader() throws IOException {
        if (!markSupported())
            return false;
        byte[] buf = header;
        mark(2);
        read(buf, 0, 2);
        reset();
        return ((((buf[0] & 0xff) << 8) | (buf[1] & 0xff)) == ZLIB_HEADER);
    }


    public boolean readValue(DicomInputStream dis) throws IOException {
        if (dis != this)
            throw new IllegalArgumentException("dis != this");
        switch (tag) {
        case Tag.Item:
            readItemValue();
            break;
        case Tag.ItemDelimitationItem:
            if (vallen > 0) {
                Log.w(TAG, "Item Delimitation Item (FFFE,E00D) with non-zero " +
                        "Item Length:" + vallen + " at pos: " + tagpos + 
                        " - try to skip length");
                skip(vallen);
            }
            break;
        case Tag.SequenceDelimitationItem:
            if (vallen > 0) {
                Log.w(TAG, "Sequence Delimitation Item (FFFE,E0DD) with " +
                        "non-zero Item Length:" + vallen + " at pos: " +
                        tagpos + " - try to skip length");
                skip(vallen);
            }
            break;
        default:
            if (vallen == -1 || vr == VR.SQ) {
                DicomElement a = vr == VR.SQ ? attrs.putSequence(tag) : attrs
                        .putFragments(tag, vr, ts.bigEndian());
                readItems(a, vallen);
            } else {
                DicomElement a = attrs.putBytes(tag, vr, readBytes(vallen), ts
                        .bigEndian());
                if (tag == 0x00020000) {
                    fmiEndPos = pos + a.getInt(false);
                }
            }
        }
        return true;
    }

    public void readItems(DicomElement sq, int sqlen) throws IOException {
        if (sqStack == null) { // lazy creation
            sqStack = new ArrayList<DicomElement>();
        }
        sqStack.add(sq);
        try {
            parse(sqlen, Tag.SequenceDelimitationItem);
        } finally {
            sqStack.remove(sqStack.size() - 1);
        }
    }

    private void readItemValue() throws IOException, DicomCodingException {
        DicomElement sq = sqStack.get(sqStack.size() - 1);
        if (vallen == -1) {
            if (sq.vr() == VR.UN) {
                DicomElement tmp = attrs.putSequence(sq.tag());
                for (int i = 0, n = sq.countItems(); i < n; ++i) {
                    byte[] b = sq.getFragment(i);
                    InputStream is = new ByteArrayInputStream(b);
                    DicomInputStream dis1 = new DicomInputStream(is,
                            TransferSyntax.ImplicitVRLittleEndian);
                    DicomObject item = new BasicDicomObject();
                    dis1.readDicomObject(item, b.length);
                    tmp.addDicomObject(item);
                }
                sqStack.set(sqStack.size() - 1, sq = tmp);
            }
            if (sq.vr() != VR.SQ) {
                throw new DicomCodingException(TagUtils.toString(tag) + " "
                        + sq.vr() + " contains item with unknown length.");
            }
        }
        if (sq.vr() == VR.SQ) {
            BasicDicomObject item = new BasicDicomObject();
            item.setParent(attrs);
            item.setItemOffset(tagpos);
            readDicomObject(item, vallen);
            sq.addDicomObject(item);
        } else {
            sq.addFragment(readBytes(vallen));
        }
    }

    public byte[] readBytes(int vallen) throws IOException {
        if (vallen == 0)
            return EMPTY_BYTES;
        if (vallen < 0)
            throw new EOFException(); // assume InputStream length < 2 GiB

        int allocLen = allocateLimit >= 0
                ? Math.min(vallen, allocateLimit)
                : vallen;
        byte[] val = new byte[allocLen];
        readFully(val, 0, allocLen);
        while (allocLen < vallen) {
            int newLength = Math.min(vallen, allocLen << 1);
            byte[] copy = new byte[newLength];
            System.arraycopy(val, 0, copy, 0, allocLen);
            readFully(val, allocLen, newLength - allocLen);
            allocLen = newLength;
        }
        return val;
    }
}
