package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client;

import com.bitsapling.sapling.udptrackerproxy.Main;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.Peer;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.PeerInfo;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.ClientRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.AnnounceResponse;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ErrorResponse;
import com.bitsapling.sapling.udptrackerproxy.util.BencodeUtil;
import com.dampcake.bencode.Type;
import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;
import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnounceRequest extends ClientRequest {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceRequest.class);

    public void read(boolean ipv6) throws Exception {
        logger.debug("AnnounceRequest::read from " + this.getDatagramPacket().sender());
        ByteBuf buffer = this.getChannelBuffer();
        if (buffer.readableBytes() < 20 + 20 + 8 + 8 + 8 + 4 + 4 + 4 + 2 + 2) {
            logger.warn("Received a too small announce packet from {}", this.getDatagramPacket().sender());
            ErrorResponse.send(getContext(), this.getDatagramPacket(), this.getTransactionId(), "Too small announce packet!");
            return;
        }
        Peer peer = new Peer();
        peer.ipv6 = ipv6;
        byte[] infoHashBytes = new byte[20];
        buffer.readBytes(infoHashBytes);
        peer.infoHash = infoHashBytes;
        byte[] peerIdBytes = new byte[20];
        buffer.readBytes(peerIdBytes);
        peer.peerId = peerIdBytes;
        peer.downloaded = buffer.readLong();
        peer.left = buffer.readLong();
        peer.uploaded = buffer.readLong();
        peer.event = buffer.readInt();
        peer.ip = buffer.readInt();
        peer.key = buffer.readInt();
        peer.numWant = buffer.readInt();
        peer.port = buffer.readUnsignedShort();
        outsideLoop:
        while (buffer.readableBytes() > 0) {
            short extensionCode = buffer.readByte();
            switch (extensionCode) {
                case 0 -> { // EOF
                    logger.debug("Extension Code = EOF, stop for reading");
                    break outsideLoop;
                }
                case 1 -> { // NOP
                    logger.debug("Extension Code = NOP");
                }
                case 2 -> { // URL Data
                    logger.debug("Extension Code = URL Data ");
                    byte length = buffer.readByte();
                    logger.debug("URL Data Length = {}", length);
                    byte[] param = new byte[length];
                    buffer.readBytes(param);
                    logger.debug("URL Data = {} [raw]", param);
                    String paramString = new String(param, StandardCharsets.UTF_8);
                    logger.debug("URL Data = {} [string]", paramString);
                    //noinspection StringConcatenationInLoop
                    peer.queryParam += paramString;
                }
                default -> {
                    logger.debug("Extension Code Unknown = {}", extensionCode);
                }
            }
        }
        int maxNumWant = Main.getConfig().node("max_wants").getInt();
        if (peer.numWant < 0 || peer.numWant > maxNumWant) {
            peer.numWant = maxNumWant;
        }

        if (peer.ip == 0 && this.getDatagramPacket().sender() != null) {
            InetSocketAddress remoteAddress = this.getDatagramPacket().sender();
            ByteBuffer addressBytes = ByteBuffer.wrap(remoteAddress.getAddress().getAddress());
            peer.ip = addressBytes.getInt();
        }
        Main.getTrackerRequestWorker()
                .announce(peer, ipv6, peer.queryParam, (resp) -> {
                    logger.info("Tracker server response: {}", resp);
                    try {
                        Map<String, Object> serverResponse = BencodeUtil.bittorrent().decode(resp.getBody().getBytes(StandardCharsets.ISO_8859_1), Type.DICTIONARY);
                        boolean success = serverResponse.containsKey("peers");
                        if (success) {
                            handleSuccessAnnounceResponse(peer, serverResponse, ipv6);
                        } else {
                            handleFailureAnnounceResponse(peer, serverResponse);
                        }
                    } catch (Exception exception) {
                        handleTrackerNotExceptedResponse(peer, resp, exception);
                    }
                });
        logger.info("{}", peer);
       // ErrorResponse.send(context, getDatagramPacket(), getTransactionId(), "Fatal error!");
        //AnnounceResponse.send(context,getDatagramPacket(),transactionId,500,15,20, Collections.emptyList());
    }

    private void handleTrackerNotExceptedResponse(Peer peer, HttpResponse<String> resp, Exception exception) {
        logger.warn("Tracker server error: {}", exception.getMessage(), exception);
    }

    private void handleFailureAnnounceResponse(Peer peer, Map<String, Object> serverResponse) {
        logger.warn("Tracker server response: {}", serverResponse);
    }

    private void handleSuccessAnnounceResponse(Peer peer, Map<String, Object> serverResponse, boolean ipv6) throws Exception {
        @SuppressWarnings("unchecked") List<Map<String, String>> peers = (List<Map<String, String>>) serverResponse.get("peers");
        int interval = 600;
        if (serverResponse.containsKey("interval")) {
            interval = Integer.parseInt(String.valueOf(serverResponse.get("interval")));
        }
        int complete = -1;
        int incomplete = -1;
        if (serverResponse.containsKey("complete")) {
            complete =  Integer.parseInt(String.valueOf(serverResponse.get("complete")));
        }
        if (serverResponse.containsKey("incomplete")) {
            incomplete =  Integer.parseInt(String.valueOf(serverResponse.get("incomplete")));
        }


        List<PeerInfo> peerInfos = new ArrayList<>();
        for (Map<String, String> peerData : peers) {
            String ip = peerData.get("ip");
            boolean peerIpv6 = InetAddresses.forString(ip) instanceof Inet6Address;
            if (ipv6 != peerIpv6) continue;
            PeerInfo peerInfo = new PeerInfo(peerData.get("peer id"), ip, Integer.parseInt(peerData.get("port")));
            peerInfos.add(peerInfo);
        }
        AnnounceResponse.send(context, getDatagramPacket(), getTransactionId(),
                interval, incomplete, complete, peerInfos, ipv6);
    }

}
