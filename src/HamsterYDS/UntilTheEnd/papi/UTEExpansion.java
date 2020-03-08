package HamsterYDS.UntilTheEnd.papi;

import HamsterYDS.UntilTheEnd.api.UntilTheEndApi;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * Create at 2020/3/7 23:04
 * Copyright Karlatemp
 * UntilTheEnd $ HamsterYDS.UntilTheEnd.papi
 */
public class UTEExpansion extends PlaceholderExpansion {

    public String getAuthor() {
        return "[南外丶仓鼠,瑞瑞瑞瑞阿,Karlatemp]";
    }

    public String getIdentifier() {
        return "ute";
    }

    public String getVersion() {
        return "5.6";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        } else if (identifier.equals("san")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getValue(player, "san"));
        } else if (identifier.equals("tem")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getValue(player, "tem"));
        } else if (identifier.equals("hum")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getValue(player, "hum"));
        } else if (identifier.equals("season")) {
            return UntilTheEndApi.WorldApi.getName(UntilTheEndApi.WorldApi.getSeason(player.getWorld()));
        } else if (identifier.equals("day")) {
            return String.valueOf(UntilTheEndApi.WorldApi.getDay(player.getWorld()));
        } else if (identifier.equals("sanitycolor")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getSanityColor(player));
        } else if (identifier.equals("temperaturecolor")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getTemperatureColor(player));
        } else if (identifier.equals("humiditycolor")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getHumidityColor(player));
        } else if (identifier.equals("seasoncolor")) {
            return String.valueOf(UntilTheEndApi.WorldApi.getSeasonColor(player.getWorld()));
        } else if (identifier.equals("sanitytend")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getChangingTend(player, "san"));
        } else if (identifier.equals("temperaturetend")) {
            return String.valueOf(UntilTheEndApi.PlayerApi.getChangingTend(player, "tem"));
        } else {
            return identifier.equals("humiditytend") ? String.valueOf(UntilTheEndApi.PlayerApi.getChangingTend(player, "hum")) : "";
        }
    }
}