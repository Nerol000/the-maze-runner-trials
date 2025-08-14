package net.nerol.mazerunner.entity.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.nerol.mazerunner.SoundEvent.ModSounds;
import net.nerol.mazerunner.entity.GrieverEntity;

import java.util.EnumSet;

public class RoarGoal extends Goal {
    private final GrieverEntity griever;
    private int cooldown;
    private boolean justAggroed;
    private LivingEntity roarTarget;

    public RoarGoal(GrieverEntity griever) {
        this.griever = griever;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0 || griever.isRoaring()) return false;

        LivingEntity target = griever.getTarget();
        if (target instanceof PlayerEntity && !justAggroed) {
            justAggroed = true;
            roarTarget = target;
            return true;
        }

        if (target != null && griever.getHealth() <= griever.getMaxHealth() * 0.25f) {
            roarTarget = target;
            return true;
        }

        if (target == null && griever.getRandom().nextInt(4800) == 0) {
            roarTarget = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        return griever.isRoaring();
    }

    @Override
    public void start() {
        griever.startRoaring();
        griever.getNavigation().stop(); // stop movement
        griever.playSound(ModSounds.GRIEVER_ROAR, 20.0f, 1.0f);
    }

    @Override
    public void tick() {
        // Decrement cooldown
        if (cooldown > 0) cooldown--;

        // Stop navigation each tick to prevent other goals moving the Griever
        griever.getNavigation().stop();

        // Face target if exists
        if (roarTarget != null && roarTarget.isAlive()) {
            griever.getLookControl().lookAt(roarTarget, 30.0F, 30.0F);
            griever.setYaw(griever.headYaw);
        }
    }

    @Override
    public void stop() {
        roarTarget = null;
    }
}