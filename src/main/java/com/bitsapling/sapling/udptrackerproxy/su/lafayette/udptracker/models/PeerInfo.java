package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models;

public class PeerInfo {
    private String peerId;
    private String ip;
    private int port;

    public PeerInfo(String peerId, String ip, int port) {
        this.peerId = peerId;
        this.ip = ip;
        this.port = port;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
