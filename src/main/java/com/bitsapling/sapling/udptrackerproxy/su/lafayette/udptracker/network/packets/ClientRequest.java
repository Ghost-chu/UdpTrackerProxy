package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.structures.Action;

public abstract class ClientRequest {
	protected ChannelHandlerContext context;
	protected DatagramPacket DatagramPacket;
	protected ByteBuf channelBuffer;
	protected Long connectionId;
	protected Action action;
	protected Integer transactionId;

	public abstract void read(boolean ipv6) throws Exception;

	public ChannelHandlerContext getContext() { return context; }
	public void setContext(ChannelHandlerContext context) { this.context = context; }

	public DatagramPacket getDatagramPacket() { return DatagramPacket; }
	public void setDatagramPacket(DatagramPacket DatagramPacket) { this.DatagramPacket = DatagramPacket; }

	public ByteBuf getChannelBuffer() { return this.channelBuffer; }
	public void setChannelBuffer(ByteBuf channelBuffer) { this.channelBuffer = channelBuffer; }

	public Long getConnectionId() { return connectionId; }
	public void setConnectionId(Long connectionId) { this.connectionId = connectionId; }

	public Action getAction() { return action; }
	public void setAction(Action action) { this.action = action; }

	public Integer getTransactionId() { return transactionId; }
	public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
}
