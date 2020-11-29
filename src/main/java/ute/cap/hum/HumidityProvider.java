package ute.cap.hum;

import java.util.HashMap;

import ute.Logging;
import ute.internal.ItemFactory;
import ute.internal.UTEi18n;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class HumidityProvider {
    public static HashMap<Material, Material> moistness = new HashMap<>();
    public static HashMap<Material, Material> driness = new HashMap<>();

    public static void loadConfig() {
        final ConfigurationSection section = Humidity.yaml.getConfigurationSection("wetBlocks");
        if (section != null) {
            for (String from : section.getKeys(false)) {
                String to = section.getString(from);
                Material sf = ItemFactory.valueOf(from);
                Material tm = ItemFactory.valueOf(to);
                Logging.getLogger().info(UTEi18n.parse("cap.hum.provider.rule", String.valueOf(to), String.valueOf(from)));
                moistness.put(sf, tm);
                driness.put(tm, sf);
            }
        }
    }
}
