/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import java.nio.ByteBuffer;


/**
 * @author Mario Mattl
 * 
 */
public class OSIDataFrame {

	OSIHeader osiheader = new OSIHeader();
	Byte code = (byte)0x0;
	Byte last = (byte)0x0;

	public ByteBuffer toByteBuffer() {

		ByteBuffer bb = ByteBuffer.allocate(100);
		
		bb.clear();

		byte[] header = osiheader.toByteArray();

		for (int idx = 0; idx < header.length; idx++) {
			bb.put(header[idx]);
		}

		bb.put(code);
		bb.put(last);
		//bb.flip();
		return (bb);

	}

	public int getLength() {

		return toByteBuffer().position();

	}

    public void initializeByBytes (byte [] in) {

		osiheader.tpkt_len[0] = in[2] ;
		osiheader.tpkt_len[1] = in[3];
		osiheader.headlen = in[4];

		code = in[5];
        last = in [6];

    }

    public Byte getCode () {
        return code;
    }
    

}
