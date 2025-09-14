package tired9494.eepy_critters;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import tired9494.eepy_critters.common.registry_helpers.*;

public final class EepyCritters {
    public static final String MOD_ID = "eepy_critters";

    public static void init() {
        ModEntityTypes.initEntityTypes();
        ModEntityAttributes.initEntityAttributes();
        ModEntitySpawns.initSpawnPlacements();
        ModItems.initItems();

        //only works for fabric, neoforge has additional register
        ClientLifecycleEvent.CLIENT_STARTED.register(listener -> {
            ModEntityRenderers.initEntityRenderers();
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(EepyCritters.MOD_ID, id);
    }
}
