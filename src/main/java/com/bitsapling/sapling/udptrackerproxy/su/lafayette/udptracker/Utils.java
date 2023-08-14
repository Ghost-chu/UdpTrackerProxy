package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	public static String getHexString(byte[] buffer) throws Exception {
		StringBuilder result = new StringBuilder();
		for (byte b : buffer) {
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1)).append(" ");
		}
		return result.toString();
	}

	public final static Random random = new Random();

}
