package tired9494.eepy_critters.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tired9494.eepy_critters.common.ModTags;
import tired9494.eepy_critters.common.registry_helpers.ModEntityTypes;

public class Splashling extends AbstractSplashling {

    public Splashling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level, ModTags.Items.SPLASHLING_FOOD);
        this.goToFluidGoal = new SplashlingGoToWaterGoal(this, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    protected @NotNull PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        Splashling splashling = ModEntityTypes.SPLASHLING.get().create(level, EntitySpawnReason.BREEDING);
        return splashling;
    }

    public void travel(Vec3 travelVector) {
        if (this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.4));
        } else {
            super.travel(travelVector);
        }

    }
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    static class SplashlingGoToWaterGoal extends MoveToBlockGoal {
        private final Splashling splashling;

        SplashlingGoToWaterGoal(Splashling splashling, double speedModifier) {
            super(splashling, speedModifier, 8, 2);
            this.splashling = splashling;
        }

        public @NotNull BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        public boolean canContinueToUse() {
            return !this.splashling.isInWater() && this.isValidTarget(this.splashling.level(), this.blockPos);
        }

        public boolean canUse() {
            return !this.splashling.isInWater() && super.canUse();
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).isPathfindable(PathComputationType.LAND);
        }
    }
}
