package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.PeerInfo;
import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Utils;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;

import java.util.List;
import java.util.logging.Logger;

public class AnnounceResponse {
    private static final Logger logger = Logger.getLogger("ErrorResponse");

    public static void send(ChannelHandlerContext ctx, DatagramPacket event, int transactionId, int interval, int leechers, int seeders, List<PeerInfo> peers, boolean ipv6) throws Exception {
        logger.fine("AnnounceResponse::send to " + event.sender());
        int step = ipv6 ? 18 : 6;
        ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + 4 + 4 + 4 + peers.size() * step);
        responseBuffer.writeInt(Action.ANNOUNCE.getId());
        responseBuffer.writeInt(transactionId);
        responseBuffer.writeInt(interval);
        responseBuffer.writeInt(leechers);
        responseBuffer.writeInt(seeders);
        for (PeerInfo peer : peers) {
            if (ipv6) {
                responseBuffer.writeBytes(InetAddresses.forString(peer.getIp()).getAddress());
            } else {
                responseBuffer.writeInt(InetAddresses.coerceToInteger(InetAddresses.forString(peer.getIp())));
            }
            responseBuffer.writeShort(peer.getPort());
        }
        logger.fine("AnnounceResponse DUMP: " + Utils.getHexString(responseBuffer.array()));
        DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
        ctx.channel().writeAndFlush(dp);
    }
}
