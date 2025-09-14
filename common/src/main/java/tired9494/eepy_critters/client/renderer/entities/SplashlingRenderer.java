package tired9494.eepy_critters.client.renderer.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import tired9494.eepy_critters.client.models.SplashlingModel;
import tired9494.eepy_critters.client.models.TextureLocations;
import tired9494.eepy_critters.client.renderer.entities.layers.SplashlingBubbleLayer;
import tired9494.eepy_critters.common.entities.Splashling;

public class SplashlingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Splashling, R> {
    private static final float ADULT_SHADOW_RADIUS = 0.5f;

    public SplashlingRenderer(EntityRendererProvider.Context renderContext) {
        super(renderContext, new SplashlingModel());
        addRenderLayer(new SplashlingBubbleLayer<>(this,
                "bubble",
                TextureLocations.SPLASHLING_TEXTURE));
        this.shadowRadius = ADULT_SHADOW_RADIUS;
    }
}
