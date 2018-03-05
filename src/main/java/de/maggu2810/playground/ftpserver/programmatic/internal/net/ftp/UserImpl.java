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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An FTP user implementation that also holds the expected remote address.
 */
public class UserImpl implements User {

    private final String name;
    private final String password;
    private final InetAddress inetAddress;
    private int maxIdleTimeSec = 0; // no limit
    private final String homeDir;
    private final boolean isEnabled;
    private final List<Authority> authorities;

    /**
     * Constructor.
     *
     * @param name the username
     * @param password the password
     * @param inetAddress the address this users connection needs to be established from
     * @param maxIdleTimeSec max idle time in seconds
     * @param homeDir the home directory of that user to restrict FTP access
     * @param isEnabled flag if the user is enabled
     * @param authorities the list of authorities
     */
    public UserImpl(final String name, final String password, final InetAddress inetAddress, final int maxIdleTimeSec,
            final String homeDir, final boolean isEnabled, final List<? extends Authority> authorities) {
        this.name = name;
        this.password = password;
        this.inetAddress = inetAddress;
        this.maxIdleTimeSec = maxIdleTimeSec;
        this.homeDir = homeDir;
        this.isEnabled = isEnabled;
        this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets the address this users connection needs to be established from.
     *
     * @return the address the users remote needs to match
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public int getMaxIdleTime() {
        return maxIdleTimeSec;
    }

    /**
     * Get the user enable status.
     */
    @Override
    public boolean getEnabled() {
        return isEnabled;
    }

    /**
     * Get the user home directory.
     */
    @Override
    public String getHomeDirectory() {
        return homeDir;
    }

    @Override
    public @Nullable AuthorizationRequest authorize(AuthorizationRequest request) {
        boolean someoneCouldAuthorize = false;
        for (final Authority authority : authorities) {
            if (authority.canAuthorize(request)) {
                someoneCouldAuthorize = true;

                request = authority.authorize(request);

                // authorization failed, return null
                if (request == null) {
                    return null;
                }
            }

        }

        if (someoneCouldAuthorize) {
            return request;
        } else {
            return null;
        }
    }

    @Override
    public List<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public List<Authority> getAuthorities(final Class<? extends Authority> clazz) {
        return authorities.stream().filter(auth -> auth.getClass().equals(clazz)).collect(Collectors.toList());
    }

}
