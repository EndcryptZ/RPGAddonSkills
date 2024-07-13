package me.exendv2.rpgaddonskills.managers;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.exendv2.rpgaddonskills.RPGAddonSkills;
import me.exendv2.rpgaddonskills.events.PlayerSkillCastEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;



public class SkillsManager {
    PlayerManager playerManager = RPGAddonSkills.getInstance().getPlayerManager();

    public void rightClick(Player player) {
        if(player.getGameMode().equals(GameMode.SPECTATOR)){
            return;
        }

        if(playerManager.getPlayerClass(player).equalsIgnoreCase("artificer")){
            AbstractPlayer abstractPlayer = BukkitAdapter.adapt(player);

            if(MythicBukkit.inst().getSkillManager().getCaster(abstractPlayer).getAuraRegistry().hasAura("Arcane_Mecha_Aura")){
                if(!playerManager.canCastSkill(player)){
                    return;
                }
                playerManager.castSkill(player, "first");
                return;
            }
        }

        if (!this.playerManager.canCastSkill(player))
            return;
        if (player.isSneaking())
            return;
        if (this.playerManager.playerCooldownIsSet(player))
            return;
        this.playerManager.setPlayerCooldown(player, Instant.now().getEpochSecond() + 3L);
        taskRightClick(player);
    }

    public void taskRightClick(Player player) {
        String combo = this.playerManager.getPlayerCombo(player);
        if (combo == null) {
            this.playerManager.setPlayerCombo(player, "R");
            this.playerManager.setPlayerCastingSkill(player, Instant.now().getEpochSecond() + 3L);
            player.sendTitle("", "§a§nR§7-§n?§7-§n?", 0, 10, 0);
        } else if (combo.equals("R")) {
            this.playerManager.setPlayerCombo(player, "RR");
            this.playerManager.setPlayerCastingSkill(player, Instant.now().getEpochSecond() + 3L);
            player.sendTitle("", "§a§nR§7-§a§nR§7-§n?", 0, 10, 0);
        } else if (combo.equals("RR")) {
            this.playerManager.setPlayerCombo(player, "RRR");
            this.playerManager.castSkill(player, "second");
            player.sendTitle("", "§a§nR§7-§a§nR§7-§a§nR§7", 0, 10, 0);
            this.playerManager.removePlayerCombo(player);
            this.playerManager.removePlayerCastingSkill(player);
            callPlayerSkillCastEvent(player, "RRR");
        } else if (combo.equals("RL")) {
            this.playerManager.setPlayerCombo(player, "RLR");
            this.playerManager.castSkill(player, "first");
            player.sendTitle("", "§a§nR§7-§a§nL§7-§a§nR§7", 0, 10, 0);
            this.playerManager.removePlayerCombo(player);
            this.playerManager.removePlayerCastingSkill(player);
            callPlayerSkillCastEvent(player, "RLR");
        }
        player.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.0F, 2.0F);
        this.playerManager.removePlayerCooldown(player);
    }

    public void leftClick(Player player) {
        if(player.getGameMode().equals(GameMode.SPECTATOR)){
            return;
        }
        if (!this.playerManager.canCastSkill(player))
            return;
        if (player.isSneaking()) {
            this.playerManager.castSkill(player, "ultimate");
            player.sendTitle("", "§a§nSHIFT§7-§a§nL§7", 0, 10, 0);
            callPlayerSkillCastEvent(player, "SL");
            return;
        }
        String combo = this.playerManager.getPlayerCombo(player);
        if (this.playerManager.playerCooldownIsSet(player))
            return;
        if (combo == null) {
            this.playerManager.castBasicAttack(player);
            callPlayerSkillCastEvent(player, "L");
            return;
        }
        this.playerManager.setPlayerCooldown(player, Instant.now().getEpochSecond() + 3L);

        if(combo.equals("RL")){
            callPlayerSkillCastEvent(player, "RLL");
        }
        if(combo.equals("RR")){
            callPlayerSkillCastEvent(player, "RRL");
        }

        taskLeftClick(player);
    }

    public void taskLeftClick(final Player player) {
        (new BukkitRunnable() {
            public void run() {
                String combo = SkillsManager.this.playerManager.getPlayerCombo(player);
                switch (combo) {
                    case "R":
                        SkillsManager.this.playerManager.setPlayerCombo(player, "RL");
                        SkillsManager.this.playerManager.setPlayerCastingSkill(player, Instant.now().getEpochSecond() + 3L);
                        player.sendTitle("", "§a§nR§7-§a§nL§7-§n?", 0, 10, 0);
                        break;
                    case "RR":
                        SkillsManager.this.playerManager.setPlayerCombo(player, "RRL");
                        SkillsManager.this.playerManager.castSkill(player, "third");
                        player.sendTitle("", "§a§nR§7-§a§nR§7-§a§nL§7", 0, 10, 0);
                        SkillsManager.this.playerManager.removePlayerCombo(player);
                        SkillsManager.this.playerManager.removePlayerCastingSkill(player);
                        break;
                    case "RL":
                        SkillsManager.this.playerManager.setPlayerCombo(player, "RLL");
                        SkillsManager.this.playerManager.castSkill(player, "fourth");
                        player.sendTitle("", "§a§nR§7-§a§nL§7-§a§nL§7", 0, 10, 0);
                        SkillsManager.this.playerManager.removePlayerCombo(player);
                        SkillsManager.this.playerManager.removePlayerCastingSkill(player);
                        break;
                }
                player.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.0F, 2.0F);
                SkillsManager.this.playerManager.removePlayerCooldown(player);
            }
        }).runTaskLaterAsynchronously(RPGAddonSkills.getInstance(), 2L);
    }

    public void callPlayerSkillCastEvent(Player player, String skill){
        PlayerSkillCastEvent playerSkillCastEvent = new PlayerSkillCastEvent(player, skill);
        Bukkit.getPluginManager().callEvent(playerSkillCastEvent);
    }
}
