package me.headzz.discordbanannouncer;

import me.headzz.discordbanannouncer.utility.ConfigUtils;
import me.headzz.discordbanannouncer.utility.Utilities;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class DiscordBanAnnouncerBungee extends Plugin implements Listener {
    public static DiscordBanAnnouncerBungee instance = null;

    private File dataFolder;
    private YamlConfiguration config;
    private Logger logger;
    private Utilities utilities;

    public DiscordBanAnnouncerBungee() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.logger = super.getLogger();
        this.dataFolder = super.getDataFolder();

        this.utilities = Utilities.getInstance(this);

        try {
            ConfigUtils.saveConfigFromResources(dataFolder);
        } catch (final IOException exception) {
            this.utilities.disable(exception, "Failed to save config");
        }

        this.config = ConfigUtils.getConfig();

        try {
            this.utilities.init(this.config.getBoolean("UseBot"), this.config.getString("WebhookURL"), this.config.getString("BotToken"), this.config.getString("ChannelID"));
        } catch (final Exception ignored) {
            return;
        }

        final PluginManager pluginManager = super.getProxy().getPluginManager();
        pluginManager.registerListener(this, this);
    }

    /*
     * This method needs to be in all instances of the main class
     */
    public void disable(final Exception exception, final String reason) {
        exception.printStackTrace();
        this.logger.severe(reason);

        final ProxyServer proxy = super.getProxy();
        final PluginManager manager = proxy.getPluginManager();

        manager.unregisterListeners(this);
        manager.unregisterCommands(this);
        this.onDisable();
        super.getProxy().stop();
    }

    @Override
    public void onDisable() {
        this.utilities.shutdown();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(final PostLoginEvent e) {
        final ProxiedPlayer p = e.getPlayer();

        final String uuid = String.valueOf(p.getUniqueId());
        final String name = String.valueOf(p.getName());

        try {
            ConfigUtils.saveName(uuid, name);
        } catch (final Exception ignored) {}
    }
}