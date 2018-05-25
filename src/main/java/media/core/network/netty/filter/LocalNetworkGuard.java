/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
        
package media.core.network.netty.filter;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import media.core.network.deprecated.IPAddressCompare;
import media.core.network.deprecated.channel.RestrictedNetworkGuard;

import java.net.InetSocketAddress;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class LocalNetworkGuard implements NetworkGuard {

    private static final Logger log = LogManager.getLogger(RestrictedNetworkGuard.class);

    private final String network;
    private final String subnet;

    public LocalNetworkGuard(String network, String subnet) {
        this.network = network;
        this.subnet = subnet;
    }

    @Override
    public boolean isSecure(Channel channel, InetSocketAddress source) {
        byte[] networkBytes = IPAddressCompare.addressToByteArrayV4(this.network);
        byte[] subnetBytes = IPAddressCompare.addressToByteArrayV4(this.subnet);
        boolean secure = IPAddressCompare.isInRangeV4(networkBytes, subnetBytes, source.getAddress().getAddress());

        if(!secure) {
            if (log.isTraceEnabled()) {
                log.trace("Dropped insecure packet [network=" + this.network + ", subnet=" + this.subnet + ", remote address=" + source.getAddress().getHostAddress() + "]");
            }
        }

        return secure;
    }

}
