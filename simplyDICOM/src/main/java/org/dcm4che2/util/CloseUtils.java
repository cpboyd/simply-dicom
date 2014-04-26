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

package org.dcm4che2.util;

/// CPB Edit: 12/11/2013

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

public final class CloseUtils {
	private static final String TAG = "dcm4che2.util.CloseUtils";

    /**
     * Safely close, absorbing exceptions and handling <code>null</code>
     * graciously.
     * 
     * @param object object to close.
     */
    public static void safeClose(Closeable object) {
        try {
            if (object != null) {
                object.close();
            }
        } catch (IOException e) {
            log(object, e);
        }
    }

    /**
     * Safely close a socket, absorbing exceptions and handling
     * <code>null</code> graciously.
     * 
     * @param socket object to close.
     */
    public static void safeClose(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log(socket, e);
        }
    }

    /**
     * Safely close a server socket, absorbing exceptions and handling
     * <code>null</code> graciously.
     * 
     * @param socket object to close.
     */
    public static void safeClose(ServerSocket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log(socket, e);
        }
    }

    /**
     * Safely close an ImageInputStream stream, absorbing exceptions and
     * handling <code>null</code> graciously.
     * 
     * @param is to close.
     */
    /*public static void safeClose(ImageInputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            log(is, e);
        }
    }*/
    
    /**
     * Safely close a JDBC Statement, absorbing exceptions and handling
     * <code>null</code> graciously.
     * 
     * @param statement
     *            to close.
     */
    public static void safeClose(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            log(statement, e);
        }
    }

    private static void log(Object object, Exception e) {
        Log.w(TAG, "error closing " + object.getClass().getName() + ": "
                + object.toString(), e);
    }
}
