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

	public static final RegistryKey<Enchantment> TEST = of("test");

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
		registry.register(TEST, EMPTY);
	}

	private static RegistryKey<Enchantment> of(String name) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, RollingStones.of(name));
	}

}
