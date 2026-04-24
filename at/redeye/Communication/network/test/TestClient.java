/**
 * 
 */
package at.redeye.Communication.network.test;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Mario Mattl
 *
 */
public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Socket s;
		BufferedOutputStream out;
		byte [] test = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		try {
			s = new Socket ("localhost", 9001);
			while (s.isConnected()) {
				out = new BufferedOutputStream (new DataOutputStream (s.getOutputStream()));
				System.out.println("Sending");
				out.write (test);
				out.flush();
				Thread.sleep(1000);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
