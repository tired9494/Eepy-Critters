package tired9494.eepy_critters.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import tired9494.eepy_critters.Eepy_critters;
import net.neoforged.fml.common.Mod;
import tired9494.eepy_critters.client.renderer.entities.SplashlingRenderer;
import tired9494.eepy_critters.common.registry_helpers.EntityTypes;

@Mod(Eepy_critters.MOD_ID)
public final class Eepy_crittersNeoForge {
    public Eepy_crittersNeoForge() {
        // Run our common setup.
        Eepy_critters.init();
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityTypes.SPLASHLING.get(), SplashlingRenderer::new);
        }
    }
}
