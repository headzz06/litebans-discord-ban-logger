package me.headzz.discordbanannouncer.utility;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public final class ConfigUtils {
    private static YamlConfiguration config = null;
    private static File dataFolder = null;
    private static boolean useBuiltIn = true;

    private static final Map<String, String> UUID_NAME_MAP = new HashMap<>();

    public static File saveConfigFromResources(final File dir) throws IOException {
        return saveConfigFromResources(dir, true);
    }

    public static File saveConfigFromResources(final File dir, final boolean load) throws IOException {
        dataFolder = dir;
        makeFolder(dataFolder);

        final File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            final InputStream in = Utilities.class.getClassLoader().getResourceAsStream("config.yml");
            Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        useBuiltIn = !load;
        if (load)
            config = YamlConfiguration.loadConfiguration(configFile);
        return configFile;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static void saveName(final String uuid, final String name) throws Exception {
        final File playerDataDir = makePlayerData(dataFolder);

        final File data = new File(playerDataDir, String.format("%s.yml", uuid));

        if (!data.exists()) {
            data.createNewFile();
        } else {
            if (getName(name).equals(name))
                return;
        }

        if (useBuiltIn) {
            final org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
            config.set("name", name);
            config.save(data);
        } else {
            final YamlConfiguration config = new YamlConfiguration();
            config.set("name", name);
            config.save(data);
        }
    }

    public static String getName(final String uuid) {
        if (UUID_NAME_MAP.containsKey(uuid))
            return UUID_NAME_MAP.get(uuid);

        final File playerDataDir = new File(dataFolder, "player-data");
        if (!playerDataDir.exists())
            return null;

        final File data = new File(playerDataDir, String.format("%s.yml", uuid));
        if (!data.exists())
            return null;

        final String name;

        if (useBuiltIn) {
            final org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(data);
            name = config.getString("name");
        } else {
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(data);
            name = config.getString("name");
        }

        UUID_NAME_MAP.put(uuid, name);
        return name;
    }

    private static void makeFolder(final File dataFolder) {
        if (!dataFolder.exists())
            dataFolder.mkdirs();
    }

    private static File makePlayerData(final File dataFolder) {
        final File playerData = new File(dataFolder, "player-data");
        makeFolder(playerData);
        return playerData;
    }
}