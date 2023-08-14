package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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
	public Map<String, Object> extensions = new LinkedHashMap<>();


	@Override
	public String toString() {
		return "Peer{" +
				"ipv6=" + ipv6 +
				", infoHash=" + Arrays.toString(infoHash) +
				", peerId=" + Arrays.toString(peerId) +
				", downloaded=" + downloaded +
				", left=" + left +
				", uploaded=" + uploaded +
				", event=" + event +
				", ip=" + ip +
				", port=" + port +
				", key=" + key +
				", numWant=" + numWant +
				", lastUpdate=" + lastUpdate +
				", extensions=" + extensions +
				'}';
	}
}
