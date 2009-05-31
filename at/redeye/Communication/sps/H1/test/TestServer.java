package at.redeye.Communication.sps.H1.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public TestServer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket sso = new ServerSocket(9001);

			while (true) {

				Socket client = sso.accept(); // ATT: OUGHT TO BE A THREAD!

				DataInputStream dis = new DataInputStream(
						new BufferedInputStream(client.getInputStream()));
				DataOutputStream out = new DataOutputStream(
						new BufferedOutputStream(client.getOutputStream()));

				byte[] buffer = new byte[512];
				int len = dis.read(buffer, 0, 512);
				for (int i = 0; i < len; i++) {
					System.out.print(String.format("%02x ", (byte) buffer[i]));
				}
				

				buffer[5] = (byte) 0xd0; // CODE_CC vorgaukeln
				
				out.write(buffer);
				out.flush();

			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
