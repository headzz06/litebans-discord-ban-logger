package me.headzz.discordbanannouncer.handle.entries;

import me.headzz.discordbanannouncer.utility.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public abstract class Entry {
    protected final EmbedBuilder embedBuilder;

    private final String playerName;
    private final String staffName;
    private final String imageURL;

    public Entry(final String playerName, final String staffName) {
        this.embedBuilder = new EmbedBuilder();

        this.playerName = this.format(playerName);
        this.staffName = this.format(staffName);

        this.imageURL = String.format("https://minotar.net/helm/%s/100.png", playerName);
        this.embedBuilder.setImage(this.imageURL);
    }

    public final String getPlayerName() {
        return this.playerName;
    }

    public final String getStaffName() {
        return this.staffName;
    }

    public final String getImageURL() {
        return this.imageURL;
    }

    public abstract void execute();

    public abstract void executeDiscordWebhook();

    protected void sendEmbed() {
        try {
            Utilities.getInstance().getTextChannel().sendMessageEmbeds(this.embedBuilder.build()).queue();
        } catch (final Exception ignored) {}
    }

    protected String format(final String str) {
        return String.format("`%s`", str.isEmpty() ? "None" : str);
    }
}