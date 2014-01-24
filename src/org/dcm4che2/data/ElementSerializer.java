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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.dcm4che2.data.DicomObject.Visitor;

class ElementSerializer implements Serializable {

    private static final long serialVersionUID = 4051046376018292793L;

    private static final DicomElement END_OF_SET = new SimpleDicomElement(
            Tag.ItemDelimitationItem, VR.UN, false, null, null);

    private transient DicomObject attrs;

    public ElementSerializer(DicomObject attrs) {
        this.attrs = attrs;
    }

    private Object readResolve() {
        return attrs;
    }

    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeLong(attrs.getItemOffset());
        try {
            attrs.accept(new Visitor() {
                public boolean visit(DicomElement attr) {
                    try {
                        s.writeObject(attr);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            if (e.getCause() instanceof IOException)
                throw (IOException) e.getCause();
        }
        s.writeObject(END_OF_SET);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        attrs = new BasicDicomObject();
        attrs.setItemOffset(s.readLong());
        DicomElement attr = (DicomElement) s.readObject();
        while (attr.tag() != Tag.ItemDelimitationItem) {
            if (attr instanceof SequenceDicomElement) {
                ((SequenceDicomElement) attr).setParentDicomObject(attrs);                       
            }
            ((BasicDicomObject) attrs).addInternal(attr);
            attr = (DicomElement) s.readObject();
        }
    }
}
