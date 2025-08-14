package net.nerol.mazerunner.entity.goals;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.nerol.mazerunner.entity.GrieverEntity;
import net.nerol.mazerunner.entity.animations.AttackAnimations;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class GrieverAttackGoal extends Goal {
    private final GrieverEntity entity;
    private LivingEntity target;
    private final double speed;
    private final double wallRange = 36.0;
    private final double sightRange = 200.0;
    private int cooldown;
    private boolean hasLineOfSight;

    public GrieverAttackGoal(GrieverEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        List<LivingEntity> entities = entity.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(entity.getX() - sightRange, entity.getY() - sightRange, entity.getZ() - sightRange,
                        entity.getX() + sightRange, entity.getY() + sightRange, entity.getZ() + sightRange),
                e -> isValidTarget(e) && e.isAlive()
        );
        LivingEntity closestPlayer = entities.stream()
                .filter(e -> e instanceof PlayerEntity)
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(entity)))
                .orElse(null);

        if (closestPlayer != null && canAttack(closestPlayer)) {
            target = closestPlayer;
            return true;
        }

        LivingEntity closestOther = entities.stream()
                .filter(e -> !(e instanceof PlayerEntity))
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(entity)))
                .orElse(null);

        if (closestOther != null && canAttack(closestOther)) {
            target = closestOther;
            return true;
        }

        target = null;
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if (target instanceof PlayerEntity player) {
            if (player.isSpectator() || player.isCreative()) {
                return false;
            }
        }
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        // No need to check LOS here, done in tick()
        this.entity.setAttacking(true);
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        target = null;
        entity.stopAttacking();
        this.entity.setAttacking(false);
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        if (cooldown > 0) cooldown--;

        hasLineOfSight = checkLineOfSight(target);

        moveToTarget();

        double distanceSq = entity.squaredDistanceTo(target);

        if (distanceSq <= 4.0D && isCooledDown()) {
            resetCooldown();
            this.entity.swingHand(Hand.MAIN_HAND);
            this.entity.tryAttack(getServerWorld(this.entity), target);
            AttackAnimations.performRandomAttack(entity, target);
        }
    }

    private void moveToTarget() {
        if (target == null) return;

        double speedFactor;
        if (entity.isClimbing() && hasLineOfSight) {
            speedFactor = 1.80d;
        }
        else if (entity.isClimbing() || hasLineOfSight) {
            speedFactor = 1.415d;
        }
        else {
            speedFactor = 1.0d;
        }

        entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), speed * speedFactor);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    private boolean checkLineOfSight(LivingEntity targetToCheck) {
        RaycastContext context = new RaycastContext(
                entity.getCameraPosVec(1.0F),
                targetToCheck.getCameraPosVec(1.0F),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        );

        BlockHitResult result = entity.getWorld().raycast(context);
        return result.getType() == HitResult.Type.MISS;
    }

    private boolean canAttack(LivingEntity possibleTarget) {
        double distSq = entity.squaredDistanceTo(possibleTarget);

        if (distSq <= sightRange * sightRange) {
            if (checkLineOfSight(possibleTarget)) {
                return true;
            }
        }

        return distSq <= wallRange * wallRange;
    }

    private boolean isValidTarget(LivingEntity e) {
        if (e instanceof PlayerEntity player) {
            return !player.isSpectator() && !player.isCreative();
        }
        return e instanceof PassiveEntity || e.getType() == EntityType.VILLAGER;
    }

    protected void resetCooldown() {
        cooldown = getTickCount(24);
    }

    protected boolean isCooledDown() {
        return cooldown <= 0;
    }

}

