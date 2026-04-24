package at.redeye.Communication.network.client;

import at.redeye.Communication.ConnectionPhase;

public interface INetClientListener {

	public void actionMessageInbound(byte[] data);

	public void actionMessageOutbound(byte[] data);

	public void actionConnectionPhaseChanged(ConnectionPhase newPhase,
			String message);

}
