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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.ftpserver.ipfilter.SessionFilter;
import org.apache.mina.core.session.IoSession;
import org.checkerframework.checker.nullness.qual.NonNull;

import de.maggu2810.playground.ftpserver.programmatic.internal.net.ConnectionInfo;

/**
 * A FTP session filter.
 *
 * <p>
 * This session filter allows only connection if the session's connection info matches to a whitelisted one.
 */
public class SessionFilterImpl implements SessionFilter {

    private final Collection<ConnectionInfo> allowed = new CopyOnWriteArrayList<>();

    /**
     * Add an allowed connection information.
     *
     * @param info the information
     */
    public void add(final ConnectionInfo info) {
        allowed.add(info);
    }

    /**
     * Remove an allowed connection information.
     *
     * @param info the information
     */
    public void remove(final ConnectionInfo info) {
        allowed.remove(info);
    }

    @Override
    public boolean accept(final IoSession session) {
        final ConnectionInfo pair = ConnectionInfo.create(session);
        if (pair == null) {
            return false;
        }
        final @NonNull ConnectionInfo nnPair = pair;
        return allowed.stream().anyMatch(ok -> ok.match(nnPair));
    }

}
