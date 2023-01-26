package me.headzz.discordbanannouncer.handle.entries.impl;

import me.headzz.discordbanannouncer.DiscordBanAnnouncer;
import me.headzz.discordbanannouncer.handle.entries.Entry;
import me.headzz.discordbanannouncer.utility.GenericMessages;
import me.headzz.discordbanannouncer.utility.WebhookColors;
import me.headzz.discordbanannouncer.utility.WebhookUtils;

import java.awt.*;

public class DelWarn extends Entry {
    private String reason;

    public DelWarn(final String playerName, final litebans.api.Entry entry) {
        super(playerName, entry.getRemovedByName());

        this.reason = super.format(entry.getRemovalReason());
    }

    @Override
    public void execute() {
        super.embedBuilder.setTitle("New Unwarn").addField(GenericMessages.USERNAME, super.getPlayerName(), false)
                .addField(GenericMessages.PUNISHED_BY, super.getStaffName(), true)
                .addField(GenericMessages.REASON, this.reason, true)
                .setColor(Color.GREEN);

        super.sendEmbed();
    }

    @Override
    public void executeDiscordWebhook() {
        WebhookUtils.newWebhookUtils(DiscordBanAnnouncer.instance.getWebhookURL()).execute(String.format("{\"content\":null,\"embeds\":[{\"title\":\"New Unwarn\",\"color\":%s,\"fields\":[{\"name\":\"%s\",\"value\":\"%s\"},{\"name\":\"%s\",\"value\":\"%s\",\"inline\":true},{\"name\":\"%s\",\"value\":\"%s\",\"inline\":true}],\"thumbnail\":{\"url\":\"%s\"}}],\"attachments\":[]}", WebhookColors.GREEN.getHexCode(),
                GenericMessages.USERNAME, super.getPlayerName(),
                GenericMessages.REMOVED_BY, super.getStaffName(),
                GenericMessages.REASON, this.reason, super.getImageURL()));
    }
}