package com.bitsapling.sapling.udptrackerproxy;

import com.bitsapling.sapling.udptrackerproxy.queue.TrackerRequestWorker;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Config;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.Server;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static TrackerRequestWorker trackerRequestWorker;
    private static Config config;
    private static Server udpTrackerServer;
    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("UdpTrackerProxy - v0.1");
        logger.info("Loading configuration...");
        config = new Config();
        logger.info("Loading tracker request queue...");
        trackerRequestWorker = new TrackerRequestWorker();
        logger.info("Setting up Unirest http client...");
        unirestSetup();
        logger.info("Booting up UDP tracker...");
        udpTrackerServer = new Server();
        System.out.println("Hello world!");
    }

    private static void unirestSetup() {
        Unirest
                .config()
                //.addDefaultHeader("User-Agent", "UdpTrackerProxy(core)/0.1")
                .automaticRetries(false)
                .cacheResponses(false)
                .concurrency(getConfig().node("global_tracker_concurrency")
                        .getInt(),getConfig().node("per_tracker_concurrency").getInt()
                );
     }

    public static TrackerRequestWorker getTrackerRequestWorker() {
        return trackerRequestWorker;
    }

    public static CommentedConfigurationNode getConfig(){
        return config.getConfig();
    }
}