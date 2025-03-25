package org.solstice.rollingStones;

import net.minecraft.util.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solstice.rollingStones.registry.ModComponentTypes;
import org.solstice.rollingStones.registry.ModItems;
import org.solstice.rollingStones.registry.ModRecipeSerializers;
import org.solstice.rollingStones.registry.ModRecipeTypes;

@Mod(RollingStones.MOD_ID)
public class RollingStones {

    public static final String MOD_ID = "rolling_stones";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier of(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public RollingStones(IEventBus bus) {
        ModItems.REGISTRY.register(bus);
        ModComponentTypes.REGISTRY.register(bus);
        ModRecipeTypes.REGISTRY.register(bus);
        ModRecipeSerializers.REGISTRY.register(bus);
    }

}
