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

import java.util.Hashtable;
import java.util.Map;

public class TransferSyntax
{

    public static final TransferSyntax ImplicitVRLittleEndian = 
            new TransferSyntax("1.2.840.10008.1.2", false, false, false, false);

    public static final TransferSyntax ImplicitVRBigEndian =
            new TransferSyntax("1.2.840.113619.5.2", false, true, false, false);

    public static final TransferSyntax ExplicitVRLittleEndian =
            new TransferSyntax("1.2.840.10008.1.2.1", true, false, false, false);

    public static final TransferSyntax ExplicitVRBigEndian = 
            new TransferSyntax("1.2.840.10008.1.2.2", true, true, false, false);

    public static final TransferSyntax DeflatedExplicitVRLittleEndian = 
            new TransferSyntax("1.2.840.10008.1.2.1.99", true, false, true, false);

    public static final TransferSyntax NoPixelData = 
            new TransferSyntax("1.2.840.10008.1.2.4.96", true, false, false, false);

    public static final TransferSyntax NoPixelDataDeflate = 
        new TransferSyntax("1.2.840.10008.1.2.4.97", true, false, true, false);

    private static final Map<String,TransferSyntax> map =
            new Hashtable<String,TransferSyntax>();

    static {
        add(ImplicitVRLittleEndian);
        add(ImplicitVRBigEndian);
        add(ExplicitVRLittleEndian);
        add(ExplicitVRBigEndian);
        add(DeflatedExplicitVRLittleEndian);
        add(NoPixelData);
        add(NoPixelDataDeflate);
    }

    /** 
     * Add entry for private Transfer Syntax to be returned by {@link valueOf}.
     * Necessary to decode DICOM Objects encoded with Private Transfer Syntax
     * with Big Endian or/and Implicit VR encoding. 
     * 
     * @param ts entry for private Transfer Syntax
     */
    public static void add(TransferSyntax ts) {
        map.put(ts.uid, ts);
    }

    public static TransferSyntax remove(String tsuid) {
        return map.remove(tsuid);
    }

    public static TransferSyntax valueOf(String uid) {
        if (uid == null)
            throw new NullPointerException("uid");
        TransferSyntax ts = map.get(uid);
        return ts != null ? ts 
                : new TransferSyntax(uid, true, false, false, true);
    }

    private final String uid;

    private final boolean bigEndian;

    private final boolean explicitVR;

    private final boolean deflated;

    private final boolean encapsulated;

    public TransferSyntax(String uid, boolean explicitVR, boolean bigEndian,
            boolean deflated, boolean encapsulated)
    {
        this.uid = uid;
        this.explicitVR = explicitVR;
        this.bigEndian = bigEndian;
        this.deflated = deflated;
        this.encapsulated = encapsulated;
    }

    public final String uid()
    {
        return uid;
    }

    public final boolean bigEndian()
    {
        return bigEndian;
    }

    public final boolean explicitVR()
    {
        return explicitVR;
    }

    public final boolean deflated()
    {
        return deflated;
    }

    public final boolean encapsulated()
    {
        return encapsulated;
    }

    public final boolean uncompressed()
    {
        return !deflated && !encapsulated;
    }
    
    /** Check to see if the transfer syntax is the same */
    @Override
    public  boolean equals(Object o2) {
    	if( ! (o2 instanceof TransferSyntax) ) return false;
    	return uid().equals(((TransferSyntax) o2).uid());
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
