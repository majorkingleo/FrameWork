/**
 * 
 */
package at.redeye.Communication.sps.H1.comm;

import java.io.IOException;

import at.redeye.Communication.sps.H1.comm.impl.ConnectionPhase;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionDefinition;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionException;

/**
 * @author Mario Mattl
 *
 */
public interface IH1Communication {

    public int handleH1Request();

    public void transmit(byte[] dataToSend) throws IOException, H1ConnectionException;

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
}
