package me.headzz.discordbanannouncer;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.headzz.discordbanannouncer.utility.ConfigUtils;
import me.headzz.discordbanannouncer.utility.Utilities;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(id = "discordbanannouncer", name = "DiscordBanAnnouncer", version = "1.0", description = "Allows you to log LiteBans related activity to a discord channel", authors = { "Headzz", "Hellin" }, dependencies = {@Dependency(id = "litebans")})
public class DiscordBanAnnouncerVelocity {
    public static DiscordBanAnnouncerVelocity instance = null;

    private File dataFolder;
    private FileConfiguration config; // Had to use some external library because velocity is gay
    private final ProxyServer proxy;
    private final Logger logger;
    private Utilities utilities;

    @Inject
    public DiscordBanAnnouncerVelocity(final ProxyServer proxy, final Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.proxy = proxy;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent e) {
        this.utilities = Utilities.getInstance(this);

        try {
            ConfigUtils.saveConfigFromResources(dataFolder);
        } catch (final IOException exception) {
            this.utilities.disable(exception, "Failed to save config");
        }

        this.config = ConfigUtils.getConfig();

        try {
            this.utilities.init(this.config.getBoolean("UseBot"), this.config.getString("WebhookURL"), this.config.getString("BotToken"), this.config.getString("ChannelID"));
        } catch (final Exception ignored) {}
    }

    @Subscribe
    public void onProxyDisable(final ProxyShutdownEvent e) {
        this.utilities.shutdown();
    }

    public void disable(final Exception exception, final String reason) {
        exception.printStackTrace();
        this.logger.error(reason);
        this.onProxyDisable(null);
        proxy.shutdown();
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPostLogin(final PostLoginEvent e) {
        final Player p = e.getPlayer();

        final String uuid = String.valueOf(p.getUniqueId());
        final String name = p.getUsername();

        try {
            ConfigUtils.saveName(uuid, name);
        } catch (final Exception ignored) {}
    }
}