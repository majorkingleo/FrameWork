/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import com.sun.corba.se.impl.ior.ByteBuffer;

/**
 * @author Mario Mattl
 * 
 */
public class OSIConnectionFrame {

	protected final static byte CODE_CR = (byte) 0xe0;
	protected final static byte CODE_CC = (byte) 0xd0;
	protected final static byte CODE_DT = (byte) 0xf0;
	protected final static byte CODE_DR = (byte) 0x80;


	protected final static byte PARA_TPDU = (byte) 0xc0;
	protected final static byte PARA_MYTSAP = (byte) 0xc1;
	protected final static byte PARA_HOSTTSAP = (byte) 0xc2;

	protected final static byte IS_LAST = (byte) 0x80;
	protected final static byte NOT_LAST = (byte) 0x00;

	protected final static byte POW_PDU = (byte) 9;
	protected final static char MAX_PDU = 512;

	OSIHeader osiheader = new OSIHeader();
	Byte code = 0x0;
	Byte[] dest_ref = {0x00, 0x00};
	Byte[] src_ref = {(byte)0xff, (byte)0xff};
	Byte tp_class = 0x0;

	public byte[] toByteArray() {

		ByteBuffer bb = new ByteBuffer();

		byte[] header = osiheader.toByteArray();

		for (int idx = 0; idx < header.length; idx++) {
			bb.append(header[idx]);
		}

		bb.append(code);
		bb.append(dest_ref[0]);
		bb.append(dest_ref[1]);
		bb.append(src_ref[0]);
		bb.append(src_ref[1]);
		bb.append(tp_class);

		return (bb.toArray());

	}

	public ByteBuffer toByteBuffer() {

		ByteBuffer bb = new ByteBuffer();

		byte[] header = osiheader.toByteArray();

		for (int idx = 0; idx < header.length; idx++) {
			bb.append(header[idx]);
		}

		bb.append(code);
		bb.append(dest_ref[0]);
		bb.append(dest_ref[1]);
		bb.append(src_ref[0]);
		bb.append(src_ref[1]);
		bb.append(tp_class);

		return (bb);

	}
	
	public void initializeByBytes (byte [] in) {
		
		osiheader.tpkt_len[0] = in[2] ;
		osiheader.tpkt_len[1] = in[3];
		osiheader.headlen = in[4];
		
		code = in[5];
		dest_ref[0] = in [6];
		dest_ref[1] = in [7];
		
		src_ref[0] = in [8];
		src_ref[1] = in [9];
		tp_class = in [10];
		
		
	}
}
