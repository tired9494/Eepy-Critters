package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import tired9494.eepy_critters.client.renderer.entities.SplashlingRenderer;

public class EntityRenderers {
    public static void initEntityRenderers() {
        EntityRendererRegistry.register(EntityTypes.SPLASHLING, SplashlingRenderer::new);
    }
}
