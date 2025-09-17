package tired9494.eepy_critters.common;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
    public static final String SPLASHLING = "splashling";
    public static final String ASHLING = "ashling";

    @Entry(category = SPLASHLING) public static int splashlingBucketCapacity = 8;

    @Entry(category = ASHLING) public static int ashlingBucketCapacity = 4;

}
