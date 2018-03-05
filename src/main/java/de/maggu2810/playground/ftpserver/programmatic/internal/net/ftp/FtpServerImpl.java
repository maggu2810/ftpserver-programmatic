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

package de.maggu2810.playground.ftpserver.programmatic.internal.net.ftp;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import de.maggu2810.playground.ftpserver.programmatic.AccessInfo;
import de.maggu2810.playground.ftpserver.programmatic.internal.net.ConnectionInfo;

/**
 * A custom FTP server implementation.
 */
public class FtpServerImpl implements Closeable {

    private final int port;

    private final FtpServer server;
    private final SessionFilterImpl sessionFilter;
    private final UserManagerImpl userManager;

    /**
     * Constructor.
     *
     * @param port the port the server should bind to
     * @param passivePorts the passive ports to be used for data connections. Ports can be
     *            defined as single ports, closed or open ranges. Multiple definitions can
     *            be separated by commas, for example:
     *            <ul>
     *            <li>2300 : only use port 2300 as the passive port</li>
     *            <li>2300-2399 : use all ports in the range</li>
     *            <li>2300- : use all ports larger than 2300</li>
     *            <li>2300, 2305, 2400- : use 2300 or 2305 or any port larger than 2400</li>
     *            </ul>
     */
    public FtpServerImpl(final int port, final String passivePorts) {
        this.port = port;

        final FtpServerFactory serverFactory = new FtpServerFactory();

        /*
         * CommandFactory
         * public void setCommandFactory(final CommandFactory commandFactory) {
         */
        // Keep default one as long as we don't want to limit the available commands.

        /*
         * ConnectionConfig
         * public void setConnectionConfig(final ConnectionConfig connectionConfig) {
         */
        final ConnectionConfigFactory connCfgFactory = new ConnectionConfigFactory();
        // connCfgFactory.set..(...);
        serverFactory.setConnectionConfig(connCfgFactory.createConnectionConfig());

        /*
         * FileSystemFactory
         * public void setFileSystem(final FileSystemFactory fileSystem) {
         */

        /*
         * Ftplets
         * public void setFtplets(final Map<String, Ftplet> ftplets) {
         */

        /*
         * Listeners
         * public void setListeners(final Map<String, Listener> listeners) {
         */
        final ListenerFactory listenerFactory = new ListenerFactory();
        // set the port of the listener
        listenerFactory.setPort(port);

        // void setDataConnectionConfiguration(DataConnectionConfiguration dataConnectionConfig)
        final DataConnectionConfigurationFactory dataConnConfFactory = new DataConnectionConfigurationFactory();
        dataConnConfFactory.setActiveEnabled(false);
        dataConnConfFactory.setPassivePorts(passivePorts);
        listenerFactory.setDataConnectionConfiguration(dataConnConfFactory.createDataConnectionConfiguration());

        // void setIdleTimeout(int idleTimeout)
        // void setImplicitSsl(boolean implicitSsl)
        // void setPort(int port)
        // void setServerAddress(String serverAddress)
        // void setSessionFilter(SessionFilter sessionFilter)
        sessionFilter = new SessionFilterImpl();
        listenerFactory.setSessionFilter(sessionFilter);
        // void setSslConfiguration(SslConfiguration ssl)
        // replace the default listener
        serverFactory.addListener("default", listenerFactory.createListener());

        /*
         * MessageResource
         * public void setMessageResource(final MessageResource messageResource) {
         */

        /*
         * UserManager
         * public void setUserManager(final UserManager userManager) {
         */
        userManager = new UserManagerImpl();
        serverFactory.setUserManager(userManager);

        // final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        // userManagerFactory.setFile(new File("myusers.properties"));
        // serverFactory.setUserManager(userManagerFactory.createUserManager());

        server = serverFactory.createServer();
    }

    /**
     * Open / start the FTP server.
     *
     * @throws FtpException on error
     */
    public void open() throws FtpException {
        server.start();
    }

    @Override
    public void close() {
        server.stop();
    }

    /**
     * Get the port of the FTP server.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Add an access info used for session filter and user management.
     *
     * @param info the access info
     */
    public void add(final AccessInfoImpl info) {
        sessionFilter.add(info.connInfo);
        userManager.add(info.user);
    }

    /**
     * Remove an access info used for session filter and user management.
     *
     * @param info the access info
     */
    public void remove(final AccessInfoImpl info) {
        userManager.remove(info.user);
        sessionFilter.remove(info.connInfo);
    }

    /**
     * Access information.
     */
    public static class AccessInfoImpl implements AccessInfo {
        /** The connection information. */
        public final ConnectionInfo connInfo;
        /** The user implementation. */
        public final UserImpl user;

        /**
         * Constructor.
         *
         * @param localHost the local host
         * @param localPort the local port (-1 if not known and check should be ignored)
         * @param remoteHost the remote host
         * @param remotePort the remote port (-1 if not known and check should be ignored)
         * @param username the FTP username
         * @param password the FTP password
         * @param homeDir the directory the FTP server should use for that user
         * @throws UnknownHostException if hostname resolution fails
         */
        public AccessInfoImpl(final String localHost, final int localPort, final String remoteHost,
                final int remotePort, final String username, final String password, final String homeDir)
                throws UnknownHostException {
            connInfo = new ConnectionInfo(InetAddress.getByName(localHost), localPort,
                    InetAddress.getByName(remoteHost), remotePort);
            user = new UserImpl(username, password, connInfo.remote.addr, 0, homeDir, true,
                    Stream.of(new WritePermission(), new ConcurrentLoginPermission(20, 2),
                            new TransferRatePermission(4800, 4800)).collect(Collectors.toList()));
        }
    }

}
