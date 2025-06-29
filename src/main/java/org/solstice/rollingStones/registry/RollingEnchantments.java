package org.solstice.rollingStones.registry;

import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import org.solstice.rollingStones.RollingStones;

import java.util.List;
import java.util.Optional;

public class RollingEnchantments {

	public static final RegistryKey<Enchantment> FREAK_WHACKER = of("freak_whacker");
	public static final RegistryKey<Enchantment> HATER = of("hater");
	public static final RegistryKey<Enchantment> ELEMENTAL_WARD = of("elemental_ward");
	public static final RegistryKey<Enchantment> IMPACT_PROTECTION = of("impact_protection");

	public static final Enchantment EMPTY = new Enchantment(
		Text.empty(),
		new Enchantment.Definition(
			RegistryEntryList.empty(),
			Optional.empty(),
			0, 0,
			new Enchantment.Cost(0, 0),
			new Enchantment.Cost(0, 0),
			0,
			List.of()
		),
		RegistryEntryList.empty(),
		ComponentMap.EMPTY
	);

	public static void bootstrap(Registerable<Enchantment> registry) {
		registry.register(FREAK_WHACKER, EMPTY);
		registry.register(HATER, EMPTY);
		registry.register(ELEMENTAL_WARD, EMPTY);
		registry.register(IMPACT_PROTECTION, EMPTY);
	}

	private static RegistryKey<Enchantment> of(String name) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, RollingStones.of(name));
	}

}
