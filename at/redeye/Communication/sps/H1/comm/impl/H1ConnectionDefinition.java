/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import at.redeye.Communication.AbstractConnectionDefinition;
import at.redeye.Communication.sps.H1.comm.IH1Communication;

/**
 * @author Mario Mattl
 * 
 */
public class H1ConnectionDefinition extends AbstractConnectionDefinition {

	private String mytsapname = "";
	private String hosttsapname = "";

	public H1ConnectionDefinition(String hostname, int port, String mytsapname,
			String hosttsapname) {
		super(hostname, port);

		this.mytsapname = mytsapname;
		this.hosttsapname = hosttsapname;
	}

	public H1ConnectionDefinition(String hostname, String mytsapname,
			String hosttsapname) {
		super(hostname, IH1Communication.tsap_port);

		this.mytsapname = mytsapname;
		this.hosttsapname = hosttsapname;
	}

	public String getMytsapname() {
		return mytsapname;
	}

	public void setMytsapname(String mytsapname) {
		this.mytsapname = mytsapname;
	}

	public String getHosttsapname() {
		return hosttsapname;
	}

	public void setHosttsapname(String hosttsapname) {
		this.hosttsapname = hosttsapname;
	}

}
