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

/**
 * Container to hold an internet address and a port (with wildcard support).
 */
public class AddressPlusPort {

    /** An InetAddress that could be used to identify a wildcard (don't care one). */
    public static final InetAddress WILDCARD_ADDR = new InetSocketAddress(0).getAddress();

    /** A port that could be used to identify a wildcard (don't care one). */
    public static final int WILDCARD_PORT = -1;

    /** The address. */
    public final InetAddress addr;
    /** The port. */
    public final int port;

    /**
     * Constructor.
     *
     * @param socketAddress the socket address
     */
    public AddressPlusPort(final InetSocketAddress socketAddress) {
        this(socketAddress.getAddress(), socketAddress.getPort());
    }

    /**
     * Constructor.
     *
     * @param addr the address
     * @param port the port
     */
    public AddressPlusPort(final InetAddress addr, final int port) {
        this.addr = addr;
        this.port = port;
    }

    /**
     * Check for a match.
     *
     * <p>
     * Ignore address or port if at least one reference (this or other) is using a wildcard reference.
     *
     * @param other the other
     * @return true if a match, otherwise false
     */
    public boolean match(final AddressPlusPort other) {
        return (this.addr == WILDCARD_ADDR || other.addr == WILDCARD_ADDR || this.addr.equals(other.addr))
                && (this.port == WILDCARD_PORT || other.port == WILDCARD_PORT || this.port == other.port);
    }

    @Override
    public String toString() {
        return "AddressPlusPort [addr=" + addr + ", port=" + port + "]";
    }

}
