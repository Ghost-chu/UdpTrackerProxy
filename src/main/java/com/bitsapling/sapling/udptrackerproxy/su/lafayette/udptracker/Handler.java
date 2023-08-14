package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.ClientRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client.AnnounceRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client.ConnectionRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ErrorResponse;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;

public class Handler extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger logger = LoggerFactory.getLogger(Handler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
		logger.info("Received packet from {}.", datagramPacket.sender());
		ByteBuf channelBuffer = datagramPacket.content();
		if (channelBuffer.readableBytes() < 16) {
			logger.warn("Incorrect packet received from {}, byte less than 16 bytes." , datagramPacket.sender());
		}
		long connectionId = channelBuffer.readLong(); // TODO: Можно проверять connectionId.
		int actionId = channelBuffer.readInt();
		int transactionId = channelBuffer.readInt();
		logger.info("Received packet from {}, connectionId: {}, actionId: {}, transactionId: {}.", datagramPacket.sender(), connectionId, actionId, transactionId);
		Action action = Action.byId(actionId);
		ClientRequest request;
		if (action != null) {
			switch (action) {
				case CONNECT -> request = new ConnectionRequest();
				case ANNOUNCE -> request = new AnnounceRequest();
				//case SCRAPE -> request = new ScrapeRequest();
				default -> {
					logger.warn("Client {} send a packet with invalid action: {}", datagramPacket.sender(), action);
					ErrorResponse.send(channelHandlerContext,datagramPacket, transactionId, "Incorrect action");
					return;
				}
			}
			request.setContext(channelHandlerContext);
			request.setDatagramPacket(datagramPacket);
			request.setChannelBuffer(channelBuffer);
			request.setConnectionId(connectionId);
			request.setAction(action);
			request.setTransactionId(transactionId);
			request.read((datagramPacket.sender().getAddress() instanceof Inet6Address));
		}
	}
}
