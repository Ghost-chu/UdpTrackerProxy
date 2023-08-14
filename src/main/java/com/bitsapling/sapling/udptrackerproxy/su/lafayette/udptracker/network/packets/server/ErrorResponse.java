package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ErrorResponse {
    private static final Logger logger = LoggerFactory.getLogger(ErrorResponse.class);

    public static void send(ChannelHandlerContext ctx, DatagramPacket event, Integer transactionId, String message) throws Exception {
        logger.info("ErrorResponse::send to {}, transaction id {}" , event.sender(), transactionId);
        ByteBuf responseBuffer = Unpooled.buffer(4 + 4 + message.getBytes(StandardCharsets.US_ASCII).length);
        responseBuffer.writeInt(Action.ERROR.getId());
        responseBuffer.writeInt(transactionId);
        //responseBuffer.writeZero(10);
        //responseBuffer.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        logger.info("ErrorResponse DUMP: {}", responseBuffer.array());
        DatagramPacket dp = new DatagramPacket(responseBuffer, event.sender());
        ctx.channel().writeAndFlush(dp);
    }
}
