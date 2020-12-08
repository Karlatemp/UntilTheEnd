package ute.item.magic;

import ute.Config;
import ute.UntilTheEnd;
import ute.cap.san.ChangeTasks;
import ute.internal.ItemFactory;
import ute.internal.NPCChecker;
import ute.item.ItemManager;
import ute.item.clothes.ClothesContainer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LifeGivingAmulet implements Listener {
    public static int sanityImprove = ItemManager.itemAttributes.getInt("LifeGivingAmulet.sanityImprove");

    public LifeGivingAmulet() {
        ChangeTasks.clothesChangeSanity.put(ItemManager.items.get("LifeGivingAmulet").displayName, sanityImprove);
        UntilTheEnd.getInstance().getServer().getPluginManager().registerEvents(this, UntilTheEnd.getInstance());
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Config.enableWorlds)
                    for (Player player : world.getPlayers()) {
                        if (NPCChecker.isNPC(player)) continue;
                        ItemStack[] clothes = ClothesContainer.getInventory(player).getStorageContents();
                        for (ItemStack cloth : clothes) {
                            if (ItemManager.isSimilar(cloth, ItemManager.items.get("LifeGivingAmulet").item)) {
                                if (cloth.getDurability() >= cloth.getType().getMaxDurability())
                                    cloth.setType(Material.AIR);
                                if (player.getHealth() + 1 > player.getMaxHealth()) continue;
                                player.setFoodLevel(player.getFoodLevel() - 1);
                                player.setHealth(player.getHealth() + 1);
                                cloth.setDurability((short) (cloth.getDurability() + 1));
                            }
                        }
                    }
            }

        }.runTaskTimer(UntilTheEnd.getInstance(), 0L, 200L);
    }

    @EventHandler
    public void onDeath(EntityDamageEvent event) {
        if (!Config.enableWorlds.contains(event.getEntity().getWorld())) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getHealth() <= event.getDamage()) {
                ItemStack[] clothes = ClothesContainer.getInventory(player).getStorageContents();
                for (ItemStack cloth : clothes) {
                    if (cloth == null) continue;
                    if (cloth.getDurability() >= ItemFactory.getType(cloth).getMaxDurability())
                        cloth.setType(Material.AIR);
                    if (ItemManager.isSimilar(cloth, getClass())) {
                        event.setCancelled(true);
                        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 10);
                        player.setHealth(player.getMaxHealth() / 2.0);
                        cloth.setDurability((short) (cloth.getDurability() + 30));
                    }
                }
            }
        }
    }
}
