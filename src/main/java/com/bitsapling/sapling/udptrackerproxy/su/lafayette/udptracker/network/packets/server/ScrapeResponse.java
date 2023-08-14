package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Utils;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.TorrentStats;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.List;
import java.util.logging.Logger;

public class ScrapeResponse {
	private static final Logger logger = Logger.getLogger("ScrapeResponse");

	public static void send(ChannelHandlerContext ctx, DatagramPacket event, Integer transactionId, List<TorrentStats> torrentStatsList) throws Exception {
		logger.fine("ScrapeResponse::send to " + event.sender());

		ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + torrentStatsList.size() * 12);
		responseBuffer.writeInt(Action.SCRAPE.getId());
		responseBuffer.writeInt(transactionId);

		for (TorrentStats torrentStats : torrentStatsList) {
			responseBuffer.writeInt(torrentStats.seeders);
			responseBuffer.writeInt(torrentStats.completed);
			responseBuffer.writeInt(torrentStats.leechers);
		}

		logger.fine("ScrapeResponse DUMP: " + Utils.getHexString(responseBuffer.array()));

		DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
		ctx.channel().writeAndFlush(dp);
	}
}
