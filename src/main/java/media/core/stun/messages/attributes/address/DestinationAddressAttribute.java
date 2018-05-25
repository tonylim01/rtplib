/*
 * 
 * Code derived and adapted from the Jitsi client side STUN framework.
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package media.core.stun.messages.attributes.address;

import media.core.stun.messages.attributes.StunAttribute;

/**
 * The DESTINATION-ADDRESS is present in Send Requests of old TURN versions.
 * <p>
 * It specifies the address and port where the data is to be sent. It is encoded
 * in the same way as MAPPED-ADDRESS.
 * </p>
 */
public class DestinationAddressAttribute extends AddressAttribute {

	public static final String NAME = "DESTINATION-ADDRESS";

	public DestinationAddressAttribute() {
		super(StunAttribute.DESTINATION_ADDRESS);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
}
