package HamsterYDS.UntilTheEnd.item.survival;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import HamsterYDS.UntilTheEnd.item.ItemManager;
import HamsterYDS.UntilTheEnd.player.PlayerManager;

public class WaterBalloon implements Listener{
	public WaterBalloon() {		
		HashMap<ItemStack,Integer> materials=new HashMap<ItemStack,Integer>();
		materials.put(ItemManager.namesAndItems.get("§6蜘蛛腺体"),2);
		materials.put(ItemManager.namesAndItems.get("§6绳子"),1);
		materials.put(new ItemStack(Material.WATER_BUCKET),1);
		ItemManager.registerRecipe(materials,ItemManager.namesAndItems.get("§6水球"),"§6生存");
		ItemManager.plugin.getServer().getPluginManager().registerEvents(this,ItemManager.plugin);
	}
	@EventHandler public void onClick(PlayerInteractEvent event) {
		Player player=event.getPlayer();
		if(!player.isSneaking()) return;
		if(!(event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK)) return;
		ItemStack item=player.getItemInHand().clone();
		if(item==null) return;
		item.setAmount(1);
		if(item.equals(ItemManager.namesAndItems.get("§6水球"))) {
			event.setCancelled(true);
			ItemStack itemr=player.getItemInHand();
			itemr.setAmount(itemr.getAmount()-1);
			PlayerManager.change(player,"tem",-10);
			Location loc=player.getLocation();
			for(int x=-3;x<=3;x++)
				for(int y=-3;y<=3;y++)
					for(int z=-3;z<=3;z++) {
						Block block=new Location(loc.getWorld(),loc.getX()+x,loc.getY()+y,loc.getZ()+z).getBlock();
						if(block==null) continue;
						if(block.getType()==Material.FIRE) 
							block.setType(Material.AIR);
						if(block.isLiquid()) 
							block.setType(Material.AIR);
					}
		}
	}
}
