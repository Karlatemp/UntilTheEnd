package ute.item.combat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ute.Config;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import ute.api.BlockApi;
import ute.item.BlockManager;
import ute.item.ItemManager;
import ute.player.death.DeathCause;
import ute.player.death.DeathMessage;

public class ToothTrap implements Listener {
    public static double damage = ItemManager.itemAttributes.getDouble("ToothTrap.damage");
    public static double brokenPercent = ItemManager.itemAttributes.getDouble("ToothTrap.brokenPercent");
    public static ArrayList<String> touched = new ArrayList<String>();

    public ToothTrap() {
        loadBlocks();
        ItemManager.plugin.getServer().getPluginManager().registerEvents(this, ItemManager.plugin);
    }

    @EventHandler
    public void onMove(EntityInteractEvent event) {
        if (event.isCancelled()) return;
        Entity entity = event.getEntity();
        if (!Config.enableWorlds.contains(entity.getWorld())) return;
        if (!(entity instanceof LivingEntity)) return;
        if (entity instanceof Player) return;
        Location loc = event.getBlock().getLocation();
        if (BlockApi.getSpecialBlocks("ToothTrap").contains(BlockApi.locToStr(loc))) {
            if (touched.contains(BlockApi.locToStr(loc))) return;
            if (Math.random() <= brokenPercent) {
                loc.getBlock().breakNaturally(new ItemStack(Material.AIR));
                BlockManager.blocks.remove(BlockApi.locToStr(loc));
                BlockManager.removeBlockData("ToothTrap", BlockApi.locToStr(loc));
            } else {
                touched.add(BlockApi.locToStr(loc));
                loc.getBlock().setType(Material.CARPET);
            }
            entity.getWorld().spawnParticle(Particle.CRIT, loc.add(0.5, 0.5, 0.5), 5);
            ((LivingEntity) entity).damage(damage);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        Location loc = event.getFrom();
        if (BlockApi.getSpecialBlocks("ToothTrap").contains(BlockApi.locToStr(loc))) {
            if (touched.contains(BlockApi.locToStr(loc))) return;
            if (Math.random() <= brokenPercent) {
                loc.getBlock().breakNaturally(new ItemStack(Material.AIR));
                BlockManager.blocks.remove(BlockApi.locToStr(loc));
                BlockManager.removeBlockData("ToothTrap", BlockApi.locToStr(loc));
            } else {
                touched.add(BlockApi.locToStr(loc));
                loc.getBlock().setType(Material.CARPET);
            }
            player.getWorld().spawnParticle(Particle.CRIT, loc.add(0.5, 0.5, 0.5), 5);
            if (player.getHealth() <= damage) DeathMessage.causes.put(player.getName(), DeathCause.TOOTHTRAP);
            player.damage(damage);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        Location loc = block.getLocation();
        if (BlockApi.getSpecialBlocks("ToothTrap").contains(BlockApi.locToStr(loc))) {
            if (!touched.contains(BlockApi.locToStr(loc))) return;
            touched.remove(BlockApi.locToStr(loc));
            loc.getBlock().setType(Material.IRON_PLATE);
            player.sendMessage("[§cUntilTheEnd]§r 陷阱重置成功");
        }
    }

    public static void saveBlocks() {
        File file = new File(ItemManager.plugin.getDataFolder() + "/data", "traps.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String loc : touched)
            yaml.set(loc, "");
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadBlocks() {
        File file = new File(ItemManager.plugin.getDataFolder() + "/data", "traps.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        touched.addAll(yaml.getKeys(false));
    }
}
