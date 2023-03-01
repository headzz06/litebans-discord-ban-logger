package me.headzz.discordbanannouncer;

import me.headzz.discordbanannouncer.utility.ConfigUtils;
import me.headzz.discordbanannouncer.utility.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class DiscordBanAnnouncer extends JavaPlugin implements Listener {
    public static DiscordBanAnnouncer instance = null;

    private File dataFolder;
    private File configFile;
    private YamlConfiguration config;
    private Logger logger;
    private Utilities utilities;

    public DiscordBanAnnouncer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.logger = super.getLogger();
        this.dataFolder = super.getDataFolder();

        this.utilities = Utilities.getInstance(this);

        try {
            this.configFile = ConfigUtils.saveConfigFromResources(dataFolder, false);
        } catch (final Exception ignored) {}

        this.config = YamlConfiguration.loadConfiguration(configFile);

        try {
            this.utilities.init(this.config.getBoolean("UseBot"), this.config.getString("WebhookURL"), this.config.getString("BotToken"), this.config.getString("ChannelID"));
        } catch (final Exception ignored) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /*
     * This method needs to be in all instances of the main class
     */
    public void disable(final Exception exception, final String reason) {
        exception.printStackTrace();
        this.logger.severe(reason);
        super.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        this.utilities.shutdown();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        final String uuid = String.valueOf(p.getUniqueId());
        final String name = p.getName();

        try {
            ConfigUtils.saveName(uuid, name);
        } catch (final Exception ignored) {}
    }
}