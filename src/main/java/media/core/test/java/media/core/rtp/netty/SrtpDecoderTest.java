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

package media.core.test.java.media.core.rtp.netty;

import io.netty.channel.embedded.EmbeddedChannel;
import media.core.rtp.rtp.RtpPacket;
import media.core.rtp.rtp.crypto.PacketTransformer;
import media.core.rtp.rtp.netty.SrtpDecoder;
import media.core.rtp.rtp.secure.SrtpPacket;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class SrtpDecoderTest {

    @Test
    public void testDecode() {
        // given
        final SrtpPacket srtpPacket = mock(SrtpPacket.class);
        final PacketTransformer transformer = mock(PacketTransformer.class);
        final SrtpDecoder decoder = new SrtpDecoder(transformer);
        final EmbeddedChannel channel = new EmbeddedChannel(decoder);
        
        final byte[] encodedPayload = "encoded".getBytes();
        final byte[] decodedPayload = "decoded".getBytes();
        
        when(srtpPacket.getRawData()).thenReturn(encodedPayload);
        when(transformer.reverseTransform(encodedPayload, 0, encodedPayload.length)).thenReturn(decodedPayload);
        
        // when
        channel.writeInbound(srtpPacket);
        Object inboundObject = channel.readInbound();
        
        // then
        verify(transformer).reverseTransform(eq(encodedPayload), any(Integer.class), any(Integer.class));
        verify(srtpPacket).wrap(decodedPayload);
        assertNotNull(inboundObject);
        assertTrue(inboundObject instanceof RtpPacket);
    }

    @Test
    public void testDecodeFailure() {
        // given
        final SrtpPacket srtpPacket = mock(SrtpPacket.class);
        final PacketTransformer transformer = mock(PacketTransformer.class);
        final SrtpDecoder decoder = new SrtpDecoder(transformer);
        final EmbeddedChannel channel = new EmbeddedChannel(decoder);
        
        final byte[] encodedPayload = "encoded".getBytes();
        final byte[] decodedPayload = null;
        
        when(srtpPacket.getRawData()).thenReturn(encodedPayload);
        when(transformer.reverseTransform(encodedPayload, 0, encodedPayload.length)).thenReturn(decodedPayload);
        
        // when
        channel.writeInbound(srtpPacket);
        Object inboundObject = channel.readInbound();
        
        // then
        verify(transformer).reverseTransform(eq(encodedPayload), any(Integer.class), any(Integer.class));
        verify(srtpPacket, never()).wrap(decodedPayload);
        assertNull(inboundObject);
    }
    
    
}
