package tired9494.eepy_critters.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
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
        super(entityType, level, ModTags.Items.SPLASHLING_FOOD, FluidTags.WATER);
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
}
