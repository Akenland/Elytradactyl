# Elytradactyl
Mobs use Elytra to glide!

This is a very simple Bukkit plugin that allows Minecraft mobs to use Elytra to glide. By default, mobs can wear Elytra, but will not deploy it while airborne. This plugin periodically checks for airborne mobs, and deploys their Elytra.

Specifically, it calls [LivingEntity.setGliding(boolean)](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/LivingEntity.html#setGliding(boolean)), which in turn sets the [FallFlying NBT tag](https://minecraft.wiki/w/Mob#Common_NBT_data) on the mob, which controls whether the glider is deployed.

Can be turned on or off for monsters, animals, Citizens NPCs, and players. Off by default for players, but can be turned on to auto-deploy Elytra without hitting the jump key.

Most mobs lose their AI while gliding, and only fly southward as a result. This plugin also corrects this behaviour by using the mob's facing direction to control gliding direction.
