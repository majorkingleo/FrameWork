/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;

import at.redeye.Communication.sps.H1.comm.IH1QueueSender;

/**
 * @author Mario Mattl
 *
 */
public class H1QueueSender implements IH1QueueSender {

	/* (non-Javadoc)
	 * @see at.redeye.Communication.sps.H1.comm.impl.IH1QueueSender#transmit(byte[])
	 */
	private Logger logger = Logger.getLogger(H1QueueSender.class.getSimpleName());
	
	private SynchronousQueue<byte[]> squeue = null;
	
	
	public H1QueueSender() {
		super();
		this.squeue = H1SenderThread.getQueue();
		if (squeue == null) {
			logger.error("Failed to get queue!");
		}
	}


	@Override
	public synchronized void transmit(byte[] data) {

		synchronized (squeue) {
			squeue.offer(data);
		}

	}

}
