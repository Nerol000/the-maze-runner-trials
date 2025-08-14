package net.nerol.mazerunner.SoundEvent;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;

public class ModSounds {
    public static final SoundEvent GRIEVER_BITE = registerSoundEvent("griever.bite");

    public static final SoundEvent GRIEVER_IDLE = registerSoundEvent("griever.idle");

    public static final SoundEvent GRIEVER_ROAR = registerSoundEvent("griever.roar");

    public static final SoundEvent GRIEVER_WALK = registerSoundEvent("griever.walk");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(TheMazeRunner.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        TheMazeRunner.LOGGER.info("Registering mod sounds for " + TheMazeRunner.MOD_ID);
    }
}
