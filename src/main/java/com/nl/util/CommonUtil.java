package com.nl.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backtype.storm.Config;

public class CommonUtil {
	
	/**
	 * 
	 * @return
	 */
	public static InetAddress getInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("unknown host!");
		}
		return null;

	}

	/**
	 * 
	 * @param netAddress
	 * @return
	 */
	public static String getHostIp(InetAddress netAddress) {
		if (null == netAddress) {
			return null;
		}
		String ip = netAddress.getHostAddress(); // get the ip address
		return ip;
	}
	
	/**
	 * 
	 * @param netAddress
	 * @return
	 */
	public static String getHostName(InetAddress netAddress) {
		if (null == netAddress) {
			return null;
		}
		String name = netAddress.getHostName(); // get the host address
		return name;
	}

	/**
	 * 获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
	 */
	public static String getdate(int i) {
		Date dat = null;
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, i);
		dat = cd.getTime();
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		return dformat.format(dat);
	}

	
	
	public static String printLog(List<String> list) {
		StringBuffer tmp=new StringBuffer();
		if (list != null) {
			tmp.append("\n=============="+new Date()+"==============");
			for (String log:list) {
				tmp.append("\n"+log);
			}
			tmp.append("\n========================================================");
		}
		return tmp.toString();
	}
	public static void printConfig(Config conf) {
		if (conf != null) {
			System.out
					.println("============================================== TOPOLOGY CONFIG INFO=====================================================");
			System.out
					.println("----------------------------------------------------------------------------------------------------------------------|");

			for (Entry<String, Object> targetConfigEntry : conf.entrySet()) {
				System.out.println(appendBlank(targetConfigEntry.getKey(), 40)
						+ "\t|\t"
						+ (cut(targetConfigEntry.getValue().toString(), 50)));
				System.out
						.println("----------------------------------------------------------------------------------------------------------------------|");
			}
			System.out
					.println("========================================================================================================================");
		}
	}

	static String appendBlank(String str, int length) {
		StringBuffer sb = new StringBuffer();
		if (str == null || "".equals(str)) {
			for (int i = 0; i < length; i++) {
				sb.append(" ");
			}
		} else {
			if (length > str.length()) {
				sb.append(str);
				for (int i = 0; i < length - str.length(); i++) {
					sb.append(" ");
				}
			} else {
				sb.append(str);
			}

		}

		return sb.toString();

	}

	static String cut(String str, int length) {
		StringBuffer sb = new StringBuffer();

		if (str == null || "".equals(str)) {
			sb.setLength(0);
			sb.append("");

		} else {
			if (length < str.length()) {
				sb.setLength(0);
				// ,
				if (str.contains(",")) {
					return sb.append(
							"..."
									+ str.substring(str.lastIndexOf(","),
											str.length())).toString();
				}

				if (str.contains("/")) {
					return sb.append(
							"..."
									+ str.substring(str.lastIndexOf("/"),
											str.length())).toString();
				}
				if (str.contains(File.separator)) {
					return sb.append(
							"..."
									+ str.substring(
											str.lastIndexOf(File.separator),
											str.length())).toString();
				}

				if (str.contains(".")) {
					return sb.append(
							"..."
									+ str.substring(str.lastIndexOf("."),
											str.length())).toString();
				}

			} else {
				sb.setLength(0);
				sb.append(str);
			}

		}

		return sb.toString();

	}

	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static int parseInt(String param) {
		int i = 0;
		try {
			i = Integer.parseInt(param);
		} catch (Exception e) {
			i = (int) parseFloat(param);
		}
		return i;
	}

	public static long parseLong(String param) {
		long l = 0L;
		try {
			l = Long.parseLong(param);
		} catch (Exception e) {
			l = 0;
		}
		return l;
	}

	public static float parseFloat(String param) {
		float f = 0.0F;
		try {
			f = Float.parseFloat(param);
		} catch (Exception e) {
		}
		return f;
	}

	public static double parseDouble(String param) {
		double d = 0.0D;
		try {
			d = Double.parseDouble(param);
		} catch (Exception e) {
		}
		return d;
	}

	public static String convertGb2312(String input) {
		String str = input;
		try {
			str = new String(input.getBytes("ISO8859_1"), "gb2312");
		} catch (Exception ignored) {
		}
		return str;
	}

	public static String isInteger(String input) {
		String ret = "1";
		for (int i = 0; i < input.length(); i++) {
			if ((input.charAt(i) < '0') || (input.charAt(i) > '9')) {
				ret = "0";
				break;
			}
		}
		return ret;
	}

	public static String Md5(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuilder md5StrBuff = new StringBuilder();

		for (byte aByteArray : byteArray) {
			if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & aByteArray));
			} else
				md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
		}
		return md5StrBuff.toString().toUpperCase();
	}
}
