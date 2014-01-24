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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dcm4che2.util.CloseUtils;

public class ResourceLocator {

    private static final String PREFIX = "META-INF/dcm4che/";

    public static List<String> findResources(Class<?> c) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (Enumeration<URL> configs = enumResources(PREFIX + c.getName());
                    configs.hasMoreElements();) {
                URL u = configs.nextElement();
                InputStream in = u.openStream();
                try {
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(in, "utf-8"));
                    String ln;
                    while ((ln = r.readLine()) != null) {
                        int end = ln.indexOf('#');
                        if (end >= 0)
                            ln = ln.substring(0, end);
                        ln = ln.trim();
                        if (ln.length() > 0)
                            list.add(ln);
                    }
                } finally {
                    CloseUtils.safeClose(in);
                }
            }
            return list;
        } catch (IOException e) {
            throw new ConfigurationError("Failed to find Resources for " + c, e);
        }
    }

    private static Enumeration<URL> enumResources(String name) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> e;
        return (cl != null && (e = cl.getResources(name)).hasMoreElements()) ? e
                : ResourceLocator.class.getClassLoader().getResources(name);
    }

    public static Object createInstance(String name) {
        try {
            return loadClass(name).newInstance();
        } catch (ClassNotFoundException ex) {
            throw new ConfigurationError("Class not found: " + name, ex);
        } catch (InstantiationException ex) {
            throw new ConfigurationError("Could not instantiate: " + name, ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationError("could not instantiate: " + name, ex);
        }
    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl.loadClass(name);
            }
        } catch (ClassNotFoundException ex) {
            // Class could not be loaded with thread context class loader. Use
            // fall back.
        }
        return ResourceLocator.class.getClassLoader().loadClass(name);
    }

    public static Object loadResource(String name) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is;
        if (cl == null || (is = cl.getResourceAsStream(name)) == null) {
            is = ResourceLocator.class.getClassLoader().getResourceAsStream(
                    name);
            if (is == null) {
                throw new ConfigurationError("Missing Resource: " + name);
            }
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            return ois.readObject();
        } catch (Exception e) {
            throw new ConfigurationError("Failed to load Resource " + name, e);
        } finally {
            CloseUtils.safeClose(is);
        }
    }

    public static void createResource(String name, Object o, File out)
            throws IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(out));
        try {
            zip.putNextEntry(new ZipEntry(PREFIX + o.getClass().getName()));
            zip.write(name.getBytes("utf-8"));
            zip.putNextEntry(new ZipEntry(name));
            ObjectOutputStream oos = new ObjectOutputStream(zip);
            oos.writeObject(o);
            oos.close();
        } finally {
            zip.close();
        }
    }

    public static void serializeTo(Object o, File out) throws IOException {
        FileOutputStream fos = new FileOutputStream(out);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
        } finally {
            CloseUtils.safeClose(oos);
            CloseUtils.safeClose(fos);
        }
    }
}
