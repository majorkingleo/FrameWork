/**
 * 
 */
package at.redeye.Communication.sps.H1.comm;

import java.io.IOException;

import at.redeye.Communication.sps.H1.comm.impl.ConnectionPhase;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionDefinition;



/**
 * @author Mario Mattl
 *
 */
public interface IH1Communication {
	
	public int handleH1Request();
	public void transmit (byte [] dataToSend) throws IOException;
	public void transmitH1Answer () throws IOException;
	public byte [] receive (int expectedBytes) throws IOException;
	public ConnectionPhase getConnectionPhase();
	public void setStopRequest ();
	public void setConnectionDefinition(H1ConnectionDefinition conndef);
	
	public void addListener (IH1CommListener listener);
	public void removeListener (IH1CommListener listener);
	public void updateListener (UpdateReason reason, byte [] data, String message);
	
	
	public final static int tsap_port = 102;
	
	public final  static int MAX_SO_TIMEOUT = 2000; // 2 seconds wait max. at read
	
	

}
