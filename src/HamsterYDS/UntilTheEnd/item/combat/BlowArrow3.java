package HamsterYDS.UntilTheEnd.item.combat;

import java.util.HashMap;

import HamsterYDS.UntilTheEnd.internal.ArrowManager;
import HamsterYDS.UntilTheEnd.internal.EventHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import HamsterYDS.UntilTheEnd.item.ItemManager;

/**
 * @author 南外丶仓鼠
 * @version V5.1.1
 */
public class BlowArrow3 implements Listener {
    public static double damage = ItemManager.itemAttributes.getDouble("麻醉吹箭.damage");
    public static double range = ItemManager.itemAttributes.getDouble("麻醉吹箭.range");
    public static int maxDist = ItemManager.itemAttributes.getInt("麻醉吹箭.maxDist");
    public static int blindPeriod = ItemManager.itemAttributes.getInt("麻醉吹箭.blindPeriod");

    public BlowArrow3() {
        HashMap<ItemStack, Integer> materials = new HashMap<ItemStack, Integer>();
        materials.put(ItemManager.items.get("Reed"), 3);
        materials.put(ItemManager.items.get("DogTooth"), 2);
        materials.put(ItemManager.items.get("Sclerite"), 2);
        materials.put(ItemManager.items.get("CatTail"), 1);
        ItemManager.items.get("").registerRecipe(materials, ItemManager.items.get("麻醉吹箭"), "战斗");
        ItemManager.plugin.getServer().getPluginManager().registerEvents(this, ItemManager.plugin);
    }

    @EventHandler
    public void onRight(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking())
            return;
        if (!event.hasItem()) return;
        if (EventHelper.isRight(event.getAction())) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (ItemManager.isSimilar(item, ItemManager.items.get("麻醉吹箭"))) {
                event.setCancelled(true);
                ArrowManager.startFire(le -> {
                            le.damage(damage);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindPeriod * 20, 0));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, blindPeriod * 20, 0));
                        }, null, new ItemStack(Material.STONE_SWORD),
                        player.getLocation().add(0, 1, 0),
                        maxDist,
                        ItemManager.plugin.getConfig().getInt("item.blowarrow.autoclear"),
                        player, range);
            }
        }
    }
}
