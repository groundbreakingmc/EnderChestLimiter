package groundbreaking.enderchestlimiter;

import groundbreaking.enderchestlimiter.listeners.ItemLimiter;
import groundbreaking.enderchestlimiter.utils.MessageSender;
import groundbreaking.enderchestlimiter.utils.ServerInfos;
import groundbreaking.enderchestlimiter.utils.UpdatesChecker;
import groundbreaking.enderchestlimiter.utils.colorizer.IColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.LegacyColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.MiniMessagesColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.VanilaColorizer;
import groundbreaking.enderchestlimiter.utils.config.Config;
import groundbreaking.enderchestlimiter.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class EnderChestLimiter extends JavaPlugin {

    private final Logger logger = getLogger();

    private final PluginManager pluginManager = getServer().getPluginManager();

    private final ServerInfos infos = new ServerInfos(getServer(), logger, pluginManager);

    private IColorizer colorizer;

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();

        if (!infos.isPaperOrFork()) {
            getLogger().warning("\u001b[91m=============== \u001b[31mWARNING \u001b[91m===============\u001b[0m");
            getLogger().warning("\u001b[91mThe plugin author against using Bukkit, Spigot etc.!\u001b[0m");
            getLogger().warning("\u001b[91mMove to Paper or his forks. To download Paper visit:\u001b[0m");
            getLogger().warning("\u001b[91mhttps://papermc.io/downloads/all\u001b[0m");
            getLogger().warning("\u001b[91m=======================================\u001b[0m");
            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        Config cfg = new Config(this, logger);
        cfg.loadConfig();
        cfg.checkVersion();
        new ConfigValues(logger, getConfig(), colorizer).setValues();

        {
            getServer().getPluginManager().registerEvents(new ItemLimiter(), this);

            getCommand("eclimiter").setExecutor((sender, command, label, args) -> {
                final long reloadStartTime = System.currentTimeMillis();
                if (!sender.hasPermission("enderchestlimiter.reload")) {
                    MessageSender.send(sender, ConfigValues.getNoPermMessages());

                    return true;
                }

                reloadConfig();

                MessageSender.send(sender, ConfigValues.getReloadMessages(), "%time%", String.valueOf(System.currentTimeMillis() - reloadStartTime));

                return true;
            });
        }

        final UpdatesChecker updatesChecker = new UpdatesChecker(this, getConfig(), logger);
        Bukkit.getScheduler().runTaskAsynchronously(this, updatesChecker::startCheck);

        logger.info("Plugin was successfully started in: " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return Config.getConfig();
    }

    @Override
    public void reloadConfig() {
        new Config(this, logger).loadConfig();
        setColorizer();
        new ConfigValues(logger, getConfig(), colorizer);
    }

    public void setColorizer() {
        colorizer = getConfig().getBoolean("use-minimessage") ? new MiniMessagesColorizer() : infos.isAbove16() ? new LegacyColorizer() : new VanilaColorizer();
    }
}
