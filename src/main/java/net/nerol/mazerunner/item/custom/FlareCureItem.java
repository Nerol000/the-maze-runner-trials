package net.nerol.mazerunner.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.nerol.mazerunner.effect.ModEffects;
import net.nerol.mazerunner.entity.GrieverEntity;

public class FlareCureItem extends Item {

    public FlareCureItem(Settings settings) {
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

            if (user.hasStatusEffect(ModEffects.FLARE)) {
                user.removeStatusEffect(ModEffects.FLARE);

                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                    user.damage(serverWorld, user.getDamageSources().cactus(), 4.75F);

                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600, 2, false, false));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 12000, 4, false, true));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 6000, 2, false, false));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 14000, 2, false, false));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 700, 1, false, false));
                }
                EntityAttributeInstance maxHealth = user.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (maxHealth != null) {
                    maxHealth.setBaseValue(20.0d);
                }
                user.getItemCooldownManager().set(stack, 100);

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    // Called when right-clicking on an entity
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (!target.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) user.getWorld();

            if (target instanceof GrieverEntity) {
                target.damage(serverWorld, serverWorld.getDamageSources().cactus(), 7.5f);
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 300, 0,false,false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1800, 1, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 2000, 1, false, false));
                user.getItemCooldownManager().set(stack, 100);

                return ActionResult.SUCCESS;
            }

            if (target.hasStatusEffect(ModEffects.FLARE)) {
                target.removeStatusEffect(ModEffects.FLARE);
                target.damage(serverWorld, serverWorld.getDamageSources().cactus(), 4.75F);
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600, 2, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 12000, 2, false, true));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 6000, 1, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 14000, 2, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 700, 1, false, false));

                EntityAttributeInstance maxHealth = target.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (maxHealth != null) {
                    maxHealth.setBaseValue(20.0d);
                }

                user.getItemCooldownManager().set(stack, 100);

                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
