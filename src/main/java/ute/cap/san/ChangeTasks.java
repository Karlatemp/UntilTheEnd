package ute.cap.san;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import ute.Config;
import ute.UntilTheEnd;
import ute.event.hud.SanityChangeEvent;
import ute.internal.NPCChecker;
import ute.internal.ResidenceChecker;
import ute.item.clothes.ClothesContainer;
import ute.player.PlayerManager;

import java.util.HashMap;

public class ChangeTasks {
    public static UntilTheEnd plugin = UntilTheEnd.getInstance();
    public static double auraRangeX = Sanity.yaml.getDouble("auraRangeX");
    public static double auraRangeY = Sanity.yaml.getDouble("auraRangeY");
    public static double auraRangeZ = Sanity.yaml.getDouble("auraRangeZ");
    public static int monsterChangeSanity = Sanity.yaml.getInt("monsterChangeSanity");
    public static int playerChangeSanity = Sanity.yaml.getInt("playerChangeSanity");
    public static int auraPeriod = Sanity.yaml.getInt("auraPeriod");
    public static int humidityPeriod = Sanity.yaml.getInt("humidityPeriod");
    public static int timePeriod = Sanity.yaml.getInt("timePeriod");
    public static int evening = Sanity.yaml.getInt("evening");
    public static int night = Sanity.yaml.getInt("night");
    public static int day = Sanity.yaml.getInt("day");
    public static HashMap<String, Integer> clothesChangeSanity = new HashMap<String, Integer>();
    public static HashMap<String, Integer> itemsChangeSanity = new HashMap<String, Integer>();

    public static void initialize() {
        new ClothesTasks().runTaskTimer(plugin, 0L, 200L);
        new ItemsTasks().runTaskTimer(plugin, 0L, 200L);
        new SanityAura().runTaskTimer(plugin, 0L, auraPeriod);
        new HumidityTask().runTaskTimer(plugin, 0L, humidityPeriod);
        new TimeTask().runTaskTimer(plugin, 0L, timePeriod);
    }

    public static class ClothesTasks extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Config.enableWorlds) {
                for (Player player : world.getPlayers()) {
                    if (NPCChecker.isNPC(player)|| ResidenceChecker.isProtected(player.getLocation())) continue;
                    PlayerInventory inv = player.getInventory();
                    String helmet = getName(inv.getHelmet());
                    String chestplate = getName(inv.getChestplate());
                    String leggings = getName(inv.getLeggings());
                    String boots = getName(inv.getBoots());
                    double change = 0.0;
                    if (clothesChangeSanity.containsKey(helmet)) {
                        change += clothesChangeSanity.get(helmet);
                    }
                    if (clothesChangeSanity.containsKey(chestplate)) {
                        change += clothesChangeSanity.get(chestplate);
                    }
                    if (clothesChangeSanity.containsKey(leggings)) {
                        change += clothesChangeSanity.get(leggings);
                    }
                    if (clothesChangeSanity.containsKey(boots)) {
                        change += clothesChangeSanity.get(boots);
                    }
                    ItemStack[] clothes = ClothesContainer.getInventory(player).getStorageContents();
                    for (ItemStack cloth : clothes) {
                        if (clothesChangeSanity.containsKey(getName(cloth))) {
                            change += clothesChangeSanity.get(getName(cloth));
                        }
                    }
                    SanityChangeEvent event = new SanityChangeEvent(player, SanityChangeEvent.ChangeCause.INVENTORYCLOTHES, change);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled())
                        PlayerManager.change(player, PlayerManager.CheckType.SANITY, change);
                }
            }
        }

        public String getName(ItemStack item) {
            if (item != null)
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName())
                        return item.getItemMeta().getDisplayName();
            return "";
        }
    }

    public static class ItemsTasks extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Config.enableWorlds) {
                for (Player player : world.getPlayers()) {
                    if (NPCChecker.isNPC(player)||ResidenceChecker.isProtected(player.getLocation())) continue;
                    PlayerInventory inv = player.getInventory();
                    for (int slot = 0; slot < inv.getSize(); slot++) {
                        ItemStack item = inv.getItem(slot);
                        String itemName = getName(item);
                        if (itemsChangeSanity.containsKey(itemName)) {
                            SanityChangeEvent event = new SanityChangeEvent(player, SanityChangeEvent.ChangeCause.INVENTORYITEM, item.getAmount() * itemsChangeSanity.get(itemName));
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                PlayerManager.change(player, PlayerManager.CheckType.SANITY, item.getAmount() * itemsChangeSanity.get(itemName));
                        }
                    }
                }
            }
        }

        public String getName(ItemStack item) {
            if (item != null)
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName())
                        return item.getItemMeta().getDisplayName();
            return "";
        }
    }

    public static class SanityAura extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Config.enableWorlds)
                for (Player player : world.getPlayers())
                    if (!NPCChecker.isNPC(player))
                        for (Entity entity : player.getNearbyEntities(auraRangeX, auraRangeY, auraRangeZ)) {
                            EntityType type = entity.getType();
                            if (SanityProvider.creatureAura.containsKey(type)) {
                                SanityChangeEvent event = new SanityChangeEvent(player, SanityChangeEvent.ChangeCause.CREATUREAURA, SanityProvider.creatureAura.get(type));
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled())
                                    PlayerManager.change(player, PlayerManager.CheckType.SANITY, SanityProvider.creatureAura.get(type));
                            }
                            if (entity instanceof Player) {
                                SanityChangeEvent event = new SanityChangeEvent(player, SanityChangeEvent.ChangeCause.PLAYER, playerChangeSanity);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled())
                                    PlayerManager.change(player, PlayerManager.CheckType.SANITY, playerChangeSanity);
                            }
                            if (entity instanceof Monster) {
                                SanityChangeEvent event = new SanityChangeEvent(player, SanityChangeEvent.ChangeCause.MONSTER, monsterChangeSanity);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled())
                                    PlayerManager.change(player, PlayerManager.CheckType.SANITY, monsterChangeSanity);
                            }
                        }
        }
    }

    public static class HumidityTask extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Config.enableWorlds) {
                for (Player player : world.getPlayers()) {
                    if (NPCChecker.isNPC(player)||ResidenceChecker.isProtected(player.getLocation())) continue;
                    double hum = PlayerManager.check(player, PlayerManager.CheckType.HUMIDITY);
                    PlayerManager.change(player, PlayerManager.CheckType.SANITY, -hum / 10);
                }
            }
        }
    }

    public static class TimeTask extends BukkitRunnable {
        public long counter = 0;

        @Override
        public void run() {
            counter++;
            for (World world : Config.enableWorlds) {
                long time = world.getTime();
                if (counter % 2 == 0)
                    if (time >= evening && time <= night)
                        for (Player player : world.getPlayers())
                            if (!NPCChecker.isNPC(player))
                                PlayerManager.change(player, PlayerManager.CheckType.SANITY, -1);
                if (time >= night && time <= day)
                    for (Player player : world.getPlayers())
                        if (!NPCChecker.isNPC(player))
                            PlayerManager.change(player, PlayerManager.CheckType.SANITY, -1);
            }
        }
    }
}
