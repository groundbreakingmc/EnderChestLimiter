package groundbreaking.enderchestlimiter.listeners;

import groundbreaking.enderchestlimiter.EnderChestLimiter;
import groundbreaking.enderchestlimiter.utils.UpdatesChecker;
import groundbreaking.enderchestlimiter.utils.colorizer.IColorizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdatesNotify implements Listener {

    private final EnderChestLimiter plugin;
    private final IColorizer colorizer;
    private final UpdatesChecker updatesChecker;

    public UpdatesNotify(EnderChestLimiter plugin, UpdatesChecker updatesChecker) {
        this.plugin = plugin;
        this.colorizer = plugin.getColorizer();
        this.updatesChecker = updatesChecker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if ((player.isOp() || player.hasPermission("eclimiter.updates")) && updatesChecker.getNew_version()) {
                player.sendMessage("");
                player.sendMessage(colorizer.colorize("&c[EnderChestLimiter] &6New update is available to download!"));
                player.sendMessage(colorizer.colorize("&c[EnderChestLimiter] &fDownload link: " + updatesChecker.getDownloadLink()));
                player.sendMessage(colorizer.colorize("&c[EnderChestLimiter] &fCurrently version: " + updatesChecker.getCurrentVersion()));
                player.sendMessage(colorizer.colorize("&c[EnderChestLimiter] &fNewest version: " + updatesChecker.getLatestVersion()));
                player.sendMessage("");
            }
        }, 40L);
    }
}
