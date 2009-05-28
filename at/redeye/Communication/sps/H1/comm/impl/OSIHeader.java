/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

/**
 * @author Mario Mattl
 * 
 */
public class OSIHeader {

	Byte version = 0x03;
	Byte res1 = 0x00;
	Byte[] tpkt_len = {0x00, 0x00};
	Byte headlen = 0x00;

	public byte[] toByteArray() {

		byte[] out = { version, res1, tpkt_len[0], tpkt_len[1], headlen };
		return out;

	}

	public int getLength() {
		byte[] out = { version, res1, tpkt_len[0], tpkt_len[1], headlen };
		return out.length;

	}
	

}
