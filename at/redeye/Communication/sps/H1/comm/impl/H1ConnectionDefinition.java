/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import at.redeye.Communication.sps.H1.comm.IH1Communication;


/**
 * @author Mario Mattl
 * 
 */
public class H1ConnectionDefinition {

	private String hostname = "";
	private int port = IH1Communication.tsap_port;
	private String mytsapname = "";
	private String hosttsapname = "";

	public H1ConnectionDefinition(String hostname, int port, String mytsapname,
			String hosttsapname) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.mytsapname = mytsapname;
		this.hosttsapname = hosttsapname;
	}

	public H1ConnectionDefinition(String hostname, String mytsapname,
			String hosttsapname) {
		super();
		this.hostname = hostname;
		this.mytsapname = mytsapname;
		this.hosttsapname = hosttsapname;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
