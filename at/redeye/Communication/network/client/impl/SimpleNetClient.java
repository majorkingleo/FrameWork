/**
 * 
 */
package at.redeye.Communication.network.client.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import org.apache.log4j.Logger;

import at.redeye.Communication.ConnectionPhase;
import at.redeye.Communication.UpdateReason;
import at.redeye.Communication.network.client.INetClient;
import at.redeye.Communication.network.client.INetClientListener;
import at.redeye.FrameWork.utilities.StringUtils;

/**
 * @author Mario Mattl
 * 
 */
public class SimpleNetClient extends Thread implements INetClient {

	private Socket s;
	private BufferedOutputStream out;
	private BufferedInputStream in;
	private ConnectionPhase phase = ConnectionPhase.Disconnected;

	private int port;
	private String url = "";

	private Logger logger = Logger.getLogger(SimpleNetClient.class
			.getSimpleName());

	private Vector<INetClientListener> allListener = new Vector<INetClientListener>();

	public SimpleNetClient(SimpleNetClientConnectionDefinition conndef) {
		super();
		this.port = conndef.getPort();
		this.url = conndef.getHostname();
	}

	@Override
	public void addListener(INetClientListener listener) {

		if (listener != null) {
			allListener.add(listener);
		}
	}

	@Override
	public void connect(String url, int port) throws IOException {

		phase = ConnectionPhase.TCPConnectionAttempt;

		s = new Socket(url, port);
		if (s != null) {
			out = new BufferedOutputStream(new DataOutputStream(s
					.getOutputStream()));
			in = new BufferedInputStream(
					new DataInputStream(s.getInputStream()));
			phase = ConnectionPhase.TCPConnected;
		} else {
			logger.error("Could not establish connection to <" + url + " / "
					+ port + ">");
		}
	}

	@Override
	public void disconnect() throws IOException {

		phase = ConnectionPhase.DisConnectionAttempt;
		if (in != null) {
			in.close();
			in = null;
		}
		if (out != null) {
			out.close();
			out = null;
		}
		if (s != null) {
			s.close();
			s = null;
		}
		phase = ConnectionPhase.Disconnected;
		logger.trace("TCP disconnected!");
	}

	@Override
	public byte[] receive() throws IOException {
		byte[] data;
		int noOfBytes = 0;
		if ((noOfBytes = in.available()) > 0) {
			data = new byte[noOfBytes];
			in.read(data);
			return data;
		}
		return null;
	}

	@Override
	public void removeListener(INetClientListener listener) {
		allListener.remove(listener);
	}

	@Override
	public void transmit(byte[] data) throws IOException {
		out.write(data);
		out.flush();
		logger.trace("Transmit data: " + data);
	}

	@Override
	public void updateListener(UpdateReason reason, byte[] data, String message) {
		for (INetClientListener listener : allListener) {
			listener.actionMessageInbound(data);
		}
	}

	@Override
	public void run() {

		byte[] dummy = { 0x00, 0x00 };
		try {
			connect(url, port);

			while (s.isConnected()) {

				// initially, I must send something
				transmit(dummy);

				byte[] data = receive();
				logger.trace(StringUtils.byteArrayToString(data));
				updateListener(null, data, null);
				sleep(1000);
			}

		} catch (IOException e) {
			logger.error(StringUtils.exceptionToString(e));
			if (s.isClosed() || !s.isConnected() || s.isInputShutdown()
					|| s.isOutputShutdown()) {
				phase = ConnectionPhase.Disconnected;
			}
		} catch (InterruptedException e) {
			logger.error(StringUtils.exceptionToString(e));
		}
	}

	/**
	 * @return the phase
	 */
	protected ConnectionPhase getPhase() {
		return phase;
	}

}
