package groundbreaking.enderchestlimiter.utils;

import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public final class ServerInfos {

    @Getter
    private final int subVersion = extractMainVersion();
    @Getter
    private final boolean isPapiExist = checkPapi();
    @Getter
    private final boolean isPaperOrFork = checkIsPaperOrFork();
    @Getter
    private final boolean isAbove16 = subVersion >= 16;

    private final Server server;
    private final Logger logger;
    private final PluginManager pluginManager;

    public ServerInfos(Server server, Logger logger, PluginManager pluginManager) {
        this.server = server;
        this.logger = logger;
        this.pluginManager = pluginManager;
    }

    public int extractMainVersion() {
        try {
            return Integer.parseInt(server.getMinecraftVersion().split("\\.", 3)[1]);
        } catch (NumberFormatException ex) {
            logger.warning("\u001b[32mFailed to extract server version. Plugin may not work correctly!");
            return 0;
        }
    }

    public boolean checkPapi() {
        return pluginManager.getPlugin("PlaceholderAPI") != null;
    }

    public boolean checkIsPaperOrFork() {
        try {
            Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
