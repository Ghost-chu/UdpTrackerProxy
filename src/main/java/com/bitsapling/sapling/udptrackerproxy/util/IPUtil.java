package com.bitsapling.sapling.udptrackerproxy.util;


import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class IPUtil {

    public static String fromSocketAddress(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            var ip = ((InetSocketAddress) socketAddress).getAddress().toString();
            if (ip == null) {
                return null;
            }
            if (ip.startsWith("/")) {
                return ip.substring(1);
            }
            return ip;
        }
        return null;
    }
}
