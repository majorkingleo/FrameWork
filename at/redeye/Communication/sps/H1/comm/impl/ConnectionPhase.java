/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

/**
 * @author Mario Mattl
 *
 */
public enum ConnectionPhase {
	
	DisConnectionAttempt,
	Disconnected,
	TCPConnectionAttempt,
	TCPConnected,
	H1ConnectionAttempt,
	H1Connected,
	
	
}
