package tired9494.eepy_critters.fabric;

import tired9494.eepy_critters.EepyCritters;
import net.fabricmc.api.ModInitializer;

public final class EepyCrittersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        EepyCritters.init();
    }
}
