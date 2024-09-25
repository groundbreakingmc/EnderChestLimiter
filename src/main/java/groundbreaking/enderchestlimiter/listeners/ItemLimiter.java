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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemLimiter implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        if (p.hasPermission("eclimiter.bypass.all")) {
            return;
        }

        final Inventory clickedInventory = e.getClickedInventory();
        final Inventory destInv = e.getView().getTopInventory();

        if (clickedInventory == null || destInv.getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        ItemStack item;
        final boolean isFromHotbar = InventoryAction.HOTBAR_SWAP.equals(e.getAction()) || InventoryAction.HOTBAR_MOVE_AND_READD.equals(e.getAction());
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

        final List<String> groupsKeys = ConfigValues.getGroupsKeys();
        for (int i = 0; i < groupsKeys.size(); i++) {
            String key = groupsKeys.get(i);
            if (p.hasPermission("enderchestlimiter.bypass." + key)) {
                continue;
            }

            final Set<String> includedItems = ConfigValues.getGroupsLimitedItems().get(key);

            p.sendMessage("Set: " + includedItems);
            p.sendMessage("");
            p.sendMessage("Contains: " + includedItems.contains(item.getType().name()));
            if (includedItems.contains(item.getType().name())) {
                p.sendMessage("1");

                final Map<Material, Integer> itemCountMap = new HashMap<>();

                final AtomicInteger amount = new AtomicInteger();
                ItemStack[] storage = destInv.getStorageContents();
                for (int r = 0; r < storage.length; r++) {
                    ItemStack itemStack = storage[r];
                    if (includedItems.contains(itemStack.getType().name())) {
                        itemCountMap.put(itemStack.getType(), itemCountMap.getOrDefault(itemStack.getType(), 0) + itemStack.getAmount());
                        amount.addAndGet(itemStack.getAmount());
                    }
                }
                p.sendMessage("");
                p.sendMessage("Amount: " + amount);
                p.sendMessage("");

                if (amount.get() + item.getAmount() > ConfigValues.getGroupsLimits().get(key)) {
                    e.setCancelled(true);
                    p.sendMessage("8");

                    MessageSender.send(p, ConfigValues.getGroupsLimitReachedMessages().get(key));

                    if (!ConfigValues.getGroupsSounds().get(key).equalsIgnoreCase("none")) {
                        playDenySound(p, ConfigValues.getGroupsSounds().get(key));
                    }
                }
            }
        }
    }

    private void playDenySound(Player player, String soundConfig) {
        final String[] params = soundConfig.split(";", 3);

        final Sound sound = params.length == 1 && params[0] != null ? Sound.valueOf(params[0].toUpperCase()) : Sound.ENTITY_SHULKER_HURT_CLOSED;
        final float volume = params.length == 2 && params[1] != null ? Float.parseFloat(params[1]) : 1.0f;
        final float pitch = params.length == 3 && params[2] != null ? Float.parseFloat(params[2]) : 1.0f;

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}