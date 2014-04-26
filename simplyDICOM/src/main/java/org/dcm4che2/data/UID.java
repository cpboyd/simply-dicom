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

/** Provides tag constants.*/
public class UID {

    /** Private constructor */
    private UID() {
    }
    
    public static final String forName(String name) {
       try {
          return (String) UID.class.getField(name).get(null);
       } catch (IllegalAccessException e) {
          throw new Error(e);
       } catch (NoSuchFieldException e) {
          throw new IllegalArgumentException("Unknown UID Name: " + name);
       }
    }

    /** Verification SOP Class - SOP Class */
    public static final String VerificationSOPClass = "1.2.840.10008.1.1";

    /** Implicit VR Little Endian - Transfer Syntax */
    public static final String ImplicitVRLittleEndian = "1.2.840.10008.1.2";

    /** Explicit VR Little Endian - Transfer Syntax */
    public static final String ExplicitVRLittleEndian = "1.2.840.10008.1.2.1";

    /** Deflated Explicit VR Little Endian - Transfer Syntax */
    public static final String DeflatedExplicitVRLittleEndian = "1.2.840.10008.1.2.1.99";

    /** Explicit VR Big Endian - Transfer Syntax */
    public static final String ExplicitVRBigEndian = "1.2.840.10008.1.2.2";

    /** JPEG Baseline (Process 1) - Transfer Syntax */
    public static final String JPEGBaseline1 = "1.2.840.10008.1.2.4.50";

    /** JPEG Extended (Process 2 & 4) - Transfer Syntax */
    public static final String JPEGExtended24 = "1.2.840.10008.1.2.4.51";

    /** JPEG Extended (Process 3 & 5) (Retired) - Transfer Syntax */
    public static final String JPEGExtended35Retired = "1.2.840.10008.1.2.4.52";

    /** JPEG Spectral Selection, Non-Hierarchical (Process 6 & 8) (Retired) - Transfer Syntax */
    public static final String JPEGSpectralSelectionNonHierarchical68Retired = "1.2.840.10008.1.2.4.53";

    /** JPEG Spectral Selection, Non-Hierarchical (Process 7 & 9) (Retired) - Transfer Syntax */
    public static final String JPEGSpectralSelectionNonHierarchical79Retired = "1.2.840.10008.1.2.4.54";

    /** JPEG Full Progression, Non-Hierarchical (Process 10 & 12) (Retired) - Transfer Syntax */
    public static final String JPEGFullProgressionNonHierarchical1012Retired = "1.2.840.10008.1.2.4.55";

    /** JPEG Full Progression, Non-Hierarchical (Process 11 & 13) (Retired) - Transfer Syntax */
    public static final String JPEGFullProgressionNonHierarchical1113Retired = "1.2.840.10008.1.2.4.56";

    /** JPEG Lossless, Non-Hierarchical (Process 14) - Transfer Syntax */
    public static final String JPEGLosslessNonHierarchical14 = "1.2.840.10008.1.2.4.57";

    /** JPEG Lossless, Non-Hierarchical (Process 15) (Retired) - Transfer Syntax */
    public static final String JPEGLosslessNonHierarchical15Retired = "1.2.840.10008.1.2.4.58";

    /** JPEG Extended, Hierarchical (Process 16 & 18) (Retired) - Transfer Syntax */
    public static final String JPEGExtendedHierarchical1618Retired = "1.2.840.10008.1.2.4.59";

    /** JPEG Extended, Hierarchical (Process 17 & 19) (Retired) - Transfer Syntax */
    public static final String JPEGExtendedHierarchical1719Retired = "1.2.840.10008.1.2.4.60";

    /** JPEG Spectral Selection, Hierarchical (Process 20 & 22) (Retired) - Transfer Syntax */
    public static final String JPEGSpectralSelectionHierarchical2022Retired = "1.2.840.10008.1.2.4.61";

    /** JPEG Spectral Selection, Hierarchical (Process 21 & 23) (Retired) - Transfer Syntax */
    public static final String JPEGSpectralSelectionHierarchical2123Retired = "1.2.840.10008.1.2.4.62";

    /** JPEG Full Progression, Hierarchical (Process 24 & 26) (Retired) - Transfer Syntax */
    public static final String JPEGFullProgressionHierarchical2426Retired = "1.2.840.10008.1.2.4.63";

    /** JPEG Full Progression, Hierarchical (Process 25 & 27) (Retired) - Transfer Syntax */
    public static final String JPEGFullProgressionHierarchical2527Retired = "1.2.840.10008.1.2.4.64";

    /** JPEG Lossless, Hierarchical (Process 28) (Retired) - Transfer Syntax */
    public static final String JPEGLosslessHierarchical28Retired = "1.2.840.10008.1.2.4.65";

    /** JPEG Lossless, Hierarchical (Process 29) (Retired) - Transfer Syntax */
    public static final String JPEGLosslessHierarchical29Retired = "1.2.840.10008.1.2.4.66";

    /** JPEG Lossless, Non-Hierarchical, First-Order Prediction (Process 14 [Selection Value 1]) - Transfer Syntax */
    public static final String JPEGLossless = "1.2.840.10008.1.2.4.70";

    /** JPEG-LS Lossless Image Compression - Transfer Syntax */
    public static final String JPEGLSLossless = "1.2.840.10008.1.2.4.80";

    /** JPEG-LS Lossy (Near-Lossless) Image Compression - Transfer Syntax */
    public static final String JPEGLSLossyNearLossless = "1.2.840.10008.1.2.4.81";

    /** JPEG 2000 Image Compression (Lossless Only) - Transfer Syntax */
    public static final String JPEG2000LosslessOnly = "1.2.840.10008.1.2.4.90";

    /** JPEG 2000 Image Compression - Transfer Syntax */
    public static final String JPEG2000 = "1.2.840.10008.1.2.4.91";

    /** JPEG 2000 Part 2 Multi-component Image Compression (Lossless Only) - Transfer Syntax */
    public static final String JPEG2000Part2MultiComponentLosslessOnly = "1.2.840.10008.1.2.4.92";

    /** JPEG 2000 Part 2 Multi-component Image Compression - Transfer Syntax */
    public static final String JPEG2000Part2MultiComponent = "1.2.840.10008.1.2.4.93";

    /** JPIP Referenced - Transfer Syntax */
    public static final String JPIPReferenced = "1.2.840.10008.1.2.4.94";

    /** JPIP Referenced Deflate - Transfer Syntax */
    public static final String JPIPReferencedDeflate = "1.2.840.10008.1.2.4.95";

    /** MPEG2 Main Profile @ Main Level - Transfer Syntax */
    public static final String MPEG2 = "1.2.840.10008.1.2.4.100";

    /** MPEG2 Main Profile @ High Level - Transfer Syntax */
    public static final String MPEG2MainProfileHighLevel = "1.2.840.10008.1.2.4.101";

    /** MPEG-4 AVC/H.264 High Profile / Level 4.1 - Transfer Syntax */
    public static final String MPEG4AVCH264HighProfileLevel41 = "1.2.840.10008.1.2.4.102";

    /** MPEG-4 AVC/H.264 BD-compatible High Profile / Level 4.1 - Transfer Syntax */
    public static final String MPEG4AVCH264BDCompatibleHighProfileLevel41 = "1.2.840.10008.1.2.4.103";

    /** RLE Lossless - Transfer Syntax */
    public static final String RLELossless = "1.2.840.10008.1.2.5";

    /** RFC 2557 MIME encapsulation - Transfer Syntax */
    public static final String RFC2557MIMEEncapsulation = "1.2.840.10008.1.2.6.1";

    /** XML Encoding - Transfer Syntax */
    public static final String XMLEncoding = "1.2.840.10008.1.2.6.2";

    /** Media Storage Directory Storage - SOP Class */
    public static final String MediaStorageDirectoryStorage = "1.2.840.10008.1.3.10";

    /** Talairach Brain Atlas Frame of Reference - Well-known frame of reference */
    public static final String TalairachBrainAtlasFrameOfReference = "1.2.840.10008.1.4.1.1";

    /** SPM2 T1 Frame of Reference - Well-known frame of reference */
    public static final String SPM2T1FrameOfReference = "1.2.840.10008.1.4.1.2";

    /** SPM2 T2 Frame of Reference - Well-known frame of reference */
    public static final String SPM2T2FrameOfReference = "1.2.840.10008.1.4.1.3";

    /** SPM2 PD Frame of Reference - Well-known frame of reference */
    public static final String SPM2PDFrameOfReference = "1.2.840.10008.1.4.1.4";

    /** SPM2 EPI Frame of Reference - Well-known frame of reference */
    public static final String SPM2EPIFrameOfReference = "1.2.840.10008.1.4.1.5";

    /** SPM2 FIL T1 Frame of Reference - Well-known frame of reference */
    public static final String SPM2FILT1FrameOfReference = "1.2.840.10008.1.4.1.6";

    /** SPM2 PET Frame of Reference - Well-known frame of reference */
    public static final String SPM2PETFrameOfReference = "1.2.840.10008.1.4.1.7";

    /** SPM2 TRANSM Frame of Reference - Well-known frame of reference */
    public static final String SPM2TRANSMFrameOfReference = "1.2.840.10008.1.4.1.8";

    /** SPM2 SPECT Frame of Reference - Well-known frame of reference */
    public static final String SPM2SPECTFrameOfReference = "1.2.840.10008.1.4.1.9";

    /** SPM2 GRAY Frame of Reference - Well-known frame of reference */
    public static final String SPM2GRAYFrameOfReference = "1.2.840.10008.1.4.1.10";

    /** SPM2 WHITE Frame of Reference - Well-known frame of reference */
    public static final String SPM2WHITEFrameOfReference = "1.2.840.10008.1.4.1.11";

    /** SPM2 CSF Frame of Reference - Well-known frame of reference */
    public static final String SPM2CSFFrameOfReference = "1.2.840.10008.1.4.1.12";

    /** SPM2 BRAINMASK Frame of Reference - Well-known frame of reference */
    public static final String SPM2BRAINMASKFrameOfReference = "1.2.840.10008.1.4.1.13";

    /** SPM2 AVG305T1 Frame of Reference - Well-known frame of reference */
    public static final String SPM2AVG305T1FrameOfReference = "1.2.840.10008.1.4.1.14";

    /** SPM2 AVG152T1 Frame of Reference - Well-known frame of reference */
    public static final String SPM2AVG152T1FrameOfReference = "1.2.840.10008.1.4.1.15";

    /** SPM2 AVG152T2 Frame of Reference - Well-known frame of reference */
    public static final String SPM2AVG152T2FrameOfReference = "1.2.840.10008.1.4.1.16";

    /** SPM2 AVG152PD Frame of Reference - Well-known frame of reference */
    public static final String SPM2AVG152PDFrameOfReference = "1.2.840.10008.1.4.1.17";

    /** SPM2 SINGLESUBJT1 Frame of Reference - Well-known frame of reference */
    public static final String SPM2SINGLESUBJT1FrameOfReference = "1.2.840.10008.1.4.1.18";

    /** ICBM 452 T1 Frame of Reference - Well-known frame of reference */
    public static final String ICBM452T1FrameOfReference = "1.2.840.10008.1.4.2.1";

    /** ICBM Single Subject MRI Frame of Reference - Well-known frame of reference */
    public static final String ICBMSingleSubjectMRIFrameOfReference = "1.2.840.10008.1.4.2.2";

    /** Hot Iron Color Palette SOP Instance - Well-known SOP Instance */
    public static final String HotIronColorPaletteSOPInstance = "1.2.840.10008.1.5.1";

    /** PET Color Palette SOP Instance - Well-known SOP Instance */
    public static final String PETColorPaletteSOPInstance = "1.2.840.10008.1.5.2";

    /** Hot Metal Blue Color Palette SOP Instance - Well-known SOP Instance */
    public static final String HotMetalBlueColorPaletteSOPInstance = "1.2.840.10008.1.5.3";

    /** PET 20 Step Color Palette SOP Instance - Well-known SOP Instance */
    public static final String PET20StepColorPaletteSOPInstance = "1.2.840.10008.1.5.4";

    /** Basic Study Content Notification SOP Class (Retired) - SOP Class */
    public static final String BasicStudyContentNotificationSOPClassRetired = "1.2.840.10008.1.9";

    /** Storage Commitment Push Model SOP Class - SOP Class */
    public static final String StorageCommitmentPushModelSOPClass = "1.2.840.10008.1.20.1";

    /** Storage Commitment Push Model SOP Instance - Well-known SOP Instance */
    public static final String StorageCommitmentPushModelSOPInstance = "1.2.840.10008.1.20.1.1";

    /** Storage Commitment Pull Model SOP Class (Retired) - SOP Class */
    public static final String StorageCommitmentPullModelSOPClassRetired = "1.2.840.10008.1.20.2";

    /** Storage Commitment Pull Model SOP Instance (Retired) - Well-known SOP Instance */
    public static final String StorageCommitmentPullModelSOPInstanceRetired = "1.2.840.10008.1.20.2.1";

    /** Procedural Event Logging SOP Class - SOP Class */
    public static final String ProceduralEventLoggingSOPClass = "1.2.840.10008.1.40";

    /** Procedural Event Logging SOP Instance - Well-known SOP Instance */
    public static final String ProceduralEventLoggingSOPInstance = "1.2.840.10008.1.40.1";

    /** Substance Administration Logging SOP Class - SOP Class */
    public static final String SubstanceAdministrationLoggingSOPClass = "1.2.840.10008.1.42";

    /** Substance Administration Logging SOP Instance - Well-known SOP Instance */
    public static final String SubstanceAdministrationLoggingSOPInstance = "1.2.840.10008.1.42.1";

    /** DICOM UID Registry - DICOM UIDs as a Coding Scheme */
    public static final String DICOMUIDRegistry = "1.2.840.10008.2.6.1";

    /** DICOM Controlled Terminology - Coding Scheme */
    public static final String DICOMControlledTerminology = "1.2.840.10008.2.16.4";

    /** DICOM Application Context Name - Application Context Name */
    public static final String DICOMApplicationContextName = "1.2.840.10008.3.1.1.1";

    /** Detached Patient Management SOP Class (Retired) - SOP Class */
    public static final String DetachedPatientManagementSOPClassRetired = "1.2.840.10008.3.1.2.1.1";

    /** Detached Patient Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String DetachedPatientManagementMetaSOPClassRetired = "1.2.840.10008.3.1.2.1.4";

    /** Detached Visit Management SOP Class (Retired) - SOP Class */
    public static final String DetachedVisitManagementSOPClassRetired = "1.2.840.10008.3.1.2.2.1";

    /** Detached Study Management SOP Class (Retired) - SOP Class */
    public static final String DetachedStudyManagementSOPClassRetired = "1.2.840.10008.3.1.2.3.1";

    /** Study Component Management SOP Class (Retired) - SOP Class */
    public static final String StudyComponentManagementSOPClassRetired = "1.2.840.10008.3.1.2.3.2";

    /** Modality Performed Procedure Step SOP Class - SOP Class */
    public static final String ModalityPerformedProcedureStepSOPClass = "1.2.840.10008.3.1.2.3.3";

    /** Modality Performed Procedure Step Retrieve SOP Class - SOP Class */
    public static final String ModalityPerformedProcedureStepRetrieveSOPClass = "1.2.840.10008.3.1.2.3.4";

    /** Modality Performed Procedure Step Notification SOP Class - SOP Class */
    public static final String ModalityPerformedProcedureStepNotificationSOPClass = "1.2.840.10008.3.1.2.3.5";

    /** Detached Results Management SOP Class (Retired) - SOP Class */
    public static final String DetachedResultsManagementSOPClassRetired = "1.2.840.10008.3.1.2.5.1";

    /** Detached Results Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String DetachedResultsManagementMetaSOPClassRetired = "1.2.840.10008.3.1.2.5.4";

    /** Detached Study Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String DetachedStudyManagementMetaSOPClassRetired = "1.2.840.10008.3.1.2.5.5";

    /** Detached Interpretation Management SOP Class (Retired) - SOP Class */
    public static final String DetachedInterpretationManagementSOPClassRetired = "1.2.840.10008.3.1.2.6.1";

    /** Storage Service Class - Service Class */
    public static final String StorageServiceClass = "1.2.840.10008.4.2";

    /** Basic Film Session SOP Class - SOP Class */
    public static final String BasicFilmSessionSOPClass = "1.2.840.10008.5.1.1.1";

    /** Basic Film Box SOP Class - SOP Class */
    public static final String BasicFilmBoxSOPClass = "1.2.840.10008.5.1.1.2";

    /** Basic Grayscale Image Box SOP Class - SOP Class */
    public static final String BasicGrayscaleImageBoxSOPClass = "1.2.840.10008.5.1.1.4";

    /** Basic Color Image Box SOP Class - SOP Class */
    public static final String BasicColorImageBoxSOPClass = "1.2.840.10008.5.1.1.4.1";

    /** Referenced Image Box SOP Class (Retired) - SOP Class */
    public static final String ReferencedImageBoxSOPClassRetired = "1.2.840.10008.5.1.1.4.2";

    /** Basic Grayscale Print Management Meta SOP Class - Meta SOP Class */
    public static final String BasicGrayscalePrintManagementMetaSOPClass = "1.2.840.10008.5.1.1.9";

    /** Referenced Grayscale Print Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String ReferencedGrayscalePrintManagementMetaSOPClassRetired = "1.2.840.10008.5.1.1.9.1";

    /** Print Job SOP Class - SOP Class */
    public static final String PrintJobSOPClass = "1.2.840.10008.5.1.1.14";

    /** Basic Annotation Box SOP Class - SOP Class */
    public static final String BasicAnnotationBoxSOPClass = "1.2.840.10008.5.1.1.15";

    /** Printer SOP Class - SOP Class */
    public static final String PrinterSOPClass = "1.2.840.10008.5.1.1.16";

    /** Printer Configuration Retrieval SOP Class - SOP Class */
    public static final String PrinterConfigurationRetrievalSOPClass = "1.2.840.10008.5.1.1.16.376";

    /** Printer SOP Instance - Well-known Printer SOP Instance */
    public static final String PrinterSOPInstance = "1.2.840.10008.5.1.1.17";

    /** Printer Configuration Retrieval SOP Instance - Well-known Printer SOP Instance */
    public static final String PrinterConfigurationRetrievalSOPInstance = "1.2.840.10008.5.1.1.17.376";

    /** Basic Color Print Management Meta SOP Class - Meta SOP Class */
    public static final String BasicColorPrintManagementMetaSOPClass = "1.2.840.10008.5.1.1.18";

    /** Referenced Color Print Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String ReferencedColorPrintManagementMetaSOPClassRetired = "1.2.840.10008.5.1.1.18.1";

    /** VOI LUT Box SOP Class - SOP Class */
    public static final String VOILUTBoxSOPClass = "1.2.840.10008.5.1.1.22";

    /** Presentation LUT SOP Class - SOP Class */
    public static final String PresentationLUTSOPClass = "1.2.840.10008.5.1.1.23";

    /** Image Overlay Box SOP Class (Retired) - SOP Class */
    public static final String ImageOverlayBoxSOPClassRetired = "1.2.840.10008.5.1.1.24";

    /** Basic Print Image Overlay Box SOP Class (Retired) - SOP Class */
    public static final String BasicPrintImageOverlayBoxSOPClassRetired = "1.2.840.10008.5.1.1.24.1";

    /** Print Queue SOP Instance (Retired) - Well-known Print Queue SOP Instance */
    public static final String PrintQueueSOPInstanceRetired = "1.2.840.10008.5.1.1.25";

    /** Print Queue Management SOP Class (Retired) - SOP Class */
    public static final String PrintQueueManagementSOPClassRetired = "1.2.840.10008.5.1.1.26";

    /** Stored Print Storage SOP Class (Retired) - SOP Class */
    public static final String StoredPrintStorageSOPClassRetired = "1.2.840.10008.5.1.1.27";

    /** Hardcopy Grayscale Image Storage SOP Class (Retired) - SOP Class */
    public static final String HardcopyGrayscaleImageStorageSOPClassRetired = "1.2.840.10008.5.1.1.29";

    /** Hardcopy Color Image Storage SOP Class (Retired) - SOP Class */
    public static final String HardcopyColorImageStorageSOPClassRetired = "1.2.840.10008.5.1.1.30";

    /** Pull Print Request SOP Class (Retired) - SOP Class */
    public static final String PullPrintRequestSOPClassRetired = "1.2.840.10008.5.1.1.31";

    /** Pull Stored Print Management Meta SOP Class (Retired) - Meta SOP Class */
    public static final String PullStoredPrintManagementMetaSOPClassRetired = "1.2.840.10008.5.1.1.32";

    /** Media Creation Management SOP Class UID - SOP Class */
    public static final String MediaCreationManagementSOPClassUID = "1.2.840.10008.5.1.1.33";

    /** Computed Radiography Image Storage - SOP Class */
    public static final String ComputedRadiographyImageStorage = "1.2.840.10008.5.1.4.1.1.1";

    /** Digital X-Ray Image Storage - For Presentation - SOP Class */
    public static final String DigitalXRayImageStorageForPresentation = "1.2.840.10008.5.1.4.1.1.1.1";

    /** Digital X-Ray Image Storage - For Processing - SOP Class */
    public static final String DigitalXRayImageStorageForProcessing = "1.2.840.10008.5.1.4.1.1.1.1.1";

    /** Digital Mammography X-Ray Image Storage - For Presentation - SOP Class */
    public static final String DigitalMammographyXRayImageStorageForPresentation = "1.2.840.10008.5.1.4.1.1.1.2";

    /** Digital Mammography X-Ray Image Storage - For Processing - SOP Class */
    public static final String DigitalMammographyXRayImageStorageForProcessing = "1.2.840.10008.5.1.4.1.1.1.2.1";

    /** Digital Intra-oral X-Ray Image Storage - For Presentation - SOP Class */
    public static final String DigitalIntraOralXRayImageStorageForPresentation = "1.2.840.10008.5.1.4.1.1.1.3";

    /** Digital Intra-oral X-Ray Image Storage - For Processing - SOP Class */
    public static final String DigitalIntraOralXRayImageStorageForProcessing = "1.2.840.10008.5.1.4.1.1.1.3.1";

    /** CT Image Storage - SOP Class */
    public static final String CTImageStorage = "1.2.840.10008.5.1.4.1.1.2";

    /** Enhanced CT Image Storage - SOP Class */
    public static final String EnhancedCTImageStorage = "1.2.840.10008.5.1.4.1.1.2.1";

    /** Ultrasound Multi-frame Image Storage (Retired) - SOP Class */
    public static final String UltrasoundMultiFrameImageStorageRetired = "1.2.840.10008.5.1.4.1.1.3";

    /** Ultrasound Multi-frame Image Storage - SOP Class */
    public static final String UltrasoundMultiFrameImageStorage = "1.2.840.10008.5.1.4.1.1.3.1";

    /** MR Image Storage - SOP Class */
    public static final String MRImageStorage = "1.2.840.10008.5.1.4.1.1.4";

    /** Enhanced MR Image Storage - SOP Class */
    public static final String EnhancedMRImageStorage = "1.2.840.10008.5.1.4.1.1.4.1";

    /** MR Spectroscopy Storage - SOP Class */
    public static final String MRSpectroscopyStorage = "1.2.840.10008.5.1.4.1.1.4.2";

    /** Enhanced MR Color Image Storage - SOP Class */
    public static final String EnhancedMRColorImageStorage = "1.2.840.10008.5.1.4.1.1.4.3";

    /** Nuclear Medicine Image Storage (Retired) - SOP Class */
    public static final String NuclearMedicineImageStorageRetired = "1.2.840.10008.5.1.4.1.1.5";

    /** Ultrasound Image Storage (Retired) - SOP Class */
    public static final String UltrasoundImageStorageRetired = "1.2.840.10008.5.1.4.1.1.6";

    /** Ultrasound Image Storage - SOP Class */
    public static final String UltrasoundImageStorage = "1.2.840.10008.5.1.4.1.1.6.1";

    /** Enhanced US Volume Storage - SOP Class */
    public static final String EnhancedUSVolumeStorage = "1.2.840.10008.5.1.4.1.1.6.2";

    /** Secondary Capture Image Storage - SOP Class */
    public static final String SecondaryCaptureImageStorage = "1.2.840.10008.5.1.4.1.1.7";

    /** Multi-frame Single Bit Secondary Capture Image Storage - SOP Class */
    public static final String MultiFrameSingleBitSecondaryCaptureImageStorage = "1.2.840.10008.5.1.4.1.1.7.1";

    /** Multi-frame Grayscale Byte Secondary Capture Image Storage - SOP Class */
    public static final String MultiFrameGrayscaleByteSecondaryCaptureImageStorage = "1.2.840.10008.5.1.4.1.1.7.2";

    /** Multi-frame Grayscale Word Secondary Capture Image Storage - SOP Class */
    public static final String MultiFrameGrayscaleWordSecondaryCaptureImageStorage = "1.2.840.10008.5.1.4.1.1.7.3";

    /** Multi-frame True Color Secondary Capture Image Storage - SOP Class */
    public static final String MultiFrameTrueColorSecondaryCaptureImageStorage = "1.2.840.10008.5.1.4.1.1.7.4";

    /** Standalone Overlay Storage (Retired) - SOP Class */
    public static final String StandaloneOverlayStorageRetired = "1.2.840.10008.5.1.4.1.1.8";

    /** Standalone Curve Storage (Retired) - SOP Class */
    public static final String StandaloneCurveStorageRetired = "1.2.840.10008.5.1.4.1.1.9";

    /** Waveform Storage - Trial (Retired) - SOP Class */
    public static final String WaveformStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.9.1";

    /** 12-lead ECG Waveform Storage - SOP Class */
    public static final String TwelveLeadECGWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.1.1";

    /** General ECG Waveform Storage - SOP Class */
    public static final String GeneralECGWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.1.2";

    /** Ambulatory ECG Waveform Storage - SOP Class */
    public static final String AmbulatoryECGWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.1.3";

    /** Hemodynamic Waveform Storage - SOP Class */
    public static final String HemodynamicWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.2.1";

    /** Cardiac Electrophysiology Waveform Storage - SOP Class */
    public static final String CardiacElectrophysiologyWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.3.1";

    /** Basic Voice Audio Waveform Storage - SOP Class */
    public static final String BasicVoiceAudioWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.4.1";

    /** General Audio Waveform Storage - SOP Class */
    public static final String GeneralAudioWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.4.2";

    /** Arterial Pulse Waveform Storage - SOP Class */
    public static final String ArterialPulseWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.5.1";

    /** Respiratory Waveform Storage - SOP Class */
    public static final String RespiratoryWaveformStorage = "1.2.840.10008.5.1.4.1.1.9.6.1";

    /** Standalone Modality LUT Storage (Retired) - SOP Class */
    public static final String StandaloneModalityLUTStorageRetired = "1.2.840.10008.5.1.4.1.1.10";

    /** Standalone VOI LUT Storage (Retired) - SOP Class */
    public static final String StandaloneVOILUTStorageRetired = "1.2.840.10008.5.1.4.1.1.11";

    /** Grayscale Softcopy Presentation State Storage SOP Class - SOP Class */
    public static final String GrayscaleSoftcopyPresentationStateStorageSOPClass = "1.2.840.10008.5.1.4.1.1.11.1";

    /** Color Softcopy Presentation State Storage SOP Class - SOP Class */
    public static final String ColorSoftcopyPresentationStateStorageSOPClass = "1.2.840.10008.5.1.4.1.1.11.2";

    /** Pseudo-Color Softcopy Presentation State Storage SOP Class - SOP Class */
    public static final String PseudoColorSoftcopyPresentationStateStorageSOPClass = "1.2.840.10008.5.1.4.1.1.11.3";

    /** Blending Softcopy Presentation State Storage SOP Class - SOP Class */
    public static final String BlendingSoftcopyPresentationStateStorageSOPClass = "1.2.840.10008.5.1.4.1.1.11.4";

    /** XA/XRF Grayscale Softcopy Presentation State Storage - SOP Class */
    public static final String XAXRFGrayscaleSoftcopyPresentationStateStorage = "1.2.840.10008.5.1.4.1.1.11.5";

    /** X-Ray Angiographic Image Storage - SOP Class */
    public static final String XRayAngiographicImageStorage = "1.2.840.10008.5.1.4.1.1.12.1";

    /** Enhanced XA Image Storage - SOP Class */
    public static final String EnhancedXAImageStorage = "1.2.840.10008.5.1.4.1.1.12.1.1";

    /** X-Ray Radiofluoroscopic Image Storage - SOP Class */
    public static final String XRayRadiofluoroscopicImageStorage = "1.2.840.10008.5.1.4.1.1.12.2";

    /** Enhanced XRF Image Storage - SOP Class */
    public static final String EnhancedXRFImageStorage = "1.2.840.10008.5.1.4.1.1.12.2.1";

    /** X-Ray Angiographic Bi-Plane Image Storage (Retired) - SOP Class */
    public static final String XRayAngiographicBiPlaneImageStorageRetired = "1.2.840.10008.5.1.4.1.1.12.3";

    /** X-Ray 3D Angiographic Image Storage - SOP Class */
    public static final String XRay3DAngiographicImageStorage = "1.2.840.10008.5.1.4.1.1.13.1.1";

    /** X-Ray 3D Craniofacial Image Storage - SOP Class */
    public static final String XRay3DCraniofacialImageStorage = "1.2.840.10008.5.1.4.1.1.13.1.2";

    /** Breast Tomosynthesis Image Storage - SOP Class */
    public static final String BreastTomosynthesisImageStorage = "1.2.840.10008.5.1.4.1.1.13.1.3";

    /** Intravascular Optical Coherence Tomography Image Storage - For Presentation - SOP Class */
    public static final String IntravascularOpticalCoherenceTomographyImageStorageForPresentation = "1.2.840.10008.5.1.4.1.1.14.1";

    /** Intravascular Optical Coherence Tomography Image Storage - For Processing - SOP Class */
    public static final String IntravascularOpticalCoherenceTomographyImageStorageForProcessing = "1.2.840.10008.5.1.4.1.1.14.2";

    /** Nuclear Medicine Image Storage - SOP Class */
    public static final String NuclearMedicineImageStorage = "1.2.840.10008.5.1.4.1.1.20";

    /** Raw Data Storage - SOP Class */
    public static final String RawDataStorage = "1.2.840.10008.5.1.4.1.1.66";

    /** Spatial Registration Storage - SOP Class */
    public static final String SpatialRegistrationStorage = "1.2.840.10008.5.1.4.1.1.66.1";

    /** Spatial Fiducials Storage - SOP Class */
    public static final String SpatialFiducialsStorage = "1.2.840.10008.5.1.4.1.1.66.2";

    /** Deformable Spatial Registration Storage - SOP Class */
    public static final String DeformableSpatialRegistrationStorage = "1.2.840.10008.5.1.4.1.1.66.3";

    /** Segmentation Storage - SOP Class */
    public static final String SegmentationStorage = "1.2.840.10008.5.1.4.1.1.66.4";

    /** Surface Segmentation Storage - SOP Class */
    public static final String SurfaceSegmentationStorage = "1.2.840.10008.5.1.4.1.1.66.5";

    /** Real World Value Mapping Storage - SOP Class */
    public static final String RealWorldValueMappingStorage = "1.2.840.10008.5.1.4.1.1.67";

    /** VL Image Storage - Trial (Retired) -  */
    public static final String VLImageStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.77.1";

    /** VL Multi-frame Image Storage - Trial (Retired) -  */
    public static final String VLMultiFrameImageStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.77.2";

    /** VL Endoscopic Image Storage - SOP Class */
    public static final String VLEndoscopicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.1";

    /** Video Endoscopic Image Storage - SOP Class */
    public static final String VideoEndoscopicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.1.1";

    /** VL Microscopic Image Storage - SOP Class */
    public static final String VLMicroscopicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.2";

    /** Video Microscopic Image Storage - SOP Class */
    public static final String VideoMicroscopicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.2.1";

    /** VL Slide-Coordinates Microscopic Image Storage - SOP Class */
    public static final String VLSlideCoordinatesMicroscopicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.3";

    /** VL Photographic Image Storage - SOP Class */
    public static final String VLPhotographicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.4";

    /** Video Photographic Image Storage - SOP Class */
    public static final String VideoPhotographicImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.4.1";

    /** Ophthalmic Photography 8 Bit Image Storage - SOP Class */
    public static final String OphthalmicPhotography8BitImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.5.1";

    /** Ophthalmic Photography 16 Bit Image Storage - SOP Class */
    public static final String OphthalmicPhotography16BitImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.5.2";

    /** Stereometric Relationship Storage - SOP Class */
    public static final String StereometricRelationshipStorage = "1.2.840.10008.5.1.4.1.1.77.1.5.3";

    /** Ophthalmic Tomography Image Storage - SOP Class */
    public static final String OphthalmicTomographyImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.5.4";

    /** VL Whole Slide Microscopy Image Storage - SOP Class */
    public static final String VLWholeSlideMicroscopyImageStorage = "1.2.840.10008.5.1.4.1.1.77.1.6";

    /** Lensometry Measurements Storage - SOP Class */
    public static final String LensometryMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.1";

    /** Autorefraction Measurements Storage - SOP Class */
    public static final String AutorefractionMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.2";

    /** Keratometry Measurements Storage - SOP Class */
    public static final String KeratometryMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.3";

    /** Subjective Refraction Measurements Storage - SOP Class */
    public static final String SubjectiveRefractionMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.4";

    /** Visual Acuity Measurements Storage - SOP Class */
    public static final String VisualAcuityMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.5";

    /** Spectacle Prescription Report Storage - SOP Class */
    public static final String SpectaclePrescriptionReportStorage = "1.2.840.10008.5.1.4.1.1.78.6";

    /** Ophthalmic Axial Measurements Storage - SOP Class */
    public static final String OphthalmicAxialMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.78.7";

    /** Intraocular Lens Calculations Storage - SOP Class */
    public static final String IntraocularLensCalculationsStorage = "1.2.840.10008.5.1.4.1.1.78.8";

    /** Macular Grid Thickness and Volume Report Storage - SOP Class */
    public static final String MacularGridThicknessAndVolumeReportStorage = "1.2.840.10008.5.1.4.1.1.79.1";

    /** Ophthalmic Visual Field Static Perimetry Measurements Storage - SOP Class */
    public static final String OphthalmicVisualFieldStaticPerimetryMeasurementsStorage = "1.2.840.10008.5.1.4.1.1.80.1";

    /** Text SR Storage - Trial (Retired) - SOP Class */
    public static final String TextSRStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.88.1";

    /** Audio SR Storage - Trial (Retired) - SOP Class */
    public static final String AudioSRStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.88.2";

    /** Detail SR Storage - Trial (Retired) - SOP Class */
    public static final String DetailSRStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.88.3";

    /** Comprehensive SR Storage - Trial (Retired) - SOP Class */
    public static final String ComprehensiveSRStorageTrialRetired = "1.2.840.10008.5.1.4.1.1.88.4";

    /** Basic Text SR Storage - SOP Class */
    public static final String BasicTextSRStorage = "1.2.840.10008.5.1.4.1.1.88.11";

    /** Enhanced SR Storage - SOP Class */
    public static final String EnhancedSRStorage = "1.2.840.10008.5.1.4.1.1.88.22";

    /** Comprehensive SR Storage - SOP Class */
    public static final String ComprehensiveSRStorage = "1.2.840.10008.5.1.4.1.1.88.33";

    /** Procedure Log Storage - SOP Class */
    public static final String ProcedureLogStorage = "1.2.840.10008.5.1.4.1.1.88.40";

    /** Mammography CAD SR Storage - SOP Class */
    public static final String MammographyCADSRStorage = "1.2.840.10008.5.1.4.1.1.88.50";

    /** Key Object Selection Document Storage - SOP Class */
    public static final String KeyObjectSelectionDocumentStorage = "1.2.840.10008.5.1.4.1.1.88.59";

    /** Chest CAD SR Storage - SOP Class */
    public static final String ChestCADSRStorage = "1.2.840.10008.5.1.4.1.1.88.65";

    /** X-Ray Radiation Dose SR Storage - SOP Class */
    public static final String XRayRadiationDoseSRStorage = "1.2.840.10008.5.1.4.1.1.88.67";

    /** Colon CAD SR Storage - SOP Class */
    public static final String ColonCADSRStorage = "1.2.840.10008.5.1.4.1.1.88.69";

    /** Implantation Plan SR Storage - SOP Class */
    public static final String ImplantationPlanSRStorage = "1.2.840.10008.5.1.4.1.1.88.70";

    /** Encapsulated PDF Storage - SOP Class */
    public static final String EncapsulatedPDFStorage = "1.2.840.10008.5.1.4.1.1.104.1";

    /** Encapsulated CDA Storage - SOP Class */
    public static final String EncapsulatedCDAStorage = "1.2.840.10008.5.1.4.1.1.104.2";

    /** Positron Emission Tomography Image Storage - SOP Class */
    public static final String PositronEmissionTomographyImageStorage = "1.2.840.10008.5.1.4.1.1.128";

    /** Standalone PET Curve Storage (Retired) - SOP Class */
    public static final String StandalonePETCurveStorageRetired = "1.2.840.10008.5.1.4.1.1.129";

    /** Enhanced PET Image Storage - SOP Class */
    public static final String EnhancedPETImageStorage = "1.2.840.10008.5.1.4.1.1.130";

    /** Basic Structured Display Storage - SOP Class */
    public static final String BasicStructuredDisplayStorage = "1.2.840.10008.5.1.4.1.1.131";

    /** RT Image Storage - SOP Class */
    public static final String RTImageStorage = "1.2.840.10008.5.1.4.1.1.481.1";

    /** RT Dose Storage - SOP Class */
    public static final String RTDoseStorage = "1.2.840.10008.5.1.4.1.1.481.2";

    /** RT Structure Set Storage - SOP Class */
    public static final String RTStructureSetStorage = "1.2.840.10008.5.1.4.1.1.481.3";

    /** RT Beams Treatment Record Storage - SOP Class */
    public static final String RTBeamsTreatmentRecordStorage = "1.2.840.10008.5.1.4.1.1.481.4";

    /** RT Plan Storage - SOP Class */
    public static final String RTPlanStorage = "1.2.840.10008.5.1.4.1.1.481.5";

    /** RT Brachy Treatment Record Storage - SOP Class */
    public static final String RTBrachyTreatmentRecordStorage = "1.2.840.10008.5.1.4.1.1.481.6";

    /** RT Treatment Summary Record Storage - SOP Class */
    public static final String RTTreatmentSummaryRecordStorage = "1.2.840.10008.5.1.4.1.1.481.7";

    /** RT Ion Plan Storage - SOP Class */
    public static final String RTIonPlanStorage = "1.2.840.10008.5.1.4.1.1.481.8";

    /** RT Ion Beams Treatment Record Storage - SOP Class */
    public static final String RTIonBeamsTreatmentRecordStorage = "1.2.840.10008.5.1.4.1.1.481.9";

    /** DICOS CT Image Storage - SOP Class */
    public static final String DICOSCTImageStorage = "1.2.840.10008.5.1.4.1.1.501.1";

    /** DICOS Digital X-Ray Image Storage - For Presentation - SOP Class */
    public static final String DICOSDigitalXRayImageStorageForPresentation = "1.2.840.10008.5.1.4.1.1.501.2.1";

    /** DICOS Digital X-Ray Image Storage - For Processing - SOP Class */
    public static final String DICOSDigitalXRayImageStorageForProcessing = "1.2.840.10008.5.1.4.1.1.501.2.2";

    /** DICOS Threat Detection Report Storage - SOP Class */
    public static final String DICOSThreatDetectionReportStorage = "1.2.840.10008.5.1.4.1.1.501.3";

    /** Eddy Current Image Storage - SOP Class */
    public static final String EddyCurrentImageStorage = "1.2.840.10008.5.1.4.1.1.601.1";

    /** Eddy Current Multi-frame Image Storage - SOP Class */
    public static final String EddyCurrentMultiFrameImageStorage = "1.2.840.10008.5.1.4.1.1.601.2";

    /** Patient Root Query/Retrieve Information Model - FIND - SOP Class */
    public static final String PatientRootQueryRetrieveInformationModelFIND = "1.2.840.10008.5.1.4.1.2.1.1";

    /** Patient Root Query/Retrieve Information Model - MOVE - SOP Class */
    public static final String PatientRootQueryRetrieveInformationModelMOVE = "1.2.840.10008.5.1.4.1.2.1.2";

    /** Patient Root Query/Retrieve Information Model - GET - SOP Class */
    public static final String PatientRootQueryRetrieveInformationModelGET = "1.2.840.10008.5.1.4.1.2.1.3";

    /** Study Root Query/Retrieve Information Model - FIND - SOP Class */
    public static final String StudyRootQueryRetrieveInformationModelFIND = "1.2.840.10008.5.1.4.1.2.2.1";

    /** Study Root Query/Retrieve Information Model - MOVE - SOP Class */
    public static final String StudyRootQueryRetrieveInformationModelMOVE = "1.2.840.10008.5.1.4.1.2.2.2";

    /** Study Root Query/Retrieve Information Model - GET - SOP Class */
    public static final String StudyRootQueryRetrieveInformationModelGET = "1.2.840.10008.5.1.4.1.2.2.3";

    /** Patient/Study Only Query/Retrieve Information Model - FIND (Retired) - SOP Class */
    public static final String PatientStudyOnlyQueryRetrieveInformationModelFINDRetired = "1.2.840.10008.5.1.4.1.2.3.1";

    /** Patient/Study Only Query/Retrieve Information Model - MOVE (Retired) - SOP Class */
    public static final String PatientStudyOnlyQueryRetrieveInformationModelMOVERetired = "1.2.840.10008.5.1.4.1.2.3.2";

    /** Patient/Study Only Query/Retrieve Information Model - GET (Retired) - SOP Class */
    public static final String PatientStudyOnlyQueryRetrieveInformationModelGETRetired = "1.2.840.10008.5.1.4.1.2.3.3";

    /** Composite Instance Root Retrieve - MOVE - SOP Class */
    public static final String CompositeInstanceRootRetrieveMOVE = "1.2.840.10008.5.1.4.1.2.4.2";

    /** Composite Instance Root Retrieve - GET - SOP Class */
    public static final String CompositeInstanceRootRetrieveGET = "1.2.840.10008.5.1.4.1.2.4.3";

    /** Composite Instance Retrieve Without Bulk Data - GET - SOP Class */
    public static final String CompositeInstanceRetrieveWithoutBulkDataGET = "1.2.840.10008.5.1.4.1.2.5.3";

    /** Modality Worklist Information Model - FIND - SOP Class */
    public static final String ModalityWorklistInformationModelFIND = "1.2.840.10008.5.1.4.31";

    /** General Purpose Worklist Information Model - FIND - SOP Class */
    public static final String GeneralPurposeWorklistInformationModelFIND = "1.2.840.10008.5.1.4.32.1";

    /** General Purpose Scheduled Procedure Step SOP Class - SOP Class */
    public static final String GeneralPurposeScheduledProcedureStepSOPClass = "1.2.840.10008.5.1.4.32.2";

    /** General Purpose Performed Procedure Step SOP Class - SOP Class */
    public static final String GeneralPurposePerformedProcedureStepSOPClass = "1.2.840.10008.5.1.4.32.3";

    /** General Purpose Worklist Management Meta SOP Class - Meta SOP Class */
    public static final String GeneralPurposeWorklistManagementMetaSOPClass = "1.2.840.10008.5.1.4.32";

    /** Instance Availability Notification SOP Class - SOP Class */
    public static final String InstanceAvailabilityNotificationSOPClass = "1.2.840.10008.5.1.4.33";

    /** RT Beams Delivery Instruction Storage - Trial (Retired) - SOP Class */
    public static final String RTBeamsDeliveryInstructionStorageTrialRetired = "1.2.840.10008.5.1.4.34.1";

    /** RT Conventional Machine Verification - Trial (Retired) - SOP Class */
    public static final String RTConventionalMachineVerificationTrialRetired = "1.2.840.10008.5.1.4.34.2";

    /** RT Ion Machine Verification - Trial (Retired) - SOP Class */
    public static final String RTIonMachineVerificationTrialRetired = "1.2.840.10008.5.1.4.34.3";

    /** Unified Worklist and Procedure Step Service Class - Trial (Retired) - Service Class */
    public static final String UnifiedWorklistAndProcedureStepServiceClassTrialRetired = "1.2.840.10008.5.1.4.34.4";

    /** Unified Procedure Step - Push SOP Class - Trial (Retired) - SOP Class */
    public static final String UnifiedProcedureStepPushSOPClassTrialRetired = "1.2.840.10008.5.1.4.34.4.1";

    /** Unified Procedure Step - Watch SOP Class - Trial (Retired) - SOP Class */
    public static final String UnifiedProcedureStepWatchSOPClassTrialRetired = "1.2.840.10008.5.1.4.34.4.2";

    /** Unified Procedure Step - Pull SOP Class - Trial (Retired) - SOP Class */
    public static final String UnifiedProcedureStepPullSOPClassTrialRetired = "1.2.840.10008.5.1.4.34.4.3";

    /** Unified Procedure Step - Event SOP Class - Trial (Retired) - SOP Class */
    public static final String UnifiedProcedureStepEventSOPClassTrialRetired = "1.2.840.10008.5.1.4.34.4.4";

    /** Unified Worklist and Procedure Step SOP Instance - Well-known SOP Instance */
    public static final String UnifiedWorklistAndProcedureStepSOPInstance = "1.2.840.10008.5.1.4.34.5";

    /** Unified Worklist and Procedure Step Service Class - Service Class */
    public static final String UnifiedWorklistAndProcedureStepServiceClass = "1.2.840.10008.5.1.4.34.6";

    /** Unified Procedure Step - Push SOP Class - SOP Class */
    public static final String UnifiedProcedureStepPushSOPClass = "1.2.840.10008.5.1.4.34.6.1";

    /** Unified Procedure Step - Watch SOP Class - SOP Class */
    public static final String UnifiedProcedureStepWatchSOPClass = "1.2.840.10008.5.1.4.34.6.2";

    /** Unified Procedure Step - Pull SOP Class - SOP Class */
    public static final String UnifiedProcedureStepPullSOPClass = "1.2.840.10008.5.1.4.34.6.3";

    /** Unified Procedure Step - Event SOP Class - SOP Class */
    public static final String UnifiedProcedureStepEventSOPClass = "1.2.840.10008.5.1.4.34.6.4";

    /** RT Beams Delivery Instruction Storage - SOP Class */
    public static final String RTBeamsDeliveryInstructionStorage = "1.2.840.10008.5.1.4.34.7";

    /** RT Conventional Machine Verification - SOP Class */
    public static final String RTConventionalMachineVerification = "1.2.840.10008.5.1.4.34.8";

    /** RT Ion Machine Verification - SOP Class */
    public static final String RTIonMachineVerification = "1.2.840.10008.5.1.4.34.9";

    /** General Relevant Patient Information Query - SOP Class */
    public static final String GeneralRelevantPatientInformationQuery = "1.2.840.10008.5.1.4.37.1";

    /** Breast Imaging Relevant Patient Information Query - SOP Class */
    public static final String BreastImagingRelevantPatientInformationQuery = "1.2.840.10008.5.1.4.37.2";

    /** Cardiac Relevant Patient Information Query - SOP Class */
    public static final String CardiacRelevantPatientInformationQuery = "1.2.840.10008.5.1.4.37.3";

    /** Hanging Protocol Storage - SOP Class */
    public static final String HangingProtocolStorage = "1.2.840.10008.5.1.4.38.1";

    /** Hanging Protocol Information Model - FIND - SOP Class */
    public static final String HangingProtocolInformationModelFIND = "1.2.840.10008.5.1.4.38.2";

    /** Hanging Protocol Information Model - MOVE - SOP Class */
    public static final String HangingProtocolInformationModelMOVE = "1.2.840.10008.5.1.4.38.3";

    /** Hanging Protocol Information Model - GET - SOP Class */
    public static final String HangingProtocolInformationModelGET = "1.2.840.10008.5.1.4.38.4";

    /** Color Palette Storage - Transfer */
    public static final String ColorPaletteStorage = "1.2.840.10008.5.1.4.39.1";

    /** Color Palette Information Model - FIND - Query/Retrieve */
    public static final String ColorPaletteInformationModelFIND = "1.2.840.10008.5.1.4.39.2";

    /** Color Palette Information Model - MOVE - Query/Retrieve */
    public static final String ColorPaletteInformationModelMOVE = "1.2.840.10008.5.1.4.39.3";

    /** Color Palette Information Model - GET - Query/Retrieve */
    public static final String ColorPaletteInformationModelGET = "1.2.840.10008.5.1.4.39.4";

    /** Product Characteristics Query SOP Class - SOP Class */
    public static final String ProductCharacteristicsQuerySOPClass = "1.2.840.10008.5.1.4.41";

    /** Substance Approval Query SOP Class - SOP Class */
    public static final String SubstanceApprovalQuerySOPClass = "1.2.840.10008.5.1.4.42";

    /** Generic Implant Template Storage - SOP Class */
    public static final String GenericImplantTemplateStorage = "1.2.840.10008.5.1.4.43.1";

    /** Generic Implant Template Information Model - FIND - SOP Class */
    public static final String GenericImplantTemplateInformationModelFIND = "1.2.840.10008.5.1.4.43.2";

    /** Generic Implant Template Information Model - MOVE - SOP Class */
    public static final String GenericImplantTemplateInformationModelMOVE = "1.2.840.10008.5.1.4.43.3";

    /** Generic Implant Template Information Model - GET - SOP Class */
    public static final String GenericImplantTemplateInformationModelGET = "1.2.840.10008.5.1.4.43.4";

    /** Implant Assembly Template Storage - SOP Class */
    public static final String ImplantAssemblyTemplateStorage = "1.2.840.10008.5.1.4.44.1";

    /** Implant Assembly Template Information Model - FIND - SOP Class */
    public static final String ImplantAssemblyTemplateInformationModelFIND = "1.2.840.10008.5.1.4.44.2";

    /** Implant Assembly Template Information Model - MOVE - SOP Class */
    public static final String ImplantAssemblyTemplateInformationModelMOVE = "1.2.840.10008.5.1.4.44.3";

    /** Implant Assembly Template Information Model - GET - SOP Class */
    public static final String ImplantAssemblyTemplateInformationModelGET = "1.2.840.10008.5.1.4.44.4";

    /** Implant Template Group Storage - SOP Class */
    public static final String ImplantTemplateGroupStorage = "1.2.840.10008.5.1.4.45.1";

    /** Implant Template Group Information Model - FIND - SOP Class */
    public static final String ImplantTemplateGroupInformationModelFIND = "1.2.840.10008.5.1.4.45.2";

    /** Implant Template Group Information Model - MOVE - SOP Class */
    public static final String ImplantTemplateGroupInformationModelMOVE = "1.2.840.10008.5.1.4.45.3";

    /** Implant Template Group Information Model - GET - SOP Class */
    public static final String ImplantTemplateGroupInformationModelGET = "1.2.840.10008.5.1.4.45.4";

    /** Native DICOM Model - Application Hosting Model */
    public static final String NativeDICOMModel = "1.2.840.10008.7.1.1";

    /** Abstract Multi-Dimensional Image Model - Application Hosting Model */
    public static final String AbstractMultiDimensionalImageModel = "1.2.840.10008.7.1.2";

    /** dicomDeviceName - LDAP OID */
    public static final String dicomDeviceName = "1.2.840.10008.15.0.3.1";

    /** dicomDescription - LDAP OID */
    public static final String dicomDescription = "1.2.840.10008.15.0.3.2";

    /** dicomManufacturer - LDAP OID */
    public static final String dicomManufacturer = "1.2.840.10008.15.0.3.3";

    /** dicomManufacturerModelName - LDAP OID */
    public static final String dicomManufacturerModelName = "1.2.840.10008.15.0.3.4";

    /** dicomSoftwareVersion - LDAP OID */
    public static final String dicomSoftwareVersion = "1.2.840.10008.15.0.3.5";

    /** dicomVendorData - LDAP OID */
    public static final String dicomVendorData = "1.2.840.10008.15.0.3.6";

    /** dicomAETitle - LDAP OID */
    public static final String dicomAETitle = "1.2.840.10008.15.0.3.7";

    /** dicomNetworkConnectionReference - LDAP OID */
    public static final String dicomNetworkConnectionReference = "1.2.840.10008.15.0.3.8";

    /** dicomApplicationCluster - LDAP OID */
    public static final String dicomApplicationCluster = "1.2.840.10008.15.0.3.9";

    /** dicomAssociationInitiator - LDAP OID */
    public static final String dicomAssociationInitiator = "1.2.840.10008.15.0.3.10";

    /** dicomAssociationAcceptor - LDAP OID */
    public static final String dicomAssociationAcceptor = "1.2.840.10008.15.0.3.11";

    /** dicomHostname - LDAP OID */
    public static final String dicomHostname = "1.2.840.10008.15.0.3.12";

    /** dicomPort - LDAP OID */
    public static final String dicomPort = "1.2.840.10008.15.0.3.13";

    /** dicomSOPClass - LDAP OID */
    public static final String dicomSOPClass = "1.2.840.10008.15.0.3.14";

    /** dicomTransferRole - LDAP OID */
    public static final String dicomTransferRole = "1.2.840.10008.15.0.3.15";

    /** dicomTransferSyntax - LDAP OID */
    public static final String dicomTransferSyntax = "1.2.840.10008.15.0.3.16";

    /** dicomPrimaryDeviceType - LDAP OID */
    public static final String dicomPrimaryDeviceType = "1.2.840.10008.15.0.3.17";

    /** dicomRelatedDeviceReference - LDAP OID */
    public static final String dicomRelatedDeviceReference = "1.2.840.10008.15.0.3.18";

    /** dicomPreferredCalledAETitle - LDAP OID */
    public static final String dicomPreferredCalledAETitle = "1.2.840.10008.15.0.3.19";

    /** dicomTLSCyphersuite - LDAP OID */
    public static final String dicomTLSCyphersuite = "1.2.840.10008.15.0.3.20";

    /** dicomAuthorizedNodeCertificateReference - LDAP OID */
    public static final String dicomAuthorizedNodeCertificateReference = "1.2.840.10008.15.0.3.21";

    /** dicomThisNodeCertificateReference - LDAP OID */
    public static final String dicomThisNodeCertificateReference = "1.2.840.10008.15.0.3.22";

    /** dicomInstalled - LDAP OID */
    public static final String dicomInstalled = "1.2.840.10008.15.0.3.23";

    /** dicomStationName - LDAP OID */
    public static final String dicomStationName = "1.2.840.10008.15.0.3.24";

    /** dicomDeviceSerialNumber - LDAP OID */
    public static final String dicomDeviceSerialNumber = "1.2.840.10008.15.0.3.25";

    /** dicomInstitutionName - LDAP OID */
    public static final String dicomInstitutionName = "1.2.840.10008.15.0.3.26";

    /** dicomInstitutionAddress - LDAP OID */
    public static final String dicomInstitutionAddress = "1.2.840.10008.15.0.3.27";

    /** dicomInstitutionDepartmentName - LDAP OID */
    public static final String dicomInstitutionDepartmentName = "1.2.840.10008.15.0.3.28";

    /** dicomIssuerOfPatientID - LDAP OID */
    public static final String dicomIssuerOfPatientID = "1.2.840.10008.15.0.3.29";

    /** dicomPreferredCallingAETitle - LDAP OID */
    public static final String dicomPreferredCallingAETitle = "1.2.840.10008.15.0.3.30";

    /** dicomSupportedCharacterSet - LDAP OID */
    public static final String dicomSupportedCharacterSet = "1.2.840.10008.15.0.3.31";

    /** dicomConfigurationRoot - LDAP OID */
    public static final String dicomConfigurationRoot = "1.2.840.10008.15.0.4.1";

    /** dicomDevicesRoot - LDAP OID */
    public static final String dicomDevicesRoot = "1.2.840.10008.15.0.4.2";

    /** dicomUniqueAETitlesRegistryRoot - LDAP OID */
    public static final String dicomUniqueAETitlesRegistryRoot = "1.2.840.10008.15.0.4.3";

    /** dicomDevice - LDAP OID */
    public static final String dicomDevice = "1.2.840.10008.15.0.4.4";

    /** dicomNetworkAE - LDAP OID */
    public static final String dicomNetworkAE = "1.2.840.10008.15.0.4.5";

    /** dicomNetworkConnection - LDAP OID */
    public static final String dicomNetworkConnection = "1.2.840.10008.15.0.4.6";

    /** dicomUniqueAETitle - LDAP OID */
    public static final String dicomUniqueAETitle = "1.2.840.10008.15.0.4.7";

    /** dicomTransferCapability - LDAP OID */
    public static final String dicomTransferCapability = "1.2.840.10008.15.0.4.8";

    /** Universal Coordinated Time - Synchronization Frame of Reference */
    public static final String UniversalCoordinatedTime = "1.2.840.10008.15.1.1";

    /** Anatomic Modifier (2) - Context Group Name */
    public static final String AnatomicModifier2 = "1.2.840.10008.6.1.1";

    /** Anatomic Region (4) - Context Group Name */
    public static final String AnatomicRegion4 = "1.2.840.10008.6.1.2";

    /** Transducer Approach (5) - Context Group Name */
    public static final String TransducerApproach5 = "1.2.840.10008.6.1.3";

    /** Transducer Orientation (6) - Context Group Name */
    public static final String TransducerOrientation6 = "1.2.840.10008.6.1.4";

    /** Ultrasound Beam Path (7) - Context Group Name */
    public static final String UltrasoundBeamPath7 = "1.2.840.10008.6.1.5";

    /** Angiographic Interventional Devices (8) - Context Group Name */
    public static final String AngiographicInterventionalDevices8 = "1.2.840.10008.6.1.6";

    /** Image Guided Therapeutic Procedures (9) - Context Group Name */
    public static final String ImageGuidedTherapeuticProcedures9 = "1.2.840.10008.6.1.7";

    /** Interventional Drug (10) - Context Group Name */
    public static final String InterventionalDrug10 = "1.2.840.10008.6.1.8";

    /** Route of Administration (11) - Context Group Name */
    public static final String RouteOfAdministration11 = "1.2.840.10008.6.1.9";

    /** Radiographic Contrast Agent (12) - Context Group Name */
    public static final String RadiographicContrastAgent12 = "1.2.840.10008.6.1.10";

    /** Radiographic Contrast Agent Ingredient (13) - Context Group Name */
    public static final String RadiographicContrastAgentIngredient13 = "1.2.840.10008.6.1.11";

    /** Isotopes in Radiopharmaceuticals (18) - Context Group Name */
    public static final String IsotopesInRadiopharmaceuticals18 = "1.2.840.10008.6.1.12";

    /** Patient Orientation (19) - Context Group Name */
    public static final String PatientOrientation19 = "1.2.840.10008.6.1.13";

    /** Patient Orientation Modifier (20) - Context Group Name */
    public static final String PatientOrientationModifier20 = "1.2.840.10008.6.1.14";

    /** Patient Gantry Relationship (21) - Context Group Name */
    public static final String PatientGantryRelationship21 = "1.2.840.10008.6.1.15";

    /** Cranio-caudad Angulation (23) - Context Group Name */
    public static final String CranioCaudadAngulation23 = "1.2.840.10008.6.1.16";

    /** Radiopharmaceuticals (25) - Context Group Name */
    public static final String Radiopharmaceuticals25 = "1.2.840.10008.6.1.17";

    /** Nuclear Medicine Projections (26) - Context Group Name */
    public static final String NuclearMedicineProjections26 = "1.2.840.10008.6.1.18";

    /** Acquisition Modality (29) - Context Group Name */
    public static final String AcquisitionModality29 = "1.2.840.10008.6.1.19";

    /** DICOM Devices (30) - Context Group Name */
    public static final String DICOMDevices30 = "1.2.840.10008.6.1.20";

    /** Abstract Priors (31) - Context Group Name */
    public static final String AbstractPriors31 = "1.2.840.10008.6.1.21";

    /** Numeric Value Qualifier (42) - Context Group Name */
    public static final String NumericValueQualifier42 = "1.2.840.10008.6.1.22";

    /** Units of Measurement (82) - Context Group Name */
    public static final String UnitsOfMeasurement82 = "1.2.840.10008.6.1.23";

    /** Units for Real World Value Mapping (83) - Context Group Name */
    public static final String UnitsForRealWorldValueMapping83 = "1.2.840.10008.6.1.24";

    /** Level of Significance (220) - Context Group Name */
    public static final String LevelOfSignificance220 = "1.2.840.10008.6.1.25";

    /** Measurement Range Concepts (221) - Context Group Name */
    public static final String MeasurementRangeConcepts221 = "1.2.840.10008.6.1.26";

    /** Normality Codes (222) - Context Group Name */
    public static final String NormalityCodes222 = "1.2.840.10008.6.1.27";

    /** Normal Range Values (223) - Context Group Name */
    public static final String NormalRangeValues223 = "1.2.840.10008.6.1.28";

    /** Selection Method (224) - Context Group Name */
    public static final String SelectionMethod224 = "1.2.840.10008.6.1.29";

    /** Measurement Uncertainty Concepts (225) - Context Group Name */
    public static final String MeasurementUncertaintyConcepts225 = "1.2.840.10008.6.1.30";

    /** Population Statistical Descriptors (226) - Context Group Name */
    public static final String PopulationStatisticalDescriptors226 = "1.2.840.10008.6.1.31";

    /** Sample Statistical Descriptors (227) - Context Group Name */
    public static final String SampleStatisticalDescriptors227 = "1.2.840.10008.6.1.32";

    /** Equation or Table (228) - Context Group Name */
    public static final String EquationOrTable228 = "1.2.840.10008.6.1.33";

    /** Yes-No (230) - Context Group Name */
    public static final String YesNo230 = "1.2.840.10008.6.1.34";

    /** Present-Absent (240) - Context Group Name */
    public static final String PresentAbsent240 = "1.2.840.10008.6.1.35";

    /** Normal-Abnormal (242) - Context Group Name */
    public static final String NormalAbnormal242 = "1.2.840.10008.6.1.36";

    /** Laterality (244) - Context Group Name */
    public static final String Laterality244 = "1.2.840.10008.6.1.37";

    /** Positive-Negative (250) - Context Group Name */
    public static final String PositiveNegative250 = "1.2.840.10008.6.1.38";

    /** Severity of Complication (251) - Context Group Name */
    public static final String SeverityOfComplication251 = "1.2.840.10008.6.1.39";

    /** Observer Type (270) - Context Group Name */
    public static final String ObserverType270 = "1.2.840.10008.6.1.40";

    /** Observation Subject Class (271) - Context Group Name */
    public static final String ObservationSubjectClass271 = "1.2.840.10008.6.1.41";

    /** Audio Channel Source (3000) - Context Group Name */
    public static final String AudioChannelSource3000 = "1.2.840.10008.6.1.42";

    /** ECG Leads (3001) - Context Group Name */
    public static final String ECGLeads3001 = "1.2.840.10008.6.1.43";

    /** Hemodynamic Waveform Sources (3003) - Context Group Name */
    public static final String HemodynamicWaveformSources3003 = "1.2.840.10008.6.1.44";

    /** Cardiovascular Anatomic Locations (3010) - Context Group Name */
    public static final String CardiovascularAnatomicLocations3010 = "1.2.840.10008.6.1.45";

    /** Electrophysiology Anatomic Locations (3011) - Context Group Name */
    public static final String ElectrophysiologyAnatomicLocations3011 = "1.2.840.10008.6.1.46";

    /** Coronary Artery Segments (3014) - Context Group Name */
    public static final String CoronaryArterySegments3014 = "1.2.840.10008.6.1.47";

    /** Coronary Arteries (3015) - Context Group Name */
    public static final String CoronaryArteries3015 = "1.2.840.10008.6.1.48";

    /** Cardiovascular Anatomic Location Modifiers (3019) - Context Group Name */
    public static final String CardiovascularAnatomicLocationModifiers3019 = "1.2.840.10008.6.1.49";

    /** Cardiology Units of Measurement (3082) - Context Group Name */
    public static final String CardiologyUnitsOfMeasurement3082 = "1.2.840.10008.6.1.50";

    /** Time Synchronization Channel Types (3090) - Context Group Name */
    public static final String TimeSynchronizationChannelTypes3090 = "1.2.840.10008.6.1.51";

    /** NM Procedural State Values (3101) - Context Group Name */
    public static final String NMProceduralStateValues3101 = "1.2.840.10008.6.1.52";

    /** Electrophysiology Measurement Functions and Techniques (3240) - Context Group Name */
    public static final String ElectrophysiologyMeasurementFunctionsAndTechniques3240 = "1.2.840.10008.6.1.53";

    /** Hemodynamic Measurement Techniques (3241) - Context Group Name */
    public static final String HemodynamicMeasurementTechniques3241 = "1.2.840.10008.6.1.54";

    /** Catheterization Procedure Phase (3250) - Context Group Name */
    public static final String CatheterizationProcedurePhase3250 = "1.2.840.10008.6.1.55";

    /** Electrophysiology Procedure Phase (3254) - Context Group Name */
    public static final String ElectrophysiologyProcedurePhase3254 = "1.2.840.10008.6.1.56";

    /** Stress Protocols (3261) - Context Group Name */
    public static final String StressProtocols3261 = "1.2.840.10008.6.1.57";

    /** ECG Patient State Values (3262) - Context Group Name */
    public static final String ECGPatientStateValues3262 = "1.2.840.10008.6.1.58";

    /** Electrode Placement Values (3263) - Context Group Name */
    public static final String ElectrodePlacementValues3263 = "1.2.840.10008.6.1.59";

    /** XYZ Electrode Placement Values (3264) - Context Group Name */
    public static final String XYZElectrodePlacementValues3264 = "1.2.840.10008.6.1.60";

    /** Hemodynamic Physiological Challenges (3271) - Context Group Name */
    public static final String HemodynamicPhysiologicalChallenges3271 = "1.2.840.10008.6.1.61";

    /** ECG Annotations (3335) - Context Group Name */
    public static final String ECGAnnotations3335 = "1.2.840.10008.6.1.62";

    /** Hemodynamic Annotations (3337) - Context Group Name */
    public static final String HemodynamicAnnotations3337 = "1.2.840.10008.6.1.63";

    /** Electrophysiology Annotations (3339) - Context Group Name */
    public static final String ElectrophysiologyAnnotations3339 = "1.2.840.10008.6.1.64";

    /** Procedure Log Titles (3400) - Context Group Name */
    public static final String ProcedureLogTitles3400 = "1.2.840.10008.6.1.65";

    /** Types of Log Notes (3401) - Context Group Name */
    public static final String TypesOfLogNotes3401 = "1.2.840.10008.6.1.66";

    /** Patient Status and Events (3402) - Context Group Name */
    public static final String PatientStatusAndEvents3402 = "1.2.840.10008.6.1.67";

    /** Percutaneous Entry (3403) - Context Group Name */
    public static final String PercutaneousEntry3403 = "1.2.840.10008.6.1.68";

    /** Staff Actions (3404) - Context Group Name */
    public static final String StaffActions3404 = "1.2.840.10008.6.1.69";

    /** Procedure Action Values (3405) - Context Group Name */
    public static final String ProcedureActionValues3405 = "1.2.840.10008.6.1.70";

    /** Non-Coronary Transcatheter Interventions (3406) - Context Group Name */
    public static final String NonCoronaryTranscatheterInterventions3406 = "1.2.840.10008.6.1.71";

    /** Purpose of Reference to Object (3407) - Context Group Name */
    public static final String PurposeOfReferenceToObject3407 = "1.2.840.10008.6.1.72";

    /** Actions with Consumables (3408) - Context Group Name */
    public static final String ActionsWithConsumables3408 = "1.2.840.10008.6.1.73";

    /** Administration of Drugs/Contrast (3409) - Context Group Name */
    public static final String AdministrationOfDrugsContrast3409 = "1.2.840.10008.6.1.74";

    /** Numeric Parameters of Drugs/Contrast (3410) - Context Group Name */
    public static final String NumericParametersOfDrugsContrast3410 = "1.2.840.10008.6.1.75";

    /** Intracoronary Devices (3411) - Context Group Name */
    public static final String IntracoronaryDevices3411 = "1.2.840.10008.6.1.76";

    /** Intervention Actions and Status (3412) - Context Group Name */
    public static final String InterventionActionsAndStatus3412 = "1.2.840.10008.6.1.77";

    /** Adverse Outcomes (3413) - Context Group Name */
    public static final String AdverseOutcomes3413 = "1.2.840.10008.6.1.78";

    /** Procedure Urgency (3414) - Context Group Name */
    public static final String ProcedureUrgency3414 = "1.2.840.10008.6.1.79";

    /** Cardiac Rhythms (3415) - Context Group Name */
    public static final String CardiacRhythms3415 = "1.2.840.10008.6.1.80";

    /** Respiration Rhythms (3416) - Context Group Name */
    public static final String RespirationRhythms3416 = "1.2.840.10008.6.1.81";

    /** Lesion Risk (3418) - Context Group Name */
    public static final String LesionRisk3418 = "1.2.840.10008.6.1.82";

    /** Findings Titles (3419) - Context Group Name */
    public static final String FindingsTitles3419 = "1.2.840.10008.6.1.83";

    /** Procedure Action (3421) - Context Group Name */
    public static final String ProcedureAction3421 = "1.2.840.10008.6.1.84";

    /** Device Use Actions (3422) - Context Group Name */
    public static final String DeviceUseActions3422 = "1.2.840.10008.6.1.85";

    /** Numeric Device Characteristics (3423) - Context Group Name */
    public static final String NumericDeviceCharacteristics3423 = "1.2.840.10008.6.1.86";

    /** Intervention Parameters (3425) - Context Group Name */
    public static final String InterventionParameters3425 = "1.2.840.10008.6.1.87";

    /** Consumables Parameters (3426) - Context Group Name */
    public static final String ConsumablesParameters3426 = "1.2.840.10008.6.1.88";

    /** Equipment Events (3427) - Context Group Name */
    public static final String EquipmentEvents3427 = "1.2.840.10008.6.1.89";

    /** Imaging Procedures (3428) - Context Group Name */
    public static final String ImagingProcedures3428 = "1.2.840.10008.6.1.90";

    /** Catheterization Devices (3429) - Context Group Name */
    public static final String CatheterizationDevices3429 = "1.2.840.10008.6.1.91";

    /** DateTime Qualifiers (3430) - Context Group Name */
    public static final String DateTimeQualifiers3430 = "1.2.840.10008.6.1.92";

    /** Peripheral Pulse Locations (3440) - Context Group Name */
    public static final String PeripheralPulseLocations3440 = "1.2.840.10008.6.1.93";

    /** Patient assessments (3441) - Context Group Name */
    public static final String PatientAssessments3441 = "1.2.840.10008.6.1.94";

    /** Peripheral Pulse Methods (3442) - Context Group Name */
    public static final String PeripheralPulseMethods3442 = "1.2.840.10008.6.1.95";

    /** Skin Condition (3446) - Context Group Name */
    public static final String SkinCondition3446 = "1.2.840.10008.6.1.96";

    /** Airway Assessment (3448) - Context Group Name */
    public static final String AirwayAssessment3448 = "1.2.840.10008.6.1.97";

    /** Calibration Objects (3451) - Context Group Name */
    public static final String CalibrationObjects3451 = "1.2.840.10008.6.1.98";

    /** Calibration Methods (3452) - Context Group Name */
    public static final String CalibrationMethods3452 = "1.2.840.10008.6.1.99";

    /** Cardiac Volume Methods (3453) - Context Group Name */
    public static final String CardiacVolumeMethods3453 = "1.2.840.10008.6.1.100";

    /** Index Methods (3455) - Context Group Name */
    public static final String IndexMethods3455 = "1.2.840.10008.6.1.101";

    /** Sub-segment Methods (3456) - Context Group Name */
    public static final String SubSegmentMethods3456 = "1.2.840.10008.6.1.102";

    /** Contour Realignment (3458) - Context Group Name */
    public static final String ContourRealignment3458 = "1.2.840.10008.6.1.103";

    /** Circumferential ExtenT (3460) - Context Group Name */
    public static final String CircumferentialExtenT3460 = "1.2.840.10008.6.1.104";

    /** Regional Extent (3461) - Context Group Name */
    public static final String RegionalExtent3461 = "1.2.840.10008.6.1.105";

    /** Chamber Identification (3462) - Context Group Name */
    public static final String ChamberIdentification3462 = "1.2.840.10008.6.1.106";

    /** QA Reference MethodS (3465) - Context Group Name */
    public static final String QAReferenceMethodS3465 = "1.2.840.10008.6.1.107";

    /** Plane Identification (3466) - Context Group Name */
    public static final String PlaneIdentification3466 = "1.2.840.10008.6.1.108";

    /** Ejection Fraction (3467) - Context Group Name */
    public static final String EjectionFraction3467 = "1.2.840.10008.6.1.109";

    /** ED Volume (3468) - Context Group Name */
    public static final String EDVolume3468 = "1.2.840.10008.6.1.110";

    /** ES Volume (3469) - Context Group Name */
    public static final String ESVolume3469 = "1.2.840.10008.6.1.111";

    /** Vessel Lumen Cross-Sectional Area Calculation Methods (3470) - Context Group Name */
    public static final String VesselLumenCrossSectionalAreaCalculationMethods3470 = "1.2.840.10008.6.1.112";

    /** Estimated Volumes (3471) - Context Group Name */
    public static final String EstimatedVolumes3471 = "1.2.840.10008.6.1.113";

    /** Cardiac Contraction Phase (3472) - Context Group Name */
    public static final String CardiacContractionPhase3472 = "1.2.840.10008.6.1.114";

    /** IVUS Procedure Phases (3480) - Context Group Name */
    public static final String IVUSProcedurePhases3480 = "1.2.840.10008.6.1.115";

    /** IVUS Distance Measurements (3481) - Context Group Name */
    public static final String IVUSDistanceMeasurements3481 = "1.2.840.10008.6.1.116";

    /** IVUS Area Measurements (3482) - Context Group Name */
    public static final String IVUSAreaMeasurements3482 = "1.2.840.10008.6.1.117";

    /** IVUS Longitudinal Measurements (3483) - Context Group Name */
    public static final String IVUSLongitudinalMeasurements3483 = "1.2.840.10008.6.1.118";

    /** IVUS Indices and Ratios (3484) - Context Group Name */
    public static final String IVUSIndicesAndRatios3484 = "1.2.840.10008.6.1.119";

    /** IVUS Volume Measurements (3485) - Context Group Name */
    public static final String IVUSVolumeMeasurements3485 = "1.2.840.10008.6.1.120";

    /** Vascular Measurement Sites (3486) - Context Group Name */
    public static final String VascularMeasurementSites3486 = "1.2.840.10008.6.1.121";

    /** Intravascular Volumetric Regions (3487) - Context Group Name */
    public static final String IntravascularVolumetricRegions3487 = "1.2.840.10008.6.1.122";

    /** Min/Max/Mean (3488) - Context Group Name */
    public static final String MinMaxMean3488 = "1.2.840.10008.6.1.123";

    /** Calcium Distribution (3489) - Context Group Name */
    public static final String CalciumDistribution3489 = "1.2.840.10008.6.1.124";

    /** IVUS Lesion Morphologies (3491) - Context Group Name */
    public static final String IVUSLesionMorphologies3491 = "1.2.840.10008.6.1.125";

    /** Vascular Dissection Classifications (3492) - Context Group Name */
    public static final String VascularDissectionClassifications3492 = "1.2.840.10008.6.1.126";

    /** IVUS Relative Stenosis Severities (3493) - Context Group Name */
    public static final String IVUSRelativeStenosisSeverities3493 = "1.2.840.10008.6.1.127";

    /** IVUS Non Morphological Findings (3494) - Context Group Name */
    public static final String IVUSNonMorphologicalFindings3494 = "1.2.840.10008.6.1.128";

    /** IVUS Plaque Composition (3495) - Context Group Name */
    public static final String IVUSPlaqueComposition3495 = "1.2.840.10008.6.1.129";

    /** IVUS Fiducial Points (3496) - Context Group Name */
    public static final String IVUSFiducialPoints3496 = "1.2.840.10008.6.1.130";

    /** IVUS Arterial Morphology (3497) - Context Group Name */
    public static final String IVUSArterialMorphology3497 = "1.2.840.10008.6.1.131";

    /** Pressure Units (3500) - Context Group Name */
    public static final String PressureUnits3500 = "1.2.840.10008.6.1.132";

    /** Hemodynamic Resistance Units (3502) - Context Group Name */
    public static final String HemodynamicResistanceUnits3502 = "1.2.840.10008.6.1.133";

    /** Indexed Hemodynamic Resistance Units (3503) - Context Group Name */
    public static final String IndexedHemodynamicResistanceUnits3503 = "1.2.840.10008.6.1.134";

    /** Catheter Size Units (3510) - Context Group Name */
    public static final String CatheterSizeUnits3510 = "1.2.840.10008.6.1.135";

    /** Specimen Collection (3515) - Context Group Name */
    public static final String SpecimenCollection3515 = "1.2.840.10008.6.1.136";

    /** Blood Source Type (3520) - Context Group Name */
    public static final String BloodSourceType3520 = "1.2.840.10008.6.1.137";

    /** Blood Gas Pressures (3524) - Context Group Name */
    public static final String BloodGasPressures3524 = "1.2.840.10008.6.1.138";

    /** Blood Gas Content (3525) - Context Group Name */
    public static final String BloodGasContent3525 = "1.2.840.10008.6.1.139";

    /** Blood Gas Saturation (3526) - Context Group Name */
    public static final String BloodGasSaturation3526 = "1.2.840.10008.6.1.140";

    /** Blood Base Excess (3527) - Context Group Name */
    public static final String BloodBaseExcess3527 = "1.2.840.10008.6.1.141";

    /** Blood pH (3528) - Context Group Name */
    public static final String BloodPH3528 = "1.2.840.10008.6.1.142";

    /** Arterial / Venous Content (3529) - Context Group Name */
    public static final String ArterialVenousContent3529 = "1.2.840.10008.6.1.143";

    /** Oxygen Administration Actions (3530) - Context Group Name */
    public static final String OxygenAdministrationActions3530 = "1.2.840.10008.6.1.144";

    /** Oxygen Administration (3531) - Context Group Name */
    public static final String OxygenAdministration3531 = "1.2.840.10008.6.1.145";

    /** Circulatory Support Actions (3550) - Context Group Name */
    public static final String CirculatorySupportActions3550 = "1.2.840.10008.6.1.146";

    /** Ventilation Actions (3551) - Context Group Name */
    public static final String VentilationActions3551 = "1.2.840.10008.6.1.147";

    /** Pacing Actions (3552) - Context Group Name */
    public static final String PacingActions3552 = "1.2.840.10008.6.1.148";

    /** Circulatory Support (3553) - Context Group Name */
    public static final String CirculatorySupport3553 = "1.2.840.10008.6.1.149";

    /** Ventilation (3554) - Context Group Name */
    public static final String Ventilation3554 = "1.2.840.10008.6.1.150";

    /** Pacing (3555) - Context Group Name */
    public static final String Pacing3555 = "1.2.840.10008.6.1.151";

    /** Blood Pressure Methods (3560) - Context Group Name */
    public static final String BloodPressureMethods3560 = "1.2.840.10008.6.1.152";

    /** Relative times (3600) - Context Group Name */
    public static final String RelativeTimes3600 = "1.2.840.10008.6.1.153";

    /** Hemodynamic Patient State (3602) - Context Group Name */
    public static final String HemodynamicPatientState3602 = "1.2.840.10008.6.1.154";

    /** Arterial lesion locations (3604) - Context Group Name */
    public static final String ArterialLesionLocations3604 = "1.2.840.10008.6.1.155";

    /** Arterial source locations (3606) - Context Group Name */
    public static final String ArterialSourceLocations3606 = "1.2.840.10008.6.1.156";

    /** Venous Source locations (3607) - Context Group Name */
    public static final String VenousSourceLocations3607 = "1.2.840.10008.6.1.157";

    /** Atrial source locations (3608) - Context Group Name */
    public static final String AtrialSourceLocations3608 = "1.2.840.10008.6.1.158";

    /** Ventricular source locations (3609) - Context Group Name */
    public static final String VentricularSourceLocations3609 = "1.2.840.10008.6.1.159";

    /** Gradient Source Locations (3610) - Context Group Name */
    public static final String GradientSourceLocations3610 = "1.2.840.10008.6.1.160";

    /** Pressure Measurements (3611) - Context Group Name */
    public static final String PressureMeasurements3611 = "1.2.840.10008.6.1.161";

    /** Blood Velocity Measurements (3612) - Context Group Name */
    public static final String BloodVelocityMeasurements3612 = "1.2.840.10008.6.1.162";

    /** Hemodynamic Time Measurements (3613) - Context Group Name */
    public static final String HemodynamicTimeMeasurements3613 = "1.2.840.10008.6.1.163";

    /** Valve Areas, non-Mitral (3614) - Context Group Name */
    public static final String ValveAreasNonMitral3614 = "1.2.840.10008.6.1.164";

    /** Valve Areas (3615) - Context Group Name */
    public static final String ValveAreas3615 = "1.2.840.10008.6.1.165";

    /** Hemodynamic Period Measurements (3616) - Context Group Name */
    public static final String HemodynamicPeriodMeasurements3616 = "1.2.840.10008.6.1.166";

    /** Valve Flows (3617) - Context Group Name */
    public static final String ValveFlows3617 = "1.2.840.10008.6.1.167";

    /** Hemodynamic Flows (3618) - Context Group Name */
    public static final String HemodynamicFlows3618 = "1.2.840.10008.6.1.168";

    /** Hemodynamic Resistance Measurements (3619) - Context Group Name */
    public static final String HemodynamicResistanceMeasurements3619 = "1.2.840.10008.6.1.169";

    /** Hemodynamic Ratios (3620) - Context Group Name */
    public static final String HemodynamicRatios3620 = "1.2.840.10008.6.1.170";

    /** Fractional Flow Reserve (3621) - Context Group Name */
    public static final String FractionalFlowReserve3621 = "1.2.840.10008.6.1.171";

    /** Measurement Type (3627) - Context Group Name */
    public static final String MeasurementType3627 = "1.2.840.10008.6.1.172";

    /** Cardiac Output Methods (3628) - Context Group Name */
    public static final String CardiacOutputMethods3628 = "1.2.840.10008.6.1.173";

    /** Procedure Intent (3629) - Context Group Name */
    public static final String ProcedureIntent3629 = "1.2.840.10008.6.1.174";

    /** Cardiovascular Anatomic Locations (3630) - Context Group Name */
    public static final String CardiovascularAnatomicLocations3630 = "1.2.840.10008.6.1.175";

    /** Hypertension (3640) - Context Group Name */
    public static final String Hypertension3640 = "1.2.840.10008.6.1.176";

    /** Hemodynamic Assessments (3641) - Context Group Name */
    public static final String HemodynamicAssessments3641 = "1.2.840.10008.6.1.177";

    /** Degree Findings (3642) - Context Group Name */
    public static final String DegreeFindings3642 = "1.2.840.10008.6.1.178";

    /** Hemodynamic Measurement Phase (3651) - Context Group Name */
    public static final String HemodynamicMeasurementPhase3651 = "1.2.840.10008.6.1.179";

    /** Body Surface Area Equations (3663) - Context Group Name */
    public static final String BodySurfaceAreaEquations3663 = "1.2.840.10008.6.1.180";

    /** Oxygen Consumption Equations and Tables (3664) - Context Group Name */
    public static final String OxygenConsumptionEquationsAndTables3664 = "1.2.840.10008.6.1.181";

    /** P50 Equations (3666) - Context Group Name */
    public static final String P50Equations3666 = "1.2.840.10008.6.1.182";

    /** Framingham Scores (3667) - Context Group Name */
    public static final String FraminghamScores3667 = "1.2.840.10008.6.1.183";

    /** Framingham Tables (3668) - Context Group Name */
    public static final String FraminghamTables3668 = "1.2.840.10008.6.1.184";

    /** ECG Procedure Types (3670) - Context Group Name */
    public static final String ECGProcedureTypes3670 = "1.2.840.10008.6.1.185";

    /** Reason for ECG Exam (3671) - Context Group Name */
    public static final String ReasonForECGExam3671 = "1.2.840.10008.6.1.186";

    /** Pacemakers (3672) - Context Group Name */
    public static final String Pacemakers3672 = "1.2.840.10008.6.1.187";

    /** Diagnosis (3673) - Context Group Name */
    public static final String Diagnosis3673 = "1.2.840.10008.6.1.188";

    /** Other Filters (3675) - Context Group Name */
    public static final String OtherFilters3675 = "1.2.840.10008.6.1.189";

    /** Lead Measurement Technique (3676) - Context Group Name */
    public static final String LeadMeasurementTechnique3676 = "1.2.840.10008.6.1.190";

    /** Summary Codes ECG (3677) - Context Group Name */
    public static final String SummaryCodesECG3677 = "1.2.840.10008.6.1.191";

    /** QT Correction Algorithms (3678) - Context Group Name */
    public static final String QTCorrectionAlgorithms3678 = "1.2.840.10008.6.1.192";

    /** ECG Morphology Descriptions (3679) - Context Group Name */
    public static final String ECGMorphologyDescriptions3679 = "1.2.840.10008.6.1.193";

    /** ECG Lead Noise Descriptions (3680) - Context Group Name */
    public static final String ECGLeadNoiseDescriptions3680 = "1.2.840.10008.6.1.194";

    /** ECG Lead Noise Modifiers (3681) - Context Group Name */
    public static final String ECGLeadNoiseModifiers3681 = "1.2.840.10008.6.1.195";

    /** Probability (3682) - Context Group Name */
    public static final String Probability3682 = "1.2.840.10008.6.1.196";

    /** Modifiers (3683) - Context Group Name */
    public static final String Modifiers3683 = "1.2.840.10008.6.1.197";

    /** Trend (3684) - Context Group Name */
    public static final String Trend3684 = "1.2.840.10008.6.1.198";

    /** Conjunctive Terms (3685) - Context Group Name */
    public static final String ConjunctiveTerms3685 = "1.2.840.10008.6.1.199";

    /** ECG Interpretive Statements (3686) - Context Group Name */
    public static final String ECGInterpretiveStatements3686 = "1.2.840.10008.6.1.200";

    /** Electrophysiology Waveform Durations (3687) - Context Group Name */
    public static final String ElectrophysiologyWaveformDurations3687 = "1.2.840.10008.6.1.201";

    /** Electrophysiology Waveform Voltages (3688) - Context Group Name */
    public static final String ElectrophysiologyWaveformVoltages3688 = "1.2.840.10008.6.1.202";

    /** Cath Diagnosis (3700) - Context Group Name */
    public static final String CathDiagnosis3700 = "1.2.840.10008.6.1.203";

    /** Cardiac Valves and Tracts (3701) - Context Group Name */
    public static final String CardiacValvesAndTracts3701 = "1.2.840.10008.6.1.204";

    /** Wall Motion (3703) - Context Group Name */
    public static final String WallMotion3703 = "1.2.840.10008.6.1.205";

    /** Myocardium Wall Morphology Findings (3704) - Context Group Name */
    public static final String MyocardiumWallMorphologyFindings3704 = "1.2.840.10008.6.1.206";

    /** Chamber Size (3705) - Context Group Name */
    public static final String ChamberSize3705 = "1.2.840.10008.6.1.207";

    /** Overall Contractility (3706) - Context Group Name */
    public static final String OverallContractility3706 = "1.2.840.10008.6.1.208";

    /** VSD Description (3707) - Context Group Name */
    public static final String VSDDescription3707 = "1.2.840.10008.6.1.209";

    /** Aortic Root Description (3709) - Context Group Name */
    public static final String AorticRootDescription3709 = "1.2.840.10008.6.1.210";

    /** Coronary Dominance (3710) - Context Group Name */
    public static final String CoronaryDominance3710 = "1.2.840.10008.6.1.211";

    /** Valvular Abnormalities (3711) - Context Group Name */
    public static final String ValvularAbnormalities3711 = "1.2.840.10008.6.1.212";

    /** Vessel Descriptors (3712) - Context Group Name */
    public static final String VesselDescriptors3712 = "1.2.840.10008.6.1.213";

    /** TIMI Flow Characteristics (3713) - Context Group Name */
    public static final String TIMIFlowCharacteristics3713 = "1.2.840.10008.6.1.214";

    /** Thrombus (3714) - Context Group Name */
    public static final String Thrombus3714 = "1.2.840.10008.6.1.215";

    /** Lesion Margin (3715) - Context Group Name */
    public static final String LesionMargin3715 = "1.2.840.10008.6.1.216";

    /** Severity (3716) - Context Group Name */
    public static final String Severity3716 = "1.2.840.10008.6.1.217";

    /** Myocardial Wall Segments (3717) - Context Group Name */
    public static final String MyocardialWallSegments3717 = "1.2.840.10008.6.1.218";

    /** Myocardial Wall Segments in Projection (3718) - Context Group Name */
    public static final String MyocardialWallSegmentsInProjection3718 = "1.2.840.10008.6.1.219";

    /** Canadian Clinical Classification (3719) - Context Group Name */
    public static final String CanadianClinicalClassification3719 = "1.2.840.10008.6.1.220";

    /** Cardiac History Dates (Retired) (3720) - Context Group Name */
    public static final String CardiacHistoryDatesRetired3720 = "1.2.840.10008.6.1.221";

    /** Cardiovascular Surgeries (3721) - Context Group Name */
    public static final String CardiovascularSurgeries3721 = "1.2.840.10008.6.1.222";

    /** Diabetic Therapy (3722) - Context Group Name */
    public static final String DiabeticTherapy3722 = "1.2.840.10008.6.1.223";

    /** MI Types (3723) - Context Group Name */
    public static final String MITypes3723 = "1.2.840.10008.6.1.224";

    /** Smoking History (3724) - Context Group Name */
    public static final String SmokingHistory3724 = "1.2.840.10008.6.1.225";

    /** Indications for Coronary Intervention (3726) - Context Group Name */
    public static final String IndicationsForCoronaryIntervention3726 = "1.2.840.10008.6.1.226";

    /** Indications for Catheterization (3727) - Context Group Name */
    public static final String IndicationsForCatheterization3727 = "1.2.840.10008.6.1.227";

    /** Cath Findings (3728) - Context Group Name */
    public static final String CathFindings3728 = "1.2.840.10008.6.1.228";

    /** Admission Status (3729) - Context Group Name */
    public static final String AdmissionStatus3729 = "1.2.840.10008.6.1.229";

    /** Insurance Payor (3730) - Context Group Name */
    public static final String InsurancePayor3730 = "1.2.840.10008.6.1.230";

    /** Primary Cause of Death (3733) - Context Group Name */
    public static final String PrimaryCauseOfDeath3733 = "1.2.840.10008.6.1.231";

    /** Acute Coronary Syndrome Time Period (3735) - Context Group Name */
    public static final String AcuteCoronarySyndromeTimePeriod3735 = "1.2.840.10008.6.1.232";

    /** NYHA Classification (3736) - Context Group Name */
    public static final String NYHAClassification3736 = "1.2.840.10008.6.1.233";

    /** Non-Invasive Test - Ischemia (3737) - Context Group Name */
    public static final String NonInvasiveTestIschemia3737 = "1.2.840.10008.6.1.234";

    /** Pre-Cath Angina Type (3738) - Context Group Name */
    public static final String PreCathAnginaType3738 = "1.2.840.10008.6.1.235";

    /** Cath Procedure Type (3739) - Context Group Name */
    public static final String CathProcedureType3739 = "1.2.840.10008.6.1.236";

    /** Thrombolytic Administration (3740) - Context Group Name */
    public static final String ThrombolyticAdministration3740 = "1.2.840.10008.6.1.237";

    /** Medication Administration, Lab Visit (3741) - Context Group Name */
    public static final String MedicationAdministrationLabVisit3741 = "1.2.840.10008.6.1.238";

    /** Medication Administration, PCI (3742) - Context Group Name */
    public static final String MedicationAdministrationPCI3742 = "1.2.840.10008.6.1.239";

    /** Clopidogrel/Ticlopidine Administration (3743) - Context Group Name */
    public static final String ClopidogrelTiclopidineAdministration3743 = "1.2.840.10008.6.1.240";

    /** EF Testing Method (3744) - Context Group Name */
    public static final String EFTestingMethod3744 = "1.2.840.10008.6.1.241";

    /** Calculation Method (3745) - Context Group Name */
    public static final String CalculationMethod3745 = "1.2.840.10008.6.1.242";

    /** Percutaneous Entry Site (3746) - Context Group Name */
    public static final String PercutaneousEntrySite3746 = "1.2.840.10008.6.1.243";

    /** Percutaneous Closure (3747) - Context Group Name */
    public static final String PercutaneousClosure3747 = "1.2.840.10008.6.1.244";

    /** Angiographic EF Testing Method (3748) - Context Group Name */
    public static final String AngiographicEFTestingMethod3748 = "1.2.840.10008.6.1.245";

    /** PCI Procedure Result (3749) - Context Group Name */
    public static final String PCIProcedureResult3749 = "1.2.840.10008.6.1.246";

    /** Previously Dilated Lesion (3750) - Context Group Name */
    public static final String PreviouslyDilatedLesion3750 = "1.2.840.10008.6.1.247";

    /** Guidewire Crossing (3752) - Context Group Name */
    public static final String GuidewireCrossing3752 = "1.2.840.10008.6.1.248";

    /** Vascular Complications (3754) - Context Group Name */
    public static final String VascularComplications3754 = "1.2.840.10008.6.1.249";

    /** Cath Complications (3755) - Context Group Name */
    public static final String CathComplications3755 = "1.2.840.10008.6.1.250";

    /** Cardiac Patient Risk Factors (3756) - Context Group Name */
    public static final String CardiacPatientRiskFactors3756 = "1.2.840.10008.6.1.251";

    /** Cardiac Diagnostic Procedures (3757) - Context Group Name */
    public static final String CardiacDiagnosticProcedures3757 = "1.2.840.10008.6.1.252";

    /** Cardiovascular Family History (3758) - Context Group Name */
    public static final String CardiovascularFamilyHistory3758 = "1.2.840.10008.6.1.253";

    /** Hypertension Therapy (3760) - Context Group Name */
    public static final String HypertensionTherapy3760 = "1.2.840.10008.6.1.254";

    /** Antilipemic agents (3761) - Context Group Name */
    public static final String AntilipemicAgents3761 = "1.2.840.10008.6.1.255";

    /** Antiarrhythmic agents (3762) - Context Group Name */
    public static final String AntiarrhythmicAgents3762 = "1.2.840.10008.6.1.256";

    /** Myocardial Infarction Therapies (3764) - Context Group Name */
    public static final String MyocardialInfarctionTherapies3764 = "1.2.840.10008.6.1.257";

    /** Concern Types (3769) - Context Group Name */
    public static final String ConcernTypes3769 = "1.2.840.10008.6.1.258";

    /** Problem Status (3770) - Context Group Name */
    public static final String ProblemStatus3770 = "1.2.840.10008.6.1.259";

    /** Health Status (3772) - Context Group Name */
    public static final String HealthStatus3772 = "1.2.840.10008.6.1.260";

    /** Use Status (3773) - Context Group Name */
    public static final String UseStatus3773 = "1.2.840.10008.6.1.261";

    /** Social History (3774) - Context Group Name */
    public static final String SocialHistory3774 = "1.2.840.10008.6.1.262";

    /** Implanted Devices (3777) - Context Group Name */
    public static final String ImplantedDevices3777 = "1.2.840.10008.6.1.263";

    /** Plaque Structures (3802) - Context Group Name */
    public static final String PlaqueStructures3802 = "1.2.840.10008.6.1.264";

    /** Stenosis Measurement Methods (3804) - Context Group Name */
    public static final String StenosisMeasurementMethods3804 = "1.2.840.10008.6.1.265";

    /** Stenosis Types (3805) - Context Group Name */
    public static final String StenosisTypes3805 = "1.2.840.10008.6.1.266";

    /** Stenosis Shape (3806) - Context Group Name */
    public static final String StenosisShape3806 = "1.2.840.10008.6.1.267";

    /** Volume Measurement Methods (3807) - Context Group Name */
    public static final String VolumeMeasurementMethods3807 = "1.2.840.10008.6.1.268";

    /** Aneurysm Types (3808) - Context Group Name */
    public static final String AneurysmTypes3808 = "1.2.840.10008.6.1.269";

    /** Associated Conditions (3809) - Context Group Name */
    public static final String AssociatedConditions3809 = "1.2.840.10008.6.1.270";

    /** Vascular Morphology (3810) - Context Group Name */
    public static final String VascularMorphology3810 = "1.2.840.10008.6.1.271";

    /** Stent Findings (3813) - Context Group Name */
    public static final String StentFindings3813 = "1.2.840.10008.6.1.272";

    /** Stent Composition (3814) - Context Group Name */
    public static final String StentComposition3814 = "1.2.840.10008.6.1.273";

    /** Source of Vascular Finding (3815) - Context Group Name */
    public static final String SourceOfVascularFinding3815 = "1.2.840.10008.6.1.274";

    /** Vascular Sclerosis Types (3817) - Context Group Name */
    public static final String VascularSclerosisTypes3817 = "1.2.840.10008.6.1.275";

    /** Non-invasive Vascular Procedures (3820) - Context Group Name */
    public static final String NonInvasiveVascularProcedures3820 = "1.2.840.10008.6.1.276";

    /** Papillary Muscle Included/Excluded (3821) - Context Group Name */
    public static final String PapillaryMuscleIncludedExcluded3821 = "1.2.840.10008.6.1.277";

    /** Respiratory Status (3823) - Context Group Name */
    public static final String RespiratoryStatus3823 = "1.2.840.10008.6.1.278";

    /** Heart Rhythm (3826) - Context Group Name */
    public static final String HeartRhythm3826 = "1.2.840.10008.6.1.279";

    /** Vessel Segments (3827) - Context Group Name */
    public static final String VesselSegments3827 = "1.2.840.10008.6.1.280";

    /** Pulmonary Arteries (3829) - Context Group Name */
    public static final String PulmonaryArteries3829 = "1.2.840.10008.6.1.281";

    /** Stenosis Length (3831) - Context Group Name */
    public static final String StenosisLength3831 = "1.2.840.10008.6.1.282";

    /** Stenosis Grade (3832) - Context Group Name */
    public static final String StenosisGrade3832 = "1.2.840.10008.6.1.283";

    /** Cardiac Ejection Fraction (3833) - Context Group Name */
    public static final String CardiacEjectionFraction3833 = "1.2.840.10008.6.1.284";

    /** Cardiac Volume Measurements (3835) - Context Group Name */
    public static final String CardiacVolumeMeasurements3835 = "1.2.840.10008.6.1.285";

    /** Time-based Perfusion Measurements (3836) - Context Group Name */
    public static final String TimeBasedPerfusionMeasurements3836 = "1.2.840.10008.6.1.286";

    /** Fiducial Feature (3837) - Context Group Name */
    public static final String FiducialFeature3837 = "1.2.840.10008.6.1.287";

    /** Diameter Derivation (3838) - Context Group Name */
    public static final String DiameterDerivation3838 = "1.2.840.10008.6.1.288";

    /** Coronary Veins (3839) - Context Group Name */
    public static final String CoronaryVeins3839 = "1.2.840.10008.6.1.289";

    /** Pulmonary Veins (3840) - Context Group Name */
    public static final String PulmonaryVeins3840 = "1.2.840.10008.6.1.290";

    /** Myocardial Subsegment (3843) - Context Group Name */
    public static final String MyocardialSubsegment3843 = "1.2.840.10008.6.1.291";

    /** Partial View Section for Mammography (4005) - Context Group Name */
    public static final String PartialViewSectionForMammography4005 = "1.2.840.10008.6.1.292";

    /** DX Anatomy Imaged (4009) - Context Group Name */
    public static final String DXAnatomyImaged4009 = "1.2.840.10008.6.1.293";

    /** DX View (4010) - Context Group Name */
    public static final String DXView4010 = "1.2.840.10008.6.1.294";

    /** DX View Modifier (4011) - Context Group Name */
    public static final String DXViewModifier4011 = "1.2.840.10008.6.1.295";

    /** Projection Eponymous Name (4012) - Context Group Name */
    public static final String ProjectionEponymousName4012 = "1.2.840.10008.6.1.296";

    /** Anatomic Region for Mammography (4013) - Context Group Name */
    public static final String AnatomicRegionForMammography4013 = "1.2.840.10008.6.1.297";

    /** View for Mammography (4014) - Context Group Name */
    public static final String ViewForMammography4014 = "1.2.840.10008.6.1.298";

    /** View Modifier for Mammography (4015) - Context Group Name */
    public static final String ViewModifierForMammography4015 = "1.2.840.10008.6.1.299";

    /** Anatomic Region for Intra-oral Radiography (4016) - Context Group Name */
    public static final String AnatomicRegionForIntraOralRadiography4016 = "1.2.840.10008.6.1.300";

    /** Anatomic Region Modifier for Intra-oral Radiography (4017) - Context Group Name */
    public static final String AnatomicRegionModifierForIntraOralRadiography4017 = "1.2.840.10008.6.1.301";

    /** Primary Anatomic Structure for Intra-oral Radiography (Permanent Dentition - Designation of Teeth) (4018) - Context Group Name */
    public static final String PrimaryAnatomicStructureForIntraOralRadiographyPermanentDentitionDesignationOfTeeth4018 = "1.2.840.10008.6.1.302";

    /** Primary Anatomic Structure for Intra-oral Radiography (Deciduous Dentition - Designation of Teeth) (4019) - Context Group Name */
    public static final String PrimaryAnatomicStructureForIntraOralRadiographyDeciduousDentitionDesignationOfTeeth4019 = "1.2.840.10008.6.1.303";

    /** PET Radionuclide (4020) - Context Group Name */
    public static final String PETRadionuclide4020 = "1.2.840.10008.6.1.304";

    /** PET Radiopharmaceutical (4021) - Context Group Name */
    public static final String PETRadiopharmaceutical4021 = "1.2.840.10008.6.1.305";

    /** Craniofacial Anatomic Regions (4028) - Context Group Name */
    public static final String CraniofacialAnatomicRegions4028 = "1.2.840.10008.6.1.306";

    /** CT and MR Anatomy Imaged (4030) - Context Group Name */
    public static final String CTAndMRAnatomyImaged4030 = "1.2.840.10008.6.1.307";

    /** Common Anatomic Regions (4031) - Context Group Name */
    public static final String CommonAnatomicRegions4031 = "1.2.840.10008.6.1.308";

    /** MR Spectroscopy Metabolites (4032) - Context Group Name */
    public static final String MRSpectroscopyMetabolites4032 = "1.2.840.10008.6.1.309";

    /** MR Proton Spectroscopy Metabolites (4033) - Context Group Name */
    public static final String MRProtonSpectroscopyMetabolites4033 = "1.2.840.10008.6.1.310";

    /** Endoscopy Anatomic Regions (4040) - Context Group Name */
    public static final String EndoscopyAnatomicRegions4040 = "1.2.840.10008.6.1.311";

    /** XA/XRF Anatomy Imaged (4042) - Context Group Name */
    public static final String XAXRFAnatomyImaged4042 = "1.2.840.10008.6.1.312";

    /** Drug or Contrast Agent Characteristics (4050) - Context Group Name */
    public static final String DrugOrContrastAgentCharacteristics4050 = "1.2.840.10008.6.1.313";

    /** General Devices (4051) - Context Group Name */
    public static final String GeneralDevices4051 = "1.2.840.10008.6.1.314";

    /** Phantom Devices (4052) - Context Group Name */
    public static final String PhantomDevices4052 = "1.2.840.10008.6.1.315";

    /** Ophthalmic Imaging Agent (4200) - Context Group Name */
    public static final String OphthalmicImagingAgent4200 = "1.2.840.10008.6.1.316";

    /** Patient Eye Movement Command (4201) - Context Group Name */
    public static final String PatientEyeMovementCommand4201 = "1.2.840.10008.6.1.317";

    /** Ophthalmic Photography Acquisition Device (4202) - Context Group Name */
    public static final String OphthalmicPhotographyAcquisitionDevice4202 = "1.2.840.10008.6.1.318";

    /** Ophthalmic Photography Illumination (4203) - Context Group Name */
    public static final String OphthalmicPhotographyIllumination4203 = "1.2.840.10008.6.1.319";

    /** Ophthalmic Filter (4204) - Context Group Name */
    public static final String OphthalmicFilter4204 = "1.2.840.10008.6.1.320";

    /** Ophthalmic Lens (4205) - Context Group Name */
    public static final String OphthalmicLens4205 = "1.2.840.10008.6.1.321";

    /** Ophthalmic Channel Description (4206) - Context Group Name */
    public static final String OphthalmicChannelDescription4206 = "1.2.840.10008.6.1.322";

    /** Ophthalmic Image Position (4207) - Context Group Name */
    public static final String OphthalmicImagePosition4207 = "1.2.840.10008.6.1.323";

    /** Mydriatic Agent (4208) - Context Group Name */
    public static final String MydriaticAgent4208 = "1.2.840.10008.6.1.324";

    /** Ophthalmic Anatomic Structure Imaged (4209) - Context Group Name */
    public static final String OphthalmicAnatomicStructureImaged4209 = "1.2.840.10008.6.1.325";

    /** Ophthalmic Tomography Acquisition Device (4210) - Context Group Name */
    public static final String OphthalmicTomographyAcquisitionDevice4210 = "1.2.840.10008.6.1.326";

    /** Ophthalmic OCT Anatomic Structure Imaged (4211) - Context Group Name */
    public static final String OphthalmicOCTAnatomicStructureImaged4211 = "1.2.840.10008.6.1.327";

    /** Languages (5000) - Context Group Name */
    public static final String Languages5000 = "1.2.840.10008.6.1.328";

    /** Countries (5001) - Context Group Name */
    public static final String Countries5001 = "1.2.840.10008.6.1.329";

    /** Overall Breast Composition (6000) - Context Group Name */
    public static final String OverallBreastComposition6000 = "1.2.840.10008.6.1.330";

    /** Overall Breast Composition from BI-RADS® (6001) - Context Group Name */
    public static final String OverallBreastCompositionFromBIRADS6001 = "1.2.840.10008.6.1.331";

    /** Change Since Last Mammogram or Prior Surgery (6002) - Context Group Name */
    public static final String ChangeSinceLastMammogramOrPriorSurgery6002 = "1.2.840.10008.6.1.332";

    /** Change Since Last Mammogram or Prior Surgery from BI-RADS® (6003) - Context Group Name */
    public static final String ChangeSinceLastMammogramOrPriorSurgeryFromBIRADS6003 = "1.2.840.10008.6.1.333";

    /** Mammography Characteristics of Shape (6004) - Context Group Name */
    public static final String MammographyCharacteristicsOfShape6004 = "1.2.840.10008.6.1.334";

    /** Characteristics of Shape from BI-RADS® (6005) - Context Group Name */
    public static final String CharacteristicsOfShapeFromBIRADS6005 = "1.2.840.10008.6.1.335";

    /** Mammography Characteristics of Margin (6006) - Context Group Name */
    public static final String MammographyCharacteristicsOfMargin6006 = "1.2.840.10008.6.1.336";

    /** Characteristics of Margin from BI-RADS® (6007) - Context Group Name */
    public static final String CharacteristicsOfMarginFromBIRADS6007 = "1.2.840.10008.6.1.337";

    /** Density Modifier (6008) - Context Group Name */
    public static final String DensityModifier6008 = "1.2.840.10008.6.1.338";

    /** Density Modifier from BI-RADS® (6009) - Context Group Name */
    public static final String DensityModifierFromBIRADS6009 = "1.2.840.10008.6.1.339";

    /** Mammography Calcification Types (6010) - Context Group Name */
    public static final String MammographyCalcificationTypes6010 = "1.2.840.10008.6.1.340";

    /** Calcification Types from BI-RADS® (6011) - Context Group Name */
    public static final String CalcificationTypesFromBIRADS6011 = "1.2.840.10008.6.1.341";

    /** Calcification Distribution Modifier (6012) - Context Group Name */
    public static final String CalcificationDistributionModifier6012 = "1.2.840.10008.6.1.342";

    /** Calcification Distribution Modifier from BI-RADS® (6013) - Context Group Name */
    public static final String CalcificationDistributionModifierFromBIRADS6013 = "1.2.840.10008.6.1.343";

    /** Mammography Single Image Finding (6014) - Context Group Name */
    public static final String MammographySingleImageFinding6014 = "1.2.840.10008.6.1.344";

    /** Single Image Finding from BI-RADS® (6015) - Context Group Name */
    public static final String SingleImageFindingFromBIRADS6015 = "1.2.840.10008.6.1.345";

    /** Mammography Composite Feature (6016) - Context Group Name */
    public static final String MammographyCompositeFeature6016 = "1.2.840.10008.6.1.346";

    /** Composite Feature from BI-RADS® (6017) - Context Group Name */
    public static final String CompositeFeatureFromBIRADS6017 = "1.2.840.10008.6.1.347";

    /** Clockface Location or Region (6018) - Context Group Name */
    public static final String ClockfaceLocationOrRegion6018 = "1.2.840.10008.6.1.348";

    /** Clockface Location or Region from BI-RADS® (6019) - Context Group Name */
    public static final String ClockfaceLocationOrRegionFromBIRADS6019 = "1.2.840.10008.6.1.349";

    /** Quadrant Location (6020) - Context Group Name */
    public static final String QuadrantLocation6020 = "1.2.840.10008.6.1.350";

    /** Quadrant Location from BI-RADS® (6021) - Context Group Name */
    public static final String QuadrantLocationFromBIRADS6021 = "1.2.840.10008.6.1.351";

    /** Side (6022) - Context Group Name */
    public static final String Side6022 = "1.2.840.10008.6.1.352";

    /** Side from BI-RADS® (6023) - Context Group Name */
    public static final String SideFromBIRADS6023 = "1.2.840.10008.6.1.353";

    /** Depth (6024) - Context Group Name */
    public static final String Depth6024 = "1.2.840.10008.6.1.354";

    /** Depth from BI-RADS® (6025) - Context Group Name */
    public static final String DepthFromBIRADS6025 = "1.2.840.10008.6.1.355";

    /** Mammography Assessment (6026) - Context Group Name */
    public static final String MammographyAssessment6026 = "1.2.840.10008.6.1.356";

    /** Assessment from BI-RADS® (6027) - Context Group Name */
    public static final String AssessmentFromBIRADS6027 = "1.2.840.10008.6.1.357";

    /** Mammography Recommended Follow-up (6028) - Context Group Name */
    public static final String MammographyRecommendedFollowUp6028 = "1.2.840.10008.6.1.358";

    /** Recommended Follow-up from BI-RADS® (6029) - Context Group Name */
    public static final String RecommendedFollowUpFromBIRADS6029 = "1.2.840.10008.6.1.359";

    /** Mammography Pathology Codes (6030) - Context Group Name */
    public static final String MammographyPathologyCodes6030 = "1.2.840.10008.6.1.360";

    /** Benign Pathology Codes from BI-RADS® (6031) - Context Group Name */
    public static final String BenignPathologyCodesFromBIRADS6031 = "1.2.840.10008.6.1.361";

    /** High Risk Lesions Pathology Codes from BI-RADS® (6032) - Context Group Name */
    public static final String HighRiskLesionsPathologyCodesFromBIRADS6032 = "1.2.840.10008.6.1.362";

    /** Malignant Pathology Codes from BI-RADS® (6033) - Context Group Name */
    public static final String MalignantPathologyCodesFromBIRADS6033 = "1.2.840.10008.6.1.363";

    /** Intended Use of CAD Output (6034) - Context Group Name */
    public static final String IntendedUseOfCADOutput6034 = "1.2.840.10008.6.1.364";

    /** Composite Feature Relations (6035) - Context Group Name */
    public static final String CompositeFeatureRelations6035 = "1.2.840.10008.6.1.365";

    /** Scope of Feature (6036) - Context Group Name */
    public static final String ScopeOfFeature6036 = "1.2.840.10008.6.1.366";

    /** Mammography Quantitative Temporal Difference Type (6037) - Context Group Name */
    public static final String MammographyQuantitativeTemporalDifferenceType6037 = "1.2.840.10008.6.1.367";

    /** Mammography Qualitative Temporal Difference Type (6038) - Context Group Name */
    public static final String MammographyQualitativeTemporalDifferenceType6038 = "1.2.840.10008.6.1.368";

    /** Nipple Characteristic (6039) - Context Group Name */
    public static final String NippleCharacteristic6039 = "1.2.840.10008.6.1.369";

    /** Non-Lesion Object Type (6040) - Context Group Name */
    public static final String NonLesionObjectType6040 = "1.2.840.10008.6.1.370";

    /** Mammography Image Quality Finding (6041) - Context Group Name */
    public static final String MammographyImageQualityFinding6041 = "1.2.840.10008.6.1.371";

    /** Status of Results (6042) - Context Group Name */
    public static final String StatusOfResults6042 = "1.2.840.10008.6.1.372";

    /** Types of Mammography CAD Analysis (6043) - Context Group Name */
    public static final String TypesOfMammographyCADAnalysis6043 = "1.2.840.10008.6.1.373";

    /** Types of Image Quality Assessment (6044) - Context Group Name */
    public static final String TypesOfImageQualityAssessment6044 = "1.2.840.10008.6.1.374";

    /** Mammography Types of Quality Control Standard (6045) - Context Group Name */
    public static final String MammographyTypesOfQualityControlStandard6045 = "1.2.840.10008.6.1.375";

    /** Units of Follow-up Interval (6046) - Context Group Name */
    public static final String UnitsOfFollowUpInterval6046 = "1.2.840.10008.6.1.376";

    /** CAD Processing and Findings Summary (6047) - Context Group Name */
    public static final String CADProcessingAndFindingsSummary6047 = "1.2.840.10008.6.1.377";

    /** CAD Operating Point Axis Label (6048) - Context Group Name */
    public static final String CADOperatingPointAxisLabel6048 = "1.2.840.10008.6.1.378";

    /** Breast Procedure Reported (6050) - Context Group Name */
    public static final String BreastProcedureReported6050 = "1.2.840.10008.6.1.379";

    /** Breast Procedure Reason (6051) - Context Group Name */
    public static final String BreastProcedureReason6051 = "1.2.840.10008.6.1.380";

    /** Breast Imaging Report section title (6052) - Context Group Name */
    public static final String BreastImagingReportSectionTitle6052 = "1.2.840.10008.6.1.381";

    /** Breast Imaging Report Elements (6053) - Context Group Name */
    public static final String BreastImagingReportElements6053 = "1.2.840.10008.6.1.382";

    /** Breast Imaging Findings (6054) - Context Group Name */
    public static final String BreastImagingFindings6054 = "1.2.840.10008.6.1.383";

    /** Breast Clinical Finding or Indicated Problem (6055) - Context Group Name */
    public static final String BreastClinicalFindingOrIndicatedProblem6055 = "1.2.840.10008.6.1.384";

    /** Associated Findings for Breast (6056) - Context Group Name */
    public static final String AssociatedFindingsForBreast6056 = "1.2.840.10008.6.1.385";

    /** Ductography Findings for Breast (6057) - Context Group Name */
    public static final String DuctographyFindingsForBreast6057 = "1.2.840.10008.6.1.386";

    /** Procedure Modifiers for Breast (6058) - Context Group Name */
    public static final String ProcedureModifiersForBreast6058 = "1.2.840.10008.6.1.387";

    /** Breast Implant Types (6059) - Context Group Name */
    public static final String BreastImplantTypes6059 = "1.2.840.10008.6.1.388";

    /** Breast Biopsy Techniques (6060) - Context Group Name */
    public static final String BreastBiopsyTechniques6060 = "1.2.840.10008.6.1.389";

    /** Breast Imaging Procedure Modifiers (6061) - Context Group Name */
    public static final String BreastImagingProcedureModifiers6061 = "1.2.840.10008.6.1.390";

    /** Interventional Procedure Complications (6062) - Context Group Name */
    public static final String InterventionalProcedureComplications6062 = "1.2.840.10008.6.1.391";

    /** Interventional Procedure Results (6063) - Context Group Name */
    public static final String InterventionalProcedureResults6063 = "1.2.840.10008.6.1.392";

    /** Ultrasound Findings for Breast (6064) - Context Group Name */
    public static final String UltrasoundFindingsForBreast6064 = "1.2.840.10008.6.1.393";

    /** Instrument Approach (6065) - Context Group Name */
    public static final String InstrumentApproach6065 = "1.2.840.10008.6.1.394";

    /** Target Confirmation (6066) - Context Group Name */
    public static final String TargetConfirmation6066 = "1.2.840.10008.6.1.395";

    /** Fluid Color (6067) - Context Group Name */
    public static final String FluidColor6067 = "1.2.840.10008.6.1.396";

    /** Tumor Stages from AJCC (6068) - Context Group Name */
    public static final String TumorStagesFromAJCC6068 = "1.2.840.10008.6.1.397";

    /** Nottingham Combined Histologic Grade (6069) - Context Group Name */
    public static final String NottinghamCombinedHistologicGrade6069 = "1.2.840.10008.6.1.398";

    /** Bloom-Richardson Histologic Grade (6070) - Context Group Name */
    public static final String BloomRichardsonHistologicGrade6070 = "1.2.840.10008.6.1.399";

    /** Histologic Grading Method (6071) - Context Group Name */
    public static final String HistologicGradingMethod6071 = "1.2.840.10008.6.1.400";

    /** Breast Implant Findings (6072) - Context Group Name */
    public static final String BreastImplantFindings6072 = "1.2.840.10008.6.1.401";

    /** Gynecological Hormones (6080) - Context Group Name */
    public static final String GynecologicalHormones6080 = "1.2.840.10008.6.1.402";

    /** Breast Cancer Risk Factors (6081) - Context Group Name */
    public static final String BreastCancerRiskFactors6081 = "1.2.840.10008.6.1.403";

    /** Gynecological Procedures (6082) - Context Group Name */
    public static final String GynecologicalProcedures6082 = "1.2.840.10008.6.1.404";

    /** Procedures for Breast (6083) - Context Group Name */
    public static final String ProceduresForBreast6083 = "1.2.840.10008.6.1.405";

    /** Mammoplasty Procedures (6084) - Context Group Name */
    public static final String MammoplastyProcedures6084 = "1.2.840.10008.6.1.406";

    /** Therapies for Breast (6085) - Context Group Name */
    public static final String TherapiesForBreast6085 = "1.2.840.10008.6.1.407";

    /** Menopausal Phase (6086) - Context Group Name */
    public static final String MenopausalPhase6086 = "1.2.840.10008.6.1.408";

    /** General Risk Factors (6087) - Context Group Name */
    public static final String GeneralRiskFactors6087 = "1.2.840.10008.6.1.409";

    /** OB-GYN Maternal Risk Factors (6088) - Context Group Name */
    public static final String OBGYNMaternalRiskFactors6088 = "1.2.840.10008.6.1.410";

    /** Substances (6089) - Context Group Name */
    public static final String Substances6089 = "1.2.840.10008.6.1.411";

    /** Relative Usage, Exposure Amount (6090) - Context Group Name */
    public static final String RelativeUsageExposureAmount6090 = "1.2.840.10008.6.1.412";

    /** Relative Frequency of Event Values (6091) - Context Group Name */
    public static final String RelativeFrequencyOfEventValues6091 = "1.2.840.10008.6.1.413";

    /** Quantitative Concepts for Usage, Exposure (6092) - Context Group Name */
    public static final String QuantitativeConceptsForUsageExposure6092 = "1.2.840.10008.6.1.414";

    /** Qualitative Concepts for Usage, Exposure Amount (6093) - Context Group Name */
    public static final String QualitativeConceptsForUsageExposureAmount6093 = "1.2.840.10008.6.1.415";

    /** QuaLItative Concepts for Usage, Exposure Frequency (6094) - Context Group Name */
    public static final String QuaLItativeConceptsForUsageExposureFrequency6094 = "1.2.840.10008.6.1.416";

    /** Numeric Properties of Procedures (6095) - Context Group Name */
    public static final String NumericPropertiesOfProcedures6095 = "1.2.840.10008.6.1.417";

    /** Pregnancy Status (6096) - Context Group Name */
    public static final String PregnancyStatus6096 = "1.2.840.10008.6.1.418";

    /** Side of Family (6097) - Context Group Name */
    public static final String SideOfFamily6097 = "1.2.840.10008.6.1.419";

    /** Chest Component Categories (6100) - Context Group Name */
    public static final String ChestComponentCategories6100 = "1.2.840.10008.6.1.420";

    /** Chest Finding or Feature (6101) - Context Group Name */
    public static final String ChestFindingOrFeature6101 = "1.2.840.10008.6.1.421";

    /** Chest Finding or Feature Modifier (6102) - Context Group Name */
    public static final String ChestFindingOrFeatureModifier6102 = "1.2.840.10008.6.1.422";

    /** Abnormal Lines Finding or Feature (6103) - Context Group Name */
    public static final String AbnormalLinesFindingOrFeature6103 = "1.2.840.10008.6.1.423";

    /** Abnormal Opacity Finding or Feature (6104) - Context Group Name */
    public static final String AbnormalOpacityFindingOrFeature6104 = "1.2.840.10008.6.1.424";

    /** Abnormal Lucency Finding or Feature (6105) - Context Group Name */
    public static final String AbnormalLucencyFindingOrFeature6105 = "1.2.840.10008.6.1.425";

    /** Abnormal Texture Finding or Feature (6106) - Context Group Name */
    public static final String AbnormalTextureFindingOrFeature6106 = "1.2.840.10008.6.1.426";

    /** Width Descriptor (6107) - Context Group Name */
    public static final String WidthDescriptor6107 = "1.2.840.10008.6.1.427";

    /** Chest Anatomic Structure Abnormal Distribution (6108) - Context Group Name */
    public static final String ChestAnatomicStructureAbnormalDistribution6108 = "1.2.840.10008.6.1.428";

    /** Radiographic Anatomy Finding or Feature (6109) - Context Group Name */
    public static final String RadiographicAnatomyFindingOrFeature6109 = "1.2.840.10008.6.1.429";

    /** Lung Anatomy Finding or Feature (6110) - Context Group Name */
    public static final String LungAnatomyFindingOrFeature6110 = "1.2.840.10008.6.1.430";

    /** Bronchovascular Anatomy Finding or Feature (6111) - Context Group Name */
    public static final String BronchovascularAnatomyFindingOrFeature6111 = "1.2.840.10008.6.1.431";

    /** Pleura Anatomy Finding or Feature (6112) - Context Group Name */
    public static final String PleuraAnatomyFindingOrFeature6112 = "1.2.840.10008.6.1.432";

    /** Mediastinum Anatomy Finding or Feature (6113) - Context Group Name */
    public static final String MediastinumAnatomyFindingOrFeature6113 = "1.2.840.10008.6.1.433";

    /** Osseous Anatomy Finding or Feature (6114) - Context Group Name */
    public static final String OsseousAnatomyFindingOrFeature6114 = "1.2.840.10008.6.1.434";

    /** Osseous Anatomy Modifiers (6115) - Context Group Name */
    public static final String OsseousAnatomyModifiers6115 = "1.2.840.10008.6.1.435";

    /** Muscular Anatomy (6116) - Context Group Name */
    public static final String MuscularAnatomy6116 = "1.2.840.10008.6.1.436";

    /** Vascular Anatomy (6117) - Context Group Name */
    public static final String VascularAnatomy6117 = "1.2.840.10008.6.1.437";

    /** Size Descriptor (6118) - Context Group Name */
    public static final String SizeDescriptor6118 = "1.2.840.10008.6.1.438";

    /** Chest Border Shape (6119) - Context Group Name */
    public static final String ChestBorderShape6119 = "1.2.840.10008.6.1.439";

    /** Chest Border Definition (6120) - Context Group Name */
    public static final String ChestBorderDefinition6120 = "1.2.840.10008.6.1.440";

    /** Chest Orientation Descriptor (6121) - Context Group Name */
    public static final String ChestOrientationDescriptor6121 = "1.2.840.10008.6.1.441";

    /** Chest Content Descriptor (6122) - Context Group Name */
    public static final String ChestContentDescriptor6122 = "1.2.840.10008.6.1.442";

    /** Chest Opacity Descriptor (6123) - Context Group Name */
    public static final String ChestOpacityDescriptor6123 = "1.2.840.10008.6.1.443";

    /** Location in Chest (6124) - Context Group Name */
    public static final String LocationInChest6124 = "1.2.840.10008.6.1.444";

    /** General Chest Location (6125) - Context Group Name */
    public static final String GeneralChestLocation6125 = "1.2.840.10008.6.1.445";

    /** Location in Lung (6126) - Context Group Name */
    public static final String LocationInLung6126 = "1.2.840.10008.6.1.446";

    /** Segment Location in Lung (6127) - Context Group Name */
    public static final String SegmentLocationInLung6127 = "1.2.840.10008.6.1.447";

    /** Chest Distribution Descriptor (6128) - Context Group Name */
    public static final String ChestDistributionDescriptor6128 = "1.2.840.10008.6.1.448";

    /** Chest Site Involvement (6129) - Context Group Name */
    public static final String ChestSiteInvolvement6129 = "1.2.840.10008.6.1.449";

    /** Severity Descriptor (6130) - Context Group Name */
    public static final String SeverityDescriptor6130 = "1.2.840.10008.6.1.450";

    /** Chest Texture Descriptor (6131) - Context Group Name */
    public static final String ChestTextureDescriptor6131 = "1.2.840.10008.6.1.451";

    /** Chest Calcification Descriptor (6132) - Context Group Name */
    public static final String ChestCalcificationDescriptor6132 = "1.2.840.10008.6.1.452";

    /** Chest Quantitative Temporal Difference Type (6133) - Context Group Name */
    public static final String ChestQuantitativeTemporalDifferenceType6133 = "1.2.840.10008.6.1.453";

    /** Qualitative Temporal Difference Type (6134) - Context Group Name */
    public static final String QualitativeTemporalDifferenceType6134 = "1.2.840.10008.6.1.454";

    /** Image Quality Finding (6135) - Context Group Name */
    public static final String ImageQualityFinding6135 = "1.2.840.10008.6.1.455";

    /** Chest Types of Quality Control Standard (6136) - Context Group Name */
    public static final String ChestTypesOfQualityControlStandard6136 = "1.2.840.10008.6.1.456";

    /** Types of CAD Analysis (6137) - Context Group Name */
    public static final String TypesOfCADAnalysis6137 = "1.2.840.10008.6.1.457";

    /** Chest Non-Lesion Object Type (6138) - Context Group Name */
    public static final String ChestNonLesionObjectType6138 = "1.2.840.10008.6.1.458";

    /** Non-Lesion Modifiers (6139) - Context Group Name */
    public static final String NonLesionModifiers6139 = "1.2.840.10008.6.1.459";

    /** Calculation Methods (6140) - Context Group Name */
    public static final String CalculationMethods6140 = "1.2.840.10008.6.1.460";

    /** Attenuation Coefficient Measurements (6141) - Context Group Name */
    public static final String AttenuationCoefficientMeasurements6141 = "1.2.840.10008.6.1.461";

    /** Calculated Value (6142) - Context Group Name */
    public static final String CalculatedValue6142 = "1.2.840.10008.6.1.462";

    /** Response Criteria (6143) - Context Group Name */
    public static final String ResponseCriteria6143 = "1.2.840.10008.6.1.463";

    /** RECIST Response Criteria (6144) - Context Group Name */
    public static final String RECISTResponseCriteria6144 = "1.2.840.10008.6.1.464";

    /** Baseline Category (6145) - Context Group Name */
    public static final String BaselineCategory6145 = "1.2.840.10008.6.1.465";

    /** Background echotexture (6151) - Context Group Name */
    public static final String BackgroundEchotexture6151 = "1.2.840.10008.6.1.466";

    /** Orientation (6152) - Context Group Name */
    public static final String Orientation6152 = "1.2.840.10008.6.1.467";

    /** Lesion boundary (6153) - Context Group Name */
    public static final String LesionBoundary6153 = "1.2.840.10008.6.1.468";

    /** Echo pattern (6154) - Context Group Name */
    public static final String EchoPattern6154 = "1.2.840.10008.6.1.469";

    /** Posterior acoustic features (6155) - Context Group Name */
    public static final String PosteriorAcousticFeatures6155 = "1.2.840.10008.6.1.470";

    /** Vascularity (6157) - Context Group Name */
    public static final String Vascularity6157 = "1.2.840.10008.6.1.471";

    /** Correlation to Other Findings (6158) - Context Group Name */
    public static final String CorrelationToOtherFindings6158 = "1.2.840.10008.6.1.472";

    /** Malignancy Type (6159) - Context Group Name */
    public static final String MalignancyType6159 = "1.2.840.10008.6.1.473";

    /** Breast Primary Tumor Assessment from AJCC (6160) - Context Group Name */
    public static final String BreastPrimaryTumorAssessmentFromAJCC6160 = "1.2.840.10008.6.1.474";

    /** Clinical Regional Lymph Node Assessment for Breast (6161) - Context Group Name */
    public static final String ClinicalRegionalLymphNodeAssessmentForBreast6161 = "1.2.840.10008.6.1.475";

    /** Assessment of Metastasis for Breast (6162) - Context Group Name */
    public static final String AssessmentOfMetastasisForBreast6162 = "1.2.840.10008.6.1.476";

    /** Menstrual Cycle Phase (6163) - Context Group Name */
    public static final String MenstrualCyclePhase6163 = "1.2.840.10008.6.1.477";

    /** Time Intervals (6164) - Context Group Name */
    public static final String TimeIntervals6164 = "1.2.840.10008.6.1.478";

    /** Breast Linear Measurements (6165) - Context Group Name */
    public static final String BreastLinearMeasurements6165 = "1.2.840.10008.6.1.479";

    /** CAD Geometry Secondary Graphical Representation (6166) - Context Group Name */
    public static final String CADGeometrySecondaryGraphicalRepresentation6166 = "1.2.840.10008.6.1.480";

    /** Diagnostic Imaging Report Document Titles (7000) - Context Group Name */
    public static final String DiagnosticImagingReportDocumentTitles7000 = "1.2.840.10008.6.1.481";

    /** Diagnostic Imaging Report Headings (7001) - Context Group Name */
    public static final String DiagnosticImagingReportHeadings7001 = "1.2.840.10008.6.1.482";

    /** Diagnostic Imaging Report Elements (7002) - Context Group Name */
    public static final String DiagnosticImagingReportElements7002 = "1.2.840.10008.6.1.483";

    /** Diagnostic Imaging Report Purposes of Reference (7003) - Context Group Name */
    public static final String DiagnosticImagingReportPurposesOfReference7003 = "1.2.840.10008.6.1.484";

    /** Waveform Purposes of Reference (7004) - Context Group Name */
    public static final String WaveformPurposesOfReference7004 = "1.2.840.10008.6.1.485";

    /** Contributing Equipment Purposes of Reference (7005) - Context Group Name */
    public static final String ContributingEquipmentPurposesOfReference7005 = "1.2.840.10008.6.1.486";

    /** SR Document Purposes of Reference (7006) - Context Group Name */
    public static final String SRDocumentPurposesOfReference7006 = "1.2.840.10008.6.1.487";

    /** Signature Purpose (7007) - Context Group Name */
    public static final String SignaturePurpose7007 = "1.2.840.10008.6.1.488";

    /** Media Import (7008) - Context Group Name */
    public static final String MediaImport7008 = "1.2.840.10008.6.1.489";

    /** Key Object Selection Document Title (7010) - Context Group Name */
    public static final String KeyObjectSelectionDocumentTitle7010 = "1.2.840.10008.6.1.490";

    /** Rejected for Quality Reasons (7011) - Context Group Name */
    public static final String RejectedForQualityReasons7011 = "1.2.840.10008.6.1.491";

    /** Best In Set (7012) - Context Group Name */
    public static final String BestInSet7012 = "1.2.840.10008.6.1.492";

    /** Document Titles (7020) - Context Group Name */
    public static final String DocumentTitles7020 = "1.2.840.10008.6.1.493";

    /** RCS Registration Method Type (7100) - Context Group Name */
    public static final String RCSRegistrationMethodType7100 = "1.2.840.10008.6.1.494";

    /** Brain Atlas Fiducials (7101) - Context Group Name */
    public static final String BrainAtlasFiducials7101 = "1.2.840.10008.6.1.495";

    /** Segmentation Property Categories (7150) - Context Group Name */
    public static final String SegmentationPropertyCategories7150 = "1.2.840.10008.6.1.496";

    /** Segmentation Property Types (7151) - Context Group Name */
    public static final String SegmentationPropertyTypes7151 = "1.2.840.10008.6.1.497";

    /** Cardiac Structure Segmentation Types (7152) - Context Group Name */
    public static final String CardiacStructureSegmentationTypes7152 = "1.2.840.10008.6.1.498";

    /** Brain Tissue Segmentation Types (7153) - Context Group Name */
    public static final String BrainTissueSegmentationTypes7153 = "1.2.840.10008.6.1.499";

    /** Abdominal Organ Segmentation Types (7154) - Context Group Name */
    public static final String AbdominalOrganSegmentationTypes7154 = "1.2.840.10008.6.1.500";

    /** Thoracic Tissue Segmentation Types (7155) - Context Group Name */
    public static final String ThoracicTissueSegmentationTypes7155 = "1.2.840.10008.6.1.501";

    /** Vascular Tissue Segmentation Types (7156) - Context Group Name */
    public static final String VascularTissueSegmentationTypes7156 = "1.2.840.10008.6.1.502";

    /** Device Segmentation Types (7157) - Context Group Name */
    public static final String DeviceSegmentationTypes7157 = "1.2.840.10008.6.1.503";

    /** Artifact Segmentation Types (7158) - Context Group Name */
    public static final String ArtifactSegmentationTypes7158 = "1.2.840.10008.6.1.504";

    /** Lesion Segmentation Types (7159) - Context Group Name */
    public static final String LesionSegmentationTypes7159 = "1.2.840.10008.6.1.505";

    /** Pelvic Organ Segmentation Types (7160) - Context Group Name */
    public static final String PelvicOrganSegmentationTypes7160 = "1.2.840.10008.6.1.506";

    /** Physiology Segmentation Types (7161) - Context Group Name */
    public static final String PhysiologySegmentationTypes7161 = "1.2.840.10008.6.1.507";

    /** Referenced Image Purposes of Reference (7201) - Context Group Name */
    public static final String ReferencedImagePurposesOfReference7201 = "1.2.840.10008.6.1.508";

    /** Source Image Purposes of Reference (7202) - Context Group Name */
    public static final String SourceImagePurposesOfReference7202 = "1.2.840.10008.6.1.509";

    /** Image Derivation (7203) - Context Group Name */
    public static final String ImageDerivation7203 = "1.2.840.10008.6.1.510";

    /** Purpose Of Reference to Alternate Representation (7205) - Context Group Name */
    public static final String PurposeOfReferenceToAlternateRepresentation7205 = "1.2.840.10008.6.1.511";

    /** Related Series Purposes Of Reference (7210) - Context Group Name */
    public static final String RelatedSeriesPurposesOfReference7210 = "1.2.840.10008.6.1.512";

    /** Multi-frame Subset Type (7250) - Context Group Name */
    public static final String MultiFrameSubsetType7250 = "1.2.840.10008.6.1.513";

    /** Person Roles (7450) - Context Group Name */
    public static final String PersonRoles7450 = "1.2.840.10008.6.1.514";

    /** Family Member (7451) - Context Group Name */
    public static final String FamilyMember7451 = "1.2.840.10008.6.1.515";

    /** Organizational Roles (7452) - Context Group Name */
    public static final String OrganizationalRoles7452 = "1.2.840.10008.6.1.516";

    /** Performing Roles (7453) - Context Group Name */
    public static final String PerformingRoles7453 = "1.2.840.10008.6.1.517";

    /** Species (7454) - Context Group Name */
    public static final String Species7454 = "1.2.840.10008.6.1.518";

    /** Sex (7455) - Context Group Name */
    public static final String Sex7455 = "1.2.840.10008.6.1.519";

    /** Units of Measure for Age (7456) - Context Group Name */
    public static final String UnitsOfMeasureForAge7456 = "1.2.840.10008.6.1.520";

    /** Units of Linear Measurement (7460) - Context Group Name */
    public static final String UnitsOfLinearMeasurement7460 = "1.2.840.10008.6.1.521";

    /** Units of Area Measurement (7461) - Context Group Name */
    public static final String UnitsOfAreaMeasurement7461 = "1.2.840.10008.6.1.522";

    /** Units of Volume Measurement (7462) - Context Group Name */
    public static final String UnitsOfVolumeMeasurement7462 = "1.2.840.10008.6.1.523";

    /** Linear Measurements (7470) - Context Group Name */
    public static final String LinearMeasurements7470 = "1.2.840.10008.6.1.524";

    /** Area Measurements (7471) - Context Group Name */
    public static final String AreaMeasurements7471 = "1.2.840.10008.6.1.525";

    /** Volume Measurements (7472) - Context Group Name */
    public static final String VolumeMeasurements7472 = "1.2.840.10008.6.1.526";

    /** General Area Calculation Methods (7473) - Context Group Name */
    public static final String GeneralAreaCalculationMethods7473 = "1.2.840.10008.6.1.527";

    /** General Volume Calculation Methods (7474) - Context Group Name */
    public static final String GeneralVolumeCalculationMethods7474 = "1.2.840.10008.6.1.528";

    /** Breed (7480) - Context Group Name */
    public static final String Breed7480 = "1.2.840.10008.6.1.529";

    /** Breed Registry (7481) - Context Group Name */
    public static final String BreedRegistry7481 = "1.2.840.10008.6.1.530";

    /** General Purpose Workitem Definition (9231) - Context Group Name */
    public static final String GeneralPurposeWorkitemDefinition9231 = "1.2.840.10008.6.1.531";

    /** Non-DICOM Output Types (9232) - Context Group Name */
    public static final String NonDICOMOutputTypes9232 = "1.2.840.10008.6.1.532";

    /** Procedure Discontinuation Reasons (9300) - Context Group Name */
    public static final String ProcedureDiscontinuationReasons9300 = "1.2.840.10008.6.1.533";

    /** Scope of Accumulation (10000) - Context Group Name */
    public static final String ScopeOfAccumulation10000 = "1.2.840.10008.6.1.534";

    /** UID Types (10001) - Context Group Name */
    public static final String UIDTypes10001 = "1.2.840.10008.6.1.535";

    /** Irradiation Event Types (10002) - Context Group Name */
    public static final String IrradiationEventTypes10002 = "1.2.840.10008.6.1.536";

    /** Equipment Plane Identification (10003) - Context Group Name */
    public static final String EquipmentPlaneIdentification10003 = "1.2.840.10008.6.1.537";

    /** Fluoro Modes (10004) - Context Group Name */
    public static final String FluoroModes10004 = "1.2.840.10008.6.1.538";

    /** X-Ray Filter Materials (10006) - Context Group Name */
    public static final String XRayFilterMaterials10006 = "1.2.840.10008.6.1.539";

    /** X-Ray Filter Types (10007) - Context Group Name */
    public static final String XRayFilterTypes10007 = "1.2.840.10008.6.1.540";

    /** Dose Related Distance Measurements (10008) - Context Group Name */
    public static final String DoseRelatedDistanceMeasurements10008 = "1.2.840.10008.6.1.541";

    /** Measured/Calculated (10009) - Context Group Name */
    public static final String MeasuredCalculated10009 = "1.2.840.10008.6.1.542";

    /** Dose Measurement Devices (10010) - Context Group Name */
    public static final String DoseMeasurementDevices10010 = "1.2.840.10008.6.1.543";

    /** Effective Dose Evaluation Method (10011) - Context Group Name */
    public static final String EffectiveDoseEvaluationMethod10011 = "1.2.840.10008.6.1.544";

    /** CT Acquisition Type (10013) - Context Group Name */
    public static final String CTAcquisitionType10013 = "1.2.840.10008.6.1.545";

    /** Contrast Imaging Technique (10014) - Context Group Name */
    public static final String ContrastImagingTechnique10014 = "1.2.840.10008.6.1.546";

    /** CT Dose Reference Authorities (10015) - Context Group Name */
    public static final String CTDoseReferenceAuthorities10015 = "1.2.840.10008.6.1.547";

    /** Anode Target Material (10016) - Context Group Name */
    public static final String AnodeTargetMaterial10016 = "1.2.840.10008.6.1.548";

    /** X-Ray Grid (10017) - Context Group Name */
    public static final String XRayGrid10017 = "1.2.840.10008.6.1.549";

    /** Ultrasound Protocol Types (12001) - Context Group Name */
    public static final String UltrasoundProtocolTypes12001 = "1.2.840.10008.6.1.550";

    /** Ultrasound Protocol Stage Types (12002) - Context Group Name */
    public static final String UltrasoundProtocolStageTypes12002 = "1.2.840.10008.6.1.551";

    /** OB-GYN Dates (12003) - Context Group Name */
    public static final String OBGYNDates12003 = "1.2.840.10008.6.1.552";

    /** Fetal Biometry Ratios (12004) - Context Group Name */
    public static final String FetalBiometryRatios12004 = "1.2.840.10008.6.1.553";

    /** Fetal Biometry Measurements (12005) - Context Group Name */
    public static final String FetalBiometryMeasurements12005 = "1.2.840.10008.6.1.554";

    /** Fetal Long Bones Biometry Measurements (12006) - Context Group Name */
    public static final String FetalLongBonesBiometryMeasurements12006 = "1.2.840.10008.6.1.555";

    /** Fetal Cranium (12007) - Context Group Name */
    public static final String FetalCranium12007 = "1.2.840.10008.6.1.556";

    /** OB-GYN Amniotic Sac (12008) - Context Group Name */
    public static final String OBGYNAmnioticSac12008 = "1.2.840.10008.6.1.557";

    /** Early Gestation Biometry Measurements (12009) - Context Group Name */
    public static final String EarlyGestationBiometryMeasurements12009 = "1.2.840.10008.6.1.558";

    /** Ultrasound Pelvis and Uterus (12011) - Context Group Name */
    public static final String UltrasoundPelvisAndUterus12011 = "1.2.840.10008.6.1.559";

    /** OB Equations and Tables (12012) - Context Group Name */
    public static final String OBEquationsAndTables12012 = "1.2.840.10008.6.1.560";

    /** Gestational Age Equations and Tables (12013) - Context Group Name */
    public static final String GestationalAgeEquationsAndTables12013 = "1.2.840.10008.6.1.561";

    /** OB Fetal Body Weight Equations and Tables (12014) - Context Group Name */
    public static final String OBFetalBodyWeightEquationsAndTables12014 = "1.2.840.10008.6.1.562";

    /** Fetal Growth Equations and Tables (12015) - Context Group Name */
    public static final String FetalGrowthEquationsAndTables12015 = "1.2.840.10008.6.1.563";

    /** Estimated Fetal Weight Percentile Equations and Tables (12016) - Context Group Name */
    public static final String EstimatedFetalWeightPercentileEquationsAndTables12016 = "1.2.840.10008.6.1.564";

    /** Growth Distribution Rank (12017) - Context Group Name */
    public static final String GrowthDistributionRank12017 = "1.2.840.10008.6.1.565";

    /** OB-GYN Summary (12018) - Context Group Name */
    public static final String OBGYNSummary12018 = "1.2.840.10008.6.1.566";

    /** OB-GYN Fetus Summary (12019) - Context Group Name */
    public static final String OBGYNFetusSummary12019 = "1.2.840.10008.6.1.567";

    /** Vascular Summary (12101) - Context Group Name */
    public static final String VascularSummary12101 = "1.2.840.10008.6.1.568";

    /** Temporal Periods Relating to Procedure or Therapy (12102) - Context Group Name */
    public static final String TemporalPeriodsRelatingToProcedureOrTherapy12102 = "1.2.840.10008.6.1.569";

    /** Vascular Ultrasound Anatomic Location (12103) - Context Group Name */
    public static final String VascularUltrasoundAnatomicLocation12103 = "1.2.840.10008.6.1.570";

    /** Extracranial Arteries (12104) - Context Group Name */
    public static final String ExtracranialArteries12104 = "1.2.840.10008.6.1.571";

    /** Intracranial Cerebral Vessels (12105) - Context Group Name */
    public static final String IntracranialCerebralVessels12105 = "1.2.840.10008.6.1.572";

    /** Intracranial Cerebral Vessels (unilateral) (12106) - Context Group Name */
    public static final String IntracranialCerebralVesselsUnilateral12106 = "1.2.840.10008.6.1.573";

    /** Upper Extremity Arteries (12107) - Context Group Name */
    public static final String UpperExtremityArteries12107 = "1.2.840.10008.6.1.574";

    /** Upper Extremity Veins (12108) - Context Group Name */
    public static final String UpperExtremityVeins12108 = "1.2.840.10008.6.1.575";

    /** Lower Extremity Arteries (12109) - Context Group Name */
    public static final String LowerExtremityArteries12109 = "1.2.840.10008.6.1.576";

    /** Lower Extremity Veins (12110) - Context Group Name */
    public static final String LowerExtremityVeins12110 = "1.2.840.10008.6.1.577";

    /** Abdominal Arteries (lateral) (12111) - Context Group Name */
    public static final String AbdominalArteriesLateral12111 = "1.2.840.10008.6.1.578";

    /** Abdominal Arteries (unilateral) (12112) - Context Group Name */
    public static final String AbdominalArteriesUnilateral12112 = "1.2.840.10008.6.1.579";

    /** Abdominal Veins (lateral) (12113) - Context Group Name */
    public static final String AbdominalVeinsLateral12113 = "1.2.840.10008.6.1.580";

    /** Abdominal Veins (unilateral) (12114) - Context Group Name */
    public static final String AbdominalVeinsUnilateral12114 = "1.2.840.10008.6.1.581";

    /** Renal Vessels (12115) - Context Group Name */
    public static final String RenalVessels12115 = "1.2.840.10008.6.1.582";

    /** Vessel Segment Modifiers (12116) - Context Group Name */
    public static final String VesselSegmentModifiers12116 = "1.2.840.10008.6.1.583";

    /** Vessel Branch Modifiers (12117) - Context Group Name */
    public static final String VesselBranchModifiers12117 = "1.2.840.10008.6.1.584";

    /** Vascular Ultrasound Property (12119) - Context Group Name */
    public static final String VascularUltrasoundProperty12119 = "1.2.840.10008.6.1.585";

    /** Blood Velocity Measurements by Ultrasound (12120) - Context Group Name */
    public static final String BloodVelocityMeasurementsByUltrasound12120 = "1.2.840.10008.6.1.586";

    /** Vascular Indices and Ratios (12121) - Context Group Name */
    public static final String VascularIndicesAndRatios12121 = "1.2.840.10008.6.1.587";

    /** Other Vascular Properties (12122) - Context Group Name */
    public static final String OtherVascularProperties12122 = "1.2.840.10008.6.1.588";

    /** Carotid Ratios (12123) - Context Group Name */
    public static final String CarotidRatios12123 = "1.2.840.10008.6.1.589";

    /** Renal Ratios (12124) - Context Group Name */
    public static final String RenalRatios12124 = "1.2.840.10008.6.1.590";

    /** Pelvic Vasculature Anatomical Location (12140) - Context Group Name */
    public static final String PelvicVasculatureAnatomicalLocation12140 = "1.2.840.10008.6.1.591";

    /** Fetal Vasculature Anatomical Location (12141) - Context Group Name */
    public static final String FetalVasculatureAnatomicalLocation12141 = "1.2.840.10008.6.1.592";

    /** Echocardiography Left Ventricle (12200) - Context Group Name */
    public static final String EchocardiographyLeftVentricle12200 = "1.2.840.10008.6.1.593";

    /** Left Ventricle Linear (12201) - Context Group Name */
    public static final String LeftVentricleLinear12201 = "1.2.840.10008.6.1.594";

    /** Left Ventricle Volume (12202) - Context Group Name */
    public static final String LeftVentricleVolume12202 = "1.2.840.10008.6.1.595";

    /** Left Ventricle Other (12203) - Context Group Name */
    public static final String LeftVentricleOther12203 = "1.2.840.10008.6.1.596";

    /** Echocardiography Right Ventricle (12204) - Context Group Name */
    public static final String EchocardiographyRightVentricle12204 = "1.2.840.10008.6.1.597";

    /** Echocardiography Left Atrium (12205) - Context Group Name */
    public static final String EchocardiographyLeftAtrium12205 = "1.2.840.10008.6.1.598";

    /** Echocardiography Right Atrium (12206) - Context Group Name */
    public static final String EchocardiographyRightAtrium12206 = "1.2.840.10008.6.1.599";

    /** Echocardiography Mitral Valve (12207) - Context Group Name */
    public static final String EchocardiographyMitralValve12207 = "1.2.840.10008.6.1.600";

    /** Echocardiography Tricuspid Valve (12208) - Context Group Name */
    public static final String EchocardiographyTricuspidValve12208 = "1.2.840.10008.6.1.601";

    /** Echocardiography Pulmonic Valve (12209) - Context Group Name */
    public static final String EchocardiographyPulmonicValve12209 = "1.2.840.10008.6.1.602";

    /** Echocardiography Pulmonary Artery (12210) - Context Group Name */
    public static final String EchocardiographyPulmonaryArtery12210 = "1.2.840.10008.6.1.603";

    /** Echocardiography Aortic Valve (12211) - Context Group Name */
    public static final String EchocardiographyAorticValve12211 = "1.2.840.10008.6.1.604";

    /** Echocardiography Aorta (12212) - Context Group Name */
    public static final String EchocardiographyAorta12212 = "1.2.840.10008.6.1.605";

    /** Echocardiography Pulmonary Veins (12214) - Context Group Name */
    public static final String EchocardiographyPulmonaryVeins12214 = "1.2.840.10008.6.1.606";

    /** Echocardiography Vena Cavae (12215) - Context Group Name */
    public static final String EchocardiographyVenaCavae12215 = "1.2.840.10008.6.1.607";

    /** Echocardiography Hepatic Veins (12216) - Context Group Name */
    public static final String EchocardiographyHepaticVeins12216 = "1.2.840.10008.6.1.608";

    /** Echocardiography Cardiac Shunt (12217) - Context Group Name */
    public static final String EchocardiographyCardiacShunt12217 = "1.2.840.10008.6.1.609";

    /** Echocardiography Congenital (12218) - Context Group Name */
    public static final String EchocardiographyCongenital12218 = "1.2.840.10008.6.1.610";

    /** Pulmonary Vein Modifiers (12219) - Context Group Name */
    public static final String PulmonaryVeinModifiers12219 = "1.2.840.10008.6.1.611";

    /** Echocardiography Common Measurements (12220) - Context Group Name */
    public static final String EchocardiographyCommonMeasurements12220 = "1.2.840.10008.6.1.612";

    /** Flow Direction (12221) - Context Group Name */
    public static final String FlowDirection12221 = "1.2.840.10008.6.1.613";

    /** Orifice Flow Properties (12222) - Context Group Name */
    public static final String OrificeFlowProperties12222 = "1.2.840.10008.6.1.614";

    /** Echocardiography Stroke Volume Origin (12223) - Context Group Name */
    public static final String EchocardiographyStrokeVolumeOrigin12223 = "1.2.840.10008.6.1.615";

    /** Ultrasound Image Modes (12224) - Context Group Name */
    public static final String UltrasoundImageModes12224 = "1.2.840.10008.6.1.616";

    /** Echocardiography Image View (12226) - Context Group Name */
    public static final String EchocardiographyImageView12226 = "1.2.840.10008.6.1.617";

    /** Echocardiography Measurement Method (12227) - Context Group Name */
    public static final String EchocardiographyMeasurementMethod12227 = "1.2.840.10008.6.1.618";

    /** Echocardiography Volume Methods (12228) - Context Group Name */
    public static final String EchocardiographyVolumeMethods12228 = "1.2.840.10008.6.1.619";

    /** Echocardiography Area Methods (12229) - Context Group Name */
    public static final String EchocardiographyAreaMethods12229 = "1.2.840.10008.6.1.620";

    /** Gradient Methods (12230) - Context Group Name */
    public static final String GradientMethods12230 = "1.2.840.10008.6.1.621";

    /** Volume Flow Methods (12231) - Context Group Name */
    public static final String VolumeFlowMethods12231 = "1.2.840.10008.6.1.622";

    /** Myocardium Mass Methods (12232) - Context Group Name */
    public static final String MyocardiumMassMethods12232 = "1.2.840.10008.6.1.623";

    /** Cardiac Phase (12233) - Context Group Name */
    public static final String CardiacPhase12233 = "1.2.840.10008.6.1.624";

    /** Respiration State (12234) - Context Group Name */
    public static final String RespirationState12234 = "1.2.840.10008.6.1.625";

    /** Mitral Valve Anatomic Sites (12235) - Context Group Name */
    public static final String MitralValveAnatomicSites12235 = "1.2.840.10008.6.1.626";

    /** Echo Anatomic Sites (12236) - Context Group Name */
    public static final String EchoAnatomicSites12236 = "1.2.840.10008.6.1.627";

    /** Echocardiography Anatomic Site Modifiers (12237) - Context Group Name */
    public static final String EchocardiographyAnatomicSiteModifiers12237 = "1.2.840.10008.6.1.628";

    /** Wall Motion Scoring Schemes (12238) - Context Group Name */
    public static final String WallMotionScoringSchemes12238 = "1.2.840.10008.6.1.629";

    /** Cardiac Output Properties (12239) - Context Group Name */
    public static final String CardiacOutputProperties12239 = "1.2.840.10008.6.1.630";

    /** Left Ventricle Area (12240) - Context Group Name */
    public static final String LeftVentricleArea12240 = "1.2.840.10008.6.1.631";

    /** Tricuspid Valve Finding Sites (12241) - Context Group Name */
    public static final String TricuspidValveFindingSites12241 = "1.2.840.10008.6.1.632";

    /** Aortic Valve Finding Sites (12242) - Context Group Name */
    public static final String AorticValveFindingSites12242 = "1.2.840.10008.6.1.633";

    /** Left Ventricle Finding Sites (12243) - Context Group Name */
    public static final String LeftVentricleFindingSites12243 = "1.2.840.10008.6.1.634";

    /** Congenital Finding Sites (12244) - Context Group Name */
    public static final String CongenitalFindingSites12244 = "1.2.840.10008.6.1.635";

    /** Surface Processing Algorithm Families (7162) - Context Group Name */
    public static final String SurfaceProcessingAlgorithmFamilies7162 = "1.2.840.10008.6.1.636";

    /** Stress Test Procedure Phases (3207) - Context Group Name */
    public static final String StressTestProcedurePhases3207 = "1.2.840.10008.6.1.637";

    /** Stages (3778) - Context Group Name */
    public static final String Stages3778 = "1.2.840.10008.6.1.638";

    /** S-M-L Size Descriptor (252) - Context Group Name */
    public static final String SMLSizeDescriptor252 = "1.2.840.10008.6.1.735";

    /** Major Coronary Arteries (3016) - Context Group Name */
    public static final String MajorCoronaryArteries3016 = "1.2.840.10008.6.1.736";

    /** Units of Radioactivity (3083) - Context Group Name */
    public static final String UnitsOfRadioactivity3083 = "1.2.840.10008.6.1.737";

    /** Rest-Stress (3102) - Context Group Name */
    public static final String RestStress3102 = "1.2.840.10008.6.1.738";

    /** PET Cardiology Protocols (3106) - Context Group Name */
    public static final String PETCardiologyProtocols3106 = "1.2.840.10008.6.1.739";

    /** PET Cardiology Radiopharmaceuticals (3107) - Context Group Name */
    public static final String PETCardiologyRadiopharmaceuticals3107 = "1.2.840.10008.6.1.740";

    /** NM/PET Procedures (3108) - Context Group Name */
    public static final String NMPETProcedures3108 = "1.2.840.10008.6.1.741";

    /** Nuclear Cardiology Protocols (3110) - Context Group Name */
    public static final String NuclearCardiologyProtocols3110 = "1.2.840.10008.6.1.742";

    /** Nuclear Cardiology Radiopharmaceuticals (3111) - Context Group Name */
    public static final String NuclearCardiologyRadiopharmaceuticals3111 = "1.2.840.10008.6.1.743";

    /** Attenuation Correction (3112) - Context Group Name */
    public static final String AttenuationCorrection3112 = "1.2.840.10008.6.1.744";

    /** Types of Perfusion Defects (3113) - Context Group Name */
    public static final String TypesOfPerfusionDefects3113 = "1.2.840.10008.6.1.745";

    /** Study Quality (3114) - Context Group Name */
    public static final String StudyQuality3114 = "1.2.840.10008.6.1.746";

    /** Stress Imaging Quality Issues (3115) - Context Group Name */
    public static final String StressImagingQualityIssues3115 = "1.2.840.10008.6.1.747";

    /** NM Extracardiac Findings (3116) - Context Group Name */
    public static final String NMExtracardiacFindings3116 = "1.2.840.10008.6.1.748";

    /** Attenuation Correction Methods (3117) - Context Group Name */
    public static final String AttenuationCorrectionMethods3117 = "1.2.840.10008.6.1.749";

    /** Level of Risk (3118) - Context Group Name */
    public static final String LevelOfRisk3118 = "1.2.840.10008.6.1.750";

    /** LV Function (3119) - Context Group Name */
    public static final String LVFunction3119 = "1.2.840.10008.6.1.751";

    /** Perfusion Findings (3120) - Context Group Name */
    public static final String PerfusionFindings3120 = "1.2.840.10008.6.1.752";

    /** Perfusion Morphology (3121) - Context Group Name */
    public static final String PerfusionMorphology3121 = "1.2.840.10008.6.1.753";

    /** Ventricular Enlargement (3122) - Context Group Name */
    public static final String VentricularEnlargement3122 = "1.2.840.10008.6.1.754";

    /** Stress Test Procedure (3200) - Context Group Name */
    public static final String StressTestProcedure3200 = "1.2.840.10008.6.1.755";

    /** Indications for Stress Test (3201) - Context Group Name */
    public static final String IndicationsForStressTest3201 = "1.2.840.10008.6.1.756";

    /** Chest Pain (3202) - Context Group Name */
    public static final String ChestPain3202 = "1.2.840.10008.6.1.757";

    /** Exerciser Device (3203) - Context Group Name */
    public static final String ExerciserDevice3203 = "1.2.840.10008.6.1.758";

    /** Stress Agents (3204) - Context Group Name */
    public static final String StressAgents3204 = "1.2.840.10008.6.1.759";

    /** Indications for Pharmacological Stress Test (3205) - Context Group Name */
    public static final String IndicationsForPharmacologicalStressTest3205 = "1.2.840.10008.6.1.760";

    /** Non-invasive Cardiac Imaging Procedures (3206) - Context Group Name */
    public static final String NonInvasiveCardiacImagingProcedures3206 = "1.2.840.10008.6.1.761";

    /** Summary Codes Exercise ECG (3208) - Context Group Name */
    public static final String SummaryCodesExerciseECG3208 = "1.2.840.10008.6.1.763";

    /** Summary Codes Stress Imaging (3209) - Context Group Name */
    public static final String SummaryCodesStressImaging3209 = "1.2.840.10008.6.1.764";

    /** Speed of Response (3210) - Context Group Name */
    public static final String SpeedOfResponse3210 = "1.2.840.10008.6.1.765";

    /** BP Response (3211) - Context Group Name */
    public static final String BPResponse3211 = "1.2.840.10008.6.1.766";

    /** Treadmill Speed (3212) - Context Group Name */
    public static final String TreadmillSpeed3212 = "1.2.840.10008.6.1.767";

    /** Stress Hemodynamic Findings (3213) - Context Group Name */
    public static final String StressHemodynamicFindings3213 = "1.2.840.10008.6.1.768";

    /** Perfusion Finding Method (3215) - Context Group Name */
    public static final String PerfusionFindingMethod3215 = "1.2.840.10008.6.1.769";

    /** Comparison Finding (3217) - Context Group Name */
    public static final String ComparisonFinding3217 = "1.2.840.10008.6.1.770";

    /** Stress Symptoms (3220) - Context Group Name */
    public static final String StressSymptoms3220 = "1.2.840.10008.6.1.771";

    /** Stress Test Termination Reasons (3221) - Context Group Name */
    public static final String StressTestTerminationReasons3221 = "1.2.840.10008.6.1.772";

    /** QTc Measurements (3227) - Context Group Name */
    public static final String QTcMeasurements3227 = "1.2.840.10008.6.1.773";

    /** ECG Timing Measurements (3228) - Context Group Name */
    public static final String ECGTimingMeasurements3228 = "1.2.840.10008.6.1.774";

    /** ECG Axis Measurements (3229) - Context Group Name */
    public static final String ECGAxisMeasurements3229 = "1.2.840.10008.6.1.775";

    /** ECG Findings (3230) - Context Group Name */
    public static final String ECGFindings3230 = "1.2.840.10008.6.1.776";

    /** ST Segment Findings (3231) - Context Group Name */
    public static final String STSegmentFindings3231 = "1.2.840.10008.6.1.777";

    /** ST Segment Location (3232) - Context Group Name */
    public static final String STSegmentLocation3232 = "1.2.840.10008.6.1.778";

    /** ST Segment Morphology (3233) - Context Group Name */
    public static final String STSegmentMorphology3233 = "1.2.840.10008.6.1.779";

    /** Ectopic Beat Morphology (3234) - Context Group Name */
    public static final String EctopicBeatMorphology3234 = "1.2.840.10008.6.1.780";

    /** Perfusion Comparison Findings (3235) - Context Group Name */
    public static final String PerfusionComparisonFindings3235 = "1.2.840.10008.6.1.781";

    /** Tolerance Comparison Findings (3236) - Context Group Name */
    public static final String ToleranceComparisonFindings3236 = "1.2.840.10008.6.1.782";

    /** Wall Motion Comparison Findings (3237) - Context Group Name */
    public static final String WallMotionComparisonFindings3237 = "1.2.840.10008.6.1.783";

    /** Stress Scoring Scales (3238) - Context Group Name */
    public static final String StressScoringScales3238 = "1.2.840.10008.6.1.784";

    /** Perceived Exertion Scales (3239) - Context Group Name */
    public static final String PerceivedExertionScales3239 = "1.2.840.10008.6.1.785";

    /** Ventricle Identification (3463) - Context Group Name */
    public static final String VentricleIdentification3463 = "1.2.840.10008.6.1.786";

    /** Colon Overall Assessment (6200) - Context Group Name */
    public static final String ColonOverallAssessment6200 = "1.2.840.10008.6.1.787";

    /** Colon Finding or Feature (6201) - Context Group Name */
    public static final String ColonFindingOrFeature6201 = "1.2.840.10008.6.1.788";

    /** Colon Finding or Feature Modifier (6202) - Context Group Name */
    public static final String ColonFindingOrFeatureModifier6202 = "1.2.840.10008.6.1.789";

    /** Colon Non-Lesion Object Type (6203) - Context Group Name */
    public static final String ColonNonLesionObjectType6203 = "1.2.840.10008.6.1.790";

    /** Anatomic Non-Colon Findings (6204) - Context Group Name */
    public static final String AnatomicNonColonFindings6204 = "1.2.840.10008.6.1.791";

    /** Clockface Location for Colon (6205) - Context Group Name */
    public static final String ClockfaceLocationForColon6205 = "1.2.840.10008.6.1.792";

    /** Recumbent Patient Orientation for Colon (6206) - Context Group Name */
    public static final String RecumbentPatientOrientationForColon6206 = "1.2.840.10008.6.1.793";

    /** Colon Quantitative Temporal Difference Type (6207) - Context Group Name */
    public static final String ColonQuantitativeTemporalDifferenceType6207 = "1.2.840.10008.6.1.794";

    /** Colon Types of Quality Control Standard (6208) - Context Group Name */
    public static final String ColonTypesOfQualityControlStandard6208 = "1.2.840.10008.6.1.795";

    /** Colon Morphology Descriptor (6209) - Context Group Name */
    public static final String ColonMorphologyDescriptor6209 = "1.2.840.10008.6.1.796";

    /** Location in Intestinal Tract (6210) - Context Group Name */
    public static final String LocationInIntestinalTract6210 = "1.2.840.10008.6.1.797";

    /** Attenuation Coefficient Descriptors (6211) - Context Group Name */
    public static final String AttenuationCoefficientDescriptors6211 = "1.2.840.10008.6.1.798";

    /** Calculated Value for Colon Findings (6212) - Context Group Name */
    public static final String CalculatedValueForColonFindings6212 = "1.2.840.10008.6.1.799";

    /** Ophthalmic Horizontal Directions (4214) - Context Group Name */
    public static final String OphthalmicHorizontalDirections4214 = "1.2.840.10008.6.1.800";

    /** Ophthalmic Vertical Directions (4215) - Context Group Name */
    public static final String OphthalmicVerticalDirections4215 = "1.2.840.10008.6.1.801";

    /** Ophthalmic Visual Acuity Type (4216) - Context Group Name */
    public static final String OphthalmicVisualAcuityType4216 = "1.2.840.10008.6.1.802";

    /** Arterial Pulse Waveform (3004) - Context Group Name */
    public static final String ArterialPulseWaveform3004 = "1.2.840.10008.6.1.803";

    /** Respiration Waveform (3005) - Context Group Name */
    public static final String RespirationWaveform3005 = "1.2.840.10008.6.1.804";

    /** Ultrasound Contrast/Bolus Agents (12030) - Context Group Name */
    public static final String UltrasoundContrastBolusAgents12030 = "1.2.840.10008.6.1.805";

    /** Protocol Interval Events (12031) - Context Group Name */
    public static final String ProtocolIntervalEvents12031 = "1.2.840.10008.6.1.806";

    /** Transducer Scan Pattern (12032) - Context Group Name */
    public static final String TransducerScanPattern12032 = "1.2.840.10008.6.1.807";

    /** Ultrasound Transducer Geometry (12033) - Context Group Name */
    public static final String UltrasoundTransducerGeometry12033 = "1.2.840.10008.6.1.808";

    /** Ultrasound Transducer Beam Steering (12034) - Context Group Name */
    public static final String UltrasoundTransducerBeamSteering12034 = "1.2.840.10008.6.1.809";

    /** Ultrasound Transducer Application (12035) - Context Group Name */
    public static final String UltrasoundTransducerApplication12035 = "1.2.840.10008.6.1.810";

    /** Instance Availability Status (50) - Context Group Name */
    public static final String InstanceAvailabilityStatus50 = "1.2.840.10008.6.1.811";

    /** Modality PPS Discontinuation Reasons (9301) - Context Group Name */
    public static final String ModalityPPSDiscontinuationReasons9301 = "1.2.840.10008.6.1.812";

    /** Media Import PPS Discontinuation Reasons (9302) - Context Group Name */
    public static final String MediaImportPPSDiscontinuationReasons9302 = "1.2.840.10008.6.1.813";

    /** DX Anatomy Imaged for Animals (7482) - Context Group Name */
    public static final String DXAnatomyImagedForAnimals7482 = "1.2.840.10008.6.1.814";

    /** Common Anatomic Regions for Animals (7483) - Context Group Name */
    public static final String CommonAnatomicRegionsForAnimals7483 = "1.2.840.10008.6.1.815";

    /** DX View for Animals (7484) - Context Group Name */
    public static final String DXViewForAnimals7484 = "1.2.840.10008.6.1.816";

    /** Institutional Departments, Units and Services (7030) - Context Group Name */
    public static final String InstitutionalDepartmentsUnitsAndServices7030 = "1.2.840.10008.6.1.817";

    /** Purpose Of Reference to Predecessor Report (7009) - Context Group Name */
    public static final String PurposeOfReferenceToPredecessorReport7009 = "1.2.840.10008.6.1.818";

    /** Visual Fixation Quality During Acquisition (4220) - Context Group Name */
    public static final String VisualFixationQualityDuringAcquisition4220 = "1.2.840.10008.6.1.819";

    /** Visual Fixation Quality Problem (4221) - Context Group Name */
    public static final String VisualFixationQualityProblem4221 = "1.2.840.10008.6.1.820";

    /** Ophthalmic Macular Grid Problem (4222) - Context Group Name */
    public static final String OphthalmicMacularGridProblem4222 = "1.2.840.10008.6.1.821";

    /** Organizations (5002) - Context Group Name */
    public static final String Organizations5002 = "1.2.840.10008.6.1.822";

    /** Mixed Breeds (7486) - Context Group Name */
    public static final String MixedBreeds7486 = "1.2.840.10008.6.1.823";

    /** Broselow-Luten Pediatric Size Categories (7040) - Context Group Name */
    public static final String BroselowLutenPediatricSizeCategories7040 = "1.2.840.10008.6.1.824";

    /** Calcium Scoring Patient Size Categories (7042) - Context Group Name */
    public static final String CalciumScoringPatientSizeCategories7042 = "1.2.840.10008.6.1.825";

    /** Cardiac Ultrasound Report Titles (12245) - Context Group Name */
    public static final String CardiacUltrasoundReportTitles12245 = "1.2.840.10008.6.1.826";

    /** Cardiac Ultrasound Indication for Study (12246) - Context Group Name */
    public static final String CardiacUltrasoundIndicationForStudy12246 = "1.2.840.10008.6.1.827";

    /** Pediatric, Fetal and Congenital Cardiac Surgical Interventions (12247) - Context Group Name */
    public static final String PediatricFetalAndCongenitalCardiacSurgicalInterventions12247 = "1.2.840.10008.6.1.828";

    /** Cardiac Ultrasound Summary Codes (12248) - Context Group Name */
    public static final String CardiacUltrasoundSummaryCodes12248 = "1.2.840.10008.6.1.829";

    /** Cardiac Ultrasound Fetal Summary Codes (12249) - Context Group Name */
    public static final String CardiacUltrasoundFetalSummaryCodes12249 = "1.2.840.10008.6.1.830";

    /** Cardiac Ultrasound Common Linear Measurements (12250) - Context Group Name */
    public static final String CardiacUltrasoundCommonLinearMeasurements12250 = "1.2.840.10008.6.1.831";

    /** Cardiac Ultrasound Linear Valve Measurements (12251) - Context Group Name */
    public static final String CardiacUltrasoundLinearValveMeasurements12251 = "1.2.840.10008.6.1.832";

    /** Cardiac Ultrasound Cardiac Function (12252) - Context Group Name */
    public static final String CardiacUltrasoundCardiacFunction12252 = "1.2.840.10008.6.1.833";

    /** Cardiac Ultrasound Area Measurements (12253) - Context Group Name */
    public static final String CardiacUltrasoundAreaMeasurements12253 = "1.2.840.10008.6.1.834";

    /** Cardiac Ultrasound Hemodynamic Measurements (12254) - Context Group Name */
    public static final String CardiacUltrasoundHemodynamicMeasurements12254 = "1.2.840.10008.6.1.835";

    /** Cardiac Ultrasound Myocardium Measurements (12255) - Context Group Name */
    public static final String CardiacUltrasoundMyocardiumMeasurements12255 = "1.2.840.10008.6.1.836";

    /** Cardiac Ultrasound Common Linear Flow Measurements (12256) - Context Group Name */
    public static final String CardiacUltrasoundCommonLinearFlowMeasurements12256 = "1.2.840.10008.6.1.837";

    /** Cardiac Ultrasound Left Ventricle (12257) - Context Group Name */
    public static final String CardiacUltrasoundLeftVentricle12257 = "1.2.840.10008.6.1.838";

    /** Cardiac Ultrasound Right Ventricle (12258) - Context Group Name */
    public static final String CardiacUltrasoundRightVentricle12258 = "1.2.840.10008.6.1.839";

    /** Cardiac Ultrasound Ventricles Measurements (12259) - Context Group Name */
    public static final String CardiacUltrasoundVentriclesMeasurements12259 = "1.2.840.10008.6.1.840";

    /** Cardiac Ultrasound Pulmonary Artery (12260) - Context Group Name */
    public static final String CardiacUltrasoundPulmonaryArtery12260 = "1.2.840.10008.6.1.841";

    /** Cardiac Ultrasound Pulmonary Vein (12261) - Context Group Name */
    public static final String CardiacUltrasoundPulmonaryVein12261 = "1.2.840.10008.6.1.842";

    /** Cardiac Ultrasound Pulmonary Valve (12262) - Context Group Name */
    public static final String CardiacUltrasoundPulmonaryValve12262 = "1.2.840.10008.6.1.843";

    /** Cardiac Ultrasound Venous Return Pulmonary Measurements (12263) - Context Group Name */
    public static final String CardiacUltrasoundVenousReturnPulmonaryMeasurements12263 = "1.2.840.10008.6.1.844";

    /** Cardiac Ultrasound Venous Return Systemic Measurements (12264) - Context Group Name */
    public static final String CardiacUltrasoundVenousReturnSystemicMeasurements12264 = "1.2.840.10008.6.1.845";

    /** Cardiac Ultrasound Atria and Atrial Septum Measurements (12265) - Context Group Name */
    public static final String CardiacUltrasoundAtriaAndAtrialSeptumMeasurements12265 = "1.2.840.10008.6.1.846";

    /** Cardiac Ultrasound Mitral Valve (12266) - Context Group Name */
    public static final String CardiacUltrasoundMitralValve12266 = "1.2.840.10008.6.1.847";

    /** Cardiac Ultrasound Tricuspid Valve (12267) - Context Group Name */
    public static final String CardiacUltrasoundTricuspidValve12267 = "1.2.840.10008.6.1.848";

    /** Cardiac Ultrasound Atrioventricular Valves Measurements (12268) - Context Group Name */
    public static final String CardiacUltrasoundAtrioventricularValvesMeasurements12268 = "1.2.840.10008.6.1.849";

    /** Cardiac Ultrasound Interventricular Septum Measurements (12269) - Context Group Name */
    public static final String CardiacUltrasoundInterventricularSeptumMeasurements12269 = "1.2.840.10008.6.1.850";

    /** Cardiac Ultrasound Aortic Valve (12270) - Context Group Name */
    public static final String CardiacUltrasoundAorticValve12270 = "1.2.840.10008.6.1.851";

    /** Cardiac Ultrasound Outflow Tracts Measurements (12271) - Context Group Name */
    public static final String CardiacUltrasoundOutflowTractsMeasurements12271 = "1.2.840.10008.6.1.852";

    /** Cardiac Ultrasound Semilunar Valves, Annulus and Sinuses Measurements (12272) - Context Group Name */
    public static final String CardiacUltrasoundSemilunarValvesAnnulusAndSinusesMeasurements12272 = "1.2.840.10008.6.1.853";

    /** Cardiac Ultrasound Aortic Sinotubular Junction (12273) - Context Group Name */
    public static final String CardiacUltrasoundAorticSinotubularJunction12273 = "1.2.840.10008.6.1.854";

    /** Cardiac Ultrasound Aorta Measurements (12274) - Context Group Name */
    public static final String CardiacUltrasoundAortaMeasurements12274 = "1.2.840.10008.6.1.855";

    /** Cardiac Ultrasound Coronary Arteries Measurements (12275) - Context Group Name */
    public static final String CardiacUltrasoundCoronaryArteriesMeasurements12275 = "1.2.840.10008.6.1.856";

    /** Cardiac Ultrasound Aorto Pulmonary Connections Measurements (12276) - Context Group Name */
    public static final String CardiacUltrasoundAortoPulmonaryConnectionsMeasurements12276 = "1.2.840.10008.6.1.857";

    /** Cardiac Ultrasound Pericardium and Pleura Measurements (12277) - Context Group Name */
    public static final String CardiacUltrasoundPericardiumAndPleuraMeasurements12277 = "1.2.840.10008.6.1.858";

    /** Cardiac Ultrasound Fetal General Measurements (12279) - Context Group Name */
    public static final String CardiacUltrasoundFetalGeneralMeasurements12279 = "1.2.840.10008.6.1.859";

    /** Cardiac Ultrasound Target Sites (12280) - Context Group Name */
    public static final String CardiacUltrasoundTargetSites12280 = "1.2.840.10008.6.1.860";

    /** Cardiac Ultrasound Target Site Modifiers (12281) - Context Group Name */
    public static final String CardiacUltrasoundTargetSiteModifiers12281 = "1.2.840.10008.6.1.861";

    /** Cardiac Ultrasound Venous Return Systemic Finding Sites (12282) - Context Group Name */
    public static final String CardiacUltrasoundVenousReturnSystemicFindingSites12282 = "1.2.840.10008.6.1.862";

    /** Cardiac Ultrasound Venous Return Pulmonary Finding Sites (12283) - Context Group Name */
    public static final String CardiacUltrasoundVenousReturnPulmonaryFindingSites12283 = "1.2.840.10008.6.1.863";

    /** Cardiac Ultrasound Atria and Atrial Septum Finding Sites (12284) - Context Group Name */
    public static final String CardiacUltrasoundAtriaAndAtrialSeptumFindingSites12284 = "1.2.840.10008.6.1.864";

    /** Cardiac Ultrasound Atrioventricular Valves Findiing Sites (12285) - Context Group Name */
    public static final String CardiacUltrasoundAtrioventricularValvesFindiingSites12285 = "1.2.840.10008.6.1.865";

    /** Cardiac Ultrasound Interventricular Septum Finding Sites (12286) - Context Group Name */
    public static final String CardiacUltrasoundInterventricularSeptumFindingSites12286 = "1.2.840.10008.6.1.866";

    /** Cardiac Ultrasound Ventricles Finding Sites (12287) - Context Group Name */
    public static final String CardiacUltrasoundVentriclesFindingSites12287 = "1.2.840.10008.6.1.867";

    /** Cardiac Ultrasound Outflow Tracts Finding Sites (12288) - Context Group Name */
    public static final String CardiacUltrasoundOutflowTractsFindingSites12288 = "1.2.840.10008.6.1.868";

    /** Cardiac Ultrasound Semilunar Valves, Annulus and Sinuses Finding Sites (12289) - Context Group Name */
    public static final String CardiacUltrasoundSemilunarValvesAnnulusAndSinusesFindingSites12289 = "1.2.840.10008.6.1.869";

    /** Cardiac Ultrasound Pulmonary Arteries Finding Sites (12290) - Context Group Name */
    public static final String CardiacUltrasoundPulmonaryArteriesFindingSites12290 = "1.2.840.10008.6.1.870";

    /** Cardiac Ultrasound Aorta Finding Sites (12291) - Context Group Name */
    public static final String CardiacUltrasoundAortaFindingSites12291 = "1.2.840.10008.6.1.871";

    /** Cardiac Ultrasound Coronary Arteries Finding Sites (12292) - Context Group Name */
    public static final String CardiacUltrasoundCoronaryArteriesFindingSites12292 = "1.2.840.10008.6.1.872";

    /** Cardiac Ultrasound Aorto Pulmonary Connections Finding Sites (12293) - Context Group Name */
    public static final String CardiacUltrasoundAortoPulmonaryConnectionsFindingSites12293 = "1.2.840.10008.6.1.873";

    /** Cardiac Ultrasound Pericardium and Pleura Finding Sites (12294) - Context Group Name */
    public static final String CardiacUltrasoundPericardiumAndPleuraFindingSites12294 = "1.2.840.10008.6.1.874";

    /** Ophthalmic Ultrasound Axial Measurements Type (4230) - Context Group Name */
    public static final String OphthalmicUltrasoundAxialMeasurementsType4230 = "1.2.840.10008.6.1.876";

    /** Lens Status (4231) - Context Group Name */
    public static final String LensStatus4231 = "1.2.840.10008.6.1.877";

    /** Vitreous Status (4232) - Context Group Name */
    public static final String VitreousStatus4232 = "1.2.840.10008.6.1.878";

    /** Ophthalmic Axial Length Measurements Segment Names (4233) - Context Group Name */
    public static final String OphthalmicAxialLengthMeasurementsSegmentNames4233 = "1.2.840.10008.6.1.879";

    /** Refractive Surgery Types (4234) - Context Group Name */
    public static final String RefractiveSurgeryTypes4234 = "1.2.840.10008.6.1.880";

    /** Keratometry Descriptors (4235) - Context Group Name */
    public static final String KeratometryDescriptors4235 = "1.2.840.10008.6.1.881";

    /** IOL Calculation Formula (4236) - Context Group Name */
    public static final String IOLCalculationFormula4236 = "1.2.840.10008.6.1.882";

    /** Lens Constant Type (4237) - Context Group Name */
    public static final String LensConstantType4237 = "1.2.840.10008.6.1.883";

    /** Refractive Error Types (4238) - Context Group Name */
    public static final String RefractiveErrorTypes4238 = "1.2.840.10008.6.1.884";

    /** Anterior Chamber Depth Definition (4239) - Context Group Name */
    public static final String AnteriorChamberDepthDefinition4239 = "1.2.840.10008.6.1.885";

    /** Ophthalmic Measurement or Calculation Data Source (4240) - Context Group Name */
    public static final String OphthalmicMeasurementOrCalculationDataSource4240 = "1.2.840.10008.6.1.886";

    /** Ophthalmic Axial Length Selection Method (4241) - Context Group Name */
    public static final String OphthalmicAxialLengthSelectionMethod4241 = "1.2.840.10008.6.1.887";

    /** Ophthalmic Axial Length Quality Metric Type (4243) - Context Group Name */
    public static final String OphthalmicAxialLengthQualityMetricType4243 = "1.2.840.10008.6.1.889";

    /** Ophthalmic Agent Concentration Units (4244) - Context Group Name */
    public static final String OphthalmicAgentConcentrationUnits4244 = "1.2.840.10008.6.1.890";

    /** Functional condition present during acquisition (91) - Context Group Name */
    public static final String FunctionalConditionPresentDuringAcquisition91 = "1.2.840.10008.6.1.891";

    /** Joint position during acquisition (92) - Context Group Name */
    public static final String JointPositionDuringAcquisition92 = "1.2.840.10008.6.1.892";

    /** Joint positioning method (93) - Context Group Name */
    public static final String JointPositioningMethod93 = "1.2.840.10008.6.1.893";

    /** Physical force applied during acquisition (94) - Context Group Name */
    public static final String PhysicalForceAppliedDuringAcquisition94 = "1.2.840.10008.6.1.894";

    /** ECG Control Variables Numeric (3690) - Context Group Name */
    public static final String ECGControlVariablesNumeric3690 = "1.2.840.10008.6.1.895";

    /** ECG Control Variables Text (3691) - Context Group Name */
    public static final String ECGControlVariablesText3691 = "1.2.840.10008.6.1.896";

    /** WSI Referenced Image Purposes of Reference (8120) - Context Group Name */
    public static final String WSIReferencedImagePurposesOfReference8120 = "1.2.840.10008.6.1.897";

    /** WSI Microscopy Lens Type (8121) - Context Group Name */
    public static final String WSIMicroscopyLensType8121 = "1.2.840.10008.6.1.898";

    /** Microscopy Illuminator and Sensor Color (8122) - Context Group Name */
    public static final String MicroscopyIlluminatorAndSensorColor8122 = "1.2.840.10008.6.1.899";

    /** Microscopy Illumination Method (8123) - Context Group Name */
    public static final String MicroscopyIlluminationMethod8123 = "1.2.840.10008.6.1.900";

    /** Microscopy Filter (8124) - Context Group Name */
    public static final String MicroscopyFilter8124 = "1.2.840.10008.6.1.901";

    /** Microscopy Illuminator Type (8125) - Context Group Name */
    public static final String MicroscopyIlluminatorType8125 = "1.2.840.10008.6.1.902";

    /** Audit Event ID (400) - Context Group Name */
    public static final String AuditEventID400 = "1.2.840.10008.6.1.903";

    /** Audit Event Type Code (401) - Context Group Name */
    public static final String AuditEventTypeCode401 = "1.2.840.10008.6.1.904";

    /** Audit Active Participant Role ID Code (402) - Context Group Name */
    public static final String AuditActiveParticipantRoleIDCode402 = "1.2.840.10008.6.1.905";

    /** Security Alert Type Code (403) - Context Group Name */
    public static final String SecurityAlertTypeCode403 = "1.2.840.10008.6.1.906";

    /** Audit Participant Object ID Type Code (404) - Context Group Name */
    public static final String AuditParticipantObjectIDTypeCode404 = "1.2.840.10008.6.1.907";

    /** Media Type Code (405) - Context Group Name */
    public static final String MediaTypeCode405 = "1.2.840.10008.6.1.908";

    /** Visual Field Static Perimetry Test Patterns (4250) - Context Group Name */
    public static final String VisualFieldStaticPerimetryTestPatterns4250 = "1.2.840.10008.6.1.909";

    /** Visual Field Static Perimetry Test Strategies (4251) - Context Group Name */
    public static final String VisualFieldStaticPerimetryTestStrategies4251 = "1.2.840.10008.6.1.910";

    /** Visual Field Static Perimetry Screening Test Modes (4252) - Context Group Name */
    public static final String VisualFieldStaticPerimetryScreeningTestModes4252 = "1.2.840.10008.6.1.911";

    /** Visual Field Static Perimetry Fixation Strategy (4253) - Context Group Name */
    public static final String VisualFieldStaticPerimetryFixationStrategy4253 = "1.2.840.10008.6.1.912";

    /** Visual Field Static Perimetry Test Analysis Results (4254) - Context Group Name */
    public static final String VisualFieldStaticPerimetryTestAnalysisResults4254 = "1.2.840.10008.6.1.913";

    /** Visual Field Illumination Color (4255) - Context Group Name */
    public static final String VisualFieldIlluminationColor4255 = "1.2.840.10008.6.1.914";

    /** Visual Field Procedure Modifier (4256) - Context Group Name */
    public static final String VisualFieldProcedureModifier4256 = "1.2.840.10008.6.1.915";

    /** Visual Field Global Index Name (4257) - Context Group Name */
    public static final String VisualFieldGlobalIndexName4257 = "1.2.840.10008.6.1.916";

    /** Abstract Multi-Dimensional Image Model Component Semantics (7180) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelComponentSemantics7180 = "1.2.840.10008.6.1.917";

    /** Abstract Multi-Dimensional Image Model Component Units (7181) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelComponentUnits7181 = "1.2.840.10008.6.1.918";

    /** Abstract Multi-Dimensional Image Model Dimension Semantics (7182) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelDimensionSemantics7182 = "1.2.840.10008.6.1.919";

    /** Abstract Multi-Dimensional Image Model Dimension Units (7183) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelDimensionUnits7183 = "1.2.840.10008.6.1.920";

    /** Abstract Multi-Dimensional Image Model Axis Direction (7184) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelAxisDirection7184 = "1.2.840.10008.6.1.921";

    /** Abstract Multi-Dimensional Image Model Axis Orientation (7185) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelAxisOrientation7185 = "1.2.840.10008.6.1.922";

    /** Abstract Multi-Dimensional Image Model Qualitative Dimension Sample Semantics (7186) - Context Group Name */
    public static final String AbstractMultiDimensionalImageModelQualitativeDimensionSampleSemantics7186 = "1.2.840.10008.6.1.923";

    /** Planning Methods (7320) - Context Group Name */
    public static final String PlanningMethods7320 = "1.2.840.10008.6.1.924";

    /** De-identification Method (7050) - Context Group Name */
    public static final String DeIdentificationMethod7050 = "1.2.840.10008.6.1.925";

    /** Measurement Orientation (12118) - Context Group Name */
    public static final String MeasurementOrientation12118 = "1.2.840.10008.6.1.926";

    /** ECG Global Waveform Durations (3689) - Context Group Name */
    public static final String ECGGlobalWaveformDurations3689 = "1.2.840.10008.6.1.927";

    /** ICDs (3692) - Context Group Name */
    public static final String ICDs3692 = "1.2.840.10008.6.1.930";

    /** Radiotherapy General Workitem Definition (9241) - Context Group Name */
    public static final String RadiotherapyGeneralWorkitemDefinition9241 = "1.2.840.10008.6.1.931";

    /** Radiotherapy Acquisition Workitem Definition (9242) - Context Group Name */
    public static final String RadiotherapyAcquisitionWorkitemDefinition9242 = "1.2.840.10008.6.1.932";

    /** Radiotherapy Registration Workitem Definition (9243) - Context Group Name */
    public static final String RadiotherapyRegistrationWorkitemDefinition9243 = "1.2.840.10008.6.1.933";

    /** Intravascular OCT Flush Agent (3850) - Context Group Name */
    public static final String IntravascularOCTFlushAgent3850 = "1.2.840.10008.6.1.934";

    /** Dcm4che Attributes Modification Notification SOP Class - SOP Class */
    public static final String Dcm4cheAttributesModificationNotificationSOPClass = "1.2.40.0.13.1.3.1.2.3.1.1";

    /** Private Study Root Query/Retrieve Information Model - FIND - SOP Class */
    public static final String PrivateStudyRootQueryRetrieveInformationModelFIND = "1.2.40.0.13.1.5.1.4.1.2.2.1";

    /** Private Blocked Study Root Query/Retrieve Information Model - FIND - SOP Class */
    public static final String PrivateBlockedStudyRootQueryRetrieveInformationModelFIND = "1.2.40.0.13.1.5.1.4.1.2.2.1.1";

    /** Private Virtual Multiframe Study Root Query/Retrieve Information Model - FIND - SOP Class */
    public static final String PrivateVirtualMultiframeStudyRootQueryRetrieveInformationModelFIND = "1.2.40.0.13.1.5.1.4.1.2.2.1.2";

    /** Siemens CSA Non-Image Storage - SOP Class */
    public static final String SiemensCSANonImageStorage = "1.3.12.2.1107.5.9.1";

    /** Toshiba US Private Data Storage - SOP Class */
    public static final String ToshibaUSPrivateDataStorage = "1.2.392.200036.9116.7.8.1.1.1";

    /** No Pixel Data - Transfer Syntax */
    public static final String NoPixelData = "1.2.840.10008.1.2.4.96";

    /** No Pixel Data Deflate - Transfer Syntax */
    public static final String NoPixelDataDeflate = "1.2.840.10008.1.2.4.97";

}