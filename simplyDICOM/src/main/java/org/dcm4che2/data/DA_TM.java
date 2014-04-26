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

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 16582 $ $Date: 2012-02-08 11:20:32 +0100 (Wed, 08 Feb 2012) $
 * @since Jul 4, 2005
 *
 */
public class DA_TM {

	private static final int[] DA_TAGS = {
		0x00080012, // Instance Creation Date
		0x00080020, // Study Date
		0x00080021, // Series Date
		0x00080022, // Acquisition Date
		0x00080023, // Content Date
		0x00080025, // Curve Date
		0x00080024, // Overlay Date
		0x00100030, // Patient's Birth Date
		0x00181012, // Date of Secondary Capture
		0x00181200, // Date of Last Calibration
		0x0018700C, // Date of Last Detector Calibration
		0x00320032, // Study Verified Date
		0x00320034, // Study Read Date
		0x00321000, // Scheduled Study Start Date
		0x00321010, // Scheduled Study Stop Date
		0x00321040, // Study Arrival Date
		0x00321050, // Study Completion Date
		0x0038001A, // Scheduled Admission Date
		0x0038001C, // Scheduled Discharge Date
		0x00380020, // Admitting Date
		0x00380030, // Discharge Date
		0x00400002, // Scheduled Procedure Step Start Date
		0x00400004, // Scheduled Procedure Step End Date
		0x00400244, // Performed Procedure Step Start Date
		0x00400250, // Performed Procedure Step End Date
		0x00402004, // Issue Date of Imaging Service Request
		0x0040A121, // Date
		0x00700082, // Presentation Creation Date
		0x21000040, // Creation Date
		0x30060008, // Structure Set Date
		0x30080024, // Treatment Control Point Date
		0x30080162, // Safe Position Exit Date
		0x30080166, // Safe Position Return Date
		0x30080250, // Treatment Date
		0x300A0006, // RT Plan Date
		0x300A022C, // Air Kerma Rate Reference Date
		0x300E0004, // Review Date
		0x40080100, // Interpretation Recorded Date
		0x40080108, // Interpretation Transcription Date
		0x40080112, // Interpretation Approval Date		
	};

	private static final int[] TM_TAGS = {
		0x00080013, // Instance Creation Time
		0x00080030, // Study Time
		0x00080031, // Series Time
		0x00080032, // Acquisition Time
		0x00080033, // Content Time
		0x00080035, // Curve Time
		0x00080034, // Overlay Time
		0x00100032, // Patient's Birth Time
		0x00181014, // Time of Secondary Capture
		0x00181201, // Time of Last Calibration
		0x0018700E, // Time of Last Detector Calibration
		0x00320033, // Study Verified Time
		0x00320035, // Study Read Time
		0x00321001, // Scheduled Study Start Time
		0x00321011, // Scheduled Study Stop Time
		0x00321041, // Study Arrival Time
		0x00321051, // Study Completion Time
		0x0038001B, // Scheduled Admission Time
		0x0038001D, // Scheduled Discharge Time
		0x00380021, // Admitting Time
		0x00380032, // Discharge Time
		0x00400003, // Scheduled Procedure Step Start Time
		0x00400005, // Scheduled Procedure Step End Time
		0x00400245, // Performed Procedure Step Start Time
		0x00400251, // Performed Procedure Step End Time
		0x00402005, // Issue Time of Imaging Service Request
		0x0040A122, // Time
		0x00700083, // Presentation Creation Time
		0x21000050, // Creation Time
		0x30060009, // Structure Set Time
		0x30080025, // Treatment Control Point Time
		0x30080164, // Safe Position Exit Time
		0x30080168, // Safe Position Return Time
		0x30080251, // Treatment Time
		0x300A0007, // RT Plan Time
		0x300A022E, // Air Kerma Rate Reference Time
		0x300E0005, // Review Time
		0x40080101, // Interpretation Recorded Time
		0x40080109, // Interpretation Transcription Time
		0x40080113, // Interpretation Approval Time		
	};
	
	public static int getTMTag(int daTag) {
		try {
			return TM_TAGS[Arrays.binarySearch(DA_TAGS, daTag)];
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}

	public static int getDATag(int tmTag) {
		try {
			return DA_TAGS[Arrays.binarySearch(TM_TAGS, tmTag)];
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
}
