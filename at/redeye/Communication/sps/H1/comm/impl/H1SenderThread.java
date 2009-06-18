/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import at.redeye.Communication.sps.H1.comm.IH1Communication;

/**
 * @author Mario Mattl
 * 
 */
public class H1SenderThread extends Thread {

	private static SynchronousQueue<byte[]> squeue = null;
	
	private Logger logger;
	
	private IH1Communication h1comm;
	
	

	public H1SenderThread(IH1Communication h1comm, Logger logger) {
		super();
		this.logger = logger;
		this.h1comm = h1comm;
	}

	public static synchronized SynchronousQueue<byte[]> getQueue() {

		if (squeue == null) {
			squeue = new SynchronousQueue<byte[]>();
		}
		return squeue;
	}

	public void run() {

		while (true) {
			if (squeue != null) {
				try {
					byte[] arr = null;
					if ((arr = squeue.poll(100, TimeUnit.MILLISECONDS)) != null) {
						logger.debug("Got something! - H1: "+h1comm.getConnectionPhase().name());
						if (h1comm.getConnectionPhase() == ConnectionPhase.H1Connected) {
							logger.debug("Transmit H1");
							h1comm.transmit(arr);
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (H1ConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
