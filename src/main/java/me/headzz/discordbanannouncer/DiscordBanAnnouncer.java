package me.headzz.discordbanannouncer;

import litebans.api.Entry;
import litebans.api.Events;
import me.headzz.discordbanannouncer.handle.Handler;
import me.headzz.discordbanannouncer.utility.JDAListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.util.logging.Logger;

public final class DiscordBanAnnouncer extends JavaPlugin implements Listener {
    public static DiscordBanAnnouncer instance = null;

    private FileConfiguration config;
    private Logger logger;
    private boolean useBot;
    private JDA jda;
    public TextChannel textChannel;
    private String webhookURL;

    public DiscordBanAnnouncer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.config = super.getConfig();
        this.logger = super.getLogger();

        this.config.options().copyDefaults();
        super.saveDefaultConfig();

        this.useBot = config.getBoolean("UseBot");

        if (this.useBot)
            if (this.initJda())
                return;
        else {
            this.initWebhook();

            try {
                new URL(this.webhookURL); // Check if it is a real url
            } catch (final Exception exception) {
                this.disable(exception, "Webhook url is not real.");
                return;
            }
        }

        this.registerEvents();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean initJda() {
        try {
            this.jda = JDABuilder.createDefault(this.config.getString("BotToken"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.watching("server punishments"))
                    .build();

            this.jda.addEventListener(new JDAListener());
        } catch (final Exception exception) {
            this.disable(exception, "Cannot connect to Discord Bot using this BotToken! Disabling!");
            return true;
        }

        return false;
    }

    private void initWebhook() {
        this.webhookURL = this.config.getString("WebhookURL");
    }

    public void disable(final Exception exception, final String reason) {
        exception.printStackTrace();
        this.logger.severe(reason);
        super.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        if (jda != null)
            jda.shutdownNow();
    }

    private void registerEvents() {
        Events.get().register(new Events.Listener(){
            @Override
            public void entryAdded(final Entry entry){
                try {
                    Handler.instance.handle(entry, false);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }
            @Override
            public void entryRemoved(final Entry entry){
                try {
                    Handler.instance.handle(entry, true);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public String getWebhookURL() {
        return this.webhookURL;
    }

    public boolean shouldUseBot() {
        return this.useBot;
    }
}