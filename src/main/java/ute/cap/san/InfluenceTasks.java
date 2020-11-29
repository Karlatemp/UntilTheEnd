package ute.cap.san;

import java.util.ArrayList;
import java.util.UUID;

import ute.internal.NPCChecker;
import ute.internal.ResidenceChecker;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import ute.Config;
import ute.UntilTheEnd;
import ute.player.PlayerManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class InfluenceTasks {
    public  static UntilTheEnd plugin= UntilTheEnd.getInstance();
    public static double disguiseRangeX = Sanity.yaml.getDouble("disguiseRangeX");
    public static double disguiseRangeY = Sanity.yaml.getDouble("disguiseRangeY");
    public static double disguiseRangeZ = Sanity.yaml.getDouble("disguiseRangeZ");
    public static double disguiseSanity = Sanity.yaml.getDouble("disguiseSanity");
    public static double confusionSanity = Sanity.yaml.getDouble("confusionSanity");

    public static void initialize() {
        try {
            new CreatureDisguise().runTaskTimer(plugin, 0L, 1000L);
        } catch (Throwable ignore) {
        }
        new Confusion().runTaskTimer(plugin, 0L, 80L);
    }

    public static class CreatureDisguise extends BukkitRunnable {
        public ArrayList<UUID> mobs = new ArrayList<UUID>();

        @Override
        public void run() {
            for (UUID uuid : mobs) {
                Entity entity = Bukkit.getEntity(uuid);
                if (entity == null) return;
                DisguiseAPI.undisguiseToAll(entity);
            }
            for (World world : Config.enableWorlds)
                for (Player player : world.getPlayers()) {
                    if (NPCChecker.isNPC(player)|| ResidenceChecker.isProtected(player.getLocation())) continue;
                    int san = (int) PlayerManager.check(player, PlayerManager.CheckType.SANITY);
                    int disguiseSanityCal = (int) (disguiseSanity * PlayerManager.check(player, PlayerManager.CheckType.SANMAX));
                    if (san <= disguiseSanityCal)
                        for (Entity entity : player.getNearbyEntities(disguiseRangeX, disguiseRangeY, disguiseRangeZ)) {
                            DisguiseType type = DisguiseType.values()[(int) (DisguiseType.values().length * Math.random() - 1)];
                            MobDisguise disguise = new MobDisguise(type);
                            DisguiseAPI.disguiseToPlayers(entity, disguise, player.getName());
                            disguise.setEntity(entity);
                            disguise.startDisguise();
                            mobs.add(entity.getUniqueId());
                        }
                }
        }
    }

    public static class Confusion extends BukkitRunnable {
        @Override
        public void run() {
            for (World world : Config.enableWorlds)
                for (Player player : world.getPlayers()) {
                    if (NPCChecker.isNPC(player)||ResidenceChecker.isProtected(player.getLocation())) continue;
                    int san = (int) PlayerManager.check(player, PlayerManager.CheckType.SANITY);
                    int confusionSanityCal = (int) (confusionSanity * PlayerManager.check(player, PlayerManager.CheckType.SANMAX));
                    if (san <= confusionSanityCal) {
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));
                    }
                }
        }
    }
//	if(san>120) continue;
//	if(san<=120) {
//		
//	}
}
