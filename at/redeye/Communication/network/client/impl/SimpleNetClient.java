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

	private int port;
	private String url = "";

	private Logger logger = Logger.getLogger(SimpleNetClient.class
			.getSimpleName());

	private Vector<INetClientListener> allListener = new Vector<INetClientListener>();

	public SimpleNetClient(String url, int port) {
		super();
		this.port = port;
		this.url = url;
	}

	@Override
	public void addListener(INetClientListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connect(String url, int port) throws IOException {
		
		s = new Socket(url, port);
		if (s != null) {
			out = new BufferedOutputStream(new DataOutputStream(s
					.getOutputStream()));
			in = new BufferedInputStream(
					new DataInputStream(s.getInputStream()));
		} else {
			logger.error("Could not establish connection to <" + url + " / "
					+ port + ">");
		}
	}

	@Override
	public void disconnect() throws IOException {
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
	}

	@Override
	public void updateListener(UpdateReason reason, byte[] data, String message) {

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
				if (data != null) {
					StringBuilder str = new StringBuilder();
					for (int index = 0; index < data.length; index++) {
						str.append((char) (data[index]));
					}

					logger.info(str.toString());
				}
				sleep(1000);
			}

		} catch (IOException e) {
			logger.error(StringUtils.exceptionToString(e));
		} catch (InterruptedException e) {
			logger.error(StringUtils.exceptionToString(e));
		}
	}

}
