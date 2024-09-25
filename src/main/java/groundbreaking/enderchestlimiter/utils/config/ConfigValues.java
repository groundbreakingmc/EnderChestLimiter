package groundbreaking.enderchestlimiter.utils.config;

import groundbreaking.enderchestlimiter.EnderChestLimiter;
import groundbreaking.enderchestlimiter.utils.colorizer.IColorizer;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class ConfigValues {

    @Getter
    private static boolean
            useMinimessage;

    @Getter
    private static final List<String>
            groupsKeys = new ArrayList<>();

    @Getter
    private static HashMap<String, Integer>
            groupsLimits = new HashMap<>();

    @Getter
    private static HashMap<String, Set<String>>
            groupsLimitedItems = new HashMap<>();

    @Getter
    private static HashMap<String, List<String>>
            groupsLimitReachedMessages = new HashMap<>();

    @Getter
    private static HashMap<String, String>
            groupsSounds = new HashMap<>();

    @Getter
    private static final List<String>
            noPermMessages = new ArrayList<>(),
            reloadMessages = new ArrayList<>();

    private final EnderChestLimiter plugin;

    public ConfigValues(EnderChestLimiter plugin) {
        this.plugin = plugin;
    }

    public void setValues() {
        final ConfigurationSection settings = plugin.getConfig().getConfigurationSection("settings");
        final ConfigurationSection groups = plugin.getConfig().getConfigurationSection("groups");
        final ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");

        final IColorizer colorizer = plugin.getColorizer();

        if (settings != null) {
            useMinimessage = settings.getBoolean("use-minimessage");
        } else {
            plugin.getLogger().warning("\u001b[91mFailed to load values from \"settings\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }

        if (groups != null) {

            groupsKeys.clear();
            groupsLimits.clear();
            groupsLimitedItems.clear();
            groupsLimitReachedMessages.clear();
            groupsSounds.clear();

            groupsKeys.addAll(groups.getKeys(false));
            for (String key : groupsKeys) {
                Set<String> itemsList = new HashSet<>();
                groups.getStringList(key + ".included-items").forEach(item ->
                        itemsList.add(item.toUpperCase())
                );
                groupsLimitedItems.put(key, itemsList);

                int limit = groups.getInt(key + ".limit");
                if (limit < 1) {
                    plugin.getLogger().warning("Limit for group \"" + key + "\" is less than 1! Limit will change to 1.");
                    plugin.getConfig().set("groups." + key + ".limit", 1);
                    plugin.saveConfig();
                    limit = 1;
                }

                groupsLimits.put(key, limit);
                groupsLimitReachedMessages.put(key, getMessages(groups, key + ".deny-message", colorizer));
                groupsSounds.put(key, groups.getString(key + ".deny-sound", ""));
            }
        } else {
            plugin.getLogger().warning("\u001b[91mFailed to load values from \"groups\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }

        if (messages != null) {
            noPermMessages.clear();
            noPermMessages.addAll(getMessages(messages, "no-perm", colorizer));
            reloadMessages.clear();
            reloadMessages.addAll(getMessages(messages, "reload", colorizer));
        } else {
            plugin.getLogger().warning("\u001b[91mFailed to load messages from \"messages\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }
    }

    public List<String> getMessages(ConfigurationSection section, String path, IColorizer colorizer) {
        final Object obj = section.get(path);

        if (obj instanceof String) {
            return List.of(colorizer.colorize(section.getString(path, ("&4(!) &cFailed to get message on path: " + path))));
        }

        if (obj instanceof List) {
            final List<String> stringList = section.getStringList(path);
            final List<String> coloredList = new ArrayList<>(stringList.size());
            for (int i = 0; i < stringList.size() - 1; i++) {
                coloredList.add(colorizer.colorize(stringList.get(i)));
            }
            return coloredList;
        }

        return List.of(colorizer.colorize("&4(!) &cFailed to get message on path: " + path));
    }
}