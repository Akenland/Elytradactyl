package com.kylenanakdewa.elytradactyl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Elytradactyl.
 */
public final class ElytradactylPlugin extends JavaPlugin {

	static ElytradactylPlugin plugin;

	/** The ID number of the task for this plugin. */
	int taskID;

	// Mobs
	static boolean playerUsesElytra;
	static boolean npcUsesElytra;
	static boolean monsterUsesElytra;
	static boolean animalUsesElytra;
	static boolean otherUsesElytra;

	/** Number of ticks between checks for falling entities. */
	static int checkTicks;

	/** Whether mob's directions should be re-assigned after they start gliding. */
	static boolean reassignDirection;

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
		npcUsesElytra = getConfig().getBoolean("mobs.npc");
		monsterUsesElytra = getConfig().getBoolean("mobs.monster");
		animalUsesElytra = getConfig().getBoolean("mobs.animal");
		otherUsesElytra = getConfig().getBoolean("mobs.other");

		checkTicks = getConfig().getInt("check-ticks");
		reassignDirection = getConfig().getBoolean("reassign-direction");
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
					 && entity.getEquipment().getChestplate()!=null
					 && entity.getEquipment().getChestplate().getType().equals(Material.ELYTRA)){

						// Get current location
						Location loc = entity.getLocation();

						// Start gliding
						entity.setGliding(true);

						// If entity is not an NPC or player, re-assign that location so they face a direction other than south
						if(reassignDirection && !(entity instanceof Player) && !entity.hasMetadata("NPC")){
							entity.teleport(loc);
						}
					}
				}
			}
		}, checkTicks, checkTicks);
	}

	private boolean isFalling(LivingEntity entity){
		// Entity must not be on ground
		return !entity.isOnGround()
		 // Entity must have air below them
		 && entity.getLocation().subtract(0, 1, 0).getBlock().isEmpty()
		 // Entity must not be a player, or...
		 && (!(entity instanceof Player)
		 // If entity is a player, they must not be flying
		  || !((Player)entity).isFlying());
    }

	/** Checks if the given entity should activate Elytra when falling. */
    private boolean isTracked(LivingEntity entity){
		if(entity instanceof Player) return playerUsesElytra;
		if(entity.hasMetadata("NPC")) return npcUsesElytra;
		if(entity instanceof Monster) return monsterUsesElytra;
		if(entity instanceof Animals) return animalUsesElytra;

		return otherUsesElytra;
	}
}