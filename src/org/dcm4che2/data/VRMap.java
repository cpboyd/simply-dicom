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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dcm4che2.util.IntHashtable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class VRMap implements Serializable {

    private static final long serialVersionUID = 6581801202183118918L;

    private static final String USAGE = "Usage: mkvrmap <xml-file> <resource-file>\n"
            + "         (Store serialized VRMap in <resource-file>.)\n"
            + "       mkvrmap <xml-file> <resource-name> <zip-file>\n"
            + "         (Create <zip-file> with serialized VRMap under <resource-name>\n"
            + "          and appendant META-INF/dcm4che/org.dcm4che2.data.VRMap.)\n";

    private static final VRMap DEFAULT = new VRMap();

    private static VRMap vrMap;

    private static Hashtable<String, VRMap> privVRMaps;

    static {
        VRMap.reloadVRMaps();
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        VRMap vrMap = new VRMap(2300);
        try {
            vrMap.loadXML(new File(args[0]));
            if (args.length > 2) {
                ResourceLocator.createResource(args[1], vrMap,
                        new File(args[2]));
                System.out.println("Create VRMap Resource  - " + args[2]);
            } else {
                ResourceLocator.serializeTo(vrMap, new File(args[1]));
                System.out.println("Serialize VRMap to - " + args[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadVRMaps() {
        VRMap newVRMap = null;
        Hashtable<String, VRMap> newPrivVRMaps = new Hashtable<String, VRMap>();
        List<String> list = ResourceLocator.findResources(VRMap.class);
        for (String s : list) {
            VRMap m = (VRMap) ResourceLocator.loadResource(s);
            if (m.getPrivateCreator() == null) {
                newVRMap = m;
            } else {
                newPrivVRMaps.put(m.getPrivateCreator(), m);
            }
        }
        VRMap.vrMap = newVRMap;
        VRMap.privVRMaps = newPrivVRMaps;
    }

    public static void loadVRMap(String resourceName) {
        VRMap m = (VRMap) ResourceLocator.loadResource(resourceName);
        if (m.getPrivateCreator() == null) {
            VRMap.vrMap = m;
        } else {
            VRMap.privVRMaps.put(m.getPrivateCreator(), m);
        }
    }

    public static VRMap getVRMap() {
        return maskNull(vrMap);
    }

    public static VRMap getPrivateVRMap(String creatorID) {
        return maskNull(creatorID != null && creatorID.length() != 0 ? (VRMap) privVRMaps
                .get(creatorID)
                : vrMap);
    }

    private static VRMap maskNull(VRMap vrMap) {
        return vrMap != null ? vrMap : DEFAULT;
    }

    private transient IntHashtable<VR> table;

    private transient String privateCreator;

    private VRMap() {
        // private c'tor to avoid instantiation
    }

    private VRMap(int initialCapacity) {
        this.table = new IntHashtable<VR>(initialCapacity);
    }

    public final String getPrivateCreator() {
        return privateCreator;
    }

    private void writeObject(final ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeObject(privateCreator);
        os.writeInt(table.size());
        try {
            table.accept(new IntHashtable.Visitor() {
                public boolean visit(int key, Object value) {
                    try {
                        os.writeInt(key);
                        os.writeShort(((VR) value).code);
                        return true;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw (IOException) e;
        }
    }

    private void readObject(ObjectInputStream is) throws IOException,
            ClassNotFoundException {
        is.defaultReadObject();
        privateCreator = (String) is.readObject();
        int size = is.readInt();
        table = new IntHashtable<VR>(size);
        for (int i = 0, tag, code; i < size; ++i) {
            tag = is.readInt();
            code = is.readUnsignedShort();
            table.put(tag, VR.valueOf(code));
        }
    }

    public VR vrOf(int tag) {
        if ((tag & 0x0000ffff) == 0) // Group Length
            return VR.UL;
        if ((tag & 0xffff0000) == 0) // Command Element
            return vrOfCommand(tag);
        if ((tag & 0x00010000) != 0) { // Private Element
            if ((tag & 0x0000ff00) == 0)
                if ((tag & 0x000000f0) == 0) //Invalid tag
                    return VR.UN;
                else // Private Creator
                    return VR.LO;
            tag &= 0xffff00ff;
        } else {
            final int ggg00000 = tag & 0xffe00000;
            if (ggg00000 == 0x50000000 || ggg00000 == 0x60000000)
                tag &= 0xff00ffff; // (50xx,eeee), (60xx,eeee)
        }
        if (table == null)
            return VR.UN;
        VR vr = table.get(tag);
        return vr != null ? vr : VR.UN;
    }

    private VR vrOfCommand(int tag) {
        switch (tag) {
        case 0x00000600: // MoveDestination
        case 0x00000200: // Initiator
        case 0x00000300: // Receiver
        case 0x00000400: // Find Location
        case 0x00001030: // MoveOriginatorAET
            return VR.AE;
        case 0x00000901: // OffendingElement:
        case 0x00001005: // AttributeIdentifierList
        case 0x00004000: // DIALOG Receiver
        case 0x00004010: // Terminal Type
        case 0x00005110: // Display Format
        case 0x00005120: // Page Position ID
            return VR.AT;
        case 0x00000010: // Recognition Code
        case 0x00005130: // Text Format ID
        case 0x00005140: // Normal/Reverse
        case 0x00005150: // Add Gray Scale
        case 0x00005160: // Borders
        case 0x00005180: // Magnification Type
        case 0x00005190: // Erase
        case 0x000051A0: // Print
            return VR.CS;
        case 0x00005170: // Copies
            return VR.IS;
        case 0x00000902: // ErrorComment:
            return VR.LO;
        case 0x00005010: // Message Set ID
        case 0x00005020: // End Message ID
            return VR.SH;
        case 0x00000002: // AffectedSOPClassUID:
        case 0x00000003: // RequestedSOPClassUID:
        case 0x00001000: // AffectedSOPInstanceUID:
        case 0x00001001: // RequestedSOPInstanceUID:
            return VR.UI;
        case 0x00000001: // Length to End
            return VR.UL;
        case 0x00000100: // CommandField:
        case 0x00000110: // MessageID:
        case 0x00000120: // MessageIDToBeingRespondedTo:
        case 0x00000850: // Number of Matches
        case 0x00000860: // Response Sequence Number
        case 0x00000700: // Priority:
        case 0x00000800: // DataSetType:
        case 0x00000900: // Status:
        case 0x00000903: // ErrorID:
        case 0x00001002: // EventTypeID:
        case 0x00001008: // ActionTypeID:
        case 0x00001020: // NumberOfRemainingSubOperations:
        case 0x00001021: // NumberOfCompletedSubOperations:
        case 0x00001022: // NumberOfFailedSubOperations:
        case 0x00001023: // NumberOfWarningSubOperations:
        case 0x00001031: // MoveOriginatorMessageID:
        case 0x000051B0: // Overlays
            return VR.US;
        }
        return VR.UN;
    }

    public void loadXML(File f) throws IOException, SAXException {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(f, new SAXAdapter());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
    }

    private final class SAXAdapter extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {
            if ("element".equals(qName)) {
                int tag = (int) Long.parseLong(attributes.getValue("tag")
                        .replace('x', '0'), 16);
                String vrstr = attributes.getValue("vr");
                if (vrstr != null && vrstr.length() != 0) {
                    VR vr = "US|SS|OW".equals(vrstr) ? VR.OW 
                            : VR.valueOf(vrstr.charAt(0) << 8
                                    | vrstr.charAt(1));
                    table.put(tag, vr);
                }
            } else if ("dictionary".equals(qName)) {
                privateCreator = attributes.getValue("creator");
            }
        }
    }
}
