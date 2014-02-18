package us.cboyd.android.dicom;

import android.content.res.Resources;
import android.view.View;

public class DcmRes {
	public static int getTagId(int tag) {
		switch (tag) {
			//<!-- SOP Common -->
			case 0x00080016:
				return R.string.dcm_00080016;
			case 0x00080018:
				return R.string.dcm_00080018;
			case 0x00080020:
				return R.string.dcm_00080020;
			case 0x00080021:
				return R.string.dcm_00080021;
			case 0x00080030:
				return R.string.dcm_00080030;
			case 0x00080031:
				return R.string.dcm_00080031;
			case 0x00080050:
				return R.string.dcm_00080050;
			case 0x00080070:
				return R.string.dcm_00080070;
			case 0x00080080:
				return R.string.dcm_00080080;
			case 0x00080090:
				return R.string.dcm_00080090;
		    //<!-- Patient -->
			case 0x00100010:
				return R.string.dcm_00100010;
			case 0x00100020:
				return R.string.dcm_00100020;
			case 0x00100030:
				return R.string.dcm_00100030;
			case 0x00100040:
				return R.string.dcm_00100040;
		    //<!-- Study -->
			case 0x0020000D:
				return R.string.dcm_0020000D;
			case 0x0020000E:
				return R.string.dcm_0020000E;
			case 0x00200010:
				return R.string.dcm_00200010;
			case 0x00200011:
				return R.string.dcm_00200011;
			default:
				return R.string.dcm_unknown;
		}
	}
	
	public static String getTag(int tag, Resources res) {
	    return res.getString(getTagId(tag));
	}
}
