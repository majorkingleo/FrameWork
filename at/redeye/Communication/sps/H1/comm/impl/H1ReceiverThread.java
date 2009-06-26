/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import at.redeye.Communication.sps.H1.comm.IH1Communication;

/**
 * @author Mario Mattl
 * 
 */
public class H1ReceiverThread extends Thread {

    IH1Communication h1comm;
    private Logger logger = null;

    public H1ReceiverThread(IH1Communication h1comm, Logger logger) {
        this.h1comm = h1comm;
        this.logger = logger;
    }

    @Override
    public void run() {

        while (true) {
            
            try {
            	if (h1comm.getConnectionPhase() != ConnectionPhase.H1Connected) {
            		logger.trace("H1 is not connected!");
                    sleep (1000);
                    continue;
                }
                if (h1comm.receive(0) != null) {
                    // If we got something, we have to send an answer
                    h1comm.transmitH1Answer();
                }

            } catch (SocketTimeoutException ste) {
                logger.trace("Receive from socket: " + ste.getMessage());

            } catch (IOException e) {
                logger.error("Receive from socket: " + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Failed to sleep: " + e.getMessage());
            } catch (H1ConnectionException hce) {
                logger.error("Receive: " + hce.getMessage());
            }

        }

    }
}
