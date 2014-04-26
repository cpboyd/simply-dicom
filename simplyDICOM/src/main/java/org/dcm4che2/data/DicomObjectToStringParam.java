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

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Reversion$ $Date: 2007-11-26 14:16:23 +0100 (Mon, 26 Nov 2007) $
 * @since Sep 6, 2005
 *
 */
public final class DicomObjectToStringParam {
    
    private static DicomObjectToStringParam defParam = 
        new DicomObjectToStringParam(
                true,   // name
                64,     // valueLength;
                10,     // numItems;
                100,    // lineLength;
                100,    // numLines;
                "",     // indent
                System.getProperty("line.separator", "\n"));
    
    public static DicomObjectToStringParam getDefaultParam() {
        return defParam;
    }
    
    public static void setDefaultParam(DicomObjectToStringParam param) {
        if (param == null) {
            throw new NullPointerException();
        }
        DicomObjectToStringParam.defParam = param;
    }
    
    public final boolean name;
    public final int valueLength;
    public final int numItems;
    public final int lineLength;
    public final int numLines;
    public final String indent;
    public final String lineSeparator;
    
    public DicomObjectToStringParam(boolean name, int valueLength,
            int numItems, int lineLength, int numLines, String indent,
            String lineSeparator) {
        if (valueLength <= 0)
            throw new IllegalArgumentException("valueLength:" + valueLength);
        if (numItems <= 0)
            throw new IllegalArgumentException("numItems:" + numItems);
        if (lineLength <= 0)
            throw new IllegalArgumentException("lineLength:" + lineLength);
        if (numLines <= 0)
            throw new IllegalArgumentException("numLines:" + numLines);
        if (lineSeparator == null)
            throw new NullPointerException();
        if (indent == null)
            throw new NullPointerException();
        this.name = name;
        this.valueLength = valueLength;
        this.numItems = numItems;
        this.lineLength = lineLength;
        this.numLines = numLines;
        this.indent = indent;
        this.lineSeparator = lineSeparator;
    }
    
    @Override
    public String toString() {
        return "DicomObjectToStringParam[name=" + name + ",valueLength="
                + valueLength + ",numItems=" + numItems + ",lineLength="
                + lineLength + ",numLines=" + numLines + ",indent=" + indent
                + ",lineSeparator=" + lineSeparator + "]";
    }
    
}
