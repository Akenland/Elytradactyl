package com.kylenanakdewa.elytradactyl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Elytradactyl.
 */
public final class ElytradactylPlugin extends JavaPlugin {

	static ElytradactylPlugin plugin;

	/** The ID number of the task for this plugin. */
	private int taskID;

	// Mobs
	private static boolean playerUsesElytra;
	private static boolean npcUsesElytra;
	private static boolean skeletonUsesElytra;
	private static boolean witherSkeletonUsesElytra;
	private static boolean zombieUsesElytra;
	private static boolean huskUsesElytra;
	private static boolean zombiePigmanUsesElytra;
	private static boolean zombieVilagerUsesElytra;
	private static boolean strayUsesElytra;

	/** Number of ticks between checks for falling entities. */
	private static int checkTicks;

	@Override
	public void onEnable(){
		plugin = this;

		// Main command
		getCommand("elytradactyl").setExecutor(new ElytradactylCommands());
		
		// Run all reload tasks
		reload();
	}
	
	@Override
	public void onDisable(){
		// Cancel all tasks
		Bukkit.getScheduler().cancelTasks(plugin);
	}
	
	/** Reloads the plugin. */
	void reload(){
		// Disable/cleanup
		onDisable();
		
		// Load config
		saveDefaultConfig();
		loadConfig();

		// Start task
		taskID = startTask();
	}


	/** Retrieve values from config. */
	private void loadConfig(){
		reloadConfig();
		playerUsesElytra = getConfig().getBoolean("mobs.player");
		checkTicks = getConfig().getInt("check-ticks");
	}

	/** Starts the task for this plugin. */
	private int startTask(){
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			// For every world...
			for(World world : Bukkit.getWorlds()){
				// For every living entity...
				for(LivingEntity entity : world.getLivingEntities()){
					// If falling, tracked by plugin, and is wearing Elytra...
					if(isFalling(entity) && isTracked(entity)
					 && entity.getEquipment().getChestplate().getType().equals(Material.ELYTRA)){
						// Start gliding
						entity.setGliding(true);
					}
				}
			}
		}, checkTicks, checkTicks);
	}

	private boolean isFalling(LivingEntity entity){
        return !entity.isOnGround();
    }

	/** Checks if the given entity should activate Elytra when falling. */
    private boolean isTracked(LivingEntity entity){
		// TEMP
		return true;

		/*if(entity instanceof Player) return playerUsesElytra;
		if(entity instanceof Skeleton) return skeletonUsesElytra;
		if(entity instanceof Zombie) return zombieUsesElytra;
		if(entity.hasMetadata("NPC")) return npcUsesElytra;

		return false;*/
	}
}
