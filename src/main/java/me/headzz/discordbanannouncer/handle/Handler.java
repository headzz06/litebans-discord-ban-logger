package me.headzz.discordbanannouncer.handle;

import litebans.api.PlayerProvider;
import me.headzz.discordbanannouncer.handle.entries.Entry;
import me.headzz.discordbanannouncer.handle.entries.impl.*;
import me.headzz.discordbanannouncer.utility.ConfigUtils;
import me.headzz.discordbanannouncer.utility.Utilities;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Handler {
    public static final Handler instance;

    private final String removed = "-removed";
    private final Map<String, Class<? extends Entry>> entries;

    static {
        instance = new Handler();
    }

    public Handler() {
        this.entries = new HashMap<>();

        /*
         * Added entries:
         */
        this.entries.put("ban", AddBan.class);
        this.entries.put("warn", AddWarn.class);
        this.entries.put("mute", AddMute.class);
        this.entries.put("kick", AddKick.class);

        /*
         * Deleted entries:
         */
        this.entries.put("ban" + this.removed, DelBan.class);
        this.entries.put("mute" + this.removed, DelMute.class);
        this.entries.put("warn" + this.removed, DelWarn.class);
    }

    public void handle(final litebans.api.Entry entry, final boolean isRemoved) throws Exception {
        final String type = entry.getType() + (isRemoved ? this.removed : "");

        if (!this.entries.containsKey(type))
            return;

        final Class<? extends Entry> clazz = this.entries.get(type);

        final String uuid = entry.getUuid();
        String userName = this.getName(uuid);
        if (userName == null)
            userName = uuid;

        final Entry newEntry = (Entry) clazz.getConstructors()[0].newInstance(userName, entry);

        if (Utilities.getInstance().shouldUseBot()) {
            newEntry.execute();
            return;
        }

        newEntry.executeDiscordWebhook();
    }

    public Map<String, Class<? extends Entry>> getEntries() {
        return this.entries;
    }

    private String getName(final String uuid) {
        return ConfigUtils.getName(uuid);
    }
}