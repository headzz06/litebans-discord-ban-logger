package me.headzz.discordbanannouncer.utility;

import me.headzz.discordbanannouncer.DiscordBanAnnouncer;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onReady(final ReadyEvent e) {
        try {
            final String channelID = DiscordBanAnnouncer.instance.getConfig().getString("ChannelID");
            DiscordBanAnnouncer.instance.textChannel = DiscordBanAnnouncer.instance.getJDA().getTextChannelById(channelID);
        } catch (final Exception exception) {
            DiscordBanAnnouncer.instance.disable(exception, "Invalid Channel ID! Disabling plugin!");
        }
    }
}