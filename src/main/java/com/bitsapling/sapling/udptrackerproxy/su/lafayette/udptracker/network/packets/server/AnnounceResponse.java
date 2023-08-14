package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.Peer;
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

	public static void send(ChannelHandlerContext ctx, DatagramPacket event, int transactionId, int interval, int leechers, int seeders, List<Peer> peers) throws Exception {
		logger.fine("AnnounceResponse::send to " + event.sender());
		ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + 4 + 4 + 4 + peers.size() * 6);
		responseBuffer.writeInt(Action.ANNOUNCE.getId());
		responseBuffer.writeInt(transactionId);
		responseBuffer.writeInt(interval);
		responseBuffer.writeInt(leechers);
		responseBuffer.writeInt(seeders);
		for (Peer peer : peers) {
			responseBuffer.writeInt(peer.ip);
			responseBuffer.writeShort(peer.port);
		}
		logger.fine("AnnounceResponse DUMP: " + Utils.getHexString(responseBuffer.array()));
		DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
		ctx.channel().writeAndFlush(dp);
	}
}
