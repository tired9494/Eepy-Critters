package tired9494.eepy_critters.client.models;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import tired9494.eepy_critters.EepyCritters;
import tired9494.eepy_critters.common.entities.Ashling;

public class AshlingModel extends DefaultedEntityGeoModel<Ashling> {
    public AshlingModel() {
        super(EepyCritters.id("splashling"), true);
        withAltTexture(EepyCritters.id("ashling"));
    }
}
