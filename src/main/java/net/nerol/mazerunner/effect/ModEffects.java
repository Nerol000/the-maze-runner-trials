package net.nerol.mazerunner.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> FLARE = registerStatusEffect(
            new FlareEffect(StatusEffectCategory.HARMFUL, 0x4f6629)
                    .addAttributeModifier(EntityAttributes.ATTACK_SPEED, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.1f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.BLOCK_BREAK_SPEED, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.15f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.05f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.BLOCK_INTERACTION_RANGE, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.06f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.ENTITY_INTERACTION_RANGE, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.1f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.125f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.SCALE, Identifier.of(TheMazeRunner.MOD_ID, "flare"), -0.025f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    private static RegistryEntry<StatusEffect> registerStatusEffect(StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(TheMazeRunner.MOD_ID, "flare"), statusEffect);
    }

    public static void registerEffects() {
        TheMazeRunner.LOGGER.info("Registering Mod Effects for " + TheMazeRunner.MOD_ID);
    }
}
