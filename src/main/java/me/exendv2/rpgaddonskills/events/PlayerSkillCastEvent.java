package me.exendv2.rpgaddonskills.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSkillCastEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String skill;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerSkillCastEvent(Player player, String skill){
        this.player = player;
        this.skill = skill;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer(){
        return this.player;
    }

    public String getSkill(){
        return this.skill;
    }
}
