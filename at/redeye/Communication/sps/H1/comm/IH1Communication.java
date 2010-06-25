/**
 * 
 */
package at.redeye.Communication.sps.H1.comm;

import java.io.IOException;

import at.redeye.Communication.ConnectionPhase;
import at.redeye.Communication.UpdateReason;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionDefinition;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionException;

/**
 * @author Mario Mattl
 *
 */
public interface IH1Communication {

    public int handleH1Request();

    public void transmitH1Answer() throws IOException, H1ConnectionException;

    public byte[] receive(int expectedBytes) throws IOException, H1ConnectionException;

    public ConnectionPhase getConnectionPhase();

    public void setStopRequest();

    public void setConnectionDefinition(H1ConnectionDefinition conndef);

    public void addListener(IH1CommListener listener);

    public void removeListener(IH1CommListener listener);

    public void updateListener(UpdateReason reason, byte[] data, String message);
    
    public final static int tsap_port = 102;
    public final static int MAX_SO_TIMEOUT = 2000; // 2 seconds wait max. at read
    public final static byte CODE_CR = (byte) 0xe0;
    public final static byte CODE_CC = (byte) 0xd0;
    public final static byte CODE_DT = (byte) 0xf0;
    public final static byte CODE_DR = (byte) 0x80;
}
