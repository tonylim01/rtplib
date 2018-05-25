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

import media.core.sdp.attributes.ConnectionModeAttribute;
import media.core.sdp.dtls.attributes.FingerprintAttribute;
import media.core.sdp.dtls.attributes.SetupAttribute;
import media.core.sdp.fields.*;
import media.core.sdp.ice.attributes.IceLiteAttribute;
import media.core.sdp.ice.attributes.IcePwdAttribute;
import media.core.sdp.ice.attributes.IceUfragAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class SessionDescription implements SessionLevelAccessor {
	
	private static final String NEWLINE = "\n";
	private final StringBuilder builder;
	
	// SDP fields (session-level)
	private VersionField version;
	private OriginField origin;
	private SessionNameField sessionName;
	private ConnectionField connection;
	private TimingField timing;
	private ConnectionModeAttribute connectionMode;
	
	// ICE attributes (session-level)
	private IceLiteAttribute iceLite;
	private IcePwdAttribute icePwd;
	private IceUfragAttribute iceUfrag;
	
	// WebRTC attributes (session-level)
	private FingerprintAttribute fingerprint;
	private SetupAttribute setup;

	// Media Descriptions
	private final Map<String, MediaDescriptionField> mediaMap;
	   
	public SessionDescription() {
		this.builder = new StringBuilder();
		this.mediaMap = new HashMap<String, MediaDescriptionField>(5);
	}
	
	public VersionField getVersion() {
		return version;
	}
	
	public void setVersion(VersionField version) {
		this.version = version;
	}
	
	public OriginField getOrigin() {
		return origin;
	}
	
	public void setOrigin(OriginField origin) {
		this.origin = origin;
	}
	
	public SessionNameField getSessionName() {
		return sessionName;
	}
	
	public void setSessionName(SessionNameField sessionName) {
		this.sessionName = sessionName;
	}
	
	@Override
	public ConnectionField getConnection() {
		return connection;
	}

	public ConnectionField getConnection(String media) {
		if(this.mediaMap.containsKey(media)) {
			ConnectionField mediaConn = this.mediaMap.get(media).getConnection();
			if(mediaConn != null) {
				return mediaConn;
			}
		}
		return this.connection;
	}
	
	public void setConnection(ConnectionField connection) {
		this.connection = connection;
	}
	
	public TimingField getTiming() {
		return timing;
	}
	
	public void setTiming(TimingField timing) {
		this.timing = timing;
	}
	
	@Override
	public ConnectionModeAttribute getConnectionMode() {
		return connectionMode;
	}

	public ConnectionModeAttribute getConnectionMode(String media) {
		if(this.mediaMap.containsKey(media)) {
			ConnectionModeAttribute connectionMode = this.mediaMap.get(media).getConnectionMode();
			if(connectionMode != null) {
				return connectionMode;
			}
		}
		return this.connectionMode;
	}
	
	public void setConnectionMode(ConnectionModeAttribute connectionMode) {
		this.connectionMode = connectionMode;
	}

	public IceLiteAttribute getIceLite() {
		return iceLite;
	}

	public void setIceLite(IceLiteAttribute iceLite) {
		this.iceLite = iceLite;
	}

	@Override
	public IcePwdAttribute getIcePwd() {
		return icePwd;
	}

	public void setIcePwd(IcePwdAttribute icePwd) {
		this.icePwd = icePwd;
	}

	@Override
	public IceUfragAttribute getIceUfrag() {
		return iceUfrag;
	}

	public void setIceUfrag(IceUfragAttribute iceUfrag) {
		this.iceUfrag = iceUfrag;
	}

	@Override
	public FingerprintAttribute getFingerprint() {
		return fingerprint;
	}

	public FingerprintAttribute getFingerprint(String media) {
		if(this.mediaMap.containsKey(media)) {
			FingerprintAttribute audioFingerprint = this.mediaMap.get(media).getFingerprint();
			if(audioFingerprint != null) {
				return audioFingerprint;
			}
		}
		return this.fingerprint;
	}
	
	@Override
	public SetupAttribute getSetup() {
		return this.setup;
	}
	
	public SetupAttribute getSetupAttribute(String media) {
		if(this.mediaMap.containsKey(media)) {
			SetupAttribute setup = this.mediaMap.get(media).getSetup();
			if(setup != null) {
				return setup;
			}
		}
		return this.setup;
	}
	
	public void setSetup(SetupAttribute setup) {
		this.setup = setup;
	}

	public void setFingerprint(FingerprintAttribute fingerprint) {
		this.fingerprint = fingerprint;
	}

	public MediaDescriptionField getMediaDescription(String mediaType) {
		return this.mediaMap.get(mediaType);
	}
	
	public boolean containsMediaDescription(String mediaType) {
		return this.mediaMap.containsKey(mediaType);
	}
	
	public void addMediaDescription(MediaDescriptionField media) {
		this.mediaMap.put(media.getMedia(), media);
	}
	
	public boolean containsIce() {
		// Look for session-level ICE attributes
		if(this.iceLite != null || this.iceUfrag != null || this.icePwd != null) {
			return true;
		}
		// Look for media-level ICE attributes
		if(!this.mediaMap.isEmpty()) {
			for (MediaDescriptionField media : this.mediaMap.values()) {
				if(media.containsIce()) {
					return true;
				}
			}
		}
		// No ICE attributes found
		return false;
	}
	
	public boolean containsDtls() {
		// Look for session-level DTLS attributes
		if(this.fingerprint != null) {
			return true;
		}
		// Look for media-level DTLS attributes
		if(!this.mediaMap.isEmpty()) {
			for (MediaDescriptionField media : this.mediaMap.values()) {
				if(media.containsDtls()) {
					return true;
				}
			}
		}
		// No DTLS attributes found
		return false;
	}
	
	@Override
	public String toString() {
		this.builder.setLength(0);
		append(this.version);
		append(this.origin);
		append(this.sessionName);
		append(this.connection);
		append(this.timing);
		append(this.iceLite);
		append(this.iceUfrag);
		append(this.icePwd);
		append(this.fingerprint);
		append(this.setup);
		
		for (MediaDescriptionField media : this.mediaMap.values()) {
			append(media);
		}
		this.builder.deleteCharAt(this.builder.length() - 1);
		return this.builder.toString();
	}
	
	private void append(SdpField field) {
		if(field != null) {
			this.builder.append(field.toString()).append(NEWLINE);
		}
	}
}
