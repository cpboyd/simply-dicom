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

package org.dcm4che2.media;

/// CPB Edit: 12/11/2013

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.util.IntHashtable;

import android.util.Log;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 12635 $ $Date:: 2010-01-18#$
 * @since Jun 25, 2005
 * 
 */
public class DicomDirReader {
	private static final String TAG = "dcm4che2.media.DicomDirReader";
    protected static final int INACTIVE = 0;
    protected static final int INUSE = 0xffff;

    protected final RandomAccessFile raf;
    protected final DicomInputStream in;
    protected final FileSetInformation filesetInfo;
    protected final IntHashtable<DicomObject> cache = new IntHashtable<DicomObject>();
    protected File file;
    protected boolean showInactiveRecords;

    protected DicomDirReader(RandomAccessFile raf,
            FileSetInformation fileSetInfo) throws IOException {
        this.raf = raf;
        this.in = new DicomInputStream(raf,
                TransferSyntax.ExplicitVRLittleEndian);
        this.filesetInfo = fileSetInfo;
    }

    public DicomDirReader(File file) throws IOException {
        this(new RandomAccessFile(file, "r"));
        this.file = file;
    }

    public DicomDirReader(RandomAccessFile raf) throws IOException {
        this.raf = raf;
        in = new DicomInputStream(raf);
        in.setHandler(new StopTagInputHandler(Tag.DirectoryRecordSequence));
        filesetInfo = new FileSetInformation();
        in.readDicomObject(filesetInfo.getDicomObject(), -1);
        in.setHandler(in);
    }

    public int getFileSetConsistencyFlag() {
        return filesetInfo.getFileSetConsistencyFlag();
    }

    public boolean isNoKnownInconsistencies() {
        return filesetInfo.isNoKnownInconsistencies();
    }

    public File getFileSetDescriptorFile() {
        if (file == null) {
            throw new IllegalStateException("Unknown File-set Base Directory");
        }
        return filesetInfo.getFileSetDescriptorFile(file.getParentFile());
    }

    public String getMediaStorageSOPInstanceUID() {
        return filesetInfo.getMediaStorageSOPInstanceUID();
    }

    public String getSpecificCharacterSetofFileSetDescriptorFile() {
        return filesetInfo.getSpecificCharacterSetofFileSetDescriptorFile();
    }

    public boolean isEmpty() {
        return filesetInfo.isEmpty();
    }

    public final File getFile() {
        return file;
    }

    public File toReferencedFile(DicomObject rec) {
        if (file == null) {
            throw new IllegalStateException("Unknown File-set Base Directory");
        }
        return FileSetInformation.toFile(rec.getStrings(Tag.ReferencedFileID),
                file.getParentFile());
    }

    public String[] toFileID(File f) {
        if (file == null) {
            throw new IllegalStateException("Unknown File-set Base Directory");
        }
        return FileSetInformation.toFileID(f, file.getParentFile());
    }

    public void clearCache() {
        cache.clear();
    }

    public FileSetInformation getFileSetInformation() {
        return filesetInfo;
    }

    public final boolean isShowInactiveRecords() {
        return showInactiveRecords;
    }

    public final void setShowInactiveRecords(boolean showInactiveRecords) {
        this.showInactiveRecords = showInactiveRecords;
    }

    public DicomObject findFirstRootRecord() throws IOException {
        return findFirstMatchingRootRecord(null, false);
    }

    protected DicomObject lastRootRecord() throws IOException {
        return readRecord(filesetInfo.getOffsetLastRootRecord());
    }

    public DicomObject findFirstMatchingRootRecord(DicomObject keys,
            boolean ignoreCaseOfPN) throws IOException {
        return readRecord(filesetInfo.getOffsetFirstRootRecord(), keys,
                ignoreCaseOfPN);
    }

    public DicomObject findPatientRecord(String pid) throws IOException {
        BasicDicomObject keys = new BasicDicomObject();
        keys.putString(Tag.DirectoryRecordType, VR.CS,
                DirectoryRecordType.PATIENT);
        keys.putString(Tag.PatientID, VR.LO, pid);
        return findFirstMatchingRootRecord(keys, false);
    }

    public DicomObject findNextSiblingRecord(DicomObject prevRecord)
            throws IOException {
        return findNextMatchingSiblingRecord(prevRecord, null, false);
    }

    public DicomObject findNextMatchingSiblingRecord(DicomObject prevRecord,
            DicomObject keys, boolean ignoreCaseOfPN) throws IOException {
        return readRecord(
                prevRecord.getInt(Tag.OffsetOfTheNextDirectoryRecord), keys,
                ignoreCaseOfPN);
    }

    public DicomObject findFirstChildRecord(DicomObject parentRecord)
            throws IOException {
        return findFirstMatchingChildRecord(parentRecord, null, false);
    }

    public DicomObject findFirstMatchingChildRecord(DicomObject parentRecord,
            DicomObject keys, boolean ignoreCaseOfPN) throws IOException {
        return readRecord(parentRecord
                .getInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity), keys,
                ignoreCaseOfPN);
    }

    public DicomObject findStudyRecord(DicomObject patrec, String uid)
            throws IOException {
        BasicDicomObject keys = new BasicDicomObject();
        keys.putString(Tag.DirectoryRecordType, VR.CS,
                DirectoryRecordType.STUDY);
        keys.putString(Tag.StudyInstanceUID, VR.UI, uid);
        keys.putString(Tag.ReferencedSOPInstanceUIDInFile, VR.UI, uid);
        return findFirstMatchingChildRecord(patrec, keys, false);
    }

    public DicomObject findSeriesRecord(DicomObject styrec, String uid)
            throws IOException {
        BasicDicomObject keys = new BasicDicomObject();
        keys.putString(Tag.DirectoryRecordType, VR.CS,
                DirectoryRecordType.SERIES);
        keys.putString(Tag.SeriesInstanceUID, VR.UI, uid);
        return findFirstMatchingChildRecord(styrec, keys, false);
    }

    public DicomObject findInstanceRecord(DicomObject serrec, String uid)
            throws IOException {
        BasicDicomObject keys = new BasicDicomObject();
        keys.putString(Tag.ReferencedSOPInstanceUIDInFile, VR.UI, uid);
        return findFirstMatchingChildRecord(serrec, keys, false);
    }

    private DicomObject readRecord(int offset, DicomObject keys,
            boolean ignoreCaseOfPN) throws IOException {
        while (offset != 0) {
            DicomObject item = readRecord(offset);
            if ((showInactiveRecords || item.getInt(Tag.RecordInUseFlag) != INACTIVE)
                    && (keys == null || item.matches(keys, ignoreCaseOfPN)))
                return item;
            offset = item.getInt(Tag.OffsetOfTheNextDirectoryRecord);
        }
        return null;
    }

    protected DicomObject readRecord(int offset) throws IOException {
        if (offset == 0) {
            return null;
        }
        DicomObject item = cache.get(offset);
        long off = offset & 0xffffffffL;
        if (item != null) {
            Log.d(TAG, "Get record @ " + Long.toString(off) + " from cache");
        } else {
            Log.d(TAG, "Load record @ " + Long.toString(off) + " from file " + file.getName());
            raf.seek(off);
            in.setStreamPosition(off);
            item = new BasicDicomObject();
            in.readItem(item);
            cache.put(offset, item);
        }
        return item;
    }

    protected DicomObject lastChildRecord(DicomObject parentRec)
            throws IOException {
        DicomObject child = readRecord(parentRec
                .getInt(Tag.OffsetOfReferencedLowerLevelDirectoryEntity));
        return child != null ? lastSiblingOrThis(child) : null;
    }

    protected DicomObject lastSiblingOrThis(DicomObject rec) throws IOException {
        DicomObject next;
        while ((next = readRecord(rec
                .getInt(Tag.OffsetOfTheNextDirectoryRecord))) != null) {
            rec = next;
        }
        return rec;
    }

    public void close() throws IOException {
        raf.close();
    }
}
