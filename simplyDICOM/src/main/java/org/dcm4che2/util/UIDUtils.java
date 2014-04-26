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

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 12609 $ $Date:: 2009-11-24$
 * @since Aug 21, 2005
 * 
 */
/**
 * @author gunter
 *
 */
public class UIDUtils {

	private static final int EXPECT_DOT = 0;
    private static final int EXPECT_FIRST_DIGIT = 1;
    private static final int EXPECT_DOT_OR_DIGIT = 2;
    private static final int ILLEGAL_UID = -1;

    /**
     * UID root for UUIDs (Universally Unique Identifiers) generated in
     * accordance with Rec. ITU-T X.667 | ISO/IEC 9834-8. Used by default.
     * @see <a href="http://www.oid-info.com/get/2.25">OID repository {joint-iso-itu-t(2) uuid(25)}</a>
     */
    public static final String UUID_ROOT = "2.25";
    
    private static String root = UUID_ROOT;
    private static boolean acceptLeadingZero = false;

    public static final boolean isAcceptLeadingZero() {
        return acceptLeadingZero;
    }

    public static final void setAcceptLeadingZero(boolean acceptLeadingZero) {
        UIDUtils.acceptLeadingZero = acceptLeadingZero;
    }

    public static void setRoot(String root) {
        verifyUIDRoot(root);
        UIDUtils.root = root;
    }

    private static void verifyUIDRoot(String root) {
        if (root.length() > 24)
            throw new IllegalArgumentException("root length > 24");

        verifyUID(root);
    }

    public static String getRoot() {
        return root;
    }

    public static void verifyUID(String uid) {
        verifyUID(uid, acceptLeadingZero);
    }

    public static void verifyUID(String uid, boolean acceptLeadingZero) {
        if (!isValidUID(uid, acceptLeadingZero))
            throw new IllegalArgumentException(uid);
    }

    public static boolean isValidUID(String uid) {
        return isValidUID(uid, acceptLeadingZero);
    }

    public static boolean isValidUID(String uid, boolean acceptLeadingZero) {
        int len = uid.length();
        if (len > 64)
            return false;

        int state = EXPECT_FIRST_DIGIT;
        for (int i = 0; i < len; i++) {
            state = nextState(state, uid.charAt(i), acceptLeadingZero);
            if (state == ILLEGAL_UID)
                return false;
        }
        return state != EXPECT_FIRST_DIGIT;
    }

    private static int nextState(int state, int ch, boolean acceptLeadingZero) {
        return ch == '.' ? (state == EXPECT_FIRST_DIGIT ? ILLEGAL_UID
                : EXPECT_FIRST_DIGIT)
                : (state == EXPECT_DOT || ch < '0' || ch > '9') ? ILLEGAL_UID
                        : !acceptLeadingZero && state == EXPECT_FIRST_DIGIT
                                && ch == '0' ? EXPECT_DOT : EXPECT_DOT_OR_DIGIT;
    }

    public static String createUID() {
        return doCreateUID(root);
    }

    public static String createUID(String root) {
        verifyUIDRoot(root);
        return doCreateUID(root);
    }

    private static String doCreateUID(String root) {
        UUID uuid = UUID.randomUUID();
        byte[] b17 = new byte[17];
        fill(b17, 1, uuid.getMostSignificantBits());
        fill(b17, 9, uuid.getLeastSignificantBits());
        return new StringBuilder(64).append(root).append('.')
                .append(new BigInteger(b17)).toString();
    }

    private static void fill(byte[] bb, int off, long val) {
        for (int i = off, shift = 56; shift >= 0; i++, shift -= 8)
            bb[i] = (byte) (val >>> shift);
    }
}
