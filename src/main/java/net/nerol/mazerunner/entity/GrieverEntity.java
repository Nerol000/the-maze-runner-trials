package net.nerol.mazerunner.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nerol.mazerunner.SoundEvent.ModSounds;
import net.nerol.mazerunner.TheMazeRunner;
import net.nerol.mazerunner.entity.goals.GrieverAttackGoal;
import net.nerol.mazerunner.entity.goals.RoarGoal;
import net.nerol.mazerunner.item.ModItems;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class GrieverEntity extends HostileEntity implements GeoAnimatable {
    private static final Random random = new Random();
    private boolean biteAnimationTriggered = false;
    private boolean stingAnimationTriggered = false;
    private boolean strikeAnimationTriggered = false;

    // TrackedData keys for syncing attack states & ticks
    private static final TrackedData<Integer> BITE_TICKS = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> BITE_ATTACK = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STING_TICKS = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> STING_ATTACK = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STRIKE_TICKS = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> STRIKE_ATTACK = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> ROAR_TICKS = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_ROARING = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> CLIMBING = DataTracker.registerData(GrieverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.walk");
    protected static final RawAnimation ATTACK_BITE_ANIM = RawAnimation.begin().thenPlay("animation.attack_bite");
    protected static final RawAnimation CHASE_ANIM = RawAnimation.begin().thenLoop("animation.chase");
    protected static final RawAnimation ATTACK_STING_ANIM = RawAnimation.begin().thenPlay("animation.attack_sting");
    protected static final RawAnimation ATTACK_STRIKE_ANIM = RawAnimation.begin().thenPlay("animation.attack_strike");
    protected static final RawAnimation CLIMB_ANIM = RawAnimation.begin().thenLoop("animation.climb");
    protected static final RawAnimation SNIFF_ANIM = RawAnimation.begin().thenPlay("animation.sniff");
    protected static final RawAnimation ROAR_ANIM = RawAnimation.begin().thenPlay("animation.roar");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GrieverEntity(EntityType<? extends GrieverEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BITE_TICKS, 0);
        builder.add(BITE_ATTACK, false);

        builder.add(STING_TICKS, 0);
        builder.add(STING_ATTACK, false);

        builder.add(STRIKE_TICKS, 0);
        builder.add(STRIKE_ATTACK, false);

        builder.add(ROAR_TICKS, 0);
        builder.add(IS_ROARING, false);

        builder.add(CLIMBING, false);
    }

    public void startBiteAttack() {
        if (!biteAnimationTriggered) {
            this.dataTracker.set(BITE_ATTACK, true);
            biteAnimationTriggered = true;
            this.dataTracker.set(BITE_TICKS, 14); // for damage logic
        }
    }

    public void startStingAttack() {
        if (!stingAnimationTriggered) {
            this.dataTracker.set(STING_ATTACK, true);
            stingAnimationTriggered = true;
            this.dataTracker.set(STING_TICKS, 20);
        }
    }
    public void startStrikeAttack() {
        if (!strikeAnimationTriggered) {
            this.dataTracker.set(STRIKE_ATTACK, true);
            strikeAnimationTriggered = true;
            this.dataTracker.set(STRIKE_TICKS, 18);
        }
    }

    public void stopAttacking() {
        this.dataTracker.set(BITE_TICKS, 0);
        this.dataTracker.set(STRIKE_TICKS, 0);
        this.dataTracker.set(STING_TICKS, 0);
    }

    public void startRoaring() {
        this.dataTracker.set(IS_ROARING, true);
        this.dataTracker.set(ROAR_TICKS, 44);
    }
    public boolean isRoaring() {
        return this.dataTracker.get(IS_ROARING) && this.dataTracker.get(ROAR_TICKS) > 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return age;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(roarController());
        controllers.add(attackAnimations());
        controllers.add(climbController());
        controllers.add(walkIdleController());
    }

    protected <T extends GeoAnimatable> AnimationController<T> walkIdleController() {
        return new AnimationController<>("walkIdle", state -> {
            if (isClimbing()) return PlayState.STOP;
            return state.setAndContinue(state.isMoving() ? WALK_ANIM : IDLE_ANIM);
        });
    }

    protected <T extends GeoAnimatable> AnimationController<T> climbController() {
        return new AnimationController<>("Climb", test -> {
            if (this.isClimbing())
                return test.setAndContinue(CLIMB_ANIM);
            return PlayState.STOP;
        });
    }

    protected <T extends GeoAnimatable> AnimationController<T> roarController() {
        return new AnimationController<>("Roar", event -> {
            if (this.dataTracker.get(IS_ROARING)) {
                return event.setAndContinue(ROAR_ANIM); // always continue while roaring
            }
            if (event.isCurrentAnimation(ROAR_ANIM)) {
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        });
    }

    protected <T extends GeoAnimatable> AnimationController<T> attackAnimations() {
        return new AnimationController<>("Attack", state -> {

            // Start attack animation only if it's triggered and not already playing
            if (this.dataTracker.get(BITE_ATTACK) && !state.isCurrentAnimation(ATTACK_BITE_ANIM)) {
                state.setAnimation(ATTACK_BITE_ANIM);
                return PlayState.CONTINUE;
            }
            if (this.dataTracker.get(STING_ATTACK) && !state.isCurrentAnimation(ATTACK_STING_ANIM)) {
                state.setAnimation(ATTACK_STING_ANIM);
                return PlayState.CONTINUE;
            }
            if (this.dataTracker.get(STRIKE_ATTACK) && !state.isCurrentAnimation(ATTACK_STRIKE_ANIM)) {
                state.setAnimation(ATTACK_STRIKE_ANIM);
                return PlayState.CONTINUE;
            }

            // If an attack animation is currently playing, keep it going
            if (state.isCurrentAnimation(ATTACK_BITE_ANIM)
                    || state.isCurrentAnimation(ATTACK_STING_ANIM)
                    || state.isCurrentAnimation(ATTACK_STRIKE_ANIM)) {
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        });
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (this.isClimbing()) {
            return;
        }
        super.takeKnockback(strength, x, z);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.isClimbing()) {
            // Prevent being pushed off wall
            if (this.horizontalCollision) {
                this.setVelocity(this.getVelocity().x, 0.25, this.getVelocity().z);
                this.setVelocity(this.getVelocity().multiply(1.0, 1.0, 1.0)); // keep current velocity
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            this.dataTracker.set(CLIMBING, this.horizontalCollision);
            decrementAttackTicks(BITE_TICKS, BITE_ATTACK);
            decrementAttackTicks(STING_TICKS, STING_ATTACK);
            decrementAttackTicks(STRIKE_TICKS, STRIKE_ATTACK);
            decrementAttackTicks(ROAR_TICKS, IS_ROARING);
        }
    }

    @Override
    public boolean isClimbing() {
        return this.dataTracker.get(CLIMBING);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SpiderNavigation(this, world);
    }

    private void decrementAttackTicks(TrackedData<Integer> tickData, TrackedData<Boolean> attackData) {
        int ticks = dataTracker.get(tickData);
        if (ticks > 0) {
            ticks--;
            dataTracker.set(tickData, ticks);
            if (ticks == 0) {
                dataTracker.set(attackData, false);
                // reset animation trigger so it can play again next time
                if (tickData == BITE_TICKS) biteAnimationTriggered = false;
                if (tickData == STING_TICKS) stingAnimationTriggered = false;
                if (tickData == STRIKE_TICKS) strikeAnimationTriggered = false;
            }
        }
    }

    @Override
    protected void initGoals() {
        this.navigation = new SpiderNavigation(this, this.getWorld());

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new RoarGoal(this));
        this.goalSelector.add(1, new GrieverAttackGoal(this, 1.05d) {
            @Override
            public boolean canStart() {
                return !GrieverEntity.this.isRoaring() && super.canStart();
            }
        });
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D) {
            @Override
            public boolean canStart() {
                return !GrieverEntity.this.isRoaring() && super.canStart();
            }
        });
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.ARMOR, 6.0f)
                .add(EntityAttributes.ATTACK_SPEED, 4.5f)
                .add(EntityAttributes.OXYGEN_BONUS, 3)
                .add(EntityAttributes.ATTACK_DAMAGE, 9.0f)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 10.0f)
                .add(EntityAttributes.JUMP_STRENGTH, 0.2f)
                .add(EntityAttributes.FOLLOW_RANGE, 240.0f)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.65f)
                .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, 0.5f)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, 3.4f)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 2.5f)
                .add(EntityAttributes.STEP_HEIGHT, 1.1f)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 0.1f)
                .add(EntityAttributes.MAX_HEALTH, 120.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3d);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.getWorld().isClient()) {
            int randomDrops = random.nextInt(7 - 3 + 1) + 3; // Between 3 and 7
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            ItemEntity netherite_scrap = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.NETHERITE_SCRAP, randomDrops));
            randomDrops = random.nextInt(9 - 4 + 1) + 4; // Between 4 and 9
            ItemEntity rotten_flesh = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.ROTTEN_FLESH, randomDrops));
            ItemEntity flare_injector = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), new ItemStack(ModItems.FLARE_INJECTOR, 1));
            serverWorld.spawnEntity(netherite_scrap);
            serverWorld.spawnEntity(rotten_flesh);
            serverWorld.spawnEntity(flare_injector);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Registries.SOUND_EVENT.get(Identifier.of(TheMazeRunner.MOD_ID, "griever.idle"));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RAVAGER_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(Registries.SOUND_EVENT.get(Identifier.of(TheMazeRunner.MOD_ID, "griever.walk")), 0.25f, 1.0f);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GRIEVER_ROAR;
    }
}