package com.bitsapling.sapling.udptrackerproxy.queue;

import com.bitsapling.sapling.udptrackerproxy.Main;
import com.bitsapling.sapling.udptrackerproxy.bt.tracker.http.urlencoding.TrackerQueryBuilder;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.Peer;
import com.google.common.net.InetAddresses;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class TrackerRequestWorker {
    private static final Random RANDOM = new Random();
    private final Logger logger = LoggerFactory.getLogger(TrackerRequestWorker.class);
    private URLCodec codec = new URLCodec();

    public TrackerRequestWorker() {
    }

    public void announce(Peer peer, boolean ipv6, String queryParam, Consumer<Pair<String, HttpResponse<String>>> callback) {
        if (queryParam.startsWith("/")) {
            queryParam = queryParam.substring(1);
        }
        String peerId = new String(peer.peerId, StandardCharsets.ISO_8859_1);
        String peerIp = InetAddresses.fromInteger(peer.ip).getHostAddress();
        if (peerId.length() < 8) {
            logger.warn("Peer id is too short: {}, announce won't forward to tracker!", peerId);
            return;
        }
        String tracker = randomPickTracker();
        try {
            String announceUrl = buildAnnounceUrl(tracker, queryParam, peer, ipv6);
            logger.info("Announcing to tracker: {}", announceUrl);
            Unirest.get(announceUrl)
                    //.headerReplace("User-Agent", "qBittorrent/4.5.4")
                    .headerReplace("User-Agent", "UdpTrackerProxy(" + peerId.substring(0, 8) + ")/0.1")
                    .header("X-Real-IP", peerIp)
                    .header("X-Forwarded-For", peerIp)
                    .asStringAsync()
                    .thenAccept(c->callback.accept(Pair.of(announceUrl,c)))
                    .exceptionally(err -> {
                        logger.error("Failed to announce to tracker: {}", tracker, err);
                        return null;
                    });

        } catch (URISyntaxException | EncoderException e) {
            logger.error("Failed to build announce url for tracker: {}", tracker);
        }
    }

    public void scrape(String queryParam, List<byte[]> infoHashes, String peerIp, Consumer<Pair<String, HttpResponse<String>>> callback) {
        if (queryParam.startsWith("/")) {
            queryParam = queryParam.substring(1);
        }
        String tracker = randomPickTracker();
        String announceUrl = buildScrapeUrl(tracker, queryParam,infoHashes);
        String scrapeUrl = announceUrl.replace("announce", "scrape");
        logger.info("Scrape to tracker: {}", scrapeUrl);
        Unirest.get(scrapeUrl)
                //.headerReplace("User-Agent", "qBittorrent/4.5.4")
                .headerReplace("User-Agent", "UdpTrackerProxy(unknown-scrape)/0.1")
                .header("X-Real-IP", peerIp)
                .header("X-Forwarded-For", peerIp)
                .asStringAsync()
                .thenAccept(c->callback.accept(Pair.of(scrapeUrl,c)))
                .exceptionally(err -> {
                    logger.error("Failed to scrape to tracker: {}", tracker, err);
                    return null;
                });
    }

    private String buildScrapeUrl(String tracker, String params, List<byte[]> infoHashes) {
        params = StringUtils.substringAfter(params, "?");
        String[] queries = params.split("&");
        TrackerQueryBuilder builder = new TrackerQueryBuilder();
        for (byte[] infoHash : infoHashes) {
            builder.add("info_hash", infoHash);
        }
        for (String query : queries) {
            String[] kv = query.split("=");
            if (kv.length == 2) {
                builder.add(kv[0], kv[1]);
            }
        }
        return tracker + "?" + builder.toQuery();
    }

    private String buildAnnounceUrl(String tracker, String params, Peer peer, boolean ipv6) throws URISyntaxException, EncoderException {
        params = StringUtils.substringAfter(params, "?");
        String[] queries = params.split("&");
        TrackerQueryBuilder builder = new TrackerQueryBuilder();
        builder.add("info_hash", peer.infoHash);
        builder.add("peer_id", peer.peerId);
        builder.add("port", String.valueOf(peer.port));
        builder.add("uploaded", String.valueOf(peer.uploaded));
        builder.add("downloaded", String.valueOf(peer.downloaded));
        builder.add("left", String.valueOf(peer.left));
        builder.add("compact", "0"); // Disable compact response, local doesn't care about that
        builder.add("numwant", String.valueOf(peer.numWant));
        builder.add("event", String.valueOf(peer.event));
        if (ipv6) {
            builder.add("ipv6", InetAddresses.fromInteger(peer.ip).getHostAddress());
        } else {
            builder.add("ipv4", InetAddresses.fromInteger(peer.ip).getHostAddress());
        }
        for (String query : queries) {
            String[] kv = query.split("=");
            if (kv.length == 2) {
                builder.add(kv[0], kv[1]);
            }
        }
        return tracker + "?" + builder.toQuery();
    }

    @SneakyThrows
    private String randomPickTracker() {
        List<String> trackers = Main.getConfig()
                .node("tracker_endpoint")
                .getList(String.class);
        if (trackers == null) return "http://0.0.0.0:0/error/no-tracker-defined";
        return trackers.get(RANDOM.nextInt(trackers.size()));
    }
}
