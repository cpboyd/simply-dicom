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

import org.dcm4che2.util.TagUtils;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Reversion$ $Date: 2007-11-26 14:16:23 +0100 (Mon, 26 Nov 2007) $
 * @since Sep 3, 2005
 *
 */
abstract class AbstractDicomElement implements DicomElement {

    protected static final int TO_STRING_MAX_VAL_LEN = 64;
    protected transient int tag;    
    protected transient VR vr;    
    protected transient boolean bigEndian;

    public AbstractDicomElement(int tag, VR vr, boolean bigEndian) {
        this.tag = tag;
        this.vr = vr;
        this.bigEndian = bigEndian;
    }

    @Override
    public int hashCode() {
        return tag;
    }

    public final boolean bigEndian() {
        return bigEndian;
    }

    public final int tag() {
        return tag;
    }

    public final VR vr() {
        return vr;
    }

    @Override
    public String toString() {
        return toStringBuffer(null, TO_STRING_MAX_VAL_LEN).toString();
    }

    public StringBuffer toStringBuffer(StringBuffer sb, int maxValLen) {
        if (sb == null)
            sb = new StringBuffer();
        TagUtils.toStringBuffer(tag, sb);
        sb.append(' ');
        sb.append(vr);
        sb.append(" #");
        sb.append(length());
        sb.append(" [");
        appendValue(sb, maxValLen);
        sb.append("]");
        return sb;
    }

    protected abstract void appendValue(StringBuffer sb, int maxValLen);
    
    public DicomElement bigEndian(boolean bigEndian) {
        if (this.bigEndian == bigEndian)
            return this;
        toggleEndian();
        this.bigEndian = bigEndian;
        return this;
    }

    protected abstract void toggleEndian();
}
