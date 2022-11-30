package dev.badbird.velocitymotd.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.badbird.velocitymotd.VelocityMOTD;
import dev.badbird.velocitymotd.object.MOTDConfig;

import javax.inject.Inject;

public class PingListener {
    private ProxyServer server;

    @Inject
    public PingListener(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        MOTDConfig config = VelocityMOTD.getInstance().getMotdConfig();
        String serverVersion = config.getServerVersion();
        ServerPing.Version version = event.getPing().getVersion();
        String versionName = version.getName();
        int protocolVersion = config.getProtocolVersion();
        if (serverVersion != null && !serverVersion.isEmpty() && !serverVersion.equalsIgnoreCase("DEFAULT")) {
            versionName = serverVersion;
        }
        if (protocolVersion < 0) {
            protocolVersion = version.getProtocol();
        }
        ServerPing.Version newVersion = new ServerPing.Version(protocolVersion, versionName);

        ServerPing.Builder description = event.getPing().asBuilder()
                .description(config.getMOTD())
                .version(newVersion)
                .maximumPlayers(config.getMaxPlayers())
                .clearSamplePlayers()
                .onlinePlayers(server.getPlayerCount())
                .samplePlayers(config.getSamplePlayers())
                ;

        event.setPing(description.build());
    }
}
