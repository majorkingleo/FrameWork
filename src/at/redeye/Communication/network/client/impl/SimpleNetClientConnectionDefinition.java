/**
 * 
 */
package at.redeye.Communication.network.client.impl;

import at.redeye.Communication.AbstractConnectionDefinition;

/**
 * @author Mario Mattl
 * 
 */
public class SimpleNetClientConnectionDefinition extends
		AbstractConnectionDefinition {

	/**
	 * @param hostname
	 * @param port
	 */
	public SimpleNetClientConnectionDefinition(String hostname, int port) {
		super(hostname, port);

	}

}
