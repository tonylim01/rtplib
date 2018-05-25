/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package media.core.test.java.media.core.rtcp;

import media.core.rtp.rtcp.RtcpBye;
import media.core.rtp.rtcp.RtcpHeader;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * 
 * @author amit bhayani
 *
 */
public class RtcpByeTest {

	// These values are from wireshark trace
	private byte[] p = new byte[] { (byte) 0x81, (byte) 0xcb, 0x00, 0x01, 0x56, 0x53, 0x34, 0x46 };

	public RtcpByeTest() {

	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of isValid method, of class RtpPacket.
	 */
	@Test
	public void testDecode() {
		// These values are from wireshark trace
		RtcpBye rtcpBye = new RtcpBye();
		int length = rtcpBye.decode(p, 0);

		assertEquals(p.length, length);

		assertEquals(2, rtcpBye.getVersion());
		assertFalse(rtcpBye.isPadding());
		assertEquals(1, rtcpBye.getCount());

		assertEquals(RtcpHeader.RTCP_BYE, rtcpBye.getPacketType());

		long ssrc = rtcpBye.getSsrcs()[0];

		assertEquals(1448293446, ssrc);

		assertEquals(8, rtcpBye.getLength());

	}

	@Test
	public void testEncode() {

		RtcpBye rtcpBye = new RtcpBye(false);
		rtcpBye.addSsrc(1448293446);

		byte[] rawData = new byte[256];

		int length = rtcpBye.encode(rawData, 0);

		assertEquals(p.length, length);

		for (int i = 0; i < p.length; i++) {
			assertEquals(p[i], rawData[i]);
		}
	}
}
