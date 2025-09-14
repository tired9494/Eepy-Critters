package tired9494.eepy_critters.client.renderer.entities.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;

public class SplashlingBubbleLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends CustomBoneTextureGeoLayer<T, O, R> {

    public SplashlingBubbleLayer(GeoRenderer<T, O, R> renderer, String boneName, ResourceLocation texture) {
        super(renderer, boneName, texture);
    }

    protected RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
