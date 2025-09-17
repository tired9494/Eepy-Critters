package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import tired9494.eepy_critters.client.renderer.entities.AshlingRenderer;
import tired9494.eepy_critters.client.renderer.entities.SplashlingRenderer;

public class ModEntityRenderers {
    public static void initEntityRenderers() {
        EntityRendererRegistry.register(ModEntityTypes.SPLASHLING, SplashlingRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.ASHLING, AshlingRenderer::new);
    }
}
