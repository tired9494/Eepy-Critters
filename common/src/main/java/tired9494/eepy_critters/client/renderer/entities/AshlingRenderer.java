package tired9494.eepy_critters.client.renderer.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import tired9494.eepy_critters.client.models.AshlingModel;
import tired9494.eepy_critters.client.models.TextureLocations;
import tired9494.eepy_critters.client.renderer.entities.layers.SplashlingBubbleLayer;
import tired9494.eepy_critters.common.entities.Ashling;

public class AshlingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Ashling, R> {
    private static final float ADULT_SHADOW_RADIUS = 0.5f;

    public AshlingRenderer(EntityRendererProvider.Context renderContext) {
        super(renderContext, new AshlingModel());
        addRenderLayer(new SplashlingBubbleLayer<>(this,
                "bubble",
                TextureLocations.ASHLING_TEXTURE));
        this.shadowRadius = ADULT_SHADOW_RADIUS;
    }
}