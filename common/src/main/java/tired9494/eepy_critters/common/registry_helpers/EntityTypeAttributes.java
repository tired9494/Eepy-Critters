package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import tired9494.eepy_critters.common.entities.Splashling;

public class EntityTypeAttributes {
    public static void initEntityAttributes() {
        EntityAttributeRegistry.register(EntityTypes.SPLASHLING, Splashling::createAttributes);

    }
}
