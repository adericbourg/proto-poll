package ui.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5HexUtil {

	static String md5Hex(String message) {
		String returnValue;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			returnValue = hex(md.digest(message.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException e) {
			returnValue = null;
		} catch (UnsupportedEncodingException e) {
			returnValue = null;
		}
		return returnValue;
	}

	private static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (byte element : array) {
			sb.append(Integer.toHexString((element & 0xFF) | 0x100).substring(
					1, 3));
		}
		return sb.toString();
	}
}
