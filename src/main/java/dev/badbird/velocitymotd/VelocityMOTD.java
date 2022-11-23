package dev.badbird.velocitymotd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.badbird.velocitymotd.listener.PingListener;
import dev.badbird.velocitymotd.object.MOTDConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

@Plugin(
        id = "velocitymotd",
        name = "VelocityMOTD",
        version = BuildConstants.VERSION
)
public class VelocityMOTD {
    @Getter
    private static VelocityMOTD instance;

    @Inject
    private Logger logger;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private MOTDConfig motdConfig;

    private ProxyServer server;

    @Inject
    public VelocityMOTD(ProxyServer server) {
        this.server = server;
    }

    @SneakyThrows
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        File pluginDirectory = new File("plugins/VelocityMOTD");
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdir();
        }
        File config = new File(pluginDirectory, "config.json");
        logger.info("Loading config from " + config.getAbsolutePath() + "...");
        if (!config.exists()) {
            motdConfig = new MOTDConfig();
            saveConfig();
        } else {
            motdConfig = gson.fromJson(new String(Files.readAllBytes(config.toPath())), MOTDConfig.class);
        }
        motdConfig.init();
        logger.info("Loaded config!");
        server.getEventManager().register(VelocityMOTD.getInstance(), new PingListener(server));
    }

    public void saveConfig() {
        File pluginDirectory = new File("plugins/VelocityMOTD");
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdir();
        }
        File config = new File(pluginDirectory, "config.json");
        String json = gson.toJson(motdConfig);
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(config);
            printStream.print(json);
            printStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
