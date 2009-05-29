package at.redeye.Communication.sps.H1.comm;

import at.redeye.Communication.sps.H1.comm.impl.ConnectionPhase;

public interface IH1CommListener {
	
	
	public void actionConnectionPhaseChanged(ConnectionPhase newPhase, String message);
	public void actionMessageInbound (byte [] data);
	public void actionMessageOutbound();

}
