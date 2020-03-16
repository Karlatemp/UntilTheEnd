package HamsterYDS.UntilTheEnd.item;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import HamsterYDS.UntilTheEnd.internal.ItemFactory;
import HamsterYDS.UntilTheEnd.item.clothes.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import HamsterYDS.UntilTheEnd.Config;
import HamsterYDS.UntilTheEnd.UntilTheEnd;
import HamsterYDS.UntilTheEnd.item.basics.Ashes;
import HamsterYDS.UntilTheEnd.item.basics.Sclerite;
import HamsterYDS.UntilTheEnd.item.basics.SpiderGland;
import HamsterYDS.UntilTheEnd.item.combat.BeeMine;
import HamsterYDS.UntilTheEnd.item.combat.BlowArrow1;
import HamsterYDS.UntilTheEnd.item.combat.BlowArrow2;
import HamsterYDS.UntilTheEnd.item.combat.BlowArrow3;
import HamsterYDS.UntilTheEnd.item.combat.ToothTrap;
import HamsterYDS.UntilTheEnd.item.combat.WeatherPain;
import HamsterYDS.UntilTheEnd.item.magic.FireWand;
import HamsterYDS.UntilTheEnd.item.materials.Brick;
import HamsterYDS.UntilTheEnd.item.materials.Coin;
import HamsterYDS.UntilTheEnd.item.materials.Fern;
import HamsterYDS.UntilTheEnd.item.materials.Hail;
import HamsterYDS.UntilTheEnd.item.materials.NightMare;
import HamsterYDS.UntilTheEnd.item.materials.Plank;
import HamsterYDS.UntilTheEnd.item.materials.PurpleGum;
import HamsterYDS.UntilTheEnd.item.materials.Reed;
import HamsterYDS.UntilTheEnd.item.materials.Rope;
import HamsterYDS.UntilTheEnd.item.science.Detector;
import HamsterYDS.UntilTheEnd.item.science.Element;
import HamsterYDS.UntilTheEnd.item.science.Hygrometer;
import HamsterYDS.UntilTheEnd.item.science.IceFlingomatic;
import HamsterYDS.UntilTheEnd.item.science.LightningArrester;
import HamsterYDS.UntilTheEnd.item.science.Refridgerator;
import HamsterYDS.UntilTheEnd.item.science.ScienceMachine;
import HamsterYDS.UntilTheEnd.item.science.Thermometer;
import HamsterYDS.UntilTheEnd.item.survival.ACDDrug;
import HamsterYDS.UntilTheEnd.item.survival.FlowerUmbrella;
import HamsterYDS.UntilTheEnd.item.survival.FurRoll;
import HamsterYDS.UntilTheEnd.item.survival.HealingSalve;
import HamsterYDS.UntilTheEnd.item.survival.HoneyPoultice;
import HamsterYDS.UntilTheEnd.item.survival.LuxuryFan;
import HamsterYDS.UntilTheEnd.item.survival.MovablePack;
import HamsterYDS.UntilTheEnd.item.survival.NormalPack;
import HamsterYDS.UntilTheEnd.item.survival.PigPack;
import HamsterYDS.UntilTheEnd.item.survival.Reviver;
import HamsterYDS.UntilTheEnd.item.survival.SiestaLeanto;
import HamsterYDS.UntilTheEnd.item.survival.StrawRoll;
import HamsterYDS.UntilTheEnd.item.survival.Umbrella;
import HamsterYDS.UntilTheEnd.item.survival.WarmStone;
import HamsterYDS.UntilTheEnd.item.survival.WaterBalloon;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author 南外丶仓鼠
 * @version V5.1.1
 */
public class ItemManager {
    public static UntilTheEnd plugin = UntilTheEnd.getInstance();
    public static HashMap<ItemStack, String> ids = new HashMap<ItemStack, String>();
    public static HashMap<String, UTEItemStack> items = new HashMap<String, UTEItemStack>();
    public static HashMap<Integer, String> machines = new HashMap<Integer, String>();
    public static YamlConfiguration itemSets=Config.autoUpdateConfigs("itemsets.yml");;
    public static YamlConfiguration itemAttributes=Config.autoUpdateConfigs("items.yml");
    public ItemManager(UntilTheEnd plugin) {
        for (String path : itemAttributes.getKeys(false)) {
            if (!itemAttributes.getBoolean(path + ".enable"))
                continue;
            
            ItemStack item=loadItem(path);
            if(item==null) 
            	continue;
            
            ids.put(item,path);
            
            UTEItemStack uteItem=new UTEItemStack(
            		itemAttributes.getBoolean(path+".canPlace"),
            		itemAttributes.getBoolean(path+".isConsume"),
            		path,
            		item.getItemMeta().getDisplayName(),
            		itemAttributes.getInt(path+".needLevel"),
            		item,
            		new NamespacedKey(plugin,"ute_"+path),
            		item.getItemMeta().getLore());
            items.put(path,uteItem);
            
            if(itemAttributes.contains(path+".provideLevel")) {
            	int level=itemAttributes.getInt(path+".provideLevel");
            	machines.put(level,path);
            }
        }
        new Brick();
        new Plank();
        new Rope();
        new NightMare();
        new Coin();
        new Fern();
        new Hail();
        new Reed();

        new Sclerite();
        new SpiderGland();
        new Ashes();
        new PurpleGum();

        new StrawHat();
        new Garland();
        new Earmuff();
        new BushesHat();
        new EyeballUmbrella();
        new ConstantTemperatureClothes();
        new SwimmingSuit();

        new MovablePack();
        new NormalPack();
        new PigPack();
        new Reviver();
        new WarmStone();
        new Umbrella();
        new FlowerUmbrella();
        new HealingSalve();
        new HoneyPoultice();
        new ACDDrug();
        new LuxuryFan();
        new StrawRoll();
        new FurRoll();
        new WaterBalloon();
        new SiestaLeanto();

        new BlowArrow1();
        new BlowArrow2();
        new BlowArrow3();
        new BeeMine();
        new ToothTrap();
        new WeatherPain();

        new Element();
        new Thermometer();
        new Hygrometer();
        new LightningArrester();
        new IceFlingomatic();
        new Refridgerator();
        new Detector();
        new ScienceMachine();

        new FireWand();
        
        ItemProvider.loadDrops();
        plugin.getServer().getPluginManager().registerEvents(new ItemListener(), plugin);
    }
    public static ItemStack loadItem(String path) {
    	try {
	    	Material material=Material.valueOf(itemSets.getString(path+".type"));
	    	String name=itemSets.getString(path+".name");
	    	List<String> lores=itemSets.getStringList(path+".lores");
	    	
	    	ItemStack item=new ItemStack(material);
	    	ItemMeta meta=item.getItemMeta();
	    	meta.setDisplayName(name);
	    	meta.setLore(lores); 
	    	item.setItemMeta(meta);
	    	return item;
    	}catch(Exception exception) {
    		System.out.println("itemSets.yml下物品"+path+"读取错误，请检查！");
    	}
    	return null;
    }
    public static boolean isSimilar(ItemStack item, ItemStack uteItem) {
        if (item == uteItem) return true;
        if (item == null || uteItem == null) return false;
        Material m1 = item.getType();
        Material m2 = uteItem.getType();
        if (m1 == Material.AIR) m1 = ItemFactory.getType(item);
        if (m2 == Material.AIR) m2 = ItemFactory.getType(uteItem);
        if (m1 == Material.AIR || m2 == Material.AIR) return m1 == m2;
        if (m1 == m2) {
            ItemMeta meta = item.getItemMeta();
            ItemMeta meta2 = uteItem.getItemMeta();
            if (Objects.equals(meta.getDisplayName(), meta2.getDisplayName())) {
                return Objects.equals(meta.getLore(), meta2.getLore());
            }
        }
        return false;
    }
}
