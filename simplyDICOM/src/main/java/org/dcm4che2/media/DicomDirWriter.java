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
package org.dcm4che2.media;

///CPB Edit: 12/11/2013

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.ByteUtils;
import org.dcm4che2.util.TagUtils;

import android.util.Log;

/**
 * @author Gunter Zeilinger<gunterze@gmail.com>
 * @version $Revision: 12635 $ $Date: 2010-01-18 14:15:50 +0100 (Mon, 18 Jan 2010) $
 * @since 06.07.2006
 */

public class DicomDirWriter extends DicomDirReader {
	private static final String TAG = "dcm4che2.media.DicomDirWriter";

    protected final long firstRecordPos;

    protected final byte[] dirInfoHeader = { 
	    0x04, 0x00, 0x00, 0x12, 'U', 'L', 4, 0, 0, 0, 0, 0, 
	    0x04, 0x00, 0x02, 0x12, 'U', 'L', 4, 0, 0, 0, 0, 0, 
	    0x04, 0x00, 0x12, 0x12, 'U', 'S', 2, 0, (byte) 0xff, (byte) 0xff, 
	    0x04, 0x00, 0x20, 0x12, 'S', 'Q', 0, 0, 0, 0, 0, 0 };

    protected final byte[] dirRecordHeader = { 
	    0x04, 0x00, 0x00, 0x14, 'U', 'L', 4, 0, 0, 0, 0, 0, 
	    0x04, 0x00, 0x10, 0x14, 'U', 'S', 2, 0, 0, 0, 
	    0x04, 0x00, 0x20, 0x14, 'U', 'L', 4, 0, 0, 0, 0, 0, };

    protected long recordSeqLen;

    protected long rollbackLen = -1;

    protected ArrayList<DicomObject> dirtyRecords = new ArrayList<DicomObject>();

    protected DicomObject cachedParentRecord;

    protected DicomObject cachedLastChildRecord;

    protected final DicomOutputStream out;

    private static final Comparator<DicomObject> offsetComparator = new Comparator<DicomObject>() {
        public int compare(DicomObject item1, DicomObject item2) {
            long d = item1.getItemOffset() - item2.getItemOffset();
            return d < 0 ? -1 : d > 0 ? 1 : 0;
        }
    };

    public DicomDirWriter(File file) throws IOException {
	super(new RandomAccessFile(checkExists(file), "rw"));
	this.file = file;
	offsetFirstRootRecord(filesetInfo.getOffsetFirstRootRecord());
	offsetLastRootRecord(filesetInfo.getOffsetLastRootRecord());
	this.firstRecordPos = in.getStreamPosition();
	this.recordSeqLen = in.valueLength();
	out = new DicomOutputStream(raf);
	out.setExplicitSequenceLength(recordSeqLen != -1);
	out.setExplicitItemLength(recordSeqLen != -1);
	if (filesetInfo.isEmpty()) {
	    this.recordSeqLen = 0;
	}
    }

    private static File checkExists(File f) throws FileNotFoundException {
	if (!f.isFile()) {
	    throw new FileNotFoundException(f.getPath());
	}
	return f;
    }

    public DicomDirWriter(File file, FileSetInformation fileSetInfo)
    throws IOException {
	super(new RandomAccessFile(file, "rw"), fileSetInfo);
	this.file = file;
	// ensure fileSetInfo represents empty file-set 
	fileSetInfo.setOffsetFirstRootRecord(0);
	fileSetInfo.setOffsetLastRootRecord(0);
	raf.setLength(0);
	out = new DicomOutputStream(raf);
	out.setExplicitSequenceLength(true);
	out.setExplicitItemLength(true);
	out.writeDicomFile(fileSetInfo.getDicomObject());
	out.writeHeader(Tag.DirectoryRecordSequence, VR.SQ, 0);
	this.firstRecordPos = (int) out.getStreamPosition();
	this.recordSeqLen = 0;
    }

    private void offsetFirstRootRecord(int val) {
	ByteUtils.int2bytesLE(val, dirInfoHeader, 8);
    }

    private int offsetFirstRootRecord() {
	return ByteUtils.bytesLE2int(dirInfoHeader, 8);
    }

    private void offsetLastRootRecord(int val) {
	ByteUtils.int2bytesLE(val, dirInfoHeader, 20);
    }

    private int offsetLastRootRecord() {
	return ByteUtils.bytesLE2int(dirInfoHeader, 20);
    }

    private void recordSeqLen(int val) {
	ByteUtils.int2bytesLE(
		isExplicitSequenceLength() ? val : -1, dirInfoHeader, 42);
    }

    public final boolean isExplicitItemLength() {
        return out.isExplicitItemLength();
    }

    public final void setExplicitItemLength(boolean explicitItemLength) {
	out.setExplicitItemLength(explicitItemLength);
    }

    public final boolean isExplicitItemLengthIfZero() {
        return out.isExplicitItemLengthIfZero();
    }

    public final void setExplicitItemLengthIfZero(
            boolean explicitItemLengthIfZero) {
	out.setExplicitItemLengthIfZero(explicitItemLengthIfZero);
    }

    public final boolean isExplicitSequenceLength() {
        return out.isExplicitSequenceLength();
    }

    public final void setExplicitSequenceLength(boolean explicitSequenceLength) {
	out.setExplicitSequenceLength(explicitSequenceLength);
    }

    public final boolean isExplicitSequenceLengthIfZero() {
        return out.isExplicitSequenceLengthIfZero();
    }

    public final void setExplicitSequenceLengthIfZero(
            boolean explicitSequenceLengthIfZero) {
	out.setExplicitSequenceLengthIfZero(explicitSequenceLengthIfZero);
    }

    public final boolean isIncludeGroupLength() {
        return out.isIncludeGroupLength();
    }

    public final void setIncludeGroupLength(boolean includeGroupLength) {
        out.setIncludeGroupLength(includeGroupLength);
    }
    
    public synchronized void addRootRecord(DicomObject rec) throws IOException {
	DicomObject lastRootRecord = lastRootRecord();
	if (lastRootRecord == null) {
	    writeRecord(firstRecordPos, rec);
	    filesetInfo.setOffsetFirstRootRecord((int) firstRecordPos);
	} else {
	    addRecord(Tag.OffsetOfTheNextDirectoryRecord, lastRootRecord, rec);
	}
       filesetInfo.setOffsetLastRootRecord((int) rec.getItemOffset());
    }

    public synchronized DicomObject addPatientRecord(DicomObject patrec)
	    throws IOException {
	DicomObject other = findPatientRecord(patrec.getString(Tag.PatientID));
	if (other != null) {
	    return other;
	}
	addRootRecord(patrec);
	return patrec;
    }

    public synchronized void addSiblingRecord(DicomObject prevRec,
	    DicomObject dcmobj) throws IOException {
	prevRec = lastSiblingOrThis(prevRec);
	addRecord(Tag.OffsetOfTheNextDirectoryRecord, prevRec, dcmobj);
	if (cachedLastChildRecord == prevRec) {
	    cachedLastChildRecord = dcmobj;
	} else {
	    cachedParentRecord = null;
	    cachedLastChildRecord = null;
	}
	if (filesetInfo.getOffsetLastRootRecord() == prevRec.getItemOffset()) {
	    filesetInfo.setOffsetLastRootRecord((int) dcmobj.getItemOffset());
	}
    }

    public synchronized void addChildRecord(DicomObject parentRec,
	    DicomObject dcmobj) throws IOException {
	if (parentRec == cachedParentRecord) {
	    Log.d(TAG, "Hit Parent/LastChild cache");
	    addRecord(Tag.OffsetOfTheNextDirectoryRecord, cachedLastChildRecord, dcmobj);
	} else {
	    DicomObject prevRec = lastChildRecord(parentRec);
	    if (prevRec != null) {
		addRecord(Tag.OffsetOfTheNextDirectoryRecord, prevRec, dcmobj);
	    } else {
		addRecord(Tag.OffsetOfReferencedLowerLevelDirectoryEntity,
			parentRec, dcmobj);
	    }
	    cachedParentRecord = parentRec;
	}
	cachedLastChildRecord = dcmobj;
    }

    public synchronized DicomObject addStudyRecord(DicomObject patrec,
	    DicomObject styrec) throws IOException {
	DicomObject other = findStudyRecord(patrec, styrec
		.getString(Tag.StudyInstanceUID));
	if (other != null) {
	    return other;
	}
	addChildRecord(patrec, styrec);
	return styrec;
    }

    public synchronized DicomObject addSeriesRecord(DicomObject styrec,
	    DicomObject serrec) throws IOException {
	DicomObject other = findSeriesRecord(styrec, serrec
		.getString(Tag.SeriesInstanceUID));
	if (other != null) {
	    return other;
	}
	addChildRecord(styrec, serrec);
	return serrec;
    }

    public synchronized void deleteRecord(DicomObject rec) throws IOException {
	if (rec.getInt(Tag.RecordInUseFlag) == INACTIVE) {
	    return; // already disabled
	}
	for (DicomObject child = readRecord(
		rec.getInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity));
		child != null; 
		child = readRecord(
			child.getInt(Tag.OffsetOfTheNextDirectoryRecord))) {
	    deleteRecord(child);
	}
	rec.putInt(Tag.RecordInUseFlag, VR.US, INACTIVE);
	markAsDirty(rec);
    }

    public synchronized void rollback() throws IOException {
	filesetInfo.setOffsetFirstRootRecord(offsetFirstRootRecord());
	filesetInfo.setOffsetLastRootRecord(offsetLastRootRecord());
	cache.clear();
        cachedParentRecord = null;
        cachedLastChildRecord = null;
	dirtyRecords.clear();
	if (rollbackLen != -1) {
	    recordSeqLen = rollbackLen - firstRecordPos;
	    raf.seek(rollbackLen);
	    if (!out.isExplicitSequenceLength() && !isEmpty()) {
		out.writeHeader(Tag.SequenceDelimitationItem, null, 0);
	    }
	    raf.setLength(raf.getFilePointer());
	    rollbackLen = -1;
	    raf.seek(firstRecordPos - 14);
	    raf.writeShort(FileSetInformation.NO_KNOWN_INCONSISTENCIES);
	    filesetInfo.setFileSetConsistencyFlag(
		    FileSetInformation.NO_KNOWN_INCONSISTENCIES);
	}
    }

    public synchronized void commit() throws IOException {
	if (rollbackLen != -1 && !out.isExplicitSequenceLength()) {
	    raf.seek(endPos());
	    out.writeHeader(Tag.SequenceDelimitationItem, null, 0);
	}
	if (offsetFirstRootRecord() != filesetInfo.getOffsetFirstRootRecord()) {
	    offsetFirstRootRecord(filesetInfo.getOffsetFirstRootRecord());
	}
	if (offsetLastRootRecord() != filesetInfo.getOffsetLastRootRecord()) {
	    offsetLastRootRecord(filesetInfo.getOffsetLastRootRecord());
	}
	filesetInfo.setFileSetConsistencyFlag(
		FileSetInformation.KNOWN_INCONSISTENCIES);
	recordSeqLen((int) recordSeqLen);
	raf.seek(firstRecordPos - dirInfoHeader.length);
	raf.write(dirInfoHeader, 0, dirInfoHeader.length);
	rollbackLen = -1;
	for (int i = 0, n = dirtyRecords.size(); i < n; i++) {
	    writeDirRecordHeader(dirtyRecords.get(i));
	}
	dirtyRecords.clear();
	raf.seek(firstRecordPos - 14);
	raf.writeShort(FileSetInformation.NO_KNOWN_INCONSISTENCIES);
        filesetInfo.setFileSetConsistencyFlag(
        	    FileSetInformation.NO_KNOWN_INCONSISTENCIES);
    }

    @Override
    public void close() throws IOException {
	commit();
	super.close();
    }

    private void writeDirRecordHeader(DicomObject rec) throws IOException {
	ByteUtils.int2bytesLE(rec.getInt(Tag.OffsetOfTheNextDirectoryRecord),
		dirRecordHeader, 8);
	ByteUtils.ushort2bytesLE(rec.getInt(Tag.RecordInUseFlag),
		dirRecordHeader, 20);
	ByteUtils.int2bytesLE(
		rec.getInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity),
		dirRecordHeader, 30);
	raf.seek(rec.getItemOffset() + 8);
	raf.write(dirRecordHeader);
    }

    private void addRecord(int tag, DicomObject prevRecord, DicomObject dcmobj)
	    throws IOException {
	long endPos = endPos();
	writeRecord(endPos, dcmobj);
	prevRecord.putInt(tag, VR.UL, (int) endPos);
	markAsDirty(prevRecord);
    }

    private long endPos() throws IOException {
	if (recordSeqLen == -1) {
	    long endPos = raf.length() - 12;
	    raf.seek(endPos);
	    if (in.readHeader() == Tag.SequenceDelimitationItem) {
		recordSeqLen = (int) (endPos - firstRecordPos);
	    } else {
		endPos = filesetInfo.getOffsetLastRootRecord();
		raf.seek(endPos);
		in.setStreamPosition(endPos);
		DicomObject dcmobj = new BasicDicomObject();
		while (in.readHeader() == Tag.Item) {
		    in.readDicomObject(dcmobj, in.valueLength());
		    dcmobj.clear();
		    endPos = in.getStreamPosition();
		}
		if (in.tag() != Tag.SequenceDelimitationItem) {
		    throw new IOException("Unexpected Tag "
			    + TagUtils.toString(in.tag()) + " at offset "
			    + endPos);
		}
		recordSeqLen = (int) (endPos - firstRecordPos);
	    }
	}
	return firstRecordPos + recordSeqLen;
    }

    private void markAsDirty(DicomObject rec) {
	int index = Collections.binarySearch(dirtyRecords, rec,
		offsetComparator);
	if (index < 0) {
	    dirtyRecords.add(-(index + 1), rec);
	}
    }

    private void writeRecord(long offset, DicomObject dcmobj)
	    throws IOException {
        Log.d(TAG, "Load record @ " + Long.toString(offset) + " from file " + file.getName());
		if (rollbackLen == -1) {
		    rollbackLen = offset;
		    filesetInfo.setFileSetConsistencyFlag(
			    FileSetInformation.KNOWN_INCONSISTENCIES);
		    raf.seek(firstRecordPos - 14);
		    raf.writeShort(FileSetInformation.KNOWN_INCONSISTENCIES);
		}
		raf.seek(offset);
		out.setStreamPosition(offset);
		dcmobj.putInt(Tag.OffsetOfTheNextDirectoryRecord, VR.UL, 0);
		dcmobj.putInt(Tag.RecordInUseFlag, VR.US, INUSE);
		dcmobj.putInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity, VR.UL, 0);
		out.writeItem(dcmobj, in.getTransferSyntax());
		recordSeqLen = (int) (out.getStreamPosition() - firstRecordPos);
		cache.put((int) dcmobj.getItemOffset(), dcmobj);
	    }

    public synchronized int purge() throws IOException {
	int[] purged = { 0 };
	for (DicomObject rec = readRecord(filesetInfo.getOffsetFirstRootRecord());
		rec != null;
		rec = readRecord(rec.getInt(Tag.OffsetOfTheNextDirectoryRecord))) {
	    if (rec.getInt(Tag.RecordInUseFlag) != INACTIVE) {
		purge(rec, purged);
	    }
	}
	return purged[0];
    }

    private boolean purge(DicomObject rec, int[] purged) throws IOException {
	boolean purge = !rec.containsValue(Tag.ReferencedFileID);
	for (DicomObject child = readRecord(
		rec.getInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity)); 
		child != null;
		child = readRecord(child.getInt(Tag.OffsetOfTheNextDirectoryRecord))) {
	    if (child.getInt(Tag.RecordInUseFlag) != INACTIVE) {
		purge = purge(child, purged) && purge;
	    }
	}
	if (purge) {
	    deleteRecord(rec);
	    purged[0]++;
	}
	return purge;
    }
}
