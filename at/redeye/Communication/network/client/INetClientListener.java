package at.redeye.Communication.network.client;


public interface INetClientListener {
	
	public void actionMessageInbound (byte [] data);
	public void actionMessageOutbound(byte [] data);

}
