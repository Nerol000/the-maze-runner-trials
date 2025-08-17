package net.nerol.mazerunner.entity.animations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;
import net.nerol.mazerunner.entity.GrieverEntity;

import java.util.Random;

public class AttackAnimations {
    private static final Random RANDOM = new Random();

    public static void performRandomAttack(GrieverEntity attacker, LivingEntity target) {
        String attackType = getRandomAttackType();
        performAttack(attacker, target, attackType);
    }

    private static String getRandomAttackType() {
        int roll = RANDOM.nextInt(100);
        if (roll < 90) return "bite";    // 90%
        if (roll >= 95) return "sting";   // 5%
        return "strike";                 // 5%
    }

    private static void performAttack(GrieverEntity attacker, LivingEntity target, String attackType) {
        switch (attackType) {
            case "bite" -> performBiteAttack(attacker, target);
            case "sting" -> performStingAttack(attacker, target);
            case "strike" -> performStrikeAttack(attacker, target);
            default -> throw new IllegalStateException("Unexpected attack type: " + attackType);
        }
    }

    private static void performBiteAttack(GrieverEntity attacker, LivingEntity target) {
        if (attacker.squaredDistanceTo(target) <= 16.0D && !attacker.getWorld().isClient()) {
            //ServerWorld serverWorld = (ServerWorld) attacker.getWorld();
            //target.damage(serverWorld, serverWorld.getDamageSources().mobAttack(attacker), 10.0f);
            attacker.playSound(Registries.SOUND_EVENT.get(
                    Identifier.of(TheMazeRunner.MOD_ID, "griever.bite")), 1.0f, 1.0f);
            attacker.startBiteAttack();
        }
    }

    private static void performStingAttack(GrieverEntity attacker, LivingEntity target) {
        if (attacker.squaredDistanceTo(target) <= 12.25D && !attacker.getWorld().isClient()) {
            //ServerWorld serverWorld = (ServerWorld) attacker.getWorld();
            //target.damage(serverWorld, serverWorld.getDamageSources().mobAttack(attacker), 12.0f);
            attacker.startStingAttack();
            /*
            target.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE, 1728000, 0));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 1, false, false));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 900, 1, false, false));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 600, 0, false, false));

             */
        }
    }

    private static void performStrikeAttack(GrieverEntity attacker, LivingEntity target) {
        if (attacker.squaredDistanceTo(target) <= 10.5625D && !attacker.getWorld().isClient()) {
            //ServerWorld serverWorld = (ServerWorld) attacker.getWorld();
            attacker.startStrikeAttack();

            /*target.damage(serverWorld, serverWorld.getDamageSources().mobAttack(attacker), 14.0f);
            Vec3d direction = target.getPos().subtract(attacker.getPos()).normalize();
            target.addVelocity(direction.x * 0.45, -0.66, direction.z * 0.45);
            target.velocityDirty = true; */
        }
    }
}