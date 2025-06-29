package org.solstice.rollingStones.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;

import java.util.function.Function;

public class RollingAttributes {

	public static void init() {}

	public static final RegistryEntry<EntityAttribute> MAX_PULL_TIME_MULTIPLIER = register("max_pull_time_multiplier",
		key -> new ClampedEntityAttribute(key, 1, 0, Integer.MAX_VALUE).setTracked(true)
	);

	public static RegistryEntry<EntityAttribute> register(String name, Function<String, EntityAttribute> function) {
		Identifier id = RollingStones.of(name);
		String key = id.toTranslationKey("attribute");
		EntityAttribute attribute = function.apply(key);
		return Registry.registerReference(Registries.ATTRIBUTE, id, attribute);
	}

}
