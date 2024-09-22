package groundbreaking.enderchestlimiter.utils.config;

import groundbreaking.enderchestlimiter.utils.colorizer.IColorizer;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

import static groundbreaking.enderchestlimiter.EnderChestLimiter.getInstance;

public final class ConfigValues {

    @Getter
    private static boolean
            useMinimessage;

    @Getter
    private static final Set<String>
            groupsKeys = new HashSet<>();

    @Getter
    private static HashMap<String, Integer>
            groupsLimits = new HashMap<>();

    @Getter
    private static HashMap<String, Set<String>>
            groupsLimitedItems = new HashMap<>();

    @Getter
    private static HashMap<String, List<String>>
            fGroupsMessages = new HashMap<>(),
            sGroupsMessages = new HashMap<>(),
            tGroupsMessages = new HashMap<>();

    @Getter
    private static HashMap<String, String>
            groupsSounds = new HashMap<>();

    @Getter
    private static final List<String>
            noPermMessages = new ArrayList<>(),
            reloadMessages = new ArrayList<>();

    private final Logger logger;
    private final FileConfiguration config;
    private final IColorizer colorizer;

    public ConfigValues(Logger logger, FileConfiguration config, IColorizer colorizer) {
        this.logger = logger;
        this.config = config;
        this.colorizer = colorizer;
    }

    public void setValues() {
        final ConfigurationSection settings = config.getConfigurationSection("settings");
        final ConfigurationSection groups = config.getConfigurationSection("groups");
        final ConfigurationSection messages = config.getConfigurationSection("messages");

        if (settings != null) {
            useMinimessage = settings.getBoolean("use-minimessage");
        } else {
            logger.warning("\u001b[91mFailed to load values from \"settings\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }

        if (groups != null) {

            groupsKeys.clear();
            groupsLimits.clear();
            groupsLimitedItems.clear();
            fGroupsMessages.clear(); sGroupsMessages.clear(); tGroupsMessages.clear();
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
                    logger.warning("Limit for group \"" + key + "\" is less than 1! Limit will change to 1.");
                    config.set("groups." + key + ".limit", 1);
                    getInstance().saveConfig();
                    limit = 1;
                }

                groupsLimits.put(key, limit);
                fGroupsMessages.put(key, getMessages(groups, key + ".deny-message-1", limit));
                sGroupsMessages.put(key, getMessages(groups, key + ".deny-message-2", limit));
                tGroupsMessages.put(key, getMessages(groups, key + ".deny-message-3", limit));
                groupsSounds.put(key, groups.getString(key + ".deny-sound", ""));
            }
        } else {
            logger.warning("\u001b[91mFailed to load values from \"groups\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }

        if (messages != null) {
            noPermMessages.clear();
            noPermMessages.addAll(getMessages(messages, "no-perm"));
            reloadMessages.clear();
            reloadMessages.addAll(getMessages(messages, "reload"));
        } else {
            logger.warning("\u001b[91mFailed to load messages from \"messages\" section. Please check your configuration file, or delete it and restart your server!\u001b[0m");
        }
    }

    public List<String> getMessages(ConfigurationSection section, String path, int limit) {
        List<String> list = getMessages(section, path);

        for (int i = 0; i < list.size() - 1; i++) {
            list.set(i, list.get(i).replace("%max%", String.valueOf(limit)));
        }

        return list;
    }

    public List<String> getMessages(ConfigurationSection section, String path) {
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