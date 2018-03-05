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

package de.maggu2810.playground.ftpserver.programmatic.internal.net.ftp.osgi;

import org.apache.ftpserver.ftplet.FtpException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import de.maggu2810.playground.ftpserver.programmatic.AccessInfo;
import de.maggu2810.playground.ftpserver.programmatic.FtpServerService;
import de.maggu2810.playground.ftpserver.programmatic.internal.net.ftp.FtpServerImpl;
import de.maggu2810.playground.ftpserver.programmatic.internal.net.ftp.FtpServerImpl.AccessInfoImpl;

@ObjectClassDefinition(name = "FTP Server Configuration")
@interface ServerConfig {

    int port() default 21;

    String passivePorts() default "-";
}

/**
 * OSGi component providing {@link FtpServerService}.
 */
@Component(name = "de.maggu2810.playground.ftpserver.programmatic")
@Designate(ocd = ServerConfig.class)
public class FtpServerServiceImpl implements FtpServerService {

    private @Nullable FtpServerImpl ftpServer;

    private static AccessInfoImpl check(final AccessInfo info) {
        if (info instanceof AccessInfoImpl) {
            return (AccessInfoImpl) info;
        } else {
            throw new IllegalStateException(
                    String.format("Expect our custom access information (recv.: %s)", info.getClass()));
        }
    }

    /**
     * Constructor.
     */
    public FtpServerServiceImpl() {
    }

    @Activate
    protected void activate(final ServerConfig config) throws FtpException {
        final FtpServerImpl ftpServer = this.ftpServer = new FtpServerImpl(config.port(), config.passivePorts());
        ftpServer.open();
    }

    @Deactivate
    protected void deactivate() {
        final FtpServerImpl ftpServer = this.ftpServer;
        if (ftpServer != null) {
            this.ftpServer = null;
            ftpServer.close();
        }
    }

    @Override
    public void add(final AccessInfo info) {
        final FtpServerImpl ftpServer = this.ftpServer;
        if (ftpServer == null) {
            throw new IllegalStateException("FTP server seems to be not open.");
        }
        ftpServer.add(check(info));
    }

    @Override
    public void remove(final AccessInfo info) {
        final FtpServerImpl ftpServer = this.ftpServer;
        if (ftpServer == null) {
            throw new IllegalStateException("FTP server seems to be not open.");
        }
        ftpServer.remove(check(info));
    }

}
