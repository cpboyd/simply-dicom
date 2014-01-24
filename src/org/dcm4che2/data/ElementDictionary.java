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
 * Alex Kogan <akogan@radiology.northwestern.edu>
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

public class ElementDictionary implements Serializable {

    private static final long serialVersionUID = 2010071664961951181L;

    private static final String USAGE = "Usage: mkelmdic <xml-file> <resource-file>\n"
            + "         (Store serialized dictionary in <resource-file>).\n"
            + "       mkelmdic <xml-file> <resource-name> <zip-file>\n"
            + "         (Create <zip-file> with serialized dictionary under <resource-name>\n"
            + "          and appendant META-INF/dcm4che/org.dcm4che2.data.ElementDictionary.)\n";

    private static String unkown = "?";

    public static final String PRIVATE_CREATOR = "Private Creator Data Element";

    public static final String GROUP_LENGTH = "Group Length";

    private static final ElementDictionary EMPTY = new ElementDictionary();

    private static ElementDictionary stdDict;

    private static Hashtable<String, ElementDictionary> privDicts;

    static {
        try {
            ElementDictionary.reloadDictionaries();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            while (e.getCause() != null) {
                e = e.getCause();
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        ElementDictionary dict = new ElementDictionary(2300);
        try {
            dict.loadXML(new File(args[0]));
            if (args.length > 2) {
                ResourceLocator
                        .createResource(args[1], dict, new File(args[2]));
                System.out.println("Create Dictionary Resource  - " + args[2]);
            } else {
                ResourceLocator.serializeTo(dict, new File(args[1]));
                System.out.println("Serialize Dictionary to - " + args[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadDictionaries() {
        ElementDictionary newStdDict = null;
        Hashtable<String, ElementDictionary> newPrivDicts = 
                new Hashtable<String, ElementDictionary>();
        List<String> list = ResourceLocator.findResources(ElementDictionary.class);
        for (int i = 0, n = list.size(); i < n; ++i) {
            ElementDictionary d = 
                (ElementDictionary) ResourceLocator.loadResource(list.get(i));
            if (d.getPrivateCreator() == null) {
                newStdDict = d;
            } else {
                newPrivDicts.put(d.getPrivateCreator(), d);
            }
        }
        ElementDictionary.stdDict = newStdDict;
        ElementDictionary.privDicts = newPrivDicts;
    }

    public static void loadDictionary(String resourceName) {
        ElementDictionary d = (ElementDictionary) ResourceLocator
                .loadResource(resourceName);
        if (d.getPrivateCreator() == null) {
            ElementDictionary.stdDict = d;
        } else {
            ElementDictionary.privDicts.put(d.getPrivateCreator(), d);
        }
    }

    public static final String getUnkown() {
        return unkown;
    }

    public static final void setUnkown(String unkown) {
        ElementDictionary.unkown = unkown;
    }

    public final String getPrivateCreator() {
        return privateCreator;
    }

    public final String getTagClassName() {
        return tagClassName;
    }

    public static ElementDictionary getDictionary() {
        return maskNull(stdDict);
    }

    public static ElementDictionary getPrivateDictionary(String creatorID) {
        return maskNull(creatorID != null && creatorID.length() != 0 ? (ElementDictionary) privDicts
                .get(creatorID)
                : stdDict);
    }

    private static ElementDictionary maskNull(ElementDictionary dict) {
        return dict != null ? dict : EMPTY;
    }

    private transient IntHashtable<String> table;

    private transient String tagClassName;

    private transient String privateCreator;

    private Class<?> tagClass;

    private ElementDictionary() {
        // empty private c'tor.
    }

    private ElementDictionary(int initialCapacity) {
        this.table = new IntHashtable<String>(initialCapacity);
    }

    private void writeObject(final ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeObject(tagClassName);
        os.writeObject(privateCreator);
        os.writeInt(table.size());
        try {
            table.accept(new IntHashtable.Visitor() {
                public boolean visit(int key, Object value) {
                    try {
                        os.writeInt(key);
                        os.writeUTF((String) value);
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
        tagClassName = (String) is.readObject();
        privateCreator = (String) is.readObject();
        int size = is.readInt();
        table = new IntHashtable<String>(size);
        for (int i = 0, tag; i < size; ++i) {
            tag = is.readInt();
            table.put(tag, is.readUTF());
        }
    }

    public String nameOf(int tag) {
        if ((tag & 0x0000ffff) == 0)
            return GROUP_LENGTH;
        if ((tag & 0x00010000) != 0) { // Private Element
            if ((tag & 0x0000ff00) == 0)
                return PRIVATE_CREATOR;
            tag &= 0xffff00ff;
        } else if ((tag & 0xffffff00) == 0x00203100)
            tag &= 0xffffff00; // (0020,31xx) Source Image Ids
        else {
            final int ggg00000 = tag & 0xffe00000;
            if (ggg00000 == 0x50000000 || ggg00000 == 0x60000000)
                tag &= 0xff00ffff; // (50xx,eeee), (60xx,eeee)
        }
        if (table == null)
            return unkown;
        String name = table.get(tag);
        return name != null ? name : unkown;
    }

    public int tagForName(String name) {
        if (tagClassName == null) {
            throw new UnsupportedOperationException(
                    "No tag class associated to dictionary");
        }
        try {
            if (tagClass == null) {
                tagClass = ResourceLocator.loadClass(tagClassName);
            }
            return tagClass.getField(name).getInt(null);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Unknown Tag Name: " + name);
        } catch (Exception e) {
            throw new ConfigurationError(e);
        }
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
        int tag = -1;

        StringBuffer name = new StringBuffer(80);

        @Override
        public void characters(char[] ch, int start, int length) {
            if (tag != -1) {
                name.append(ch, start, length);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {
            if ("element".equals(qName)) {
                tag = (int) Long.parseLong(attributes.getValue("tag").replace(
                        'x', '0'), 16);
            } else if ("dictionary".equals(qName)) {
                tagClassName = attributes.getValue("tagclass");
                privateCreator = attributes.getValue("creator");
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("element".equals(qName)) {
                table.put(tag, name.toString());
                name.setLength(0);
                tag = -1;
            }
        }
    }

}
