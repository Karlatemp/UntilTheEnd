package HamsterYDS.UntilTheEnd.cap.hum;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import HamsterYDS.UntilTheEnd.manager.WetManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import HamsterYDS.UntilTheEnd.Config;
import HamsterYDS.UntilTheEnd.UntilTheEnd;

/**
 * @author 南外丶仓鼠
 * @version V5.1.1
 */
public class InfluenceEvents implements Listener {
    public static double wetFoodLevel = Humidity.yaml.getDouble("wetFoodLevel");

    public InfluenceEvents(UntilTheEnd plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private HashMap<UUID, Integer> wetFoodLevels = new HashMap<>();

    @EventHandler
    public void onUse(PlayerItemConsumeEvent event) {
        if (!Config.enableWorlds.contains(event.getPlayer().getWorld())) return;
        ItemStack item = event.getItem();
        if (!item.getType().isEdible()) return;
        if (WetManager.isWet(item))
            wetFoodLevels.put(event.getPlayer().getUniqueId(), event.getPlayer().getFoodLevel());
    }

    @EventHandler
    public void onEat(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (!Config.enableWorlds.contains(event.getEntity().getWorld())) return;
        if (wetFoodLevels.containsKey(entity.getUniqueId())) {
            int currentLevel = wetFoodLevels.get(entity.getUniqueId());
            event.setFoodLevel((int) (((double) (event.getFoodLevel() - currentLevel)) * wetFoodLevel) + currentLevel);
            entity.sendMessage("§6[§cUntilTheEnd§6]§r 潮湿的食物真难吃~");  //Language-TODO
            wetFoodLevels.remove(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!Config.enableWorlds.contains(event.getWhoClicked().getWorld())) return;
        ItemStack item = event.getCursor();
        if (item == null) return;
        if (WetManager.isWet(item)) {
            event.getWhoClicked().sendMessage("§6[§cUntilTheEnd§6]§r 潮湿的物品貌似不能拖动，它们太笨重了！"); //Language-TODO
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!Config.enableWorlds.contains(event.getWhoClicked().getWorld())) return;
        Inventory inv = event.getClickedInventory();
        if (inv == null) return;
        if (!(inv.getType() == InventoryType.WORKBENCH || inv.getType() == InventoryType.CRAFTING)) return;
        ItemStack item = event.getCursor();
        if (item == null) return;
        if (WetManager.isWet(item)) {
            event.getWhoClicked().sendMessage("§6[§cUntilTheEnd§6]§r 潮湿的物品貌似不能用于合成，它们太笨重了！"); //Language-TODO
            event.setCancelled(true);
        }
    }

}
