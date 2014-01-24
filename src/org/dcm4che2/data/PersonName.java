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

import java.util.StringTokenizer;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 6089 $ $Date: 2008-02-27 23:05:37 +0100 (Wed, 27 Feb 2008) $
 * @since Mar 7, 2006
 *
 */
public class PersonName {
    
    /**  
     * Field number for get and set indicating the family name complex 
     */
    public static final int FAMILY = 0;
  
    /**  
     * Field number for get and set indicating the given name complex 
     */
    public static final int GIVEN = 1;
  
    /**  
     * Field number for get and set indicating the middle name 
     */
    public static final int MIDDLE = 2;
  
    /**  
     * Field number for get and set indicating the name prefix 
     */
    public static final int PREFIX = 3;
  
    /**  
     * Field number for get and set indicating the name suffix 
     */
    public static final int SUFFIX = 4;
    
    /**  
     * Group number for get and set indicating the single-byte character
     * representation
     */
    public static final int SINGLE_BYTE = 0;
  
    /**  
     * Group number for get and set indicating the ideographic representation
     */
    public static final int IDEOGRAPHIC = 1;
  
    /**  
     * Group number for get and set indicating the phonetic representation
     */
    public static final int PHONETIC = 2;
        
    private final String[][] components = { 
            new String[5], 
            new String[5], 
            new String[5] };
    
    /**
     * Default c'tor.
     */
    public PersonName() {
        // empty
    }

    /**
     * C'tor that can parse a PN-encoded value.
     * 
     * @param s the PN-encoded value to initialize from.
     */
    public PersonName(String s) {
        if (s == null)
            return;
        s = s.trim();
        int group = 0;
        int field = 0;
        try {
            for (StringTokenizer stk = new StringTokenizer(s, "^=", true); stk
                    .hasMoreTokens();) {
                String tk = stk.nextToken();
                if (tk.equals("^")) {
                    field++;
                } else if (tk.equals("=")) {
                    group++;
                    field = 0;
                } else {
                    set(group, field, tk);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(s);
        }
    }
    
    public static PersonName[] toPersonNames(String[] ss) {
        if (ss == null) {
            return null;
        }
        PersonName[] pns = new PersonName[ss.length];
        for (int i = 0; i < pns.length; i++) {
            pns[i] = new PersonName(ss[i]);
        }
        return pns;
    }
    
    public final String get(int field) {
        return get(SINGLE_BYTE, field);
    }

    public final String get(int group, int field) {
        return components[group][field];
    }

    public final void set(int field, String s) {
        set(SINGLE_BYTE, field, s);
    }

    public final void set(int group, int field, String s) {
        if (s != null) {
            s = s.trim();
            if (s.length() == 0)
                s = null;
        }
        components[group][field] = s;
    }
    
    @Override
    public String toString() {
        int[] groupLen = { 0, 0, 0 };
        int[] compDelim = { 0, 0, 0 };
        int groupDelim = 0;
        for (int g = 0; g < 3; g++) {
            for (int c = 0; c < 5; c++) {
                if (components[g][c] != null) {
                    groupLen[g] += components[g][c].length();
                    compDelim[g] = c;
                    groupDelim = g;
                }
            }
        }
        int len = groupLen[0] + groupLen[1] + groupLen[2]
                + compDelim[0] + compDelim[1] + compDelim[2]
                + groupDelim;
        if (len == 0)
            return "";
        StringBuffer sb = new StringBuffer(len);
        for (int g = 0; g <= groupDelim; g++) {
            if (g != 0) {
                sb.append('=');
            }
            for (int c = 0; c <= compDelim[g]; c++) {
                if (c != 0) {
                    sb.append('^');
                }
                if (components[g][c] != null) {
                    sb.append(components[g][c]);
                }
            }
        }
        return sb.toString();
    }

    public String componentGroupString(int group, boolean trim) {
        int len = 0;
        int delim = 0;
        for (int c = 0; c < 5; c++) {
            if (components[group][c] != null) {
                len += components[group][c].length();
                delim = c;
            }
        }
        if (len == 0)
            return "";
        StringBuffer sb = new StringBuffer(len + (trim ? delim : 4));
        for (int c = 0; c <= delim; c++) {
            if (c != 0) {
                sb.append('^');
            }
            if (components[group][c] != null) {
                sb.append(components[group][c]);
            }
        }
        if (!trim) {
            for (; delim < 4; delim++) {
                sb.append('^');
            }
        }
        return sb.toString();
    }
}
