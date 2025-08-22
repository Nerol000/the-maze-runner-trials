package net.nerol.mazerunner.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;

import net.minecraft.server.world.ServerWorld;
import net.nerol.mazerunner.effect.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    private static final Random RANDOM = new Random();

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();

        if (attacker instanceof LivingEntity livingAttacker) {
            if (livingAttacker.hasStatusEffect(ModEffects.FLARE)) {
                LivingEntity self = (LivingEntity)(Object)this;
                // 50% chance of getting the Flare effect
                if (RANDOM.nextFloat() < 0.3333f + (float)(Objects.requireNonNull(livingAttacker.getStatusEffect(ModEffects.FLARE)).getAmplifier() / 10)) {
                    self.addStatusEffect(new StatusEffectInstance( ModEffects.FLARE, 1728000, 0)); // 24 hrs, amplifier 0
                }
            }
        }
    }
}