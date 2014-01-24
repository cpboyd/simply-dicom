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

package org.dcm4che2.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.CloseUtils;
import org.dcm4che2.util.StringUtils;
import org.dcm4che2.util.TagUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SAXWriter implements DicomInputHandler {
    
    private static final String TAG_ATTR = "attr";
    private static final String TAG_DICOM = "dicom";
    private static final String TAG_ITEM = "item";
    private static final String ATTR_OFF = "off";
    private static final String ATTR_SRC = "src";
    private static final String ATTR_LEN = "len";
    private static final String ATTR_VR = "vr";
    private static final String ATTR_TAG = "tag";
    private static final int CBUF_LENGTH = 512;
    private final char[] cbuf = new char[CBUF_LENGTH];
    private ContentHandler ch;
    private LexicalHandler lh;
    private File baseDir;
    private int baseOff;
    private File file;
    private int[] exclude;
    private boolean seenFirst = false;
    private static final byte[] EMPTY_BYTES = {};

    public SAXWriter(ContentHandler ch, LexicalHandler lh) {
        this.ch = ch;
        this.lh = lh;
    }
    
    public final File getBaseDir() {
        return baseDir;
    }

    public final void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        this.baseOff = 0;
        if (baseDir != null) {
            String path = baseDir.getPath();
            baseOff = path.length();
            if (!path.endsWith(File.separator))
                ++baseOff;
        }
    }

    public final int[] getExclude() {
        return exclude != null ? (int[]) exclude.clone() : null;
    }

    public final void setExclude(int[] exclude) {
        if (exclude != null) {
            this.exclude = exclude.clone();
            Arrays.sort(exclude);
        } else {
            this.exclude = null;
        }
    }

    public void write(DicomObject attrs)
            throws SAXException, IOException {
        ch.startDocument();
        file = baseDir;
        writeContent(attrs, attrs.isRoot() ? TAG_DICOM : TAG_ITEM);
        ch.endDocument();
    }

    private void writeContent(DicomObject attrs, String qName)
            throws SAXException, IOException {
        AttributesImpl atts = new AttributesImpl();
        if (!attrs.isRoot()) {
            atts.addAttribute("", ATTR_OFF, ATTR_OFF, "",
                    Long.toString(attrs.getItemOffset()));
        }
        ch.startElement("", qName, qName, new AttributesImpl());
        for (Iterator<DicomElement> it = attrs.iterator(); it.hasNext();) {
            writeElement(attrs, it.next());
        }
        ch.endElement("", qName, qName);
    }

    private void writeElement(DicomObject attrs, DicomElement a)
            throws SAXException, IOException {
        VR vr = a.vr();
        final int tag = a.tag();
        if (file != null)
            file = new File(file, StringUtils.intToHex(tag));
        String fpath = fpath(tag, vr, a.length());
        startAttributeElement(tag, vr, a.length(), fpath, attrs);
        if (a.hasItems()) {
            for (int i = 0, n = a.countItems(); i < n; ++i) {
                writeItem(a, i);
            }
        } else {
            if (fpath != null) {
                writeToFile(a.getBytes());
            } else {
                vr.formatXMLValue(a.getBytes(), a.bigEndian(),
                        attrs.getSpecificCharacterSet(), cbuf, ch);
            }
        }
        endAttributeElement();
        if (file != null)
            file = file.getParentFile();
    }

    private void writeItem(DicomElement a, int index)
            throws SAXException, IOException {
        if (file != null)
            file = new File(file, Integer.toString(index+1));
        if (a.vr() == VR.SQ) {
            writeContent(a.getDicomObject(index), TAG_ITEM);
        } else {
            final byte[] data = a.getFragment(index);
            writeFragment(a.vr(), data, a.bigEndian(),
                     fpath(a.tag(), a.vr(), data.length));
        }
        if (file != null)
            file = file.getParentFile();
    }

    private void writeFragment(VR vr, byte[] bytes, boolean bigEndian,
            String fpath)
            throws SAXException, IOException {
        startItemElement(-1, (bytes.length + 1) & ~1, fpath);
        if (fpath != null) {
            writeToFile(bytes);
        } else {
            vr.formatXMLValue(bytes, bigEndian, null, cbuf, ch);
        }
        endItemElement();
    }

    public boolean readValue(DicomInputStream in) throws IOException {
        try {
            switch (in.tag()) {
            case Tag.Item:
            {
                final boolean isRoot = !seenFirst;
                if (isRoot) {
                    seenFirst = true;
                    file = baseDir;
                    ch.startDocument();
                }
                transcodeItem(in);
                if (isRoot)
                    ch.endDocument();
                break;
            }
            case Tag.ItemDelimitationItem:
                in.readValue(in);
                if (in.level() == 0) {
                    ch.endElement("", TAG_DICOM, TAG_DICOM);
                    ch.endDocument();
                }
                break;
            case Tag.SequenceDelimitationItem:
                in.readValue(in);
                break;
            default:
                if (!seenFirst) {
                    seenFirst = true;
                    file = baseDir;
                    ch.startDocument();
                    ch.startElement("", TAG_DICOM, TAG_DICOM, new AttributesImpl());
                }
                transcodeAttribute(in);
            }
        } catch (SAXException e) {
            throw (IOException) new IOException().initCause(e);
        }
        return true;
    }

    private void transcodeItem(DicomInputStream in) throws SAXException,
            IOException {
        final DicomElement sq = in.sq();
        final int itemLen = in.valueLength();
        final VR sqvr = sq.vr();
        final int index = sq.countItems();
        if (file != null)
            file = new File(file, Integer.toString(index+1));
        final String fpath = fpath(sq.tag(), sqvr, itemLen);
        startItemElement(in.tagPosition(), itemLen, fpath);
        in.readValue(in);
        if (sq.hasFragments() && index < sq.countItems()) {
            byte[] data = sq.getFragment(index);
            if (fpath != null) {
                writeToFile(data);
            } else {
                final boolean bigEndian = in.getTransferSyntax().bigEndian();
                sqvr.formatXMLValue(data, bigEndian, null, cbuf, ch);
            }
            sq.setFragment(index, EMPTY_BYTES); // allow gc to release byte[]
        }
        endItemElement();
        if (file != null)
            file = file.getParentFile();
    }

    private void endItemElement() throws SAXException {
        ch.endElement("", TAG_ITEM, TAG_ITEM);
    }

    private void startItemElement(long off, int itemLen, String fpath)
            throws SAXException {
        final AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", ATTR_OFF, ATTR_OFF, "", Long.toString(off));
        atts.addAttribute("", ATTR_LEN, ATTR_LEN, "", Integer.toString(itemLen));
        if (fpath != null) {
            atts.addAttribute("", ATTR_SRC, ATTR_SRC, "", fpath);
        }
        ch.startElement("", TAG_ITEM, TAG_ITEM, atts);
    }

    private void writeToFile(byte[] data) throws IOException {
        if (file == null)
            return;
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(data);
        } finally {
            CloseUtils.safeClose(out);
        }
    }

    private void transcodeAttribute(DicomInputStream in)
            throws SAXException, IOException {
        final int tag = in.tag();
        final VR vr = in.vr();
        final int vallen = in.valueLength();
        final DicomObject attrs = in.getDicomObject();
        final String tagHex = StringUtils.intToHex(tag);
        if (file != null)
            file = new File(file, tagHex);
        final String fpath = fpath(tag, vr, vallen);
        startAttributeElement(tag, vr, vallen, fpath, attrs);
        if (vallen == -1 || vr == VR.SQ) {
            in.readValue(in);
            attrs.remove(tag);
        } else {
            byte[] val = in.readBytes(vallen);
            final boolean bigEndian = in.getTransferSyntax().bigEndian();
            if (fpath != null) {
                writeToFile(val);
            } else {
                vr.formatXMLValue(val, bigEndian,
                        attrs.getSpecificCharacterSet(), cbuf, ch);
            }
            if (tag == Tag.SpecificCharacterSet
                    || tag == Tag.TransferSyntaxUID
                    || TagUtils.isPrivateCreatorDataElement(tag)) {
                attrs.putBytes(tag, vr, val, bigEndian);
            }
            if (tag == 0x00020000) {
                in.setEndOfFileMetaInfoPosition(
                        in.getStreamPosition() + vr.toInt(val, bigEndian));
            }
        }
        if (file != null)
            file = file.getParentFile();
        endAttributeElement();
    }

    private void endAttributeElement() throws SAXException {
        ch.endElement("", TAG_ATTR, TAG_ATTR);
    }

    private void startAttributeElement(int tag, VR vr, int vallen,
            String fpath, DicomObject attrs)
            throws SAXException {
        if (lh != null) {
            String name = attrs.nameOf(tag);
            lh.comment(name.toCharArray(), 0, name.length());
        }
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", ATTR_TAG, ATTR_TAG, "", StringUtils.intToHex(tag));
        atts.addAttribute("", ATTR_VR, ATTR_VR, "", vr.toString());
        atts.addAttribute("", ATTR_LEN, ATTR_LEN, "", Integer.toString(vallen));
        if (fpath != null) {
            atts.addAttribute("", ATTR_SRC, ATTR_SRC, "", fpath);                    
        }
        ch.startElement("", TAG_ATTR, TAG_ATTR, atts);
    }

    private String fpath(int tag, VR vr, int vallen) {
        return !exclude(tag, vr, vallen) ? null : file == null ? "" 
                : file.getPath().substring(baseOff)
                        .replace(File.separatorChar, '/');
    }

    private boolean exclude(int tag, VR vr, int vallen) {
        return exclude != null && vallen > 0 && vr != VR.SQ 
                && Arrays.binarySearch(exclude, tag) >= 0;
    }
}
