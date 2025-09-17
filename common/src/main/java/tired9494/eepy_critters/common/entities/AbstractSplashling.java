package tired9494.eepy_critters.common.entities;

import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;
import tired9494.eepy_critters.common.ModConfig;
import tired9494.eepy_critters.common.registry_helpers.ModItems;

import static software.bernie.geckolib.constant.DefaultAnimations.JUMP;

public class AbstractSplashling extends Animal implements GeoEntity, Bucketable {
    private final TemptGoal temptGoal;
    protected MoveToBlockGoal goToFluidGoal;
    private final TagKey<Item> foodTag;
    private static final EntityDataAccessor<Boolean> FROM_BUCKET;
    private static final boolean DEFAULT_FROM_BUCKET = false;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public AbstractSplashling(EntityType<? extends Animal> entityType, Level level, TagKey<Item> foodTag) {
        super(entityType, level);
        this.goToFluidGoal = null;
        this.foodTag = foodTag;
        this.temptGoal = new TemptGoal(this, 1.2, (itemStack) -> itemStack.is(foodTag), false);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public int getAirSupply() {
        return 300;
    }

    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0F));
        this.goalSelector.addGoal(3, temptGoal);
        this.goalSelector.addGoal(4, goToFluidGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.15F));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean canBeLeashed() {
        return true;
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROM_BUCKET, false);
    }

    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("FromBucket", this.fromBucket());
    }

    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setFromBucket(input.getBooleanOr("FromBucket", false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, (double)10.0F).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(foodTag);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>("jump_controller", 0, animTest -> PlayState.STOP)
                .triggerableAnim("jump", JUMP));
        controllers.add(DefaultAnimations.genericWalkIdleController().transitionLength(4));
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
        if (jumping && this.level() instanceof ServerLevel)
            triggerAnim("jump_controller", "jump");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, (compoundTag) -> {
            compoundTag.putInt("Age", this.getAge());
        });
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.setAge(tag.getIntOr("Age", 0));
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        ItemStack itemStack = new ItemStack(ModItems.SPLASHLING_BUCKET.get());
        itemStack.setDamageValue(ModConfig.splashlingBucketCapacity-1);
        return itemStack;
    }

    @Override
    public @NotNull SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    static {
        FROM_BUCKET = SynchedEntityData.defineId(AbstractSplashling.class, EntityDataSerializers.BOOLEAN);
    }
}
