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
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.UserMetadata;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A FTP user manager implementation that checks if the remote address matches on authentication.
 */
public class UserManagerImpl implements UserManager {

    private final Collection<UserImpl> users = new CopyOnWriteArrayList<>();

    /**
     * Add an user.
     *
     * @param user the user
     */
    public void add(final UserImpl user) {
        users.add(user);
    }

    /**
     * Remove an user.
     *
     * @param user the user
     */
    public void remove(final UserImpl user) {
        users.remove(user);
    }

    @Override
    public @Nullable UserImpl getUserByName(final String username) {
        return users.stream().filter(user -> user.getName().equals(username)).findFirst().orElse(null);
    }

    @Override
    public String[] getAllUserNames() {
        return users.stream().map(user -> user.getName()).toArray(String[]::new);
    }

    @Override
    public void delete(final String username) {
        throw new UnsupportedOperationException(String.format("delete user (%s) is not supported.", username));
    }

    @Override
    public void save(final User user) {
        throw new UnsupportedOperationException(String.format("save user (%s) is not supported.", user.getName()));
    }

    @Override
    public boolean doesExist(final String username) {
        return users.stream().filter(user -> user.getName().equals(username)).findFirst().isPresent();
    }

    @Override
    public User authenticate(final Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            final UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) authentication;

            final String authUsr = auth.getUsername();
            if (authUsr == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            final String authPwd = auth.getPassword();
            if (authPwd == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            final UserMetadata authMetaData = auth.getUserMetadata();
            if (authMetaData == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            final UserImpl user = getUserByName(authUsr);
            if (user == null) {
                // user does not exist
                throw new AuthenticationFailedException("Authentication failed");
            }

            final InetAddress userInetAddress = user.getInetAddress();
            if (userInetAddress != null && !userInetAddress.equals(authMetaData.getInetAddress())) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (authPwd.equals(user.getPassword())) {
                return user;
            }

            throw new AuthenticationFailedException("Authentication failed");
        } else if (authentication instanceof AnonymousAuthentication) {
            // anonymous authentication is not supported
            throw new AuthenticationFailedException("Authentication failed");
        } else {
            throw new IllegalArgumentException(
                    String.format("Authentication (%s) not supported by this user manager", authentication.getClass()));
        }
    }

    @Override
    public String getAdminName() throws FtpException {
        return "there-is-explicit-admin-user-at-all";
    }

    @Override
    public boolean isAdmin(final String username) throws FtpException {
        return false;
    }

}
