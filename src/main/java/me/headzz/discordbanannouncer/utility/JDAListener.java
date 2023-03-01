package me.headzz.discordbanannouncer.utility;

import me.headzz.discordbanannouncer.DiscordBanAnnouncer;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onReady(final ReadyEvent e) {
        try {
            final String channelID = Utilities.getInstance().getChannelId();
            Utilities.getInstance().setTextChannel(Utilities.getInstance().getJDA().getTextChannelById(channelID));
        } catch (final Exception exception) {
            Utilities.getInstance().disable(exception, "Invalid Channel ID! Disabling plugin!");
        }
    }
}