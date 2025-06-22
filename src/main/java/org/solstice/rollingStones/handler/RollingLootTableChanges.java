package org.solstice.rollingStones.handler;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.solstice.rollingStones.registry.RollingItems;

import java.util.List;

public class RollingLootTableChanges {

	public static void modifyVillagerTrades() {
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 2, factories ->
			addOffer(factories, RollingItems.SIMPLE_SMITHING_STONE, 28, 4, 24)
		);
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.TOOLSMITH, 2, factories ->
			addOffer(factories, RollingItems.SIMPLE_SMITHING_STONE, 28, 4, 24)
		);
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 2, factories ->
			addOffer(factories, RollingItems.SIMPLE_SMITHING_STONE, 28, 4, 24)
		);
	}

	public static void modifyLootTables(RegistryKey<LootTable> key, LootTable.Builder builder, LootTableSource s, RegistryWrapper.WrapperLookup l) {
		switch (key.getValue().toString()) {
			case "minecraft:chests/abandoned_mineshaft" -> addPool(builder, RollingItems.SIMPLE_SMITHING_STONE);
			case "minecraft:chests/simple_dungeon" -> addPool(builder, RollingItems.SIMPLE_SMITHING_STONE, 0.5F);
			case "minecraft:entities/zombie",
				 "minecraft:entities/skeleton",
				 "minecraft:entities/spider" -> addPool(builder, RollingItems.SIMPLE_SMITHING_STONE, 0.05F);
			case "minecraft:chests/village/village_armorer",
				 "minecraft:chests/village/village_weaponsmith",
				 "minecraft:chests/village/village_toolsmith" -> addPool(builder, RollingItems.SIMPLE_SMITHING_STONE, 0.333F);

			case "minecraft:chests/nether_bridge" -> addPool(builder, RollingItems.HONED_SMITHING_STONE, 0.2F);
			case "minecraft:chests/bastion_other" -> addPool(builder, RollingItems.HONED_SMITHING_STONE, 0.333F);
			case "minecraft:gameplay/piglin_bartering" -> modifyPool(builder, RollingItems.HONED_SMITHING_STONE, 10);
			case "minecraft:entities/piglin_brute" -> addPool(builder, RollingItems.HONED_SMITHING_STONE, 0.05F);
		}
	}

	public static void addPool(LootTable.Builder builder, Item item) {
		builder.pool(LootPool.builder()
			.with(ItemEntry.builder(item))
			.rolls(ConstantLootNumberProvider.create(1))
			.build()
		);
	}

	public static void addPool(LootTable.Builder builder, Item item, float chance) {
		builder.pool(LootPool.builder()
			.with(ItemEntry.builder(item))
			.rolls(ConstantLootNumberProvider.create(1))
			.conditionally(RandomChanceLootCondition.builder(chance).build())
			.build()
		);
	}

	public static void modifyPool(LootTable.Builder builder, Item item, int weight) {
		builder.modifyPools(pool ->
			pool.with(ItemEntry.builder(item).weight(weight))
		);
	}

	public static void addOffer(List<TradeOffers.Factory> factories, Item item, int price, int maxUses, int experience) {
		TradeOffers.Factory factory = new TradeOffers.SellItemFactory(new ItemStack(item), price, 1, maxUses, experience);
		factories.add(factory);
	}

}
