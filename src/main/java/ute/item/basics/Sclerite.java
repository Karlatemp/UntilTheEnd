package ute.item.basics;

import ute.Config;
import ute.internal.DisableManager;
import ute.internal.EventHelper;
import ute.internal.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ute.item.ItemManager;

/**
 * @author 南外丶仓鼠
 * @version V5.1.1
 */
public class Sclerite implements Listener {
    public static double damage = ItemManager.itemAttributes.getDouble("Sclerite.damage");
    public static double range = ItemManager.itemAttributes.getDouble("Sclerite.range");
    public static double maxDist = ItemManager.itemAttributes.getDouble("Sclerite.maxDist");

    public Sclerite() {
        ItemManager.plugin.getServer().getPluginManager().registerEvents(this, ItemManager.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRight(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Config.enableWorlds.contains(event.getPlayer().getWorld())) return;
        if (event.isCancelled() && !DisableManager.bypass_right_action_cancelled) return;
        if (!event.hasItem()) return;
        if (EventHelper.isRight(event.getAction())) {
            ItemStack item = event.getItem();
            if (ItemManager.isSimilar(item, getClass())) {
                event.setCancelled(true);
                Vector vec = player.getEyeLocation().getDirection().multiply(10);
                Entity entity = player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0),
                        EntityType.ARMOR_STAND);
                ArmorStand armor = (ArmorStand) entity;
                armor.setSmall(true);
                armor.setItemInHand(new ItemStack(Material.PRISMARINE_CRYSTALS));
                armor.setCollidable(false);
                armor.setVisible(false);
                armor.setAI(false);
                new BukkitRunnable() {
                    int dist = 0;

                    @Override
                    public void run() {
                        for (int i = 0; i <= 5; i++)
                            armor.setVelocity(vec);

                        for (Entity entity : armor.getNearbyEntities(range, range, range)) {
                            if (entity.getUniqueId() == player.getUniqueId())
                                continue;
                            if (!(entity instanceof LivingEntity))
                                continue;
                            ((LivingEntity) entity).damage(damage);
                            clear();
                        }

                        if (!ItemFactory.getType(armor.getLocation().add(0, 0.2, 0).getBlock()).isTransparent()) {
                            armor.setGravity(false);
                            armor.setVelocity(vec);
                            armor.getWorld().spawnParticle(Particle.CRIT, armor.getLocation().add(0, 0.2, 0), 1);
                            clear();
                        }
                        if (dist++ >= maxDist)
                            clear();
                    }

                    public void clear() {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                armor.remove();
                                cancel();
                            }
                        }.runTaskTimer(ItemManager.plugin,
                                ItemManager.plugin.getConfig().getInt("item.blowarrow.autoclear") * 20, 20L);
                        cancel();
                    }
                }.runTaskTimer(ItemManager.plugin, 0L, 1L);
            }
        }
    }
}
