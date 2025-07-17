package net.nerol.mazerunner.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.nerol.mazerunner.effect.ModEffects;

import java.util.Objects;

import static net.nerol.mazerunner.effect.ModEffects.FLARE;

public class FlareInjectorItem extends Item {

    public FlareInjectorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        HitResult hit = user.raycast(5.0D, 0.0F, true);
        if (hit.getType() == HitResult.Type.ENTITY) {
            return ActionResult.PASS;
        }

        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
                user.damage(serverWorld, serverWorld.getDamageSources().cactus(), 4.75f);
            }

            if (user.hasStatusEffect(ModEffects.FLARE)) {
                user.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                        1728000, Objects.requireNonNull(user.getStatusEffect(FLARE)).getAmplifier(), false,true));
            }

            else {
                user.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE, 1728000, 0, false, true));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (!target.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) user.getWorld();

            target.damage(serverWorld, serverWorld.getDamageSources().cactus(), 4.75f);
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }


            if (target.hasStatusEffect(ModEffects.FLARE)) {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                        1728000, Objects.requireNonNull(target.getStatusEffect(FLARE)).getAmplifier(), false,true));
            }
            else {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE,
                        1728000, 0, false, true));
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    
}
