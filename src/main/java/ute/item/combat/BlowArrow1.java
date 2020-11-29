package ute.item.combat;

import ute.Config;
import ute.internal.ArrowManager;
import ute.internal.DisableManager;
import ute.internal.EventHelper;
import ute.item.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author 南外丶仓鼠
 * @version V5.1.1
 */
public class BlowArrow1 implements Listener {
    public static double damage = ItemManager.itemAttributes.getDouble("BlowArrow1.damage");
    public static double range = ItemManager.itemAttributes.getDouble("BlowArrow1.range");
    public static int maxDist = ItemManager.itemAttributes.getInt("BlowArrow1.maxDist");

    public BlowArrow1() {
        ItemManager.plugin.getServer().getPluginManager().registerEvents(this, ItemManager.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRight(PlayerInteractEvent event) {
        if (event.isCancelled() && !DisableManager.bypass_right_action_cancelled) return;
        Player player = event.getPlayer();
        if (!Config.enableWorlds.contains(player.getWorld())) return;
        if (!event.hasItem()) return;
        if (EventHelper.isRight(event.getAction())) {
            ItemStack item = event.getItem();
            if (ItemManager.isSimilar(item, getClass())) {
                event.setCancelled(true);
                ArrowManager.startFire(e -> e.damage(damage), null, new ItemStack(Material.IRON_SWORD),
                        player.getLocation().add(0, 1, 0), maxDist, ItemManager.plugin.getConfig().getInt("item.blowarrow.autoclear"),
                        player, range);
            }
        }
    }
}
