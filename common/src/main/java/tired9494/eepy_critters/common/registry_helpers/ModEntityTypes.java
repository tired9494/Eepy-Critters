package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tired9494.eepy_critters.EepyCritters;
import tired9494.eepy_critters.common.entities.Ashling;
import tired9494.eepy_critters.common.entities.Splashling;

import java.util.function.Supplier;

public class ModEntityTypes {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(EepyCritters.MOD_ID, Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<Splashling>> SPLASHLING;
    public static RegistrySupplier<EntityType<Ashling>> ASHLING;

    public static ResourceKey<EntityType<?>> SPLASHLING_KEY = ResourceKey.create(Registries.ENTITY_TYPE, EepyCritters.id("splashling"));
    public static ResourceKey<EntityType<?>> ASHLING_KEY = ResourceKey.create(Registries.ENTITY_TYPE, EepyCritters.id("ashling"));

    public static void initEntityTypes() {
        SPLASHLING = registerEntityType("splashling", () -> EntityType.Builder.of(Splashling::new, MobCategory.WATER_CREATURE)
                .sized(0.8f, 0.4f)
                .eyeHeight(0.2f)
                .passengerAttachments(0.4f)
                .clientTrackingRange(10)
                .build(SPLASHLING_KEY));
        ASHLING = registerEntityType("ashling", () -> EntityType.Builder.of(Ashling::new, MobCategory.CREATURE)
                .sized(0.8f, 0.4f)
                .eyeHeight(0.2f)
                .passengerAttachments(0.4f)
                .clientTrackingRange(10)
                .build(ASHLING_KEY));

        ENTITY_TYPES.register();
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityTypeSupplier) {
        return ENTITY_TYPES.register(EepyCritters.id(name), entityTypeSupplier);
    }

}
