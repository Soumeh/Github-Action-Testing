package org.solstice.rollingStones.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.solstice.rollingStones.RollingStones;

import java.util.function.Function;

public class RollingItems {

	public static void init() {}

	public static final Item SMITHING_STONE = register(
		"smithing_stone",
		Item::new,
		new Item.Settings()
	);
    public static final Item SIMPLE_SMITHING_STONE = register("simple_smithing_stone",
		Item::new,
		new Item.Settings()
			.maxCount(1)
    );
    public static final Item HONED_SMITHING_STONE = register("honed_smithing_stone",
		Item::new,
		new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.UNCOMMON)
    );
    public static final Item PERFECT_SMITHING_STONE = register("perfect_smithing_stone",
		Item::new,
		new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.RARE)
    );

	public static Item register(String name) {
		return register(name, Item::new);
	}

	public static Item register(String name, Function<Item.Settings, Item> function) {
		return register(name, function, new Item.Settings());
	}

	public static Item register(String name, Item.Settings settings) {
		return register(name, Item::new, settings);
	}

	public static Item register(String name, Function<Item.Settings, Item> function, Item.Settings settings) {
		Identifier id = RollingStones.of(name);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		Item item = function.apply(settings);
		return Registry.register(Registries.ITEM, key, item);
	}

}
