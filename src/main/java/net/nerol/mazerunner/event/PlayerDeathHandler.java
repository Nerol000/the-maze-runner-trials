package net.nerol.mazerunner.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nerol.mazerunner.effect.ModEffects;

import java.util.Objects;

public class PlayerDeathHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player) {
                BlockPos deathPos = player.getBlockPos();
                //String playerName = player.getName().getString();
                World world = player.getWorld();

                if (entity.hasStatusEffect(ModEffects.FLARE)) {
                    HuskEntity husk = new HuskEntity(EntityType.HUSK, world);
                    husk.refreshPositionAndAngles(deathPos.getX() + 0.5, deathPos.getY(), deathPos.getZ() + 0.5, 0.0F, 0.0F);

                    husk.setPersistent();

                    Objects.requireNonNull(husk.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(40.0);  // 40 HP
                    Objects.requireNonNull(husk.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE)).setBaseValue(7.5f);
                    Objects.requireNonNull(husk.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(0.275f);
                    Objects.requireNonNull(husk.getAttributeInstance(EntityAttributes.ARMOR)).setBaseValue(5.0);
                    Objects.requireNonNull(husk.getAttributeInstance(EntityAttributes.FOLLOW_RANGE)).setBaseValue(255.0);

                    husk.addStatusEffect(new StatusEffectInstance(ModEffects.FLARE, 1728000, 0, false, true));

                    husk.setCustomName(Text.of("Revenant of " + player.getName().getString()));
                    husk.setCustomNameVisible(true);

                    world.spawnEntity(husk);
                    husk.setHealth(40.0F); // Also set current health

                    // Equip the husk with the player's items
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        ItemStack stack = player.getEquippedStack(slot);
                        if (!stack.isEmpty()) {
                            husk.equipStack(slot, stack.copy());
                            husk.setEquipmentDropChance(slot, 1.0f); // Optional: prevent drops
                        }
                    }
                }
            }
        });
    }
}
