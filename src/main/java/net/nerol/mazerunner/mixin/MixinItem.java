package net.nerol.mazerunner.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.nerol.mazerunner.effect.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    private void onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient) {
            List<RegistryEntry<StatusEffect>> toRemove = new ArrayList<>();
            for (StatusEffectInstance effect : user.getStatusEffects()) {
                RegistryEntry<StatusEffect> effectType = effect.getEffectType();  // Keep as RegistryEntry

                // Remove all effects except ModEffects.FLARE (also RegistryEntry)
                if (!effectType.equals(ModEffects.FLARE)) {
                    toRemove.add(effectType);
                }
            }

            for (RegistryEntry<StatusEffect> effectType : toRemove) {
                user.removeStatusEffect(effectType);
            }

            ItemStack recipeRemainder = stack.getItem().getRecipeRemainder();
            if (recipeRemainder != null && !recipeRemainder.isEmpty()) {
                cir.setReturnValue(recipeRemainder.copy());
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
}