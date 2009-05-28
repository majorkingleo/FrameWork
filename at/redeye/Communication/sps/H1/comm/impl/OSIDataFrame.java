/**
 * 
 */
package at.redeye.Communication.sps.H1.comm.impl;

import com.sun.corba.se.impl.ior.ByteBuffer;

/**
 * @author Mario Mattl
 * 
 */
public class OSIDataFrame {

	OSIHeader osiheader = new OSIHeader();
	Byte code;
	Byte last;

	public ByteBuffer toByteBuffer() {

		ByteBuffer bb = new ByteBuffer();

		byte[] header = osiheader.toByteArray();

		for (int idx = 0; idx < header.length; idx++) {
			bb.append(header[idx]);
		}

		bb.append(code);
		bb.append(last);

		return (bb);

	}

	public int getLength() {

		return toByteBuffer().size();

	}

}
