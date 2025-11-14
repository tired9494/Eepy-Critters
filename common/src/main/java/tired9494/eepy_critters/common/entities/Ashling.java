package tired9494.eepy_critters.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;
import tired9494.eepy_critters.common.ModTags;
import tired9494.eepy_critters.common.registry_helpers.ModEntityTypes;

public class Ashling extends AbstractSplashling {
    private static final ResourceLocation SUFFOCATING_MODIFIER_ID = ResourceLocation.withDefaultNamespace("suffocating");
    private static final AttributeModifier SUFFOCATING_MODIFIER;
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
    public Ashling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level, ModTags.Items.ASHLING_FOOD, FluidTags.LAVA);
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

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        Ashling ashling = ModEntityTypes.ASHLING.get().create(level, EntitySpawnReason.BREEDING);
        return ashling;
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
            this.setSuffocating(checkSuffocating());
        }
        super.tick();
        this.floatAshling();
    }

    private boolean checkSuffocating() {
        BlockState blockState = this.level().getBlockState(this.blockPosition());
        boolean warm = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > (double)0.0F;
        if (!warm) return true;
        Entity mountedEntity = this.getVehicle();
        return mountedEntity instanceof Ashling mountedAshling && mountedAshling.isSuffocating();
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
