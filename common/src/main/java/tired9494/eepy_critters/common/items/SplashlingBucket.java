package tired9494.eepy_critters.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import tired9494.eepy_critters.common.ModConfig;

public class SplashlingBucket extends MobBucketItem {
    public SplashlingBucket(EntityType<? extends Mob> type, Fluid content, SoundEvent emptySound, Properties properties) {
        super(type, content, emptySound, properties.durability(ModConfig.splashlingBucketDurability()));
    }

    //TODO: add bucket recharging, check BucketItem class
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            if (level.mayInteract(player, blockPos) && player.mayUseItemAt(blockPos2, direction, itemStack)) {
                BlockState blockState = level.getBlockState(blockPos);
                BlockPos blockPos3 = blockState.getBlock() instanceof LiquidBlockContainer ? blockPos : blockPos2;
                if (this.emptyContents(player, level, blockPos3, blockHitResult)) {
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos3, itemStack);
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (player.isCrouching() || itemStack.nextDamageWillBreak()) {
                        this.checkExtraContent(player, level, itemStack, blockPos3);
                        ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, getEmptySuccessItem(itemStack, player));
                        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack2);
                    }
                    itemStack.hurtWithoutBreaking(1, player);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    public static @NotNull ItemStack getEmptySuccessItem(ItemStack bucketStack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(Items.BUCKET) : bucketStack;
    }
}
