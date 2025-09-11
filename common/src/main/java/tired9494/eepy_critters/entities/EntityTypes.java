package tired9494.eepy_critters.entities;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tired9494.eepy_critters.Eepy_critters;

import java.util.function.Supplier;

public class EntityTypes {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Eepy_critters.MOD_ID, Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<Splashling>> SPLASHLING;

    public static ResourceKey<EntityType<?>> SPLASHLING_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Eepy_critters.MOD_ID, "splashling"));

    public static void initEntityTypes() {
        SPLASHLING = registerEntityType("splashling", () -> EntityType.Builder.of(Splashling::new, MobCategory.WATER_AMBIENT)
                .sized(0.8f, 0.8f)
                .eyeHeight(0.6f)
                .passengerAttachments(0.8f)
                .clientTrackingRange(10)
                .build(SPLASHLING_KEY));

        ENTITY_TYPES.register();
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityTypeSupplier) {
        return ENTITY_TYPES.register(ResourceLocation.fromNamespaceAndPath(Eepy_critters.MOD_ID, name), entityTypeSupplier);
    }

}
