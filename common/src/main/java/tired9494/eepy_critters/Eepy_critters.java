package tired9494.eepy_critters;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import tired9494.eepy_critters.common.registry_helpers.EntityRenderers;
import tired9494.eepy_critters.common.registry_helpers.EntitySpawnPlacements;
import tired9494.eepy_critters.common.registry_helpers.EntityTypeAttributes;
import tired9494.eepy_critters.common.registry_helpers.EntityTypes;

public final class Eepy_critters {
    public static final String MOD_ID = "eepy_critters";

    public static void init() {
        EntityTypes.initEntityTypes();
        EntityTypeAttributes.initEntityAttributes();
        EntitySpawnPlacements.initSpawnPlacements();

        ClientLifecycleEvent.CLIENT_STARTED.register(listener -> {
            EntityRenderers.initEntityRenderers();
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(Eepy_critters.MOD_ID, id);
    }
}
