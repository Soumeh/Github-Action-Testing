package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.solstice.euclidsElements.tag.api.MapTagKey;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.upgrade.Upgrade;

public class RollingTags {

	public static void init() {}

	public static final MapTagKey<Item, RegistryEntry<Upgrade>> UPGRADE_MATERIALS = ofMap(RegistryKeys.ITEM, Upgrade.ENTRY_CODEC, "upgrade_materials");

	public static <T, R> MapTagKey<T, R> ofMap(RegistryKey<Registry<T>> registry, Codec<R> codec, String name) {
		return MapTagKey.of(registry, codec, RollingStones.of(name));
	}

}
