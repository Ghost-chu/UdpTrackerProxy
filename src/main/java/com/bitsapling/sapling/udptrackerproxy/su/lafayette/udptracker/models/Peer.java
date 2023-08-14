package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models;


import com.bitsapling.sapling.udptrackerproxy.util.InfoHashUtil;

import java.nio.charset.StandardCharsets;

public class Peer {
	public boolean ipv6;
	public byte[] infoHash;
	public byte[] peerId;
	public long downloaded;
	public long left;
	public long uploaded;
	public int event;
	public int ip;
	public int port;
	public int key;
	public int numWant;
	public long lastUpdate;
	public String queryParam = "";


	@Override
	public String toString() {
		return "Peer{" +
				"ipv6=" + ipv6 +
				", infoHash=" + InfoHashUtil.parseInfoHash(new String(infoHash, StandardCharsets.ISO_8859_1))+
				", peerId=" + new String(peerId,StandardCharsets.ISO_8859_1) +
				", downloaded=" + downloaded +
				", left=" + left +
				", uploaded=" + uploaded +
				", event=" + event +
				", ip=" + ip +
				", port=" + port +
				", key=" + key +
				", numWant=" + numWant +
				", lastUpdate=" + lastUpdate +
				", queryParam=" + queryParam +
				'}';
	}
}
