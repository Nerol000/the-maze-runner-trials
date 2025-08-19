package net.nerol.mazerunner.entity.goals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.nerol.mazerunner.SoundEvent.ModSounds;
import net.nerol.mazerunner.entity.GrieverEntity;

import java.util.EnumSet;

public class GrieverAmbientGoal extends Goal {
    private final GrieverEntity griever;
    private int cooldown;
    private static final int MIN_COOLDOWN = 200;
    private static final int MAX_COOLDOWN = 600;

    public GrieverAmbientGoal(GrieverEntity griever) {
        this.griever = griever;
        this.setControls(EnumSet.of(Control.MOVE , Control.LOOK));
        this.cooldown = getNewCooldown();
    }

    @Override
    public boolean canStart() {
        return griever.getTarget() == null && --cooldown <= 0;
    }

    @Override
    public void start() {
        if (griever.getWorld().isClient()) return;

        this.griever.getNavigation().stop();
        griever.setVelocity(Vec3d.ZERO);
        if (griever.getRandom().nextBoolean()) {
            griever.setRoaring(true);
            griever.playSound(ModSounds.GRIEVER_ROAR, 20.0f, 1.0f);
            System.out.println("Griever roaring");

        }
        else {
            griever.setSniffing(true);
            griever.playSound(SoundEvents.ENTITY_WARDEN_SNIFF, 5.0f, 1.0f);
            System.out.println("Griever sniffing");
        }
        cooldown = getNewCooldown();
    }

    private int getNewCooldown() {
        return griever.getRandom().nextBetween(MIN_COOLDOWN, MAX_COOLDOWN);
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}
