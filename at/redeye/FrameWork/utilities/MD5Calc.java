package at.redeye.FrameWork.utilities;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * @author Mario Mattl
 *
 */
public class MD5Calc {
	
	private MessageDigest md5;
	private byte[] digest;

	public MD5Calc(String algorithm) {
		try {
			md5 = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
	}

	public String toHexString(byte b) {
		int value = (b & 0x7F) + (b < 0 ? 128 : 0);

		String ret = (value < 16 ? "0" : "");
		ret += Integer.toHexString(value).toUpperCase();

		return ret;
	}

	public String calcChecksum(String data) {
		
		StringBuffer strbuf = new StringBuffer();

		md5.update(data.getBytes(), 0, data.length());
		digest = md5.digest();

		for (int i = 0; i < digest.length; i++) {
			strbuf.append(toHexString(digest[i]));
		}

		return strbuf.toString();
	}
	
	public String calcChecksum(byte [] data) {
		StringBuffer strbuf = new StringBuffer();

		md5.update(data, 0, data.length);
		digest = md5.digest();

		for (int i = 0; i < digest.length; i++) {
			strbuf.append(toHexString(digest[i]));
		}

		return strbuf.toString();
	}
	
}
