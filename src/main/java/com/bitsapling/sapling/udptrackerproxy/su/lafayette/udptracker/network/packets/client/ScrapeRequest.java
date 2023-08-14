package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.client;


import com.bitsapling.sapling.udptrackerproxy.Main;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.TorrentStats;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.ClientRequest;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ErrorResponse;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.network.packets.server.ScrapeResponse;
import com.bitsapling.sapling.udptrackerproxy.util.BencodeUtil;
import com.bitsapling.sapling.udptrackerproxy.util.InfoHashUtil;
import com.dampcake.bencode.Type;
import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScrapeRequest extends ClientRequest {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeRequest.class);

    public void read(boolean ipv6) {
        logger.debug("ScrapeRequest::read from " + this.getDatagramPacket().sender());
        List<byte[]> infoHashes = new ArrayList<>();
        while (this.getChannelBuffer().readableBytes() >= 20) {
            byte[] bytesToRead = new byte[20];
            this.channelBuffer.readBytes(bytesToRead);
            infoHashes.add(bytesToRead);
            if (infoHashes.size() >= 74) {
                break;
            }
        }
        String queryParam = "";
        outsideLoop:
        while (this.getChannelBuffer().readableBytes() > 0) {
            short extensionCode = this.getChannelBuffer().readByte();
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
                    byte length = this.getChannelBuffer().readByte();
                    logger.debug("URL Data Length = {}", length);
                    byte[] param = new byte[length];
                    this.getChannelBuffer().readBytes(param);
                    logger.debug("URL Data = {} [raw]", param);
                    String paramString = new String(param, StandardCharsets.UTF_8);
                    logger.debug("URL Data = {} [string]", paramString);
                    //noinspection StringConcatenationInLoop
                    queryParam += paramString;
                }
                default -> {
                    logger.debug("Extension Code Unknown = {}", extensionCode);
                }
            }
        }

        Main.getTrackerRequestWorker().scrape(
                queryParam,
                infoHashes,
                getDatagramPacket().sender().getHostName(),
                (resp) -> {
                    try {
                        Map<String, Object> serverResponse = BencodeUtil
                                .bittorrent()
                                .decode(resp.getRight().getBody().getBytes(StandardCharsets.ISO_8859_1), Type.DICTIONARY);
                        boolean success = serverResponse.containsKey("peers");
                        if (success) {
                            handleSuccessScrapeResponse(serverResponse, resp.getLeft(), infoHashes);
                        } else {
                            handleFailureScrapeResponse(serverResponse, resp.getLeft());
                        }
                    } catch (Exception e) {
                        handleTrackerNotExceptedResponse(resp.getRight(), e, resp.getLeft());
                    }
                }
        );
    }

    private void handleTrackerNotExceptedResponse(HttpResponse<String> right, Exception e, String tracker) {
    }

    private void handleFailureScrapeResponse(Map<String, Object> serverResponse, String tracker) {
    }

    private void handleSuccessScrapeResponse(Map<String, Object> serverResponse, String tracker, List<byte[]> infoHashes) throws Exception {
        List<TorrentStats> torrentStatsList = new ArrayList<>();
        @SuppressWarnings("unchecked") Map<String, Map<String, Long>> files = (Map<String, Map<String, Long>>) serverResponse.get("files");
        hashLoop:
        for (byte[] infoHash : infoHashes) {
            if (files == null) {
                ErrorResponse.send(context, getDatagramPacket(), getTransactionId(), "files not found in response");
                return;
            }
            TorrentStats torrentStats = new TorrentStats();
            torrentStats.infoHash = infoHash;
            torrentStats.completed = 0;
            torrentStats.leechers = 0;
            torrentStats.seeders = 0;
            for (Map.Entry<String, Map<String, Long>> e : files.entrySet()) {
                if (e.getKey().equals(InfoHashUtil.parseInfoHash(new String(infoHash, StandardCharsets.ISO_8859_1)))) {
                    torrentStats.seeders = Integer.parseInt(String.valueOf(e.getValue().get("complete")));
                    torrentStats.leechers = Integer.parseInt(String.valueOf(e.getValue().get("incomplete")));
                    torrentStats.completed = Integer.parseInt(String.valueOf(e.getValue().get("downloaded")));
                    torrentStatsList.add(torrentStats);
                    continue hashLoop;
                }
            }
        }
        logger.info("[OK] scrape:success -> {}", torrentStatsList);
        ScrapeResponse.send(context, getDatagramPacket(), getTransactionId(), torrentStatsList);
    }
}
