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

import java.io.File;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.StringUtils;
import org.dcm4che2.util.UIDUtils;

/**
 * @author Gunter Zeilinger<gunterze@gmail.com>
 * @version $Revision: 12635 $ $Date:: 2010-01-18#$
 * @since 10.07.2006
 */

public class FileSetInformation extends FileMetaInformation {

    public static final int NO_KNOWN_INCONSISTENCIES = 0;
    public static final int KNOWN_INCONSISTENCIES = 0xffff;
    
    public FileSetInformation(DicomObject dcmobj) {
        super(dcmobj);
    }

    public FileSetInformation() {
        super();
    }
    
    @Override
    public void init() {
        super.init();
        dcmobj.putNull(Tag.FileSetID, VR.CS);
        setOffsetFirstRootRecord(0);
        setOffsetLastRootRecord(0);
        setFileSetConsistencyFlag(0);
    }

    public String getFileSetID() {
        return dcmobj.getString(Tag.FileSetID);
    }

    public void setFileSetID(String id) {
        dcmobj.putString(Tag.FileSetID, VR.CS, id);
    }

    public String[] getFileSetDescriptorFileID() {
        return dcmobj.getStrings(Tag.FileSetDescriptorFileID);
    }

    public void setFileSetDescriptorFileID(String[] cs) {
        dcmobj.putStrings(Tag.FileSetDescriptorFileID, VR.CS, cs);
    }

    public File getFileSetDescriptorFile(File basedir) {
        return toFile(getFileSetDescriptorFileID(), basedir);
    }
    
    public void setFileSetDescriptorFile(File file, File basedir) {
        setFileSetDescriptorFileID(toFileID(file, basedir));
    }
    
    public String getSpecificCharacterSetofFileSetDescriptorFile() {
        return dcmobj.getString(Tag.SpecificCharacterSetOfFileSetDescriptorFile);
    }

    public void setSpecificCharacterSetofFileSetDescriptorFile(String cs) {
        dcmobj.putString(Tag.FileSetID, VR.CS, cs);
    }
    
    public int getOffsetFirstRootRecord() {
        return dcmobj.getInt(
                Tag.OffsetOfTheFirstDirectoryRecordOfTheRootDirectoryEntity);
    }

    public void setOffsetFirstRootRecord(int offset) {
        dcmobj.putInt(Tag.OffsetOfTheFirstDirectoryRecordOfTheRootDirectoryEntity,
                VR.UL, offset);
    }

    public int getOffsetLastRootRecord() {
        return dcmobj.getInt(
                Tag.OffsetOfTheLastDirectoryRecordOfTheRootDirectoryEntity);
    }

    public void setOffsetLastRootRecord(int offset) {
        dcmobj.putInt(Tag.OffsetOfTheLastDirectoryRecordOfTheRootDirectoryEntity,
                VR.UL, offset);
    }
    
    public boolean isEmpty() {
        return getOffsetFirstRootRecord() == 0;
    }

    public int getFileSetConsistencyFlag() {
        return dcmobj.getInt(Tag.FileSetConsistencyFlag);
    }
    
    public void setFileSetConsistencyFlag(int flag) {
        dcmobj.putInt(Tag.FileSetConsistencyFlag, VR.US, flag);
    }
    
    public boolean isNoKnownInconsistencies() {
	return getFileSetConsistencyFlag() == NO_KNOWN_INCONSISTENCIES;
    }
    
    @Override
    protected String getSOPClassUID() {
        return UID.MediaStorageDirectoryStorage;
    }

    @Override
    protected String getSOPInstanceUID() {
        return UIDUtils.createUID();
    }

    public static File toFile(String[] fileID, File basedir) {
        if (fileID == null || fileID.length == 0) {
            return null;
        }        
        StringBuilder sb = new StringBuilder(fileID[0]);
        for (int i = 1; i < fileID.length; i++) {
            sb.append(File.separatorChar).append(fileID[i]);
        }
        return new File(basedir, sb.toString());
    }

    public static String[] toFileID(File file, File basedir) {
        String filepath = file.getPath();
        if (basedir != null) {
            String dirpath = trimDirPath(basedir.getPath())
                    + File.separatorChar;
            if (!filepath.startsWith(dirpath)) {
                throw new IllegalArgumentException("file " + file 
                        + " not included in file-set " + basedir);
            }
            filepath = filepath.substring(dirpath.length());
        }
        return StringUtils.split(trimFilePath(filepath), File.separatorChar);
    }

    private static String trimFilePath(String path) {
        return (path.length() > 1 && path.charAt(0) == '.' 
                && path.charAt(1) == File.separatorChar)
                    ? path.substring(2) : path;
    }

    private static String trimDirPath(String path) {
        int len;
        return ((len = path.length()) > 1 && path.charAt(len-1) == '.' 
            && path.charAt(len-2) == File.separatorChar)
            ? path.substring(0, len-2) : path;
    }


}
