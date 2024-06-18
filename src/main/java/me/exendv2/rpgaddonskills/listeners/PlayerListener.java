package me.exendv2.rpgaddonskills.listeners;

import me.exendv2.rpgaddonskills.RPGAddonSkills;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerRightClick1(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        RPGAddonSkills.getInstance().getSkillsManager().rightClick(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeftClick1(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        RPGAddonSkills.getInstance().getSkillsManager().leftClick(event.getPlayer());
    }
}

