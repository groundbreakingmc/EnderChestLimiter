package groundbreaking.enderchestlimiter.listeners;

import groundbreaking.enderchestlimiter.utils.MessageSender;
import groundbreaking.enderchestlimiter.utils.config.ConfigValues;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemLimiter implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (p.hasPermission("eclimiter.bypass.all")) return;

        Inventory clickedInventory = e.getClickedInventory();
        Inventory destInv = e.getView().getTopInventory();

        if (clickedInventory == null || destInv.getType() != InventoryType.ENDER_CHEST) return;

        ItemStack item;
        boolean isFromHotbar = InventoryAction.HOTBAR_SWAP.equals(e.getAction()) || InventoryAction.HOTBAR_MOVE_AND_READD.equals(e.getAction());
        if (isFromHotbar && e.getHotbarButton() >= 0 && e.getHotbarButton() <= 8) {
            item = p.getInventory().getItem(e.getHotbarButton());
        }
        else {
            if (clickedInventory.getType() != InventoryType.ENDER_CHEST) {
                return;
            }

            item = e.getCursor();
        }

        if (item == null) {
            return;
        }

        ConfigValues.getGroupsKeys().stream()
                .filter(group -> !p.hasPermission("enderchestlimiter.bypass." + group))
                .forEach(group -> {
                    Set<String> includedItems = ConfigValues.getGroupsLimitedItems().get(group);

                    p.sendMessage("Set: " + includedItems);
                    p.sendMessage("");
                    p.sendMessage("Contains: " + includedItems.contains(item.getType().name()));
                    if (includedItems.contains(item.getType().name())) {
                        p.sendMessage("1");

                        Map<Material, Integer> itemCountMap = new HashMap<>();

                        AtomicInteger amount = new AtomicInteger();
                        Arrays.stream(destInv.getStorageContents())
                                .filter(it -> includedItems.contains(it.getType().name()))
                                .forEach(it -> {
                                    itemCountMap.put(it.getType(), itemCountMap.getOrDefault(it.getType(), 0) + it.getAmount());
                                    amount.addAndGet(it.getAmount());
                                });
                        p.sendMessage("");
                        p.sendMessage("Amount: " + amount);
                        p.sendMessage("");

                        if (amount.get() + item.getAmount() > ConfigValues.getGroupsLimits().get(group)) {
                            e.setCancelled(true);
                            p.sendMessage("8");

                            MessageSender.send(p, getDenyMessage(ConfigValues.getGroupsLimits().get(group), group));

                            if (!ConfigValues.getGroupsSounds().get(group).equalsIgnoreCase("none")) {
                                playDenySound(p, ConfigValues.getGroupsSounds().get(group));
                            }
                        }
                    }
                });
    }

    private List<String> getDenyMessage(int limit, String group) {
        if (limit % 100 >= 11 && limit % 100 <= 19) {
            return ConfigValues.getTGroupsMessages().get(group);
        } else {
            switch (limit % 10) {
                case 1:
                    return ConfigValues.getFGroupsMessages().get(group);
                case 2:
                case 3:
                case 4:
                    return ConfigValues.getSGroupsMessages().get(group);
                default:
                    return ConfigValues.getTGroupsMessages().get(group);
            }
        }
    }

    private void playDenySound(Player player, String soundConfig) {
        String[] params = soundConfig.split(";", 3);

        Sound sound = Sound.ENTITY_SHULKER_HURT_CLOSED;
        float volume = 1, pitch = 1;

        switch (params.length) {
            case 3:
                pitch = Float.parseFloat(params[2]);
            case 2:
                volume = Float.parseFloat(params[1]);
            case 1:
                sound = Sound.valueOf(params[0].toUpperCase());
            default:
                player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}