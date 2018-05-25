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

package media.core.sdp.fields.parser;

import media.core.sdp.SdpException;
import media.core.sdp.SdpParser;
import media.core.sdp.fields.OriginField;

import java.util.regex.Pattern;

/**
 * Parses SDP text to construct {@link OriginField} objects.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public class OriginFieldParser implements SdpParser<OriginField> {
	
	// TODO use proper regex for IP address instead of [0-9\\.]+
	private static final String REGEX = "^o=\\S+\\s\\d+\\s\\d+\\s\\w+\\s\\w+\\s[0-9\\.]+";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean canParse(String sdp) {
		if(sdp == null || sdp.isEmpty()) {
			return false;
		}
		return PATTERN.matcher(sdp.trim()).matches();
	}

	@Override
	public OriginField parse(String sdp) throws SdpException {
		try {
			String[] values = sdp.trim().substring(2).split(" ");
			String username = values[0];
			String sessionId = values[1];
			String sessionVersion = values[2];
			String netType = values[3];
			String addressType = values[4];
			String address = values[5];
			return new OriginField(username, sessionId, sessionVersion, netType, addressType, address);
		} catch (Exception e) {
			throw new SdpException(PARSE_ERROR + sdp, e);
		}
	}

	@Override
	public void parse(OriginField field, String sdp) throws SdpException {
		try {
			String[] values = sdp.trim().substring(2).split(" ");
			field.setUsername(values[0]);
			field.setSessionId(values[1]);
			field.setSessionVersion(values[2]);
			field.setNetType(values[3]);
			field.setAddressType(values[4]);
			field.setAddress(values[5]);
		} catch (Exception e) {
			throw new SdpException(PARSE_ERROR + sdp, e);
		}
	}

}
