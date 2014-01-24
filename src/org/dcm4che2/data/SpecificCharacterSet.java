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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SpecificCharacterSet {

    private static final Map<String, String> CHARSET = new HashMap<String, String>();
    static {
        CHARSET.put("", "US-ASCII");
        CHARSET.put("ISO_IR 100", "ISO-8859-1");
        CHARSET.put("ISO_IR 101", "ISO-8859-2");
        CHARSET.put("ISO_IR 109", "ISO-8859-3");
        CHARSET.put("ISO_IR 110", "ISO-8859-4");
        CHARSET.put("ISO_IR 144", "ISO-8859-5");
        CHARSET.put("ISO_IR 127", "ISO-8859-6");
        CHARSET.put("ISO_IR 126", "ISO-8859-7");
        CHARSET.put("ISO_IR 138", "ISO-8859-8");
        CHARSET.put("ISO_IR 148", "ISO-8859-9");
        CHARSET.put("ISO_IR 13", "JIS_X0201");
        CHARSET.put("ISO_IR 166", "TIS-620");
        CHARSET.put("ISO 2022 IR 6", "US-ASCII");
        CHARSET.put("ISO 2022 IR 100", "ISO-8859-1");
        CHARSET.put("ISO 2022 IR 101", "ISO-8859-2");
        CHARSET.put("ISO 2022 IR 109", "ISO-8859-3");
        CHARSET.put("ISO 2022 IR 110", "ISO-8859-4");
        CHARSET.put("ISO 2022 IR 144", "ISO-8859-5");
        CHARSET.put("ISO 2022 IR 127", "ISO-8859-6");
        CHARSET.put("ISO 2022 IR 126", "ISO-8859-7");
        CHARSET.put("ISO 2022 IR 138", "ISO-8859-8");
        CHARSET.put("ISO 2022 IR 148", "ISO-8859-9");
        CHARSET.put("ISO 2022 IR 13", "JIS_X0201");
        CHARSET.put("ISO 2022 IR 166", "TIS-620");
        CHARSET.put("ISO 2022 IR 87", "JIS0208");
        CHARSET.put("ISO 2022 IR 159", "JIS0212");
        CHARSET.put("ISO 2022 IR 149", "cp949");
        CHARSET.put("ISO_IR 192", "UTF-8");
        CHARSET.put("GB18030", "GB18030");
    }

    protected final String charset;

    public SpecificCharacterSet(String charset) {
        this.charset = charset;
    }

    public static SpecificCharacterSet valueOf(String[] codes) {
        if (codes == null || codes.length == 0)
            return null;
        String charset = CHARSET.get(codes[0]);
        if (charset == null)
            return null;
        return codes.length > 1 ? new ISO2022(charset)
                : new SpecificCharacterSet(charset);
    }

    public byte[] encode(String val) {
        try {
            return val.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationError(e);
        }
    }

    public String decode(byte[] val) {
        return decode(val, 0, val.length, charset);
    }

    static String decode(byte[] b, int off, int len, String cs) {
        try {
            return new String(b, off, len, cs);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationError(e);
        }
    }

    private static final class ISO2022 extends SpecificCharacterSet {

        ISO2022(String charset) {
            super(charset);
        }

        @Override
        public String decode(byte[] b) {
            String cs = charset;
            int off = 0;
            int cur = 0;
            int step = 1;
            StringBuffer sb = new StringBuffer(b.length);
            while (cur < b.length) {
                if (b[cur] == 0x1b) { // ESC
                    if (off < cur) {
                        sb.append(decode(b, off, cur - off, cs));
                    }
                    cur += 3;
                    switch ((b[cur - 2] << 8) | b[cur - 1]) {
                    case 0x2428:
                        if (b[cur++] == 0x44) {
                            cs = "JIS0212";
                            step = 2;
                        } else { // decode invalid ESC sequence as chars
                            sb.append(decode(b, cur - 4, 4, cs));
                        }
                        break;
                    case 0x2429:
                        if (b[cur++] == 0x43) {
                            cs = "cp949";
                            step = -1;
                        } else { // decode invalid ESC sequence as chars
                            sb.append(decode(b, cur - 4, 4, cs));
                        }
                        break;
                    case 0x2442:
                        cs = "JIS0208";
                        step = 2;
                        break;
                    case 0x2842:
                        cs = "US-ASCII";
                        step = 1;
                        break;
                    case 0x284a:
                    case 0x2949:
                        cs = "JIS_X0201";
                        step = 1;
                        break;
                    case 0x2d41:
                        cs = "ISO-8859-1";
                        step = 1;
                        break;
                    case 0x2d42:
                        cs = "ISO-8859-2";
                        step = 1;
                        break;
                    case 0x2d43:
                        cs = "ISO-8859-3";
                        step = 1;
                        break;
                    case 0x2d44:
                        cs = "ISO-8859-4";
                        step = 1;
                        break;
                    case 0x2d46:
                        cs = "ISO-8859-7";
                        step = 1;
                        break;
                    case 0x2d47:
                        cs = "ISO-8859-6";
                        step = 1;
                        break;
                    case 0x2d48:
                        cs = "ISO-8859-8";
                        step = 1;
                        break;
                    case 0x2d4c:
                        cs = "ISO-8859-5";
                        step = 1;
                        break;
                    case 0x2d4d:
                        cs = "ISO-8859-9";
                        step = 1;
                        break;
                    case 0x2d54:
                        cs = "TIS-620";
                        step = 1;
                        break;
                    default: // decode invalid ESC sequence as chars
                        sb.append(decode(b, cur - 3, 3, cs));
                    }
                    off = cur;
                } else {
                    cur += step > 0 ? step : b[cur] < 0 ? 2 : 1;
                }
            }
            if (off < cur) {
                sb.append(decode(b, off, cur - off, cs));
            }
            return sb.toString();
        }
    }

}
