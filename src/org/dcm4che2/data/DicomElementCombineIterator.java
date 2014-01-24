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
 * Bill Wallace, Agfa Healthcare, 375 Hagey Blvd, Waterloo, ON, CA
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Bill Wallace, <bill.wallace@agfa.com>
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

import java.util.Iterator;

/**
 * Combines two iterators over DICOM elements into one, preferring the first instance 
 * @author AMOBE
 *
 */
public class DicomElementCombineIterator implements Iterator<DicomElement> {
	
	Iterator<DicomElement> it1, it2;
	
	DicomElement next1, next2;
	
	public DicomElementCombineIterator(Iterator<DicomElement> it1, Iterator<DicomElement> it2) {
		this.it1 = it1;
		this.it2 = it2;
	}
	
	protected void fill() {
		if( next1==null && it1.hasNext() ) {
			next1 = it1.next();
		}
		if( next2==null && it2.hasNext() ) {
			next2 = it2.next();
		}
		if( next2!=null && next1!=null && next1.tag()==next2.tag() ) {
			next2 = null;
		}
	}

	public boolean hasNext() {
		fill();
		return next1!=null || next2!=null;
	}

	public DicomElement next() {
		fill();
		if( next1==null ) {
			DicomElement ret = next2;
			next2 = null;
			return ret;
		}
		if( next2==null ) {
			DicomElement ret = next1;
			next1 = null;
			return ret;
		}
		if( next1.tag() <  next2.tag() ) {
			DicomElement ret = next1;
			next1 = null;
			return ret;
		}
		DicomElement ret = next2;
		next2 = null;
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
