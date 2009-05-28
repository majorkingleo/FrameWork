package at.redeye.Communication.sps.H1.test;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import at.redeye.Communication.sps.H1.comm.IH1Communication;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionDefinition;
import at.redeye.Communication.sps.H1.comm.impl.H1ReceiverThread;
import at.redeye.Communication.sps.H1.comm.impl.H1TCPHandling;

public class H1Test {

	
	public static Logger logger = Logger.getLogger(H1Test.class.getSimpleName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		IH1Communication h1comm = H1TCPHandling.getInstance(new H1ConnectionDefinition("127.0.0.1", 9001,
						"LLR-PFA1", "LLR-PFA1"), logger);
		PatternLayout layout = new PatternLayout(
				"%d{ISO8601} %-5p (%F:%L): %m%n");
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);

		h1comm.handleH1Request();

		H1ReceiverThread poller = new H1ReceiverThread(h1comm, logger);
		poller.start();

	}

}
