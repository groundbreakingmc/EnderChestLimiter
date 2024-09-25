package groundbreaking.enderchestlimiter.utils.config;

import groundbreaking.enderchestlimiter.EnderChestLimiter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Objects;

public final class Config {

    private final EnderChestLimiter plugin;
    private final FileConfiguration config;

    public Config(EnderChestLimiter plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void checkVersion() {
        final double version = 1.0;
        if (config.getDouble("settings.config-version") != version
                || !config.contains("settings.config-version")) {

            final File folder = plugin.getDataFolder();
            if (!folder.exists() && !folder.mkdirs()) {
                plugin.getLogger().warning("An error occurred while creating backups folder!");
                return;
            }

            final int backupNumber = Objects.requireNonNull(folder.listFiles()).length;
            final File file = new File(folder, "config.yml");
            final File backupFile = new File(folder, ("config_backup_" + backupNumber + ".yml"));
            if (file.renameTo(backupFile)) {
                plugin.saveResource("config.yml", true);
            } else {
                plugin.getLogger().warning("Your configuration file is old, but create new isn't possible.");
            }

            plugin.saveDefaultConfig();
        }
    }
}