package ute.cap.tem;

import ute.Config;
import ute.Logging;
import ute.UntilTheEnd;
import ute.api.BlockApi;
import ute.api.WorldApi;
import ute.internal.ItemFactory;
import ute.internal.ScriptProvider;
import ute.internal.UTEi18n;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import ute.world.WorldProvider;

import javax.script.CompiledScript;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class TemperatureProvider {
    public interface TemperatureAllocator {
        double apply(int day, World world);
    }

    public static UntilTheEnd plugin = UntilTheEnd.getInstance();
    public static HashMap<World, AtomicDouble> worldTemperatures = new HashMap<>();
    public static HashMap<Material, Integer> blockTemperatures = new HashMap<>();
    public static HashMap<Material, FMBlock> fmBlocks = new HashMap<>();
    public static HashMap<String, Integer> uteBlockTemperatures = new HashMap<>();
    private static final BiFunction<World, AtomicDouble, AtomicDouble> merger = (w, v) -> v == null ? new AtomicDouble() : v;
    private static final Map<WorldProvider.Season, TemperatureAllocator> rules = new HashMap<>();

    private static TemperatureAllocator fromScript(String script) {
        final CompiledScript compile = ScriptProvider.of(script);
        if (script != null) {
            return (day, world) -> {
                Number num = (Number)
                        ScriptProvider.of(compile)
                                .name(script)
                                .append("day", day)
                                .append("world", world)
                                .invoke();
                if (num == null) return 0D;
                return num.doubleValue();
            };
        }
        return null;
    }

    private static void make(YamlConfiguration configuration, WorldProvider.Season s, TemperatureAllocator default_) {
        String path = configuration.getString(s.name().toLowerCase(), "default");
        if (path.equals("default")) {
            rules.put(s, default_);
        } else {
            final TemperatureAllocator script = fromScript(path);
            rules.put(WorldProvider.Season.SPRING, script == null ? default_ : script);
        }
    }

    static {
        final YamlConfiguration configuration = Config.autoUpdateConfigs("season-tmp.yml");
        make(configuration, WorldProvider.Season.SPRING, (day, world) -> (Math.random() * (-5) + Math.random() * 5 + 1.5 * day - Math.sqrt(Math.sqrt(Math.sqrt(world.getTime())))));
        make(configuration, WorldProvider.Season.SUMMER, (day, world) -> (Math.random() * (-5) + Math.random() * 15 - 0.2 * day * day + 4.6 * day + 35.5 - Math.sqrt(Math.sqrt(Math.sqrt(world.getTime())))));
        make(configuration, WorldProvider.Season.AUTUMN, (day, world) -> (Math.random() * (-5) + Math.random() * 5 + 50 - 1.5 * day - Math.sqrt(Math.sqrt(Math.sqrt(world.getTime())))));
        make(configuration, WorldProvider.Season.WINTER, (day, world) -> (0.15 * day * day - 3.5 * day + 3.3 - Math.random() * (-5) + Math.random() * 5 - Math.sqrt(Math.sqrt(Math.sqrt(world.getTime())))));
    }

    public static void updateWorldTemperature(World world, double value) {
        worldTemperatures.compute(world, merger).set(value);
    }

    public static void initialize(UntilTheEnd plugin) {
        loadWorldTemperatures();
        loadBlockTemperatures();
        loadFMBlocks();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler()
            void onWorldLoad(WorldLoadEvent event) {
                loadWorldTemperature(event.getWorld());
            }

            @EventHandler()
            void onWorldUnload(WorldUnloadEvent event) {
                worldTemperatures.remove(event.getWorld());
            }
        }, plugin);
    }

    public static void loadFMBlocks() {
        for (String path : Temperature.yaml.getKeys(true)) {
            if (path.startsWith("fmBlocks.")) {
                if (path.replace("fmBlocks.", "").contains(".")) continue;
                Material currentMaterial = ItemFactory.valueOf(path.replace("fmBlocks.", ""));
                Material newMaterial = ItemFactory.valueOf(Temperature.yaml.getString(path + ".newMaterial"));
                boolean isIncrease = Temperature.yaml.getBoolean(path + ".increase");
                int temperature = Temperature.yaml.getInt(path + ".temperature");
                fmBlocks.put(currentMaterial, new FMBlock(newMaterial, temperature, isIncrease));
                Logging.getLogger().info(UTEi18n.parse("cap.tem.provider.fmb.rule", path.replace("fmBlocks.", ""), String.valueOf(newMaterial), String.valueOf(temperature)));
            }
        }
    }

    public static void loadWorldTemperatures() {
        for (World world : Bukkit.getWorlds()) {
            loadWorldTemperature(world);
        }
    }

    public static void loadWorldTemperature(World world) {
        if (Config.enableWorlds.contains(world)) updateWorldTemperature(world, getWorldTemperature(world));
        else updateWorldTemperature(world, 37);
    }

    public static void loadBlockTemperatures() {
        for (String path : Temperature.yaml.getKeys(true)) {
            if (path.startsWith("blockTemperature.")) {
                int tem = Temperature.yaml.getInt(path);
                path = path.replace("blockTemperature.", "");
                Material material = ItemFactory.valueOf(path);
                Logging.getLogger().info(UTEi18n.parse(
                        "cap.tem.provider.block.tem.rule",
                        ItemFactory.toString(material) + "(" + path + ")",
                        String.valueOf(tem)
                ));
                blockTemperatures.put(material, tem);
            }
        }
    }

    public static double getWorldTemperature(World world) {
        WorldProvider.Season season = WorldApi.getSeason(world);
        int day = WorldApi.getDay(world);
        final TemperatureAllocator allocator = rules.get(season);
        if (allocator != null) return allocator.apply(day, world);
        return 37;
    }

    public static double getBlockTemperature(Location loc) {
        if (!Config.enableWorlds.contains(loc.getWorld())) return 37;
        if (loc.getBlock() == null) return 37;
        World world = loc.getWorld();

        // 我们需要你的帮助来优化此温度算法! 谢谢!
        // We need the help for update this Temperature algorithm. Thank you very much!
        final Integer value = blockTemperatures.get(ItemFactory.getType(loc.getBlock()));
        if (value != null) return value;

        double season = TemperatureProvider.worldTemperatures.get(world).doubleValue();
        Location l = loc.clone();
        Location blockBaseLoc = loc.clone();
        {
            blockBaseLoc.setX(blockBaseLoc.getBlockX() + 0.5);
            blockBaseLoc.setY(blockBaseLoc.getBlockY() + 0.5);
            blockBaseLoc.setZ(blockBaseLoc.getBlockZ() + 0.5);
        }
        final int d = 4;
        double temUpOffset = 0;
        double temDownOffset = 0;
        double temUpScale = 1;
        double temDownScale = 1;
        boolean edited = false;
        for (int x = -d; x <= d; x++) {
            l.setX(blockBaseLoc.getX() + x);
            for (int z = -d; z <= d; z++) {
                l.setZ(blockBaseLoc.getZ() + z);
                for (int y = -d; y <= d; y++) {
                    l.setY(blockBaseLoc.getY() + y);
                    Block b = l.getBlock();
                    if (b == null) continue;
                    Material mt = ItemFactory.getType(b);
                    Integer tmp = blockTemperatures.get(mt);
                    if (uteBlockTemperatures.containsKey(BlockApi.getSpecialBlock(l))) {
                        tmp = uteBlockTemperatures.get(BlockApi.getSpecialBlock(l));
                    }
                    if (tmp != null) {
                        double scale = l.distance(loc);
                        double val = tmp - season;
                        if (val > 0) {
                            temUpScale += scale * Math.max(1, scale * 3 / d);
                            temUpOffset += val * scale;
                        } else {
                            temDownScale += scale * Math.max(1, scale * 3 / d);
                            temDownOffset += val * scale;
                        }
                        edited = true;
                    }

                }
            }
        }
        if (edited) {
            season += (temUpOffset / temUpScale) + (temDownOffset / temDownScale);
        }

        return season;
    }

    public static class FMBlock {
        Material newMaterial;
        int temperature;
        boolean upOrDown;

        public FMBlock(Material newBlock, int temperature, boolean upOrDown) {
            this.newMaterial = newBlock;
            this.temperature = temperature;
            this.upOrDown = upOrDown;
        }
    }
}
