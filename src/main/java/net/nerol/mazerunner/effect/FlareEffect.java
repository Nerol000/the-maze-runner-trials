package net.nerol.mazerunner.effect;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.nerol.mazerunner.entity.GrieverEntity;

import java.util.Random;

import static net.nerol.mazerunner.effect.ModEffects.FLARE;

public class FlareEffect extends StatusEffect {
    private static final Random RANDOM = new Random();

    public FlareEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {

        if (!world.isClient && !(entity instanceof GrieverEntity)) {

            StatusEffectInstance effectInstance = entity.getStatusEffect(FLARE);
            if (effectInstance == null) return true;
            int duration = effectInstance.getDuration();

            // Infect nearby Hostile entities
            for (HostileEntity nearby : world.getEntitiesByClass(
                    HostileEntity.class,
                    entity.getBoundingBox().expand(6 + (amplifier / 1.25)), // 6-block radius + amplifier
                    e -> e != entity && !e.hasStatusEffect(ModEffects.FLARE) && !(e instanceof GrieverEntity))) {

                if (RANDOM.nextFloat() < 0.00018518518f + (amplifier * 0.00003)) { // 1 in 5400 per tick (roughly being around for 4m30s)
                    nearby.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                            1728000,           // duration in ticks (24hr)
                            amplifier,     // same amplifier
                            false,         // ambient
                            true));        // show particles
                }
            }
            // For Villager entities
            for (VillagerEntity nearby : world.getEntitiesByClass(
                    VillagerEntity.class,
                    entity.getBoundingBox().expand(6 + (amplifier / 1.25)), // 6-block radius + amplifier
                    e -> e != entity && !e.hasStatusEffect(ModEffects.FLARE))) {

                if (RANDOM.nextFloat() < 0.00018518518f + (amplifier * 0.00003)) { // 1 in 5400 per tick (roughly being around for 4m30s)
                    nearby.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                            1728000,           // duration in ticks (24hr)
                            amplifier,     // same amplifier
                            false,         // ambient
                            true));        // show particles
                }
            }
            // Infect nearby Players
            for (PlayerEntity nearby : world.getEntitiesByClass(
                    PlayerEntity.class,
                    entity.getBoundingBox().expand(6 + (amplifier / 1.133)), // 6-block radius + amplifier
                    e -> e != entity && !e.hasStatusEffect(ModEffects.FLARE) && !e.isCreative() && !e.isSpectator())) {

                if (RANDOM.nextFloat() < 0.00027777777f + (amplifier * 0.00003)) { // 1 in 5400 per tick (roughly being around for 4m30s)
                    nearby.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                            1728000,           // duration in ticks
                            amplifier,     // same amplifier
                            false,         // ambient
                            true));        // show particles
                }
            }

            //13200
            if (duration % 13200 - (amplifier * 40) == 0 && duration != 0) {   //every 11 minutes
                EntityAttributeInstance maxHealth = entity.getAttributeInstance(EntityAttributes.MAX_HEALTH);

                if (maxHealth != null) {
                    double base = maxHealth.getBaseValue();
                    double reduction = (double) (amplifier / 2) + 1;
                    double newBase = Math.max(1.0, base - reduction);
                    maxHealth.setBaseValue(newBase); //reduce by 1 (if amplifier is 1)

                    entity.damage(world, entity.getDamageSources().wither(), amplifier + 3.25f);
                }
            }
            //0.00071428571
            if (RANDOM.nextFloat() < 0.00071428571f + (amplifier * 0.000005)) { //average about once per 70s
                entity.damage(world, entity.getDamageSources().wither(), amplifier + 2.0F);
            }

            // Mutation code: Check if effect has lasted 30,000 ticks (25 minutes)
            if (duration % 30000 == 0 && amplifier < 6 && duration != 0) {
                int newAmplifier = amplifier + 1;

                entity.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE, // Re-apply the effect with the new amplifier
                        1728000,
                        newAmplifier,
                        false,
                        true));
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}