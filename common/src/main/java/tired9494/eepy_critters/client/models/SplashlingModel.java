package tired9494.eepy_critters.client.models;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import tired9494.eepy_critters.Eepy_critters;
import tired9494.eepy_critters.common.entities.Splashling;

public class SplashlingModel extends DefaultedEntityGeoModel<Splashling> {
    public SplashlingModel() {
        super(Eepy_critters.id("splashling"));
    }
}
