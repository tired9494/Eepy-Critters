package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluids;
import tired9494.eepy_critters.EepyCritters;
import tired9494.eepy_critters.common.entities.Splashling;

import java.util.function.Supplier;

public class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(EepyCritters.MOD_ID, Registries.ITEM);

    public static Supplier<Item> SPLASHLING_SPAWN_EGG;
    public static Supplier<Item> SPLASHLING_BUCKET;
    public static void initItems() {
        SPLASHLING_SPAWN_EGG = registerItem("splashling_spawn_egg", () ->
                new ArchitecturySpawnEggItem(EntityTypes.SPLASHLING, baseProperties("splashling_spawn_egg").arch$tab(CreativeModeTabs.SPAWN_EGGS)));
        SPLASHLING_BUCKET = registerItem("splashling_bucket", () ->
                new MobBucketItem(EntityTypes.SPLASHLING.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL, baseProperties("splashling_bucket").arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));

        ITEMS.register();
    }

    public static RegistrySupplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(EepyCritters.id(name), item);
    }

    public static Item.Properties baseProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, EepyCritters.id(name)));
    }
}
