/*-
 * #%L
 * ftpserver-programmatic
 * %%
 * Copyright (C) 2018 maggu2810
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.maggu2810.playground.ftpserver.programmatic;

import java.net.UnknownHostException;

import de.maggu2810.playground.ftpserver.programmatic.internal.net.ftp.FtpServerImpl;

/**
 * Factory for access information.
 */
public class AccessInfoFactory {

    private AccessInfoFactory() {
    }

    /**
     * Create a new access information.
     *
     * @param localHost the local host
     * @param localPort the local port (-1 if not known and check should be ignored)
     * @param remoteHost the remote host
     * @param remotePort the remote port (-1 if not known and check should be ignored)
     * @param username the FTP username
     * @param password the FTP password
     * @param homeDir the directory the FTP server should use for that user
     * @return a newly created access information
     * @throws UnknownHostException if hostname resolution fails
     */
    public static AccessInfo create(final String localHost, final int localPort, final String remoteHost,
            final int remotePort, final String username, final String password, final String homeDir)
            throws UnknownHostException {
        return new FtpServerImpl.AccessInfoImpl(localHost, localPort, remoteHost, remotePort, username, password,
                homeDir);
    }

}
