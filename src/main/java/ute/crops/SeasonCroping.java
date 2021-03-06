package ute.crops;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import ute.Config;
import ute.UntilTheEnd;
import ute.api.WorldApi;
import ute.world.WorldProvider;

import java.util.HashMap;

public class SeasonCroping implements Listener {
    public SeasonCroping() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UntilTheEnd.getInstance());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGrow(BlockGrowEvent event) {
        if (event.getBlock() == null) return;
        Block block = event.getBlock();
        World world = block.getWorld();
        if (!Config.enableWorlds.contains(world)) return;
        WorldProvider.Season season = WorldApi.getSeason(world);
        Material material = block.getState().getData().getItemType();
        String name = material.toString();
        if (CropProvider.seasonCrops.containsKey(name)) {
            HashMap<String, Double> crop = CropProvider.seasonCrops.get(name);
            if (crop.containsKey(season.toString())) {
                double percent = crop.get(season.toString());
                if (Math.random() > percent)
                    event.setCancelled(true);
            } else event.setCancelled(true);
        }
    }
}
