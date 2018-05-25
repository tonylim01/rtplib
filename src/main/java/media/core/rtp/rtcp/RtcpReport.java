/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package media.core.rtp.rtcp;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstraction of an RTCP Report.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public abstract class RtcpReport extends RtcpHeader {

	/**
	 * Source that generated the report
	 */
	public long ssrc;

	/**
	 * Reports coming from other sync sources
	 */
	protected List<RtcpReportBlock> reportBlocks;
	
	protected RtcpReport() {
		this.reportBlocks = new ArrayList<RtcpReportBlock>(RtcpPacket.MAX_SOURCES);
	}

	protected RtcpReport(boolean padding, long ssrc, int packetType) {
		super(padding, packetType);
		this.ssrc = ssrc;
		this.reportBlocks = new ArrayList<RtcpReportBlock>(RtcpPacket.MAX_SOURCES);
	}

	/**
	 * Tells whether this reports was generated by a sender or a receiver.
	 * 
	 * @return Whether this is a Sender Report or not.
	 */
	public abstract boolean isSender();
	
	public long getSsrc() {
		return this.ssrc;
	}
	
	public RtcpReportBlock[] getReportBlocks() {
		RtcpReportBlock[] blocks = new RtcpReportBlock[this.reportBlocks.size()];
		return this.reportBlocks.toArray(blocks);
	}
	
	public RtcpReportBlock getReportBlock(long ssrc) {
		for (RtcpReportBlock report : this.reportBlocks) {
			if(report.getSsrc() == ssrc) {
				return report;
			}
		}
		return null;
	}

	public void addReceiverReport(RtcpReportBlock rtcpReceptionReportItem) {
		if(this.count >= RtcpPacket.MAX_SOURCES) {
			throw new ArrayIndexOutOfBoundsException("Reached maximum number of items: "+ RtcpPacket.MAX_SOURCES);
		}
		this.reportBlocks.add(rtcpReceptionReportItem);
		this.count++;
	}
}
