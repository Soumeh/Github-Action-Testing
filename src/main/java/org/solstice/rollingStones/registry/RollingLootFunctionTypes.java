package org.solstice.rollingStones.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.loot.function.SetUpgradesLootFunction;

public class RollingLootFunctionTypes {

	public static void init() {}

	public static LootFunctionType<SetUpgradesLootFunction> SET_UPGRADES =
		register("set_upgrades", SetUpgradesLootFunction.CODEC);

	private static <T extends LootFunction> LootFunctionType<T> register(String name, MapCodec<T> codec) {
		return Registry.register(Registries.LOOT_FUNCTION_TYPE, RollingStones.of(name), new LootFunctionType<>(codec));
	}

}
