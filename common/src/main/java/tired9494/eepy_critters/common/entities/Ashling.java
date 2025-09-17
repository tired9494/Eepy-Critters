package tired9494.eepy_critters.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import tired9494.eepy_critters.common.ModTags;

public class Ashling extends Splashling {
    private TemptGoal temptGoal;
    private static final ResourceLocation SUFFOCATING_MODIFIER_ID = ResourceLocation.withDefaultNamespace("suffocating");
    private static final AttributeModifier SUFFOCATING_MODIFIER;
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
    public Ashling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.65));
        this.goalSelector.addGoal(2, new BreedGoal(this, (double)1.0F));
        this.temptGoal = new TemptGoal(this, 1.4, (itemStack) -> itemStack.is(ModTags.Items.ASHLING_FOOD), false);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new Ashling.AshlingGoToLavaGoal(this, (double)1.0F));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, (double)1.0F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, (double)1.0F, 60));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Ashling.class, 8.0F));
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean canStandOnFluid(FluidState fluidState) {
        return fluidState.is(FluidTags.LAVA);
    }

    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (this.isInLava()) {
            this.resetFallDistance();
        } else {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.ASHLING_FOOD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, (double)10.0F).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    public void tick() {
        if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
            this.makeSound(SoundEvents.STRIDER_HAPPY);
        } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
            this.makeSound(SoundEvents.STRIDER_RETREAT);
        }

        if (!this.isNoAi()) {
            boolean warm;
            boolean mountedSuffocating;
            checkSuffocating: {
                BlockState blockState = this.level().getBlockState(this.blockPosition());
                BlockState blockState2 = this.getBlockStateOnLegacy();
                warm = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > (double)0.0F;
                Entity mountedEntity = this.getVehicle();
                if (mountedEntity instanceof Strider mountedStrider) {
                    if (mountedStrider.isSuffocating()) {
                        mountedSuffocating = true;
                        break checkSuffocating;
                    }
                }

                mountedSuffocating = false;
            }
            this.setSuffocating(!warm || mountedSuffocating);
        }
        super.tick();
        this.floatAshling();
    }

    private void floatAshling() {
        if (this.isInLava()) {
            CollisionContext collisionContext = CollisionContext.of(this);
            if (collisionContext.isAbove(LiquidBlock.SHAPE_STABLE, this.blockPosition(), true) && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.5F).add((double)0.0F, 0.05, (double)0.0F));
            }
        }

    }

    static class AshlingGoToLavaGoal extends MoveToBlockGoal {
        private final Ashling ashling;

        AshlingGoToLavaGoal(Ashling ashling, double speedModifier) {
            super(ashling, speedModifier, 8, 2);
            this.ashling = ashling;
        }

        public @NotNull BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        public boolean canContinueToUse() {
            return !this.ashling.isInLava() && this.isValidTarget(this.ashling.level(), this.blockPos);
        }

        public boolean canUse() {
            return !this.ashling.isInLava() && super.canUse();
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(Blocks.LAVA) && level.getBlockState(pos.above()).isPathfindable(PathComputationType.LAND);
        }
    }

    public boolean isSuffocating() {
        return (Boolean)this.entityData.get(DATA_SUFFOCATING);
    }

    public void setSuffocating(boolean suffocating) {
        this.entityData.set(DATA_SUFFOCATING, suffocating);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance != null) {
            if (suffocating) {
                attributeInstance.addOrUpdateTransientModifier(SUFFOCATING_MODIFIER);
            } else {
                attributeInstance.removeModifier(SUFFOCATING_MODIFIER_ID);
            }
        }

    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SUFFOCATING, false);
    }

    static {
        SUFFOCATING_MODIFIER = new AttributeModifier(SUFFOCATING_MODIFIER_ID, (double)-0.34F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        DATA_SUFFOCATING = SynchedEntityData.defineId(Ashling.class, EntityDataSerializers.BOOLEAN);
    }

    private boolean isBeingTempted() {
        return this.temptGoal != null && this.temptGoal.isRunning();
    }

}
