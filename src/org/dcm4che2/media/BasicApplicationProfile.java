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

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @version $Revision: 15806 $ $Date: 2011-08-12 18:19:11 +0200 (Fri, 12 Aug 2011) $
 * @since 16.07.2006
 */

public class BasicApplicationProfile implements ApplicationProfile {

    private static final int[] PATIENT_KEYS = { Tag.SpecificCharacterSet,
            Tag.PatientName, Tag.PatientID };

    private static final int[] STUDY_KEYS = { Tag.SpecificCharacterSet,
            Tag.StudyDate, Tag.StudyTime, Tag.AccessionNumber,
            Tag.StudyDescription, Tag.StudyInstanceUID, Tag.StudyID };

    private static final int[] SERIES_KEYS = { Tag.SpecificCharacterSet,
            Tag.Modality, Tag.SeriesInstanceUID, Tag.SeriesNumber };

    private static final int[] IMAGE_KEYS = { Tag.SpecificCharacterSet,
            Tag.InstanceNumber };

    private static final int[] RT_DOSE_SET = { Tag.SpecificCharacterSet,
            Tag.InstanceNumber, Tag.DoseSummationType };

    private static final int[] RT_STRUCTURE_SET = { Tag.SpecificCharacterSet,
            Tag.InstanceNumber, Tag.StructureSetLabel, Tag.StructureSetDate,
            Tag.StructureSetTime };

    private static final int[] RT_PLAN_KEYS = { Tag.SpecificCharacterSet,
            Tag.InstanceNumber, Tag.RTPlanLabel, Tag.RTPlanDate, Tag.RTPlanTime };

    private static final int[] RT_TREATMENT_KEYS = { Tag.SpecificCharacterSet,
            Tag.InstanceNumber, Tag.TreatmentDate, Tag.TreatmentTime };

    private static final int[] PRESENTATION_KEYS = { Tag.SpecificCharacterSet,
            Tag.ReferencedSeriesSequence, Tag.InstanceNumber, Tag.ContentLabel,
            Tag.ContentDescription, Tag.PresentationCreationDate,
            Tag.PresentationCreationTime, Tag.ContentCreatorName, };

    private static final int[] SR_DOCUMENT_KEYS = { Tag.SpecificCharacterSet,
            Tag.ContentDate, Tag.ContentTime, Tag.InstanceNumber,
            Tag.VerificationDateTime, Tag.ConceptNameCodeSequence,
            Tag.CompletionFlag, Tag.VerificationFlag, };

    private static final int[] KEY_OBJECT_DOCUMENT_KEYS = {
            Tag.SpecificCharacterSet, Tag.ContentDate, Tag.ContentTime,
            Tag.InstanceNumber, Tag.ConceptNameCodeSequence };

    private static final int[] WAVEFROM_KEYS = { Tag.SpecificCharacterSet,
            Tag.ContentDate, Tag.ContentTime, Tag.InstanceNumber };

    private static final int[] SPECTROSCOPY_KEYS = { Tag.SpecificCharacterSet,
            Tag.ImageType, Tag.ContentDate, Tag.ContentTime,
            Tag.ReferencedImageEvidenceSequence, Tag.InstanceNumber,
            Tag.NumberOfFrames, Tag.Rows, Tag.Columns, Tag.DataPointRows,
            Tag.DataPointColumns };

    private static final int[] RAWDATA_KEYS = WAVEFROM_KEYS;

    private static final int[] REGISTRATION_KEYS = { Tag.SpecificCharacterSet,
            Tag.ContentDate, Tag.ContentTime, Tag.InstanceNumber,
            Tag.ContentLabel, Tag.ContentDescription, Tag.ContentCreatorName };

    private static final int[] FIDUCIAL_KEYS = REGISTRATION_KEYS;

    private static final int[] HANGING_PROTOCOL_KEYS = {
            Tag.SpecificCharacterSet, Tag.HangingProtocolName,
            Tag.HangingProtocolDescription, Tag.HangingProtocolLevel,
            Tag.HangingProtocolCreator, Tag.HangingProtocolCreationDateTime,
            Tag.HangingProtocolDefinitionSequence,
            Tag.NumberOfPriorsReferenced,
            Tag.HangingProtocolUserIdentificationCodeSequence };

    private static final int[] ENCAPSULATED_DOCUMENT_KEYS = {
            Tag.SpecificCharacterSet, Tag.ContentDate, Tag.ContentTime,
            Tag.InstanceNumber, Tag.ConceptNameCodeSequence, Tag.DocumentTitle,
            Tag.MIMETypeOfEncapsulatedDocument };

    private static final int[] HL7_STRUCTURED_DOCUMENT_KEYS = {
            Tag.SpecificCharacterSet, Tag.HL7InstanceIdentifier,
            Tag.HL7DocumentEffectiveTime, Tag.HL7DocumentTypeCodeSequence,
            Tag.DocumentTitle };

    private static final int[] REAL_WORLD_VALUE_MAPPING_KEYS = REGISTRATION_KEYS;

    private int[] patientKeys = PATIENT_KEYS;
    private int[] studyKeys = STUDY_KEYS;
    private int[] seriesKeys = SERIES_KEYS;
    private int[] imageKeys = IMAGE_KEYS;
    private int[] rtDoseKeys = RT_DOSE_SET;
    private int[] rtStructureSetKeys = RT_STRUCTURE_SET;
    private int[] rtPlanKeys = RT_PLAN_KEYS;
    private int[] rtTreatmentRecordKeys = RT_TREATMENT_KEYS;
    private int[] presentationKeys = PRESENTATION_KEYS;
    private int[] waveformKeys = WAVEFROM_KEYS;
    private int[] srDocumentKeys = SR_DOCUMENT_KEYS;
    private int[] keyObjectDocumentKeys = KEY_OBJECT_DOCUMENT_KEYS;
    private int[] spectroscopyKeys = SPECTROSCOPY_KEYS;
    private int[] rawdataKeys = RAWDATA_KEYS;
    private int[] registrationKeys = REGISTRATION_KEYS;
    private int[] fiducialKeys = FIDUCIAL_KEYS;
    private int[] hangingProtocolKeys = HANGING_PROTOCOL_KEYS;
    private int[] encapsulatedDocumentKeys = ENCAPSULATED_DOCUMENT_KEYS;
    private int[] hl7StructuredDocumentKeys = HL7_STRUCTURED_DOCUMENT_KEYS;
    private int[] realWorldValueMappingKeys = REAL_WORLD_VALUE_MAPPING_KEYS;

    public final int[] getEncapsulatedDocumentKeys() {
        return encapsulatedDocumentKeys.clone();
    }
    
    public final void setEncapsulatedDocumentKeys(int[] encapsulatedDocumentKeys) {
        this.encapsulatedDocumentKeys = encapsulatedDocumentKeys.clone();
    }
    
    public final int[] getFiducialKeys() {
        return fiducialKeys.clone();
    }
    
    public final void setFiducialKeys(int[] fiducialKeys) {
        this.fiducialKeys = fiducialKeys.clone();
    }
    
    public final int[] getHangingProtocolKeys() {
        return hangingProtocolKeys.clone();
    }
    
    public final void setHangingProtocolKeys(int[] hangingProtocolKeys) {
        this.hangingProtocolKeys = hangingProtocolKeys.clone();
    }
    
    public final int[] getHl7StructuredDocumentKeys() {
        return hl7StructuredDocumentKeys.clone();
    }
    
    public final void setHl7StructuredDocumentKeys(
            int[] hl7StructuredDocumentKeys) {
        this.hl7StructuredDocumentKeys = hl7StructuredDocumentKeys.clone();
    }
    
    public final int[] getImageKeys() {
        return imageKeys.clone();
    }
    
    public final void setImageKeys(int[] imageKeys) {
        this.imageKeys = imageKeys.clone();
    }
    
    public final int[] getKeyObjectDocumentKeys() {
        return keyObjectDocumentKeys.clone();
    }
    
    public final void setKeyObjectDocumentKeys(int[] keyObjectDocumentKeys) {
        this.keyObjectDocumentKeys = keyObjectDocumentKeys.clone();
    }
    
    public final int[] getPatientKeys() {
        return patientKeys.clone();
    }
    
    public final void setPatientKeys(int[] patientKeys) {
        this.patientKeys = patientKeys.clone();
    }
    
    public final int[] getPresentationKeys() {
        return presentationKeys.clone();
    }
    
    public final void setPresentationKeys(int[] presentationKeys) {
        this.presentationKeys = presentationKeys.clone();
    }
    
    public final int[] getRawdataKeys() {
        return rawdataKeys.clone();
    }
    
    public final void setRawdataKeys(int[] rawdataKeys) {
        this.rawdataKeys = rawdataKeys.clone();
    }
    
    public final int[] getRealWorldValueMappingKeys() {
        return realWorldValueMappingKeys.clone();
    }
    
    public final void setRealWorldValueMappingKeys(
            int[] realWorldValueMappingKeys) {
        this.realWorldValueMappingKeys = realWorldValueMappingKeys.clone();
    }
    
    public final int[] getRegistrationKeys() {
        return registrationKeys.clone();
    }
    
    public final void setRegistrationKeys(int[] registrationKeys) {
        this.registrationKeys = registrationKeys.clone();
    }
    
    public final int[] getRtDoseKeys() {
        return rtDoseKeys.clone();
    }
    
    public final void setRtDoseKeys(int[] rtDoseKeys) {
        this.rtDoseKeys = rtDoseKeys.clone();
    }
    
    public final int[] getRtPlanKeys() {
        return rtPlanKeys.clone();
    }
    
    public final void setRtPlanKeys(int[] rtPlanKeys) {
        this.rtPlanKeys = rtPlanKeys.clone();
    }
    
    public final int[] getRtStructureSetKeys() {
        return rtStructureSetKeys.clone();
    }
    
    public final void setRtStructureSetKeys(int[] rtStructureSetKeys) {
        this.rtStructureSetKeys = rtStructureSetKeys.clone();
    }
    
    public final int[] getRtTreatmentRecordKeys() {
        return rtTreatmentRecordKeys.clone();
    }
    
    public final void setRtTreatmentRecordKeys(int[] rtTreatmentRecordKeys) {
        this.rtTreatmentRecordKeys = rtTreatmentRecordKeys.clone();
    }
    
    public final int[] getSeriesKeys() {
        return seriesKeys.clone();
    }
    
    public final void setSeriesKeys(int[] seriesKeys) {
        this.seriesKeys = seriesKeys.clone();
    }
    
    public final int[] getSpectroscopyKeys() {
        return spectroscopyKeys.clone();
    }
    
    public final void setSpectroscopyKeys(int[] spectroscopyKeys) {
        this.spectroscopyKeys = spectroscopyKeys.clone();
    }
    
    public final int[] getSrDocumentKeys() {
        return srDocumentKeys.clone();
    }
    
    public final void setSrDocumentKeys(int[] srDocumentKeys) {
        this.srDocumentKeys = srDocumentKeys.clone();
    }
    
    public final int[] getStudyKeys() {
        return studyKeys.clone();
    }
    
    public final void setStudyKeys(int[] studyKeys) {
        this.studyKeys = studyKeys.clone();
    }
    
    public final int[] getWaveformKeys() {
        return waveformKeys.clone();
    }
    
    public final void setWaveformKeys(int[] waveformKeys) {
        this.waveformKeys = waveformKeys.clone();
    }
    
    private DicomObject makeRecord(String type, int[] keys, DicomObject dcmobj) {
        DicomObject rec = new BasicDicomObject();
        rec.putString(Tag.DirectoryRecordType, VR.CS, type);
        dcmobj.subSet(keys).copyTo(rec);
        return rec;
    }

    private DicomObject makeRecord(String type, int[] keys, DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(type,  keys, dcmobj);
        rec.putStrings(Tag.ReferencedFileID, VR.CS, fileIDs);
        rec.putString(Tag.ReferencedSOPInstanceUIDInFile, VR.UI,
                dcmobj.getString(Tag.MediaStorageSOPInstanceUID));
        rec.putString(Tag.ReferencedSOPClassUIDInFile, VR.UI,
                dcmobj.getString(Tag.MediaStorageSOPClassUID));
        rec.putString(Tag.ReferencedTransferSyntaxUIDInFile, VR.UI,
                dcmobj.getString(Tag.TransferSyntaxUID));
        String relcuid = dcmobj.getString(Tag.RelatedGeneralSOPClassUID);
        if (relcuid != null) {
            rec.putString(Tag.ReferencedRelatedGeneralSOPClassUIDInFile, VR.UI,
                    relcuid);
        }
        return rec;
    }
        
    public DicomObject makePatientDirectoryRecord(DicomObject dcmobj) {
        DicomObject rec = makeRecord(DirectoryRecordType.PATIENT, patientKeys,
                dcmobj);
        if (!rec.contains(Tag.PatientName)) {
            rec.putNull(Tag.PatientName, VR.PN);
        }
        if (!rec.containsValue(Tag.PatientID)) {
            rec.putString(Tag.PatientID, VR.LO, dcmobj
                    .getString(Tag.StudyInstanceUID));
        }
        return rec;
    }

    public DicomObject makeStudyDirectoryRecord(DicomObject dcmobj) {
        DicomObject rec = makeRecord(DirectoryRecordType.STUDY, studyKeys,
                dcmobj);
        return rec;
    }

    public DicomObject makeSeriesDirectoryRecord(DicomObject dcmobj) {
        DicomObject rec = makeRecord(DirectoryRecordType.SERIES, seriesKeys,
                dcmobj);
        return rec;
    }

    public DicomObject makeInstanceDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        String cuid = dcmobj.getString(Tag.MediaStorageSOPClassUID);
        switch (cuid.hashCode()) {
        case -525617006:
            if (UID.RawDataStorage.equals(cuid)) {
                return makeRawDataDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case -525617005:
            if (UID.RealWorldValueMappingStorage.equals(cuid)) {
                return makeRealWorldValueMappingDirectorRecord(dcmobj, fileIDs);
            }
            break;
        case 789790566:
            if (UID.EncapsulatedPDFStorage.equals(cuid)) {
                return makeEncapsulatedDocumentDirectorRecord(dcmobj, fileIDs);
            }
            break;
        case 792796575:
            if (UID.RTDoseStorage.equals(cuid)) {
                return makeRTDoseDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796576:
            if (UID.RTStructureSetStorage.equals(cuid)) {
                return makeRTStructuredSetDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796577:
            if (UID.RTBeamsTreatmentRecordStorage.equals(cuid)) {
                return makeRTTreatmentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796578:
            if (UID.RTPlanStorage.equals(cuid)) {
                return makeRTPlanDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796579:
            if (UID.RTBrachyTreatmentRecordStorage.equals(cuid)) {
                return makeRTTreatmentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796580:
            if (UID.RTTreatmentSummaryRecordStorage.equals(cuid)) {
                return makeRTTreatmentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796581:
            if (UID.RTIonPlanStorage.equals(cuid)) {
                return makeRTPlanDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 792796582:
            if (UID.RTIonBeamsTreatmentRecordStorage.equals(cuid)) {
                return makeRTTreatmentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796487868:
            if (UID.BasicTextSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796487900:
            if (UID.EnhancedSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796487932:
            if (UID.ComprehensiveSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796487960:
            if (UID.ProcedureLogStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796487991:
            if (UID.MammographyCADSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796488000:
            if (UID.KeyObjectSelectionDocumentStorage.equals(cuid)) {
                return makeKeyObjectDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796488027:
            if (UID.ChestCADSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 796488029:
            if (UID.XRayRadiationDoseSRStorage.equals(cuid)) {
                return makeSRDocumentDirectoryRecord(dcmobj, fileIDs);
            }
            break;            
        case 797116269:
            if (UID.TwelveLeadECGWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 797116270:
            if (UID.GeneralECGWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 797116271:
            if (UID.AmbulatoryECGWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 797117230:
            if (UID.HemodynamicWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 797118191:
            if (UID.CardiacElectrophysiologyWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 797119152:
            if (UID.BasicVoiceAudioWaveformStorage.equals(cuid)) {
                return makeWaveformDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 885739878:
            if (UID.MRSpectroscopyStorage.equals(cuid)) {
                return makeSpectroscopyDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688045877:
            if (UID.GrayscaleSoftcopyPresentationStateStorageSOPClass.equals(cuid)) {
                return makePresentationStateDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688045878:
            if (UID.ColorSoftcopyPresentationStateStorageSOPClass.equals(cuid)) {
                return makePresentationStateDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688045879:
            if (UID.PseudoColorSoftcopyPresentationStateStorageSOPClass.equals(cuid)) {
                return makePresentationStateDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688045880:
            if (UID.BlendingSoftcopyPresentationStateStorageSOPClass.equals(cuid)) {
                return makePresentationStateDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688199637:
            if (UID.SpatialRegistrationStorage.equals(cuid)) {
                return makeRegistrationDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        case 1688199638:
            if (UID.SpatialFiducialsStorage.equals(cuid)) {
                return makeFiducialDirectoryRecord(dcmobj, fileIDs);
            }
            break;
        }
        return makeImageDirectoryRecord(dcmobj, fileIDs);
    }

    public DicomObject makeImageDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.IMAGE, imageKeys,
                dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRTDoseDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.RT_DOSE, rtDoseKeys,
                dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRTStructuredSetDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.RT_STRUCTURE_SET,
                rtStructureSetKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRTPlanDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.RT_PLAN, rtPlanKeys,
                dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRTTreatmentDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.RT_TREAT_RECORD,
                rtTreatmentRecordKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makePresentationStateDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.PRESENTATION,
                presentationKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeWaveformDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.WAVEFORM,
                waveformKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeSRDocumentDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.SR_DOCUMENT,
                srDocumentKeys, dcmobj, fileIDs);
        copyConceptNameModifiers(dcmobj, rec);
        return rec;
    }

    public DicomObject makeKeyObjectDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.KEY_OBJECT_DOC,
                keyObjectDocumentKeys, dcmobj, fileIDs);
        copyConceptNameModifiers(dcmobj, rec);
        return rec;
    }

    private void copyConceptNameModifiers(DicomObject dcmobj, DicomObject rec) {
        DicomElement objsq = dcmobj.get(Tag.ContentSequence);
        if (objsq == null) {
            return;
        }
        DicomElement recsq = null;
        DicomObject item;
        for (int i = 0, n = objsq.countItems(); i < n; i++) {
            item = objsq.getDicomObject(i);
            if ("HAS CONCEPT MOD".equals(item.getString(Tag.RelationshipType))) {
                if (recsq == null) { // lazy sequence creation
                    recsq = rec.putSequence(Tag.ContentSequence);
                }
                recsq.addDicomObject(item);
            }
        }
    }

    public DicomObject makeSpectroscopyDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.SPECTROSCOPY,
                spectroscopyKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRawDataDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.RAW_DATA, rawdataKeys,
                dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRegistrationDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.REGISTRATION,
                registrationKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeFiducialDirectoryRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.FIDUCIAL,
                fiducialKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeHangingProtocolDirectorRecord(DicomObject dcmobj,
            String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.HANGING_PROTOCOL,
                hangingProtocolKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeEncapsulatedDocumentDirectorRecord(
            DicomObject dcmobj, String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.ENCAP_DOC,
                encapsulatedDocumentKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeHL7StructuredDocumentDirectorRecord(
            DicomObject dcmobj, String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.HL7_STRUC_DOC,
                hl7StructuredDocumentKeys, dcmobj, fileIDs);
        return rec;
    }

    public DicomObject makeRealWorldValueMappingDirectorRecord(
            DicomObject dcmobj, String[] fileIDs) {
        DicomObject rec = makeRecord(DirectoryRecordType.VALUE_MAP,
                realWorldValueMappingKeys, dcmobj, fileIDs);
        return rec;
    }

}
