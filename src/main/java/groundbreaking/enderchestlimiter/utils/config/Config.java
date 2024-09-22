package groundbreaking.enderchestlimiter.utils.config;

import com.google.common.base.Charsets;
import groundbreaking.enderchestlimiter.EnderChestLimiter;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Logger;

public final class Config {

    @Getter
    private static FileConfiguration config = null;
    private static File file = null;
    
    private final EnderChestLimiter plugin;
    private final Logger logger;

    public Config(EnderChestLimiter plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }


    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }

        try {
            new YamlConfiguration().load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        config = YamlConfiguration.loadConfiguration(file);

        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
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
            final File backupFile = new File(folder, ("config_backup_" + backupNumber + ".yml"));
            if (file.renameTo(backupFile)) {
                plugin.saveResource("config.yml", true);
            } else {
                plugin.getLogger().warning("Your configuration file is old, but create new isn't possible.");
            }

            loadConfig();
        }
    }
}