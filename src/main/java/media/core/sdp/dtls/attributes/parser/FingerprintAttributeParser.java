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

package media.core.sdp.dtls.attributes.parser;

import media.core.sdp.SdpException;
import media.core.sdp.SdpParser;
import media.core.sdp.dtls.attributes.FingerprintAttribute;
import media.core.sdp.fields.AttributeField;

import java.util.regex.Pattern;

/**
 * Parses SDP text to construct {@link FingerprintAttribute} objects.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public class FingerprintAttributeParser implements SdpParser<FingerprintAttribute> {

	private static final String REGEX = "^a=fingerprint:\\S+\\s(\\w+(:\\w+)+)$";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	@Override
	public boolean canParse(String sdp) {
		if(sdp == null || sdp.isEmpty()) {
			return false;
		}
		return PATTERN.matcher(sdp.trim()).matches();
	}

	@Override
	public FingerprintAttribute parse(String sdp) throws SdpException {
		try {
			int separator = sdp.indexOf(AttributeField.ATTRIBUTE_SEPARATOR);
			if(separator == -1) {
				throw new IllegalArgumentException("No value found");
			}
			
			String[] values = sdp.trim().substring(separator + 1).split(" ");
			String hashFunction = values[0];
			String fingerprint = values[1];
			return new FingerprintAttribute(hashFunction, fingerprint);
		} catch (Exception e) {
			throw new SdpException(PARSE_ERROR + sdp, e);
		}
	}

	@Override
	public void parse(FingerprintAttribute field, String sdp) throws SdpException {
		try {
			int separator = sdp.indexOf(AttributeField.ATTRIBUTE_SEPARATOR);
			if(separator == -1) {
				throw new IllegalArgumentException("No value found");
			}
			
			String[] values = sdp.trim().substring(separator + 1).split(" ");
			String hashFunction = values[0];
			String fingerprint = values[1];
			field.setHashFunction(hashFunction);
			field.setFingerprint(fingerprint);
		} catch (Exception e) {
			throw new SdpException(PARSE_ERROR + sdp, e);
		}
	}

}
