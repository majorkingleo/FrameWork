package at.redeye.Communication;

/**
 * 
 * @author Mario Mattl
 * 
 */
public abstract class AbstractConnectionDefinition {

	private String hostname = "";
	private int port;

	public AbstractConnectionDefinition(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;

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

}
