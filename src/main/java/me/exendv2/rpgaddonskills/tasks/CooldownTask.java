package me.exendv2.rpgaddonskills.tasks;

import me.exendv2.rpgaddonskills.RPGAddonSkills;
import me.exendv2.rpgaddonskills.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.util.Map;

public class CooldownTask {


    PlayerManager playerManager = RPGAddonSkills.getInstance().getPlayerManager();

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Map.Entry<Player, Long> entry : playerManager.playerCastingMap.entrySet()){
                    if (entry.getValue() < Instant.now().getEpochSecond()){
                        Player player = entry.getKey();
                        player.sendMessage("§cSkill Casting Expired!");
                        playerManager.removePlayerCastingSkill(player);
                        playerManager.removePlayerCombo(player);
                        playerManager.removePlayerCooldown(player);
                    }
                }
            }
        }.runTaskTimer(RPGAddonSkills.getInstance(), 0L, 20L);
    }
}
