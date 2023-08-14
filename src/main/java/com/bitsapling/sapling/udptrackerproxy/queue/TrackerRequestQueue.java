package com.bitsapling.sapling.udptrackerproxy.queue;

import com.bitsapling.sapling.udptrackerproxy.Main;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.Peer;
import com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker.models.PeerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

public class TrackerRequestQueue {
    private Logger logger = LoggerFactory.getLogger(TrackerRequestQueue.class);
    private Deque<Function<Peer, PeerResponse>> workQueue;
    private ElementEjectMethod ejectMethod;

    public TrackerRequestQueue(){
        int workQueueCapacity = Main.getConfig().node("work-queue-size").getInt(100);
        workQueue = new LinkedBlockingDeque<>(Main.getConfig().node("work-queue-size").getInt(100));
        ejectMethod = ElementEjectMethod.valueOf(Main.getConfig().node("work-queue-element-eject-method").getString());
        logger.info("work queue capacity: {}, element eject method: {}",workQueueCapacity , ejectMethod);
    }

    public void announce(Peer peer, boolean ipv6){



    }
    public void scrape(Peer peer){

    }

    public boolean schedule(Function<Peer, PeerResponse> task){
       return workQueue.offer(task);
    }

    public Function<Peer, PeerResponse> next(){
        //noinspection SwitchStatementWithTooFewBranches
        return switch (ejectMethod) {
            case TAIL_FIRST -> workQueue.pollLast();
            default -> workQueue.pollFirst();
        };
    }


    enum ElementEjectMethod{
        HEAD_FIRST,
        TAIL_FIRST;
    }
}
