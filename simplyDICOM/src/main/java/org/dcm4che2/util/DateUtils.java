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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 8397 $ $Date: 2008-11-27 15:38:16 +0100 (Thu, 27 Nov 2008) $
 * @since Jun 27, 2005
 * 
 */
public class DateUtils {

    public static Date parseDA(String s, boolean end) {
        if (s == null || s.length() == 0)
            return null;
        Calendar c = new GregorianCalendar();
        c.clear();
        if (end) {
            setToDec31(c);
        }
        parseDA(c, s, 0, s.length());
        return c.getTime();
    }

    /**
     * Parses a value that has a TM VR as a {@link Date}.
     * 
     * @param s the TM string to parse.
     * @param end whether or not to initialize missing precision to the maximum values (December
     *        31st, 23:59).
     * @return the parsed date or <code>null</code> if the input is empty.
     */
    public static Date parseTM(String s, boolean end) {
        if (s == null || s.length() == 0) {
            return null;
        }

        Calendar c = new GregorianCalendar();
        c.clear();
        if (end) {
            setTo2359(c);
        }

        parseTM(c, s, 0, s.length());
        return c.getTime();
    }

    public static Date parseDT(String s, boolean end) {
        if (s == null || s.length() == 0) {
            return null;
        }
        
        Calendar c = new GregorianCalendar();
        c.clear();
        
        if (end) {
            setToDec31(c);
            setTo2359(c);
        }
        
        int len = s.length();
        
        if (len >= 5) {
            final char tzsign = s.charAt(len - 5);
            if (tzsign == '+' || tzsign == '-') {
                len -= 5;
                c.setTimeZone(TimeZone.getTimeZone("GMT" + s.substring(len)));
            }
        }
        
        int pos = parseDA(c, s, 0, len);
        if (pos + 2 <= len) {
            parseTM(c, s, pos, len);
        }
        return c.getTime();
    }

    public static String formatDA(Date d) {
        if (d == null)
            return null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        return formatDA(c, new StringBuffer(8)).toString();
    }

    public static String formatTM(Date d) {
        if (d == null)
            return null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        return formatTM(c, new StringBuffer(10)).toString();
    }

    public static String formatDT(Date d) {
        if (d == null)
            return null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        StringBuffer sb = new StringBuffer(18);
        formatDA(c, sb);
        formatTM(c, sb);
        return sb.toString();
    }

    private static void setToDec31(Calendar c) {
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        setTo2359(c);
    }

    private static void setTo2359(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
    }

    private static int parseDA(Calendar c, String s, int off, int len) {
        int pos = off;
        c.set(Calendar.YEAR, Integer.parseInt(s.substring(pos, pos += 4)));
        if (pos < len) {
            if (!Character.isDigit(s.charAt(pos)))
                ++pos;
            if (pos + 2 <= len) {
                c.set(Calendar.MONTH, Integer.parseInt(s.substring(pos, pos += 2)) - 1);
                if (pos < len) {
                    if (!Character.isDigit(s.charAt(pos)))
                        ++pos;
                    if (pos + 2 <= len) {
                        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(pos, pos += 2)));
                    }
                }
            }
        }
        return pos;
    }

    private static int parseTM(Calendar c, String s, int off, int len) {
        int pos = off;

        String hours = s.substring(pos, pos += 2);
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));

        if (pos < len) {
            if (!Character.isDigit(s.charAt(pos))) {
                ++pos;
            }

            if (pos + 2 <= len) {
                String minutes = s.substring(pos, pos += 2);
                c.set(Calendar.MINUTE, Integer.parseInt(minutes));

                if (pos < len) {
                    if (!Character.isDigit(s.charAt(pos))) {
                        ++pos;
                    }

                    if (pos + 2 <= len) {
                        String seconds = s.substring(pos, pos += 2);
                        c.set(Calendar.SECOND, Integer.parseInt(seconds));

                        if (pos + 1 < len) {
                            String micros = s.substring(pos + 1, Math.min(len, pos + 5));
                            while (micros.length() < 4) {
                                micros += '0';
                            }

                            int millis = roundMicrosToMillis(micros);
                            c.set(Calendar.MILLISECOND, millis);
                        }
                    }
                }
            }
        }
        return pos;
    }
    
    private static int roundMicrosToMillis(String micros) {
        assert micros != null && micros.length() == 4;
        double dblMicros = Integer.valueOf(micros).doubleValue();
        return (int) Math.round(dblMicros / 10.0);
    }

    private static StringBuffer formatDA(Calendar c, StringBuffer sb) {
        int yyyy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH) + 1;
        int dd = c.get(Calendar.DAY_OF_MONTH);
        sb.append(yyyy);
        if (mm < 10)
            sb.append("0");
        sb.append(mm);
        if (dd < 10)
            sb.append("0");
        sb.append(dd);
        return sb;
    }

    private static StringBuffer formatTM(Calendar c, StringBuffer sb) {
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);
        int ss = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        if (hh < 10)
            sb.append("0");
        sb.append(hh);
        if (mm < 10)
            sb.append("0");
        sb.append(mm);
        if (ss < 10)
            sb.append("0");
        sb.append(ss);
        sb.append(".");
        if (ms < 100)
            sb.append("0");
        if (ms < 10)
            sb.append("0");
        sb.append(ms);
        return sb;
    }

    public static Date toDateTime(Date date, Date time) {
        if (date == null)
            return null;
        if (time == null)
            return date;
        Calendar d = new GregorianCalendar();
        d.setTime(date);
        Calendar t = new GregorianCalendar();
        t.setTime(time);
        t.set(Calendar.YEAR, d.get(Calendar.YEAR));
        t.set(Calendar.MONTH, d.get(Calendar.MONTH));
        t.set(Calendar.DAY_OF_MONTH, d.get(Calendar.DAY_OF_MONTH));
        return t.getTime();
    }

}
