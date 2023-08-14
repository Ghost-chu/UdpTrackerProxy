package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client;

import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.ClientRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ErrorResponse;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnounceRequest extends ClientRequest {
	private static final Logger logger = LoggerFactory.getLogger(AnnounceRequest.class);

	public void read(boolean ipv6) throws Exception {
		logger.debug("AnnounceRequest::read from " + this.getDatagramPacket().sender());
		ByteBuf buffer = this.getChannelBuffer();
		ErrorResponse.send(getContext(), this.getDatagramPacket(), this.getTransactionId(), "Fail testing!");
		return;
//		if (buffer.readableBytes() < 20 + 20 + 8 + 8 + 8 + 4 + 4 + 4 + 2 + 2) {
//			logger.warn("Received a too small announce packet from {}", this.getDatagramPacket().sender());
//			ErrorResponse.send(getContext(), this.getDatagramPacket(), this.getTransactionId(), "Too small announce packet!");
//			return;
//		}
//		Peer peer = new Peer();
//		peer.ipv6 = ipv6;
//		byte[] infoHashBytes = new byte[20];
//		buffer.readBytes(infoHashBytes);
//		peer.infoHash = infoHashBytes;
//		byte[] peerIdBytes = new byte[20];
//		buffer.readBytes(peerIdBytes);
//		peer.peerId = peerIdBytes;
//		peer.downloaded = buffer.readLong();
//		peer.left = buffer.readLong();
//		peer.uploaded = buffer.readLong();
//		peer.event = buffer.readInt();
//		peer.ip = buffer.readInt();
//		peer.key = buffer.readInt();
//		peer.numWant = buffer.readInt();
//		peer.port = buffer.readUnsignedShort();
//		outsideLoop:
//        while (buffer.readableBytes() > 0) {
//            short extensionCode = buffer.readByte();
//            switch (extensionCode) {
//                case 0 -> { // EOF
//					logger.debug("Extension Code = EOF, stop for reading");
//					break outsideLoop;
//                }
//                case 1 -> { // NOP
//					logger.debug("Extension Code = NOP");
//                }
//                case 2 -> { // URL Data
//					logger.debug("Extension Code = URL Data ");
//                    byte length = buffer.readByte();
//					logger.debug("URL Data Length = {}", length);
//                    byte[] param = new byte[length];
//					buffer.readBytes(param);
//					logger.debug("URL Data = {} [raw]", param);
//					String paramString = new String(param, StandardCharsets.UTF_8);
//					logger.debug("URL Data = {} [string]",paramString);
//                    String urlData = (String)peer.extensions.get("url_data");
//					if(urlData == null){
//						urlData = paramString;
//					}else{
//						urlData += paramString;
//						logger.debug("Append URL Data = {}", urlData);
//					}
//					peer.extensions.put("url_data", urlData);
//                }
//                default -> {
//					logger.debug("Extension Code Unknown = {}", extensionCode);
//                }
//            }
//        }
//		int maxNumWant = Main.getConfig().node("max_wants").getInt();
//		if (peer.numWant < 0 || peer.numWant > maxNumWant) {
//			peer.numWant = maxNumWant;
//		}
//
//		if (peer.ip == 0 && this.getDatagramPacket().sender() != null) {
//			InetSocketAddress remoteAddress = this.getDatagramPacket().sender();
//			ByteBuffer addressBytes = ByteBuffer.wrap(remoteAddress.getAddress().getAddress());
//			peer.ip = addressBytes.getInt();
//		}
//		logger.info("{}", peer);
//		ErrorResponse.send(context, getDatagramPacket(), getTransactionId(),"Fatal error!");
//		//AnnounceResponse.send(context,getDatagramPacket(),transactionId,500,15,20, Collections.emptyList());
	}
}
