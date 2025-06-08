package org.solstice.rollingStones.content.upgrade;

import net.minecraft.component.ComponentMap;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.List;
import java.util.Optional;

public class Upgrades {

    public static final RegistryKey<Upgrade> DENSITY = of("density");
	public static final RegistryKey<Upgrade> DRAWBACK = of("drawback");
	public static final RegistryKey<Upgrade> DURABILITY = of("durability");
	public static final RegistryKey<Upgrade> EFFICIENCY = of("efficiency");
	public static final RegistryKey<Upgrade> PROTECTION = of("protection");
	public static final RegistryKey<Upgrade> SHARPNESS = of("sharpness");
	public static final RegistryKey<Upgrade> TENSION = of("tension");

	public static final Upgrade EMPTY = new Upgrade(
		Optional.empty(),
		new Upgrade.Definition(RegistryEntryList.empty(), 0, List.of()),
		ComponentMap.EMPTY
	);

	public static void bootstrap(Registerable<Upgrade> registry) {
		registry.register(DENSITY, EMPTY);
		registry.register(DRAWBACK, EMPTY);
		registry.register(DURABILITY, EMPTY);
		registry.register(EFFICIENCY, EMPTY);
		registry.register(PROTECTION, EMPTY);
		registry.register(SHARPNESS, EMPTY);
		registry.register(TENSION, EMPTY);
	}

    private static RegistryKey<Upgrade> of(String name) {
        return RegistryKey.of(RollingRegistryKeys.UPGRADE, RollingStones.of(name));
    }

}
