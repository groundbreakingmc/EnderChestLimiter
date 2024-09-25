package groundbreaking.enderchestlimiter.utils;

import groundbreaking.enderchestlimiter.EnderChestLimiter;
import lombok.Getter;
import org.bukkit.Bukkit;

public final class ServerInfos {

    @Getter
    private final boolean
            isPapiExist = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null,
            isAbove16,
            isPaperOrFork = checkIsPaperOrFork();

    @Getter
    private final int subVersion;

    private final EnderChestLimiter plugin;

    public ServerInfos(EnderChestLimiter plugin) {
        this.plugin = plugin;
        this.subVersion = extractMainVersion();
        this.isAbove16 = subVersion >= 16;
    }

    public int extractMainVersion() {
        try {
            return Integer.parseInt(plugin.getServer().getMinecraftVersion().split("\\.", 3)[1]);
        } catch (NumberFormatException ex) {
            plugin.getLogger().warning("\u001b[32mFailed to extract server version. Plugin may not work correctly!");
            return 0;
        }
    }

    public boolean checkIsPaperOrFork() {
        try {
            Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger");
            return true;
        } catch (ClassNotFoundException ignore) {
            return false;
        }
    }
}
