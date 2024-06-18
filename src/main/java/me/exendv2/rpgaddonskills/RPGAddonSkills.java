package me.exendv2.rpgaddonskills;

import me.exendv2.rpgaddonskills.listeners.PlayerListener;
import me.exendv2.rpgaddonskills.managers.PlayerManager;
import me.exendv2.rpgaddonskills.managers.SkillsManager;
import me.exendv2.rpgaddonskills.tasks.CooldownTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGAddonSkills extends JavaPlugin {

    private static RPGAddonSkills instance;
    PlayerManager playerManager;
    SkillsManager skillsManager;

    @Override
    public void onEnable() {
        instance = this;


        // Register Listeners
        registerListeners();


        // Initialize Managers
        playerManager = new PlayerManager();
        skillsManager = new SkillsManager();

        // Tasks
        CooldownTask cooldownTask = new CooldownTask();
        cooldownTask.start();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RPGAddonSkills getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    public SkillsManager getSkillsManager(){
        return skillsManager;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
