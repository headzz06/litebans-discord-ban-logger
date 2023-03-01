package me.headzz.discordbanannouncer.utility;

import litebans.api.Entry;
import litebans.api.Events;
import me.headzz.discordbanannouncer.handle.Handler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.net.URL;

public final class Utilities {
    private static final Utilities INSTANCE;

    static {
        INSTANCE = new Utilities();
    }

    private Object mainObject;

    private boolean useBot;
    private String webhookURL;
    private String token;
    private String channelId;

    private JDA jda;
    private TextChannel textChannel;

    public void init(final boolean useBot, final String webhookURL, final String token, final String channelId) throws Exception {
        this.useBot = useBot;
        this.webhookURL = webhookURL;
        this.token = token;
        this.channelId = channelId;

        if (this.useBot) {
            if (this.initJDA(this.token))
                return;
        } else {
            try {
                new URL(this.webhookURL); // Check if it is a real url
            } catch (final Exception exception) {
                this.disable(exception, "Webhook url is not real.");
                throw exception;
            }
        }

        this.registerEvents();
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

    private boolean initJDA(final String token) throws Exception {
        try {
            this.jda = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.watching("server punishments"))
                    .build();

            this.jda.addEventListener(new JDAListener());
        } catch (final Exception exception) {
            this.disable(exception, "Cannot connect to Discord Bot using this BotToken! Disabling!");
            throw exception;
        }

        return false;
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

    public String getToken() {
        return this.token;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public void setTextChannel(final TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    public Utilities setMainObject(final Object obj) {
        this.mainObject = obj;
        return this;
    }

    public void disable(final Exception exception, final String reason) {
        try {
            mainObject.getClass().getMethod("disable", Exception.class, String.class).invoke(mainObject, exception, reason);
        } catch (final Exception reflectionException) {
            reflectionException.printStackTrace();
        }
    }

    public void shutdown() {
        if (this.jda != null)
            this.jda.shutdownNow();
    }

    public Object getMainObject() {
        return this.mainObject;
    }

    public static Utilities getInstance(final Object mainObject) {
        return INSTANCE.setMainObject(mainObject);
    }

    public static Utilities getInstance() {
        return INSTANCE;
    }
}