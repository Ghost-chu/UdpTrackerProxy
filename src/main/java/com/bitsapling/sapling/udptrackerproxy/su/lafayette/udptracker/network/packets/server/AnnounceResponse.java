package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.PeerInfo;
import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Utils;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

public class AnnounceResponse {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceResponse.class);

    public static void send(ChannelHandlerContext ctx, DatagramPacket event, int transactionId, int interval, int leechers, int seeders, List<PeerInfo> peers, boolean ipv6) throws Exception {
        logger.debug("AnnounceResponse::send to {}" , event.sender());
        int step = ipv6 ? 18 : 6;
        ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + 4 + 4 + 4 + peers.size() * step);
        responseBuffer.writeInt(Action.ANNOUNCE.getId());
        responseBuffer.writeInt(transactionId);
        responseBuffer.writeInt(interval);
        responseBuffer.writeInt(leechers);
        responseBuffer.writeInt(seeders);
        for (PeerInfo peer : peers) {
            boolean peerIpv6 = InetAddress.getByName(peer.getIp()) instanceof Inet6Address;
            if(peerIpv6 != ipv6) continue;
            if (ipv6) {
                logger.info("Length: {}",InetAddresses.forString(peer.getIp()).getAddress().length);
                responseBuffer.writeBytes(InetAddresses.forString(peer.getIp()).getAddress());
            } else {
                responseBuffer.writeInt(InetAddresses.coerceToInteger(InetAddresses.forString(peer.getIp())));
            }
            responseBuffer.writeShort(peer.getPort());
        }
        logger.info("[OK] announce:proxy-client:success -> Peers: {}", peers.size());
        logger.debug("AnnounceResponse DUMP: {}" , Utils.getHexString(responseBuffer.array()));
        DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
        ctx.channel().writeAndFlush(dp);
    }
}
