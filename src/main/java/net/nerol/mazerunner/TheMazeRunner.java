package net.nerol.mazerunner;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.nerol.mazerunner.effect.ModEffects;
import net.nerol.mazerunner.event.PlayerDeathHandler;
import net.nerol.mazerunner.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class TheMazeRunner implements ModInitializer {
	public static final String MOD_ID = "mazerunner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		//Heloo

		ModEffects.registerEffects();
		ModItems.registerModItems();
		PlayerDeathHandler.register();

		// Detect when a player respawns, reset max_health
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			// Set max health to 20.0f (10 hearts)
			Objects.requireNonNull(newPlayer.getAttributeInstance(EntityAttributes.MAX_HEALTH))
					.setBaseValue(20.0f);
			newPlayer.setHealth(newPlayer.getMaxHealth());
		});
	}
}