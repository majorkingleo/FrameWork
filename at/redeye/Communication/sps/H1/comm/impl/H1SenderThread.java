/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author Mario Mattl
 * 
 */
public class H1SenderThread extends Thread {

	private static SynchronousQueue<byte[]> squeue = null;

	private Logger logger;

	private H1TCPHandling h1comm = null;

	public H1SenderThread(Logger logger) {
		super();
		this.logger = logger;
		h1comm = H1TCPHandling.getInstance(null, null);
		if (h1comm == null) {
			logger.error("Failed to get H1-Communikation reference!");
			this.interrupt();
			return;
		}
	}

	public static synchronized SynchronousQueue<byte[]> getQueue() {

		if (squeue == null) {
			squeue = new SynchronousQueue<byte[]>();
		}
		return squeue;
	}

    @Override
	public void run() {

		while (!isInterrupted()) {

			if (squeue != null) {
				try {

					byte[] arr = null;
					if ((arr = squeue.poll(100, TimeUnit.MILLISECONDS)) != null) {
						logger.debug("Got something! - H1: "
								+ h1comm.getConnectionPhase().name());
						if (h1comm.getConnectionPhase() == ConnectionPhase.H1Connected) {
							logger.debug("Transmit H1");
							if (arr.length > 0) {
								h1comm.transmit(arr);
							} else {
								logger.error ("Invalid data length -> do not send!");
							}
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
