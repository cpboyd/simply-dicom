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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.CloseUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 13898 $ $Date: 2010-08-19 13:50:09 +0200 (Thu, 19 Aug 2010) $
 * @since Jul 12, 2005
 */
public class ContentHandlerAdapter extends DefaultHandler {
    private static enum State {
        EXPECT_ELM, EXPECT_VAL_OR_FIRST_ITEM, EXPECT_FRAG, EXPECT_NEXT_ITEM;
    }
    
    private State state = State.EXPECT_ELM;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final StringBuffer sb = new StringBuffer();    
    private final Stack<DicomElement> sqStack = new Stack<DicomElement>();
    private DicomObject attrs;
    private int tag;
    private VR vr;
    private String src;
    private Locator locator;
    private static final byte[] EMPTY_VALUE = {};

    public ContentHandlerAdapter(DicomObject attrs) {
        this.attrs = attrs;
    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) {
        if ("attr".equals(qName)) {
            onStartElement(atts.getValue("tag"), atts.getValue("vr"),
                    atts.getValue("src"));
        } else if ("item".equals(qName)) {
            onStartItem(atts.getValue("off"), atts.getValue("src"));
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if ("attr".equals(qName)) {
            onEndElement();
        } else if ("item".equals(qName)) {
            onEndItem();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if ((state == State.EXPECT_VAL_OR_FIRST_ITEM && vr != VR.SQ)
                || state == State.EXPECT_FRAG) {
            sb.append(ch, start, length);
            vr.parseXMLValue(sb, out, false, attrs.getSpecificCharacterSet());
        }
    }

    private void onStartElement(String tagStr, String vrStr, String src) {
        if (state != State.EXPECT_ELM)
            throw new IllegalStateException("state:" + state);
        this.tag = (int) Long.parseLong(tagStr,16);
        this.vr = vrStr == null ? attrs.vrOf(tag)
                : VR.valueOf(vrStr.charAt(0) << 8 | vrStr.charAt(1));
        state = State.EXPECT_VAL_OR_FIRST_ITEM;
        this.src = src;
    }

    private void onStartItem(String offStr, String src) {
        this.src = src;

        if (state != State.EXPECT_VAL_OR_FIRST_ITEM
                && state != State.EXPECT_NEXT_ITEM) {
            throw new IllegalStateException("state:" + state);
        }

        if (state == State.EXPECT_VAL_OR_FIRST_ITEM) {
            sqStack.push(vr == VR.SQ ? attrs.putSequence(tag) : attrs
                    .putFragments(tag, vr, false));
        }

        DicomElement sq = sqStack.peek();
        if (sq.vr() == VR.SQ) {
            DicomObject parent = attrs;
            attrs = new BasicDicomObject();
            ((BasicDicomObject) attrs).setParent(parent);
            if (offStr != null) {
                attrs.setItemOffset(Long.parseLong(offStr));
            }
            sq.addDicomObject(attrs);
            state = State.EXPECT_ELM;
        } else {
            sb.setLength(0);
            state = State.EXPECT_FRAG;
        }
    }
 
    private void onEndItem() throws SAXException {
        switch (state) {
        case EXPECT_ELM:
            attrs = attrs.getParent();
            break;
        case EXPECT_FRAG:
            DicomElement sq = sqStack.peek();
            byte[] data =  getValue(sq.vr(), null);
            sq.addFragment(data != null ? data : EMPTY_VALUE);
            sb.setLength(0);
            out.reset();
            break;
        default:
            throw new IllegalStateException("state:" + state);
        }
        state = State.EXPECT_NEXT_ITEM;
    }

    private void onEndElement() throws SAXException {
        switch (state) {
        case EXPECT_VAL_OR_FIRST_ITEM:
            if (vr == VR.SQ) {
                attrs.putNull(tag, VR.SQ);
            } else {
                attrs.putBytes(tag, vr, getValue(vr, attrs.getSpecificCharacterSet()), 
                        false);
                sb.setLength(0);
                out.reset();
            }
            break;
        case EXPECT_NEXT_ITEM:
            sqStack.pop();
            break;
        default:
            throw new IllegalStateException("state:" + state);
        }
        state = State.EXPECT_ELM;
    }

    private byte[] getValue(VR vr, SpecificCharacterSet cs)
            throws SAXException {
        if (src == null)
            return vr.parseXMLValue(sb, out, true, cs);
        if (src.length() == 0)
            return EMPTY_VALUE;
        return readFromSrc();
    }    
    
    private byte[] readFromSrc() throws SAXException {
        URL url;
        try {
            url = new URL(src);
        } catch (MalformedURLException e) {
            String systemId = locator.getSystemId();
            if (systemId == null) {
                throw new SAXException("Missing systemId which is needed " +
                        "for resolving relative src: " + src);
            }
            try {
                url = new URL(
                        systemId.substring(0, systemId.lastIndexOf('/')+1) + src);
            } catch (MalformedURLException e1) {
                throw new SAXException(
                        "Invalid reference to external value src: " + src);
            }
        }
        DataInputStream in = null;
        try {
            URLConnection con = url.openConnection();
            in = new DataInputStream(con.getInputStream());
            int len = con.getContentLength();
            byte[] data = new byte[(len + 1) & ~1];
            in.readFully(data, 0, len);
            return data;
        } catch (IOException e) {
            throw new SAXException("Failed to read value from external src: "
                    + url, e);
        } finally {
            CloseUtils.safeClose(in);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
}
