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

package media.core.sdp;

import media.core.sdp.attributes.*;
import media.core.sdp.attributes.parser.*;
import media.core.sdp.dtls.attributes.FingerprintAttribute;
import media.core.sdp.dtls.attributes.SetupAttribute;
import media.core.sdp.dtls.attributes.parser.FingerprintAttributeParser;
import media.core.sdp.dtls.attributes.parser.SetupAttributeParser;
import media.core.sdp.fields.*;
import media.core.sdp.fields.parser.*;
import media.core.sdp.ice.attributes.CandidateAttribute;
import media.core.sdp.ice.attributes.IceLiteAttribute;
import media.core.sdp.ice.attributes.IcePwdAttribute;
import media.core.sdp.ice.attributes.IceUfragAttribute;
import media.core.sdp.ice.attributes.parser.CandidateAttributeParser;
import media.core.sdp.ice.attributes.parser.IceLiteAttributeParser;
import media.core.sdp.ice.attributes.parser.IcePwdAttributeParser;
import media.core.sdp.ice.attributes.parser.IceUfragAttributeParser;
import media.core.sdp.rtcp.attributes.RtcpAttribute;
import media.core.sdp.rtcp.attributes.RtcpMuxAttribute;
import media.core.sdp.rtcp.attributes.parser.RtcpAttributeParser;
import media.core.sdp.rtcp.attributes.parser.RtcpMuxAttributeParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a pipeline composed of {@link SdpParser} objects that can be used to
 * parse SDP dynamically.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public class SdpParserPipeline {

	private final Map<Character, SdpParser<? extends SdpField>> fieldParsers;
	private final Map<String, SdpParser<? extends AttributeField>> attributeParsers;

	public SdpParserPipeline() {
		this.fieldParsers = new HashMap<Character, SdpParser<?>>(6);
		this.attributeParsers = new HashMap<String, SdpParser<? extends AttributeField>>(16);

		// SDP Fields
		this.fieldParsers.put(VersionField.FIELD_TYPE, new VersionFieldParser());
		this.fieldParsers.put(OriginField.FIELD_TYPE, new OriginFieldParser());
		this.fieldParsers.put(SessionNameField.FIELD_TYPE, new SessionNameFieldParser());
		this.fieldParsers.put(TimingField.FIELD_TYPE, new TimingFieldParser());
		this.fieldParsers.put(ConnectionField.FIELD_TYPE, new ConnectionFieldParser());
		this.fieldParsers.put(MediaDescriptionField.FIELD_TYPE, new MediaDescriptionFieldParser());
		
		// SDP attributes
		ConnectionModeAttributeParser connModeParser = new ConnectionModeAttributeParser();
		this.attributeParsers.put(ConnectionModeAttribute.SENDONLY, connModeParser);
		this.attributeParsers.put(ConnectionModeAttribute.RECVONLY, connModeParser);
		this.attributeParsers.put(ConnectionModeAttribute.SENDRECV, connModeParser);
		this.attributeParsers.put(ConnectionModeAttribute.INACTIVE, connModeParser);
		this.attributeParsers.put(PacketTimeAttribute.ATTRIBUTE_TYPE, new PacketTimeAttributeParser());
		this.attributeParsers.put(MaxPacketTimeAttribute.ATTRIBUTE_TYPE, new MaxPacketTimeAttributeParser());
		this.attributeParsers.put(RtpMapAttribute.ATTRIBUTE_TYPE, new RtpMapAttributeParser());
		this.attributeParsers.put(FormatParameterAttribute.ATTRIBUTE_TYPE, new FormatParameterAttributeParser());
		this.attributeParsers.put(RtcpAttribute.ATTRIBUTE_TYPE, new RtcpAttributeParser());
		this.attributeParsers.put(RtcpMuxAttribute.ATTRIBUTE_TYPE, new RtcpMuxAttributeParser());
		this.attributeParsers.put(SsrcAttribute.ATTRIBUTE_TYPE, new SsrcAttributeParser());
		
		// ICE attributes
		this.attributeParsers.put(IceLiteAttribute.ATTRIBUTE_TYPE, new IceLiteAttributeParser());
		this.attributeParsers.put(IcePwdAttribute.ATTRIBUTE_TYPE, new IcePwdAttributeParser());
		this.attributeParsers.put(IceUfragAttribute.ATTRIBUTE_TYPE, new IceUfragAttributeParser());
		this.attributeParsers.put(CandidateAttribute.ATTRIBUTE_TYPE, new CandidateAttributeParser());
		
		// DTLS attributes
		this.attributeParsers.put(SetupAttribute.ATTRIBUTE_TYPE, new SetupAttributeParser());
		this.attributeParsers.put(FingerprintAttribute.ATTRIBUTE_TYPE, new FingerprintAttributeParser());
	}

	/**
	 * Adds a parser to the pipeline.
	 * 
	 * @param parser
	 *            The parser to be registered
	 */
	public void addFieldParser(char type, SdpParser<? extends SdpField> parser) {
		synchronized (this.fieldParsers) {
			this.fieldParsers.put(type, parser);
		}
	}

	/**
	 * Removes an existing parser from the pipeline.
	 * 
	 * @param parser
	 *            The parser to be removed from the pipeline
	 */
	public void removeFieldParser(char type) {
		synchronized (this.fieldParsers) {
			this.fieldParsers.remove(type);
		}
	}

	/**
	 * Removes all registered parsers from the pipeline.
	 */
	public void removeAllFieldParsers() {
		synchronized (fieldParsers) {
			this.fieldParsers.clear();
		}
	}

	/**
	 * Gets the field parser capable of parsing a field of a certain type.
	 * 
	 * @param type
	 *            the type of field to be parsed
	 * @return The parser capable of parsing the field. Returns null if no
	 *         capable parser is registered.
	 */
	public SdpParser<? extends SdpField> getFieldParser(char type) {
		return this.fieldParsers.get(type);
	}

	/**
	 * Adds an attribute parser to the pipeline.
	 * 
	 * @param parser
	 *            The parser to be registered
	 */
	public void addAttributeParser(String type, SdpParser<? extends AttributeField> parser) {
		synchronized (this.attributeParsers) {
			this.attributeParsers.put(type, parser);
		}
	}
	
	/**
	 * Removes an existing attribute parser from the pipeline.
	 * 
	 * @param parser
	 *            The parser to be removed from the pipeline
	 */
	public void removeAttributeParser(String type) {
		synchronized (this.attributeParsers) {
			this.attributeParsers.remove(type);
		}
	}
	
	/**
	 * Removes all registered attribute parsers from the pipeline.
	 */
	public void removeAllAttributeParsers() {
		synchronized (this.attributeParsers) {
			this.attributeParsers.clear();
		}
	}
	
	/**
	 * Gets the attribute parser capable of parsing an attribute field of a certain type.
	 * 
	 * @param type
	 *            the type of attribute to be parsed
	 * @return The parser capable of parsing the field. Returns null if no
	 *         capable parser is registered.
	 */
	public SdpParser<? extends AttributeField> getAttributeParser(String type) {
		return this.attributeParsers.get(type);
	}

}
