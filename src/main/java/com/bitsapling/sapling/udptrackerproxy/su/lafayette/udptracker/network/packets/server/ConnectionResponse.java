package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.socket.DatagramPacket;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Utils;

import java.util.logging.Logger;

public class ConnectionResponse {
	private static final Logger logger = Logger.getLogger("ConnectionResponse");

	public static void send(ChannelHandlerContext ctx, DatagramPacket event, Integer transactionId, Long connectionId) throws Exception {
		logger.fine("ConnectionResponse::send to " + event.sender());
		ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + 8);
		responseBuffer.writeInt(Action.CONNECT.getId());
		responseBuffer.writeInt(transactionId);
		responseBuffer.writeLong(connectionId);
		logger.fine("ConnectionResponse DUMP: " + Utils.getHexString(responseBuffer.array()));
		DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
		ctx.channel().writeAndFlush(dp);
	}
}
