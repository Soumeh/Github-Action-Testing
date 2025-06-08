package org.solstice.rollingStones.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class RollingLootTables {

	public static void modifyLootTables(RegistryKey<LootTable> key, LootTable.Builder builder, LootTableSource source, RegistryWrapper.WrapperLookup lookup) {
		if (key == LootTables.PIGLIN_BARTERING_GAMEPLAY) modifyBarterLootTable(builder);
		else if (key == LootTables.ABANDONED_MINESHAFT_CHEST) modifyMineshaftLootTable(builder);
	}

	public static void modifyBarterLootTable(LootTable.Builder builder) {
		builder.modifyPools(pool ->
			pool.with(ItemEntry.builder(RollingItems.HONED_SMITHING_STONE).weight(10))
		);
	}

	public static void modifyMineshaftLootTable(LootTable.Builder builder) {
		builder.pool(LootPool.builder()
			.with(ItemEntry.builder(RollingItems.SIMPLE_SMITHING_STONE))
			.rolls(ConstantLootNumberProvider.create(1))
			.build()
		);
	}

}
