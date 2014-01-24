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
 * Damien Evans <damien.daddy@gmail.com>
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

package org.dcm4che2.io;

import java.io.IOException;

/**
 * Interface used by the <code>DicomInputStream</code> when reading values.
 * <code>DicomInputStream</code> provides an implementation, which may be
 * overridden by setting a custom implementation of
 * <code>DicomInputHandler</code> into the stream object.
 * 
 * @see org.dcm4che2.io.DicomInputStream
 * @see org.dcm4che2.io.TranscoderInputHandler
 * @see org.dcm4che2.io.StopTagInputHandler
 * 
 * @author gunter zeilinger(gunterze@gmail.com)
 */
public interface DicomInputHandler {
    
	/**
     * Called by the input stream when reading a DICOM value in the stream.
     * 
     * @param in
     *            The <code>DicomInputStream</code> calling this method.
     * @return boolean true if the stream should continue reading the item.
     * @throws IOException
     */
	boolean readValue(DicomInputStream in) throws IOException;
}