package tired9494.eepy_critters.common.registry_helpers;

import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntitySpawns {
    public static void initSpawnPlacements() {
        //TODO: update spawn rules
        SpawnPlacementsRegistry.register(ModEntityTypes.SPLASHLING, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.ASHLING, SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }
}
