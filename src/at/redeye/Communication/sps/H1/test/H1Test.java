package at.redeye.Communication.sps.H1.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import at.redeye.Communication.sps.H1.comm.IH1Communication;
import at.redeye.Communication.sps.H1.comm.impl.H1ConnectionDefinition;
import at.redeye.Communication.sps.H1.comm.impl.H1ReceiverThread;
import at.redeye.Communication.sps.H1.comm.impl.H1TCPHandling;
import at.redeye.Communication.sps.H1.ui.impl.H1CommunicationManagerUI;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;

public class H1Test {

	
	public static Logger logger = Logger.getLogger(H1Test.class.getSimpleName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicConfigurator.configure();
		IH1Communication h1comm = H1TCPHandling.getInstance(new H1ConnectionDefinition("", 
						"", ""), logger);
		
		h1comm.handleH1Request();

		H1ReceiverThread poller = new H1ReceiverThread(h1comm, logger);
		poller.start();
		
		java.awt.EventQueue.invokeLater(new Runnable() {

		Root root  = new LocalRoot ("H1 Testprogramm");
        public void run() {
                new H1CommunicationManagerUI(root).setVisible(true);
            }
        });

	}

}
