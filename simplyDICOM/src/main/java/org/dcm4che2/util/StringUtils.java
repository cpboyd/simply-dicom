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

package org.dcm4che2.util;

public class StringUtils {

	private static final String[] EMPTY_STRING_ARRAY = {};
	private static final char[] HEX_DIGITS = {
		'0' , '1' , '2' , '3' , '4' , '5' ,
		'6' , '7' , '8' , '9' , 'A' , 'B' ,
		'C' , 'D' , 'E' , 'F'
	};

	public static String join(String[] ss, char delim) {
		if (ss == null)
			return null;
		if (ss.length == 0)
			return "";
		if (ss.length == 1)
			return ss[0];
		int sumlen = 0;
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null)
				sumlen += ss[i].length();
		}
		StringBuffer sb = new StringBuffer(sumlen + ss.length);
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null)
				sb.append(ss[i]);
			sb.append(delim);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public static String[] split(String s, char delim) {
        if (s == null)
            return null;
        if (s.length() == 0)
            return EMPTY_STRING_ARRAY;
        final int r0 = s.indexOf(delim);
        if (r0 == -1)
            return new String[]{s};
        int i = 2;
        int l, r = r0;
        for (; (r = s.indexOf(delim, l = r + 1)) != -1; ++i) {
        	// empty
        }
        String[] ss = new String[i];
        i = l = 0;
        r = r0;
        do ss[i++] = s.substring(l, r);
        while ((r = s.indexOf(delim, l = r + 1)) != -1);
        ss[i] = s.substring(l);
        return ss;
    }
    
    public static int count(String s, char ch) {
        if (s == null || s.length() == 0)
            return 0;
        int count = 0;
        for (int off = 0, next; 
            (next = s.indexOf(ch, off)) != -1;
            off = next+1, count++) {
        	// empty
        }
        return count;        
    }

	public static String first(String s, char delim) {
        if (s == null || s.length() == 0)
            return null;
        final int r0 = s.indexOf(delim);
        return r0 == -1 ? s : s.substring(0, r0);
	}
	
    private static String trim(String s, char lead, char tail1, char tail2) {
		if (s == null)
			return null;		
		int len = s.length();
		int st = 0;
		char c;
		while ((st < len) && (s.charAt(st) == lead))
		    st++;
		while ((st < len) && ((c = s.charAt(len - 1)) == tail1 || c == tail2))
		    len--;		
		return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
	}

    private static String[] trim(String[] ss, char lead, char tail1, char tail2) {
		if (ss == null)
			return null;		
		for (int i = 0; i < ss.length; i++)
			ss[i] = trim(ss[i], lead, tail1, tail2);
		return ss;
    }
	
    public static String trim(String s) {
		return trim(s, ' ', '\0', ' ');
	}

    public static String[] trim(String[] ss) {
		return trim(ss, ' ', '\0', ' ');
    }

    public static String trimPN(String s) {
		return trim(s, ' ', '^', ' ');
	}

    public static String[] trimPN(String[] ss) {
		return trim(ss, ' ', '^', ' ');
    }

    public static String trimEnd(String s) {
		return trim(s, '\0', '\0', ' ');
	}

    public static String[] trimEnd(String[] ss) {
		return trim(ss, '\0', '\0', ' ');
    }

    public static String[] ints2strs(int[] val) {
        if (val == null || val.length == 0)
            return EMPTY_STRING_ARRAY;
        String[] ss = new String[val.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = Integer.toString(val[i]);
        }
        return ss;
    }
    
    public static String[] uints2strs(int[] val) {
        if (val == null || val.length == 0)
            return EMPTY_STRING_ARRAY;
        String[] ss = new String[val.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = Long.toString(val[i] & 0xffffffffL);
        }
        return ss;
    }
    
    public static String[] floats2strs(float[] fs) {
        if (fs == null || fs.length == 0)
            return EMPTY_STRING_ARRAY;
        String[] ss = new String[fs.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = Float.toString(fs[i]);
        }
        return ss;
    }
    
    public static String[] doubles2strs(double[] ds) {
        if (ds == null || ds.length == 0)
            return EMPTY_STRING_ARRAY;
        String[] ss = new String[ds.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = Double.toString(ds[i]);
        }
        return ss;
    }
    

    public static StringBuffer intToHex(int val, StringBuffer sb) {
		sb.append(HEX_DIGITS[(val >> 28) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 24) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 20) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 16) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 12) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 8) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 4) & 0xf]);
		sb.append(HEX_DIGITS[val & 0xf]);
		return sb;
	}

	public static StringBuffer shortToHex(int val, StringBuffer sb) {
		sb.append(HEX_DIGITS[(val >> 12) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 8) & 0xf]);
		sb.append(HEX_DIGITS[(val >> 4) & 0xf]);
		sb.append(HEX_DIGITS[val & 0xf]);
		return sb;
	}

	public static StringBuffer byteToHex(int val, StringBuffer sb) {
		sb.append(HEX_DIGITS[(val >> 4) & 0xf]);
		sb.append(HEX_DIGITS[val & 0xf]);
		return sb;
	}

    public static void intToHex(int val, char[] ch, int off) {
        ch[off] = HEX_DIGITS[(val >> 28) & 0xf];
        ch[off+1] = HEX_DIGITS[(val >> 24) & 0xf];
        ch[off+2] = HEX_DIGITS[(val >> 20) & 0xf];
        ch[off+3] = HEX_DIGITS[(val >> 16) & 0xf];
        ch[off+4] = HEX_DIGITS[(val >> 12) & 0xf];
        ch[off+5] = HEX_DIGITS[(val >> 8) & 0xf];
        ch[off+6] = HEX_DIGITS[(val >> 4) & 0xf];
        ch[off+7] = HEX_DIGITS[val & 0xf];
    }

    public static void shortToHex(int val, char[] ch, int off) {
        ch[off] = HEX_DIGITS[(val >> 12) & 0xf];
        ch[off+1] = HEX_DIGITS[(val >> 8) & 0xf];
        ch[off+2] = HEX_DIGITS[(val >> 4) & 0xf];
        ch[off+3] = HEX_DIGITS[val & 0xf];
    }

    public static void byteToHex(int val, char[] ch, int off) {
        ch[off] = HEX_DIGITS[(val >> 4) & 0xf];
        ch[off+1] = HEX_DIGITS[val & 0xf];
    }
 
    public static String intToHex(int val) {
        char[] ch = new char[8];
        intToHex(val, ch, 0);
        return new String(ch);
    }

    public static String[] intsToHex(int[] val) {
        if (val == null || val.length == 0)
            return EMPTY_STRING_ARRAY;
        String[] ss = new String[val.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = intToHex(val[i]);
        }
        return ss;
    }
    
    
    public static String shortToHex(int val) {
        char[] ch = new char[4];
        shortToHex(val, ch, 0);
		return new String(ch);
	}

}
