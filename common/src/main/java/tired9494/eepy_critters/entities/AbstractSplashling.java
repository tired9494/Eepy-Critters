package tired9494.eepy_critters.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public abstract class AbstractSplashling extends Animal {
    public AbstractSplashling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }
}
