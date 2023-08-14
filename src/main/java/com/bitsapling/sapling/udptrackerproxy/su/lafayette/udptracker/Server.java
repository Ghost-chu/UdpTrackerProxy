package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker;

import com.bitsapling.sapling.udptrackerproxy.Main;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	private final EventLoopGroup bossLoopGroup;

	private final ChannelGroup channelGroup;
	public Server() throws InterruptedException {
		this.bossLoopGroup = new NioEventLoopGroup();
		this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		start2();
	}

	public void start2() throws InterruptedException {
		int port = Main.getConfig().node("proxy_port").getInt(5757);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(bossLoopGroup)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.AUTO_CLOSE, true)
				.option(ChannelOption.SO_BROADCAST, true);
		bootstrap.handler(new Handler());
		try {
			ChannelFuture channelFuture = bootstrap
					.bind(port)
					.sync();
			channelGroup.add(channelFuture.channel());
			logger.info("Listening on port " +port);
		} catch (Exception e) {
			shutdown();
			throw e;
		}
	}

	public void shutdown() {
		channelGroup.close();
		bossLoopGroup.shutdownGracefully();
	}
//	public void start() {
//		logger.info("Starting BitTorrent UDP tracker...");
//		Executor threadPool = Executors.newCachedThreadPool();
//		InetSocketAddress listenOn = new InetSocketAddress(5757);
//		EventLoopGroup group = new NioEventLoopGroup();
//		Bootstrap b = new Bootstrap();
//		b.option(ChannelOption.SO_REUSEADDR, true);
//		try {
//			b.group(group)
//					.channelFactory((ChannelFactory<NioDatagramChannel>)
//							() -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
//					.handler(new ChannelInitializer<NioDatagramChannel>() {
//						@Override
//						protected void initChannel(NioDatagramChannel nioDatagramChannel) {
//							nioDatagramChannel.pipeline().addLast(new Handler());
//						}
//					})
//					//.option(ChannelOption.SO_BROADCAST, true)
//					.option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
//					.option(ChannelOption.SO_REUSEADDR, true);
//			NioDatagramChannel ch = (NioDatagramChannel) b.bind(listenOn.getPort()).sync().channel();
//			logger.info("Listening on " + listenOn.getHostName() + ":" + listenOn.getPort());
//			ch.joinGroup(listenOn, NetUtil.LOOPBACK_IF).sync();
//
//			ch.closeFuture().await();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			group.shutdownGracefully();
//		}
//	}
}
