package org.solstice.rollingStones.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;

public class RollingAttributes {

	public static void init() {}

	public static RegistryEntry<EntityAttribute> MAX_PULL_TIME_MULTIPLIER = register("max_pull_time_multiplier", 1, 0, Integer.MAX_VALUE, true);

	public static RegistryEntry<EntityAttribute> register(String name, double fallback, double min, double max, boolean tracked) {
		Identifier id = RollingStones.of(name);
		EntityAttribute attribute = new ClampedEntityAttribute(id.toTranslationKey("attribute"), fallback, min, max).setTracked(tracked);
		return Registry.registerReference(Registries.ATTRIBUTE, id, attribute);
	}

}
