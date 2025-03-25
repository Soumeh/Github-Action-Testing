package org.solstice.rollingStones.content.upgrade;

import net.minecraft.registry.RegistryKey;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.registry.ModRegistryKeys;

public class Upgrades {

    public static final RegistryKey<Upgrade> DURABILITY = of("durability");

    private static RegistryKey<Upgrade> of(String name) {
        return RegistryKey.of(ModRegistryKeys.UPGRADE, RollingStones.of(name));
    }

}
