package me.exendv2.rpgaddonskills.managers;

import ch.njol.skript.variables.Variables;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import me.exendv2.rpgaddonskills.RPGAddonSkills;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerManager {
    public final HashMap<Player, String> playerComboMap = new HashMap<>();

    public final HashMap<Player, Long> playerCooldownMap = new HashMap<>();

    public final HashMap<Player, Long> playerCastingMap = new HashMap<>();

    public String getPlayerClass(Player player) {
        return String.valueOf(Variables.getVariable("-RPGAddon::" + player.getUniqueId() + "::class", null, false));
    }

    public double getMastery(Player player) {
        return Double.parseDouble(Variables.getVariable("-RPGAddon::" + player.getUniqueId() + "::mastery", null, false).toString());
    }

    public double getPlayerDamage(Player player) {
        return MMOPlayerData.get(player).getStatMap().getStat("ATTACK_DAMAGE");
    }

    public double getTotalDamage(Player player) {
        double masteryTotal = getMastery(player) * 1.5D;
        return masteryTotal + getPlayerDamage(player);
    }

    public int getPlayerIntelligence(Player player) {
        return PlayerData.get(player).getAttributes().getInstance("intelligence").getBase();
    }

    public boolean canCastSkill(Player player) {
        boolean canCast = false;
        String playerClass = getPlayerClass(player);
        TreeMap values = (TreeMap)Variables.getVariable("-class::" + playerClass + "::weapons::*", null, false);
        String weapon = NBTItem.get(player.getInventory().getItemInMainHand()).getString("MMOITEMS_ITEM_ID");
        Collection<String> weaponsCollection = values.values();
        for (String weapons : weaponsCollection) {
            if (weapons.equalsIgnoreCase(weapon)) {
                canCast = true;
                break;
            }
        }

        return canCast;
    }

    public void setPlayerCombo(Player player, String combo) {
        this.playerComboMap.put(player, combo);
    }

    public void removePlayerCombo(Player player) {
        this.playerComboMap.remove(player);
    }

    public String getPlayerCombo(Player player) {
        return this.playerComboMap.get(player);
    }

    public void setPlayerCooldown(Player player, Long cooldown) {
        this.playerCooldownMap.put(player, cooldown);
    }

    public void removePlayerCooldown(Player player) {
        this.playerCooldownMap.remove(player);
    }

    public boolean playerCooldownIsSet(Player player) {
        return this.playerCooldownMap.containsKey(player);
    }

    public void setPlayerCastingSkill(Player player, Long casting) {
        this.playerCastingMap.put(player, casting);
    }

    public void removePlayerCastingSkill(Player player) {
        this.playerCastingMap.remove(player);
    }

    public void castBasicAttack(Player player) {
        String playerClass = getPlayerClass(player);
        String skill = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::basic::name", null, false));
        if (skill.equalsIgnoreCase("null")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&cThis class doesn't have basic attack!"));
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
            return;
        }
        if (player.getWorld().getName().equalsIgnoreCase("pvp"))
            skill = skill + "_PVP";
        String skillCommand = "mmoitems ability " + skill + " " + player.getName();
        String dmgMultiplier = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::basic::multiplier", null, false));
        String cooldown = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::basic::cooldown", null, false));
        String manaadd = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::basic::manaadd", null, false));
        if (!dmgMultiplier.equals("null")) {
            double damage = getTotalDamage(player) * Double.parseDouble(dmgMultiplier);
            skillCommand = skillCommand + " damage " + damage;
        }
        if (!cooldown.equals("null"))
            skillCommand = skillCommand + " cooldown " + cooldown;
        if (!manaadd.equals("null"))
            skillCommand = skillCommand + " manaadd " + manaadd;
        RPGAddonSkills.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), skillCommand);
    }

    public void castSkill(Player player, String skillType) {
        String playerClass = getPlayerClass(player);
        String skill = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::name", null, false));
        if (skill.equalsIgnoreCase("null")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&cThis class doesn't have " + skillType + " skill!"));
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
            return;
        }
        if (player.getWorld().getName().equalsIgnoreCase("pvp"))
            skill = skill + "_PVP";
        String skillCommand = "mmoitems ability " + skill + " " + player.getName();
        String dmgMultiplier = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::multiplier", null, false));
        String cooldown = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::cooldown", null, false));
        String duration = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::duration", null, false));
        String heal = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::heal", null, false));
        String mana = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::mana", null, false));
        String mastery = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::mastery", null, false));
        String damagebuff = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::damagebuff", null, false));
        String healthcost = String.valueOf(Variables.getVariable("-class::" + playerClass + "::skills::" + skillType + "::healthcost", null, false));
        if (Integer.parseInt(mastery) > getMastery(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&cThis skill requires &f&6" + mastery + " class mastery &cto be used!"));
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
            return;
        }
        if (!dmgMultiplier.equals("null")) {
            double damage = getTotalDamage(player) * Double.parseDouble(dmgMultiplier);
            skillCommand = skillCommand + " damage " + damage;
        }
        if (!cooldown.equals("null"))
            skillCommand = skillCommand + " cooldown " + cooldown;
        if (!duration.equals("null"))
            skillCommand = skillCommand + " duration " + duration;
        if (!heal.equals("null")) {
            double healTotal = Double.parseDouble(heal) * (getMastery(player) + getPlayerIntelligence(player));
            skillCommand = skillCommand + " heal " + healTotal;
        }
        if (!mana.equals("null"))
            skillCommand = skillCommand + " mana " + mana;
        if (!damagebuff.equals("null"))
            skillCommand = skillCommand + " damage_buff " + damagebuff;
        if (!healthcost.equals("null")) {
            double healthcostTotal = Double.parseDouble(healthcost) * player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            skillCommand = skillCommand + " healthcost " + healthcostTotal;
        }
        taskCmd(skillCommand);
    }

    public void taskCmd(final String skillCommand) {
        (new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), skillCommand);
            }
        }).runTask(RPGAddonSkills.getInstance());
    }
}
