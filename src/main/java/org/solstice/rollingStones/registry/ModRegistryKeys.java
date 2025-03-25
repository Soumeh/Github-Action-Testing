package org.solstice.rollingStones.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.upgrade.Upgrade;

public class ModRegistryKeys {

    public static final RegistryKey<Registry<Upgrade>> UPGRADE = of("upgrade");

    public static <T> RegistryKey<Registry<T>> of(String name) {
        return RegistryKey.ofRegistry(RollingStones.of(name));
    }

}
