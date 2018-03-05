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

package de.maggu2810.playground.ftpserver.programmatic.internal.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.session.IoSession;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An information about a connection (a pair of {@link AddressPlusPort}).
 */
public class ConnectionInfo {

    /** The local address plus port. */
    public final AddressPlusPort local;
    /** The remote address plus port. */
    public final AddressPlusPort remote;

    /**
     * Constructor.
     *
     * @param local the local
     * @param remote the remote
     */
    public ConnectionInfo(final InetSocketAddress local, final InetSocketAddress remote) {
        this(new AddressPlusPort(local), new AddressPlusPort(remote));
    }

    /**
     * Constructor.
     *
     * @param localAddr local address
     * @param localPort local port
     * @param remoteAddr remote address
     * @param remotePort remote port
     */
    public ConnectionInfo(final InetAddress localAddr, final int localPort, final InetAddress remoteAddr,
            final int remotePort) {
        this(new AddressPlusPort(localAddr, localPort), new AddressPlusPort(remoteAddr, remotePort));
    }

    /**
     * Constructor.
     *
     * @param local local
     * @param remote remote
     */
    public ConnectionInfo(final AddressPlusPort local, final AddressPlusPort remote) {
        this.local = local;
        this.remote = remote;
    }

    /**
     * Create a connection information from an I/O session.
     *
     * @param session the session
     * @return a new connection information on success, null on error.
     */
    public static @Nullable ConnectionInfo create(final IoSession session) {
        final SocketAddress saLocal = session.getLocalAddress();
        final SocketAddress saRemote = session.getRemoteAddress();
        if (saLocal instanceof InetSocketAddress && saRemote instanceof InetSocketAddress) {
            return new ConnectionInfo((InetSocketAddress) saLocal, (InetSocketAddress) saRemote);
        } else {
            return null;
        }
    }

    /**
     * Check for a match.
     *
     * @param other the other connection information
     * @return true on match, otherwise false
     */
    public boolean match(final ConnectionInfo other) {
        return local.match(other.local) && remote.match(other.remote);
    }

    @Override
    public String toString() {
        return "ConnectionInfo [local=" + local + ", remote=" + remote + "]";
    }

}
