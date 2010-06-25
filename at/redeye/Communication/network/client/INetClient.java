package at.redeye.Communication.network.client;

import java.io.IOException;
import at.redeye.Communication.UpdateReason;

public interface INetClient {

	public void start ();
	public void connect(String url, int port) throws IOException;
	public void disconnect() throws IOException;
	public byte [] receive() throws IOException;
	public void transmit (byte [] data) throws IOException;
	
	
	public void addListener(INetClientListener listener);

    public void removeListener(INetClientListener listener);

    public void updateListener(UpdateReason reason, byte[] data, String message);
	
}
