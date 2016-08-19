package cn.com.custom.widgetproject.utils;

import java.security.MessageDigest;

/**
 * MD5编码加密工具类
 *  Created by custom on 2016/6/19.
 */
public class MD5Encoder {
	
	public static String encode(String string) throws Exception {
	    byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) {
	        	hex.append("0");
	        }
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
}
