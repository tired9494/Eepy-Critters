package tired9494.eepy_critters.common.items;

import com.mojang.logging.LogUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static net.minecraft.world.level.block.LiquidBlock.LEVEL;

public class SplashlingBucket extends MobBucketItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static int MAX_DURABILITY;
    public SplashlingBucket(EntityType<? extends Mob> type, Fluid content, SoundEvent emptySound, Properties properties, int durability) {
        super(type, content, emptySound, properties.durability(durability));
        MAX_DURABILITY = durability;
    }

    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (tryEmptyBucket(level, player, itemStack, true)) {
                if (!player.hasInfiniteMaterials())
                    itemStack = ItemUtils.createFilledResult(itemStack, player, getEmptySuccessItem(itemStack, player));
                return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
            }
            return InteractionResult.PASS;
        }
        else if (itemStack.isDamaged()) {
            if (tryRechargeBucket(level, player, itemStack)) {
                if (!player.hasInfiniteMaterials())
                    itemStack.setDamageValue(itemStack.getDamageValue() - 1);
                return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
            }
            else {
                boolean releaseSplashling = !player.hasInfiniteMaterials() && itemStack.getDamageValue() + 1 >= MAX_DURABILITY;
                if (tryEmptyBucket(level, player, itemStack, releaseSplashling)) {
                    if (player.hasInfiniteMaterials()) {
                        return InteractionResult.SUCCESS;
                    }
                    else if (releaseSplashling) {
                        ItemStack emptyStack = ItemUtils.createFilledResult(itemStack, player, getEmptySuccessItem(itemStack, player));
                        return InteractionResult.SUCCESS.heldItemTransformedTo(emptyStack);
                    }
                    else {
                        itemStack.hurtWithoutBreaking(1, player);
                        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
                    }
                }
                else {
                    return InteractionResult.PASS;
                }
            }
        }
        else {
            if (tryEmptyBucket(level, player, itemStack, false)) {
                if (player.hasInfiniteMaterials()) {
                    return InteractionResult.SUCCESS;
                }
                else {
                    itemStack.hurtWithoutBreaking(1, player);
                    return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
                }
            }
            else {
                return InteractionResult.PASS;
            }
        }
    }

    private boolean tryRechargeBucket(Level level, Player player, ItemStack itemStack) {
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return false;
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        } else {
            BlockPos targetedBlockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos adjacentBlockPos = targetedBlockPos.relative(direction);
            // check if player is allowed to use bucket
            if (level.mayInteract(player, targetedBlockPos) && player.mayUseItemAt(adjacentBlockPos, direction, itemStack)) {
                BlockState blockState = level.getBlockState(targetedBlockPos);
                Block selectedBlock = blockState.getBlock();
                if (selectedBlock instanceof BucketPickup bucketpickup) {
                    if (blockState.getValue(LEVEL) == 0)
                        level.setBlock(targetedBlockPos, Blocks.AIR.defaultBlockState(), 11);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    bucketpickup.getPickupSound().ifPresent((soundEvent) -> player.playSound(soundEvent, 1.0F, 1.0F));
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, targetedBlockPos);
                    if (!level.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemStack);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private boolean tryEmptyBucket(Level level, Player player, ItemStack itemStack, boolean releaseSplashling) {
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return false;
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        } else {
            BlockPos targetedBlockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos adjacentBlockPos = targetedBlockPos.relative(direction);
            // check if player is allowed to use bucket
            if (level.mayInteract(player, targetedBlockPos) && player.mayUseItemAt(adjacentBlockPos, direction, itemStack)) {
                BlockState blockState = level.getBlockState(targetedBlockPos);
                Block selectedBlock = blockState.getBlock();
                // then try empty bucket
                BlockPos fluidFillPos = selectedBlock instanceof LiquidBlockContainer ? targetedBlockPos : adjacentBlockPos;
                if (this.emptyContents(player, level, fluidFillPos, blockHitResult)) {
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, fluidFillPos, itemStack);
                    }
                    if (releaseSplashling)
                        this.checkExtraContent(player, level, itemStack, fluidFillPos);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return true;
                }
            }
            return false;
        }
    }

    public static @NotNull ItemStack getEmptySuccessItem(ItemStack bucketStack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(Items.BUCKET) : bucketStack;
    }
}
