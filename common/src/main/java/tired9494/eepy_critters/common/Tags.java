package tired9494.eepy_critters.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tired9494.eepy_critters.EepyCritters;

public class Tags {
    public static class Items {
        public static final TagKey<Item> SPLASHLING_FOOD = create("splashling_food");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, EepyCritters.id(name));
        }
    }
}
