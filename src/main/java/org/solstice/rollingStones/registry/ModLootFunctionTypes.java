package org.solstice.rollingStones.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.loot.function.SetUpgradesLootFunction;

public class ModLootFunctionTypes {

	public static final DeferredRegister<LootFunctionType<?>> REGISTRY =
		DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, RollingStones.MOD_ID);

	public static DeferredHolder<LootFunctionType<?>, LootFunctionType<SetUpgradesLootFunction>> SET_UPGRADES =
		register("set_upgrades", SetUpgradesLootFunction.CODEC);

	private static <T extends LootFunction> DeferredHolder<LootFunctionType<?>, LootFunctionType<T>> register(String name, MapCodec<T> codec) {
		return REGISTRY.register(name, () -> new LootFunctionType<>(codec));
	}

}
