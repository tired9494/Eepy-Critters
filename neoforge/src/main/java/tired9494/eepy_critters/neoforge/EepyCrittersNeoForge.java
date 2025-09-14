package tired9494.eepy_critters.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import tired9494.eepy_critters.EepyCritters;
import net.neoforged.fml.common.Mod;
import tired9494.eepy_critters.client.renderer.entities.SplashlingRenderer;
import tired9494.eepy_critters.common.registry_helpers.ModEntityTypes;

@Mod(EepyCritters.MOD_ID)
public final class EepyCrittersNeoForge {
    public EepyCrittersNeoForge() {
        // Run our common setup.
        EepyCritters.init();
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntityTypes.SPLASHLING.get(), SplashlingRenderer::new);
        }
    }
}
