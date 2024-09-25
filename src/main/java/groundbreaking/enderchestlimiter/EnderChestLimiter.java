package groundbreaking.enderchestlimiter;

import groundbreaking.enderchestlimiter.listeners.ItemLimiter;
import groundbreaking.enderchestlimiter.listeners.UpdatesNotify;
import groundbreaking.enderchestlimiter.utils.MessageSender;
import groundbreaking.enderchestlimiter.utils.ServerInfos;
import groundbreaking.enderchestlimiter.utils.UpdatesChecker;
import groundbreaking.enderchestlimiter.utils.colorizer.IColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.LegacyColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.MiniMessagesColorizer;
import groundbreaking.enderchestlimiter.utils.colorizer.VanilaColorizer;
import groundbreaking.enderchestlimiter.utils.config.Config;
import groundbreaking.enderchestlimiter.utils.config.ConfigValues;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderChestLimiter extends JavaPlugin {

    private final ServerInfos infos = new ServerInfos(this);

    @Getter
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

        saveDefaultConfig();
        new Config(this).checkVersion();
        new ConfigValues(this).setValues();

        final UpdatesChecker updatesChecker = new UpdatesChecker(this);
        Bukkit.getScheduler().runTaskAsynchronously(this, updatesChecker::startCheck);

        {
            getServer().getPluginManager().registerEvents(new ItemLimiter(), this);
            getServer().getPluginManager().registerEvents(new UpdatesNotify(this, updatesChecker), this);

            getCommand("eclimiter").setExecutor((sender, command, label, args) -> {
                final long reloadStartTime = System.currentTimeMillis();
                if (!sender.hasPermission("enderchestlimiter.reload")) {
                    MessageSender.send(sender, ConfigValues.getNoPermMessages());

                    return true;
                }

                new Config(this).checkVersion();
                setColorizer();
                new ConfigValues(this);

                MessageSender.send(sender, ConfigValues.getReloadMessages(), "%time%", String.valueOf(System.currentTimeMillis() - reloadStartTime));

                return true;
            });
        }

        getLogger().info("Plugin was successfully started in: " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    public void setColorizer() {
        colorizer = getConfig().getBoolean("use-minimessage") ? new MiniMessagesColorizer() : infos.isAbove16() ? new LegacyColorizer() : new VanilaColorizer();
    }
}
