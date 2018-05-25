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
import media.core.sdp.fields.ConnectionField;
import media.core.sdp.ice.attributes.IcePwdAttribute;
import media.core.sdp.ice.attributes.IceUfragAttribute;

/**
 * Exposes access to session-level fields and attributes necessary for a Media
 * Description
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public interface SessionLevelAccessor {

	/*
	 *  Common
	 */
	ConnectionField getConnection();
	
	ConnectionModeAttribute getConnectionMode();

	/*
	 *  DTLS
	 */
	FingerprintAttribute getFingerprint();
	
	SetupAttribute getSetup();

	/*
	 *  ICE
	 */
	IceUfragAttribute getIceUfrag();

	IcePwdAttribute getIcePwd();

}
