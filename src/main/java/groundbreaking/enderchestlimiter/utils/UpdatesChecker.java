package groundbreaking.enderchestlimiter.utils;

import groundbreaking.enderchestlimiter.EnderChestLimiter;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class UpdatesChecker {

    @Getter
    private Boolean new_version = false;
    @Getter
    private String currentVersion, latestVersion, downloadLink;
    
    private final EnderChestLimiter plugin;
    private final FileConfiguration config;
    private final Logger logger;

    public UpdatesChecker(EnderChestLimiter plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();
    }

    public void startCheck() {
        if (!config.getBoolean("check-updates", true)) {
            logger.info("\u001b[33mUpdates checker were disabled, but it's not recomend by author to do it!\u001b[0m");
            return;
        }

        try {
            logger.info("\u001b[33mChecking for updates...\u001b[0m");
            HttpClient httpClient = HttpClient.newHttpClient();
            String VERSION_URL = "https://raw.githubusercontent.com/groundbreakingmc/EnderChestLimiter/master/version";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(VERSION_URL))
                    .build();

            CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

            responseFuture.thenAccept(response -> {
                if (response.statusCode() == 200) {
                    String[] versionIfo = response.body().split("->", 2);
                    currentVersion = plugin.getDescription().getVersion();
                    String[] currentVersionParams = currentVersion.split("\\.");
                    String[] newVersion = versionIfo[0].split("\\.");

                    for (int i = 0; i < newVersion.length; i++) {
                        final int currVer = Integer.parseInt(currentVersionParams[i]);
                        final int newVer = Integer.parseInt(newVersion[i]);

                        if (currVer < newVer) {
                            new_version = true;
                            latestVersion = versionIfo[0];
                            downloadLink = versionIfo[1];
                        }
                    }

                    if (new_version) {
                        logger.info("\u001b[92mNo updates were found!\u001b[0m");
                        logger.info("\u001b[33m=========== EnderChestLimiter ===========\u001b[0m");
                        logger.info("\u001b[33mCurrent version: \u001b[93m" + currentVersion + "\u001b[0m");
                        logger.info("\u001b[33mNewest version: \u001b[92m" + latestVersion + "\u001b[0m");
                        logger.info("\u001b[33mDownload link: \u001b[94m" + downloadLink + "\u001b[0m");
                        logger.info("\u001b[33m=========================================\u001b[0m");
                    } else {
                        logger.info("\u001b[92mNo updates were found!\u001b[0m");
                    }
                } else {
                    logger.warning("\u001b[31mCheck was canceled with response code: \u001b[91m" + response.statusCode() + "\u001b[31m.\u001b[0m");
                    logger.warning("\u001b[31mPlease create an issue \u001b[94https://github.com/groundbreakingmc/EnderChestLimiter/issues \u001b[31mand report this error.\u001b[0m");
                }
            }).join();
        }
        catch (Exception ex) {
            logger.warning("\u001b[31mCheck was canceled with response code: \u001b[91m" + ex.getMessage() + "\u001b[31m.\u001b[0m");
            logger.warning("\u001b[31mPlease create an issue here: \u001b[94mhttps://github.com/groundbreakingmc/EnderChestLimiter/issues\u001b[31m, and report this error.\u001b[0m");
        }
    }
}
