package net.nerol.mazerunner.entity.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.nerol.mazerunner.SoundEvent.ModSounds;
import net.nerol.mazerunner.entity.GrieverEntity;

import java.util.EnumSet;
import java.util.List;

public class GrieverRoarGoal extends Goal {
    private final GrieverEntity griever;
    private LivingEntity target;
    private int cooldown;

    public GrieverRoarGoal(GrieverEntity griever) {
        this.griever = griever;
        this.setControls(EnumSet.of(Control.MOVE , Control.LOOK));
        this.cooldown = 0;
    }

    @Override
    public boolean canStart() {
        if (griever.getTarget() != null && --cooldown <= 0 && (griever.getHealth() <= griever.getMaxHealth() / 3.0f)) {
            target = griever.getTarget();
            System.out.println("GrieverRoarGoal can start!");
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (griever.getWorld().isClient()) return;
        System.out.println("Started Roaring");
        this.griever.getNavigation().stop();
        griever.setVelocity(Vec3d.ZERO);
        griever.getLookControl().lookAt(target, 30.0F, 30.0F);
        griever.setRoaring(true);
        griever.playSound(ModSounds.GRIEVER_ROAR, 20.0f, 1.0f);

        List<GrieverEntity> nearbyGrievers = griever.getWorld()
                .getEntitiesByClass(GrieverEntity.class, griever.getBoundingBox().expand(75), e -> e != griever);

        for (GrieverEntity otherGriever : nearbyGrievers) {
            if (!otherGriever.isAlive()) continue;

            LivingEntity currentTarget = otherGriever.getTarget();
            if (!(currentTarget instanceof PlayerEntity)) {
                otherGriever.setTarget(target);
            }
        }
        this.cooldown = 3600;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}
