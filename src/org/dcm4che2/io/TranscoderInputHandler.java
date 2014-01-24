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
 * Java(TM), available at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2005
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

package org.dcm4che2.io;

import java.io.IOException;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.TagUtils;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Reversion$ $Date: 2007-10-09 13:41:10 +0200 (Tue, 09 Oct 2007) $
 * @since Oct 14, 2005
 *
 */
public class TranscoderInputHandler implements DicomInputHandler
{

    private static int defaultBufferSize = 1024;
    
    private DicomOutputStream out;
    private byte[] buf;

    public TranscoderInputHandler(DicomOutputStream out)
    {
        this(out, defaultBufferSize);
    }

    public TranscoderInputHandler(DicomOutputStream out, int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.out = out;
        this.buf = new byte[(size+7) & ~7]; // ensure buf.length = 8*n
    }

    public boolean readValue(DicomInputStream in)
    throws IOException
    {
        final int tag = in.tag();
        switch (tag) {
        case Tag.Item:
            transcodeItem(in);
            break;
        case Tag.ItemDelimitationItem:
        case Tag.SequenceDelimitationItem:
            in.readValue(in);
            break;
        default:
            if (TagUtils.isFileMetaInfoElement(tag)
                    || TagUtils.isGroupLengthElement(tag))
                in.readValue(in);
            else
                transcodeAttribute(in);
        }
        return true;
    }

    private void transcodeItem(DicomInputStream in)
    throws IOException
    {
        final DicomElement sq = in.sq();
        final VR sqvr = sq.vr();
        final int vallen = in.valueLength();
        if (vallen == -1 || sqvr == VR.SQ) {
            out.writeHeader(Tag.Item, null, -1);
            in.readValue(in);
            out.writeHeader(Tag.ItemDelimitationItem, null, 0);
        } else {
            out.writeHeader(Tag.Item, null, in.valueLength());
            transcodeValue(in, sqvr);
        }
    }

    private void transcodeAttribute(DicomInputStream in) 
    throws IOException
    {
        final int tag = in.tag();
        final VR vr = in.vr();
        final int vallen = in.valueLength();
        final DicomObject attrs = in.getDicomObject();
        if (vallen == -1 || vr == VR.SQ) {
            out.writeHeader(tag, vr, -1);
            TransferSyntax prevTS = out.getTransferSyntax();
            if (vr == VR.UN) {
                out.setTransferSyntax(TransferSyntax.ImplicitVRLittleEndian);
            }
            in.readValue(in);
            attrs.remove(tag);
            out.writeHeader(Tag.SequenceDelimitationItem, null, 0);
            out.setTransferSyntax(prevTS);
        } else if (!TagUtils.isGroupLengthElement(tag)) {
            out.writeHeader(tag, vr, vallen);
            if (tag == Tag.SpecificCharacterSet
                    || TagUtils.isPrivateCreatorDataElement(tag)) {
                byte[] val = in.readBytes(vallen);
                boolean bigEndian = in.getTransferSyntax().bigEndian();
                attrs.putBytes(tag, vr, val, bigEndian);
                out.write(val);
            } else {
                transcodeValue(in, vr);
            }
        }
     }

    private void transcodeValue(DicomInputStream in, VR vr)
    throws IOException
    {
        boolean toggleEndian = out.getTransferSyntax().bigEndian()
                             != in.getTransferSyntax().bigEndian();
        int remaining = in.valueLength();
        while (remaining > 0)
        {
            int len = Math.min(remaining, buf.length);
            in.readFully(buf, 0, len);
            if (toggleEndian)
                vr.toggleEndian(buf, 0, len);
            out.write(buf, 0, len);
            remaining -= len;
        }
    }

}
