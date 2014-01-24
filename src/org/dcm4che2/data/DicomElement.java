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

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

public interface DicomElement extends Serializable {
    int tag();

    VR vr();

    int vm(SpecificCharacterSet cs);

    boolean bigEndian();

    DicomElement bigEndian(boolean bigEndian);

    int length();

    boolean isEmpty();

    boolean hasItems();

    int countItems();

    byte[] getBytes();

    boolean hasDicomObjects();

    boolean hasFragments();

    DicomObject getDicomObject();

    DicomObject getDicomObject(int index);

    DicomObject removeDicomObject(int index);
    
    boolean removeDicomObject(DicomObject item);   

    DicomObject addDicomObject(DicomObject item);

    DicomObject addDicomObject(int index, DicomObject item);

    DicomObject setDicomObject(int index, DicomObject item);

    byte[] getFragment(int index);

    byte[] removeFragment(int index);

    boolean removeFragment(byte[] b);

    byte[] addFragment(byte[] b);

    byte[] addFragment(int index, byte[] b);

    byte[] setFragment(int index, byte[] b);

    short[] getShorts(boolean cache);

    int getInt(boolean cache);

    int[] getInts(boolean cache);

    float getFloat(boolean cache);

    float[] getFloats(boolean cache);

    double getDouble(boolean cache);

    double[] getDoubles(boolean cache);

    String getString(SpecificCharacterSet cs, boolean cache);

    String[] getStrings(SpecificCharacterSet cs, boolean cache);

    Date getDate(boolean cache);

    Date[] getDates(boolean cache);

    DateRange getDateRange(boolean cache);

    Pattern getPattern(SpecificCharacterSet cs, boolean ignoreCase,
            boolean cache);

    DicomElement share();

    DicomElement filterItems(DicomObject filter);

    StringBuffer toStringBuffer(StringBuffer sb, int maxValLen);

    String getValueAsString(SpecificCharacterSet cs, int truncate);
}
