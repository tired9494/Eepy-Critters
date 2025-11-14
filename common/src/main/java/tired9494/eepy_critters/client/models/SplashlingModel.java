package tired9494.eepy_critters.client.models;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import tired9494.eepy_critters.EepyCritters;
import tired9494.eepy_critters.common.entities.Splashling;

public class SplashlingModel extends DefaultedEntityGeoModel<Splashling> {
    public SplashlingModel() {
        super(EepyCritters.id("splashling"), true);
    }
}
