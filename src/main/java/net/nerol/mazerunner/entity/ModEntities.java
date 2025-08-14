package net.nerol.mazerunner.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;

public class ModEntities {

    public static final Identifier GRIEVER_ID = Identifier.of(TheMazeRunner.MOD_ID, "griever");

    public static final RegistryKey<EntityType<?>> GRIEVER_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, GRIEVER_ID);

    public static EntityType<GrieverEntity> GRIEVER;

    public static void register() {
        GRIEVER = Registry.register(Registries.ENTITY_TYPE, GRIEVER_ID, EntityType.Builder.create(GrieverEntity::new, SpawnGroup.MONSTER)
                .dimensions(1.75f, 2.5f).build(GRIEVER_KEY));
    }
}