package tired9494.eepy_critters.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import tired9494.eepy_critters.common.ModTags;

public class Ashling extends AbstractSplashling {
    private static final ResourceLocation SUFFOCATING_MODIFIER_ID = ResourceLocation.withDefaultNamespace("suffocating");
    private static final AttributeModifier SUFFOCATING_MODIFIER;
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
    public Ashling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level, ModTags.Items.ASHLING_FOOD);
        this.goToFluidGoal = new AshlingGoToLavaGoal(this, 1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
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

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (this.isInLava()) {
            this.resetFallDistance();
        } else {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }
    public void tick() {
        if (!this.isNoAi()) {
            boolean warm;
            boolean mountedSuffocating;
            checkSuffocating: {
                BlockState blockState = this.level().getBlockState(this.blockPosition());
                warm = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > (double)0.0F;
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
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F).add(0.0F, 0.05, 0.0F));
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
        return this.entityData.get(DATA_SUFFOCATING);
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
        SUFFOCATING_MODIFIER = new AttributeModifier(SUFFOCATING_MODIFIER_ID, -0.34F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        DATA_SUFFOCATING = SynchedEntityData.defineId(Ashling.class, EntityDataSerializers.BOOLEAN);
    }

}
