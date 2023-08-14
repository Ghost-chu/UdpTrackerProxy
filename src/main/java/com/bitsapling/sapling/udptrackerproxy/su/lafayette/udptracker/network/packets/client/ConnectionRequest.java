package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.ClientRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ConnectionResponse;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ConnectionRequest extends ClientRequest {
	public final static long PROTOCOL_ID = 0x41727101980L;

	private static final Logger logger = LoggerFactory.getLogger(ConnectionRequest.class);

	public void read(boolean ipv6) throws Exception {
		logger.debug("ConnectionRequest::read from " + this.getDatagramPacket().sender());

		if (this.connectionId != PROTOCOL_ID) {
			logger.debug("ConnectionRequest::read from " + this.getDatagramPacket().sender() + " wrong protocol.");
			ErrorResponse.send(getContext(),this.getDatagramPacket(), this.getTransactionId(), "Wrong protocol.");
			return;
		}

		Random random = new Random();
		do {
			this.connectionId = random.nextLong();
		} while (this.connectionId == PROTOCOL_ID);
		// TODO: В будущем можно сохранять выданные connectionId для определения пользователей.
		logger.info("Handshake with {} successfully. connection_id={}, transaction_id={}", getDatagramPacket().sender(), this.connectionId, this.getTransactionId());
		ConnectionResponse.send(getContext(),this.getDatagramPacket(), this.getTransactionId(), this.getConnectionId());
	}
}
