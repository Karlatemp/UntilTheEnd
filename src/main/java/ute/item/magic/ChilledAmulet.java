package ute.item.magic;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ute.Config;
import ute.UntilTheEnd;
import ute.api.PlayerApi;
import ute.cap.san.ChangeTasks;
import ute.api.event.cap.TemperatureChangeEvent;
import ute.internal.NPCChecker;
import ute.item.ItemManager;
import ute.item.clothes.ClothesContainer;

public class ChilledAmulet implements Listener {
    public static int sanityImprove = ItemManager.itemAttributes.getInt("ChilledAmulet.sanityImprove");

    public ChilledAmulet() {
        ChangeTasks.clothesChangeSanity.put(ItemManager.items.get("ChilledAmulet").displayName, sanityImprove);
        UntilTheEnd.getInstance().getServer().getPluginManager().registerEvents(this, UntilTheEnd.getInstance());
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Config.enableWorlds)
                    for (Player player : world.getPlayers()) {
                        if (NPCChecker.isNPC(player)) continue;
                        ItemStack[] clothes = ClothesContainer.getInventory(player).getStorageContents();
                        for (ItemStack cloth : clothes) {
                            if (ItemManager.isSimilar(cloth, ItemManager.items.get("ChilledAmulet").item)) {
                                if (cloth.getDurability() >= cloth.getType().getMaxDurability())
                                    cloth.setType(Material.AIR);
                                PlayerApi.TemperatureOperations.changeTemperature(player, TemperatureChangeEvent.ChangeCause.CLOTHES,-1);
                                if (Math.random() <= 0.3)
                                    cloth.setDurability((short) (cloth.getDurability() + 1));
                            }
                        }
                    }
            }

        }.runTaskTimer(UntilTheEnd.getInstance(), 0L, 200L);
    }
}
