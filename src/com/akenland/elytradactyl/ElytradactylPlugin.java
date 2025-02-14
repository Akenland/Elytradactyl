package com.akenland.elytradactyl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion.Target;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

/**
 * Main plugin class for Elytradactyl.
 */
@Plugin(name = "Elytradactyl", version = "1.1")
@Description("Mobs use Elytra to glide!")
@Author("Kade")
@Website("https://plugins.akenland.com/Elytradactyl")
@ApiVersion(Target.v1_20)
public final class ElytradactylPlugin extends JavaPlugin {

	/** The ID number of the task for this plugin. */
	int taskID;

	// Mobs
	boolean playerUsesElytra;
	boolean npcUsesElytra;
	boolean monsterUsesElytra;
	boolean animalUsesElytra;
	boolean otherUsesElytra;

	/** Number of ticks between checks for falling entities. */
	int checkTicks;

	/** Whether mob's directions should be re-assigned after they start gliding. */
	boolean reassignDirection;

	@Override
	public void onEnable() {
		// Main command
		getCommand("elytradactyl").setExecutor(new ElytradactylCommands(this));

		// Run all reload tasks
		reload();
	}

	@Override
	public void onDisable() {
		// Cancel all tasks
		Bukkit.getScheduler().cancelTasks(this);
	}

	/** Reloads the plugin. */
	void reload() {
		// Disable/cleanup
		onDisable();

		// Load config
		saveDefaultConfig();
		loadConfig();

		// Start task
		taskID = startTask();
	}

	/** Retrieve values from config. */
	private void loadConfig() {
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
	private int startTask() {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			// For every world...
			for (World world : Bukkit.getWorlds()) {
				// For every living entity...
				for (LivingEntity entity : world.getLivingEntities()) {
					// If falling, tracked by plugin, and is wearing Elytra or any glider item...
					if (isFalling(entity) && isTracked(entity) && isGlider(entity.getEquipment().getChestplate())) {
						// Get current location
						Location loc = entity.getLocation();

						// Start gliding
						entity.setGliding(true);

						// If entity is not an NPC or player, re-assign that location so they face a
						// direction other than south
						if (reassignDirection && !(entity instanceof Player) && !entity.hasMetadata("NPC")) {
							entity.teleport(loc);
						}
					}
				}
			}
		}, checkTicks, checkTicks);
	}

	private boolean isFalling(LivingEntity entity) {
		// Entity must not be on ground
		return !entity.isOnGround()
				// Entity must have air below them
				&& entity.getLocation().subtract(0, 1, 0).getBlock().isEmpty()
				&& entity.getLocation().subtract(0, 2, 0).getBlock().isEmpty()
				// Entity must not be a player, or they must not be flying
				&& (!(entity instanceof Player) || !((Player) entity).isFlying());
	}

	/** Checks if the given entity should activate Elytra when falling. */
	private boolean isTracked(LivingEntity entity) {
		if (entity.hasMetadata("NPC"))
			return npcUsesElytra;
		if (entity instanceof Player)
			return playerUsesElytra;
		if (entity instanceof Monster)
			return monsterUsesElytra;
		if (entity instanceof Animals)
			return animalUsesElytra;

		return otherUsesElytra;
	}

	/**
	 * Checks if the given item is a glider (either Elytra or has glider component).
	 */
	private boolean isGlider(ItemStack item) {
		if (item == null)
			return false;

		if (item.getType().equals(Material.ELYTRA))
			return true;

		return item.hasItemMeta() && item.getItemMeta().isGlider();
	}

}