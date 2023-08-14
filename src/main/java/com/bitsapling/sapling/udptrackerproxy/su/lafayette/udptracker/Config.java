package com.bitsapling.sapling.udptrackerproxy.su.lafayette.udptracker;

import com.bitsapling.sapling.udptrackerproxy.Main;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config  {
	private static final File CONFIG_FILE = new File("udp-tracker-config.yml");
	private YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
			.file(CONFIG_FILE)
			.build();
	private CommentedConfigurationNode node ;
	public Config() throws IOException {
		if(!CONFIG_FILE.exists()){
			Files.copy(Main.class.getResourceAsStream("/config.yml"), CONFIG_FILE.toPath());
		}
		node = LOADER.load();
	}

	public CommentedConfigurationNode getConfig() {
		return node;
	}
}
