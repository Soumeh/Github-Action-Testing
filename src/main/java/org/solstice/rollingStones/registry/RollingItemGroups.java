package org.solstice.rollingStones.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.content.upgrade.Upgrade;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RollingItemGroups {

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(RollingItemGroups::addStrongbox);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(RollingItemGroups::addStrongbox);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(RollingItemGroups::addSmithingStones);
	}

	public static void addSmithingStones(FabricItemGroupEntries entries) {
		entries.addAll(List.of(
			RollingItems.SIMPLE_SMITHING_STONE.getDefaultStack().copy(),
			RollingItems.MALEDICTIVE_SMITHING_STONE.getDefaultStack().copy(),
			RollingItems.HONED_SMITHING_STONE.getDefaultStack().copy(),
			RollingItems.GILDED_SMITHING_STONE.getDefaultStack().copy(),
			RollingItems.MIDAS_SMITHING_STONE.getDefaultStack().copy()
		));
		entries.getContext().lookup().getOptionalWrapper(RollingRegistryKeys.UPGRADE).ifPresent(registry -> {
			addMaxTierSmithingStones(entries, registry);
			addAllTierSmithingStones(entries, registry);
		});
	}

	public static void addStrongbox(FabricItemGroupEntries entries) {
		entries.addAfter(Items.BARREL, RollingBlocks.STRONGBOX);
	}

	private static void addMaxTierSmithingStones(ItemGroup.Entries entries, RegistryWrapper<Upgrade> registryWrapper) {
		registryWrapper.streamEntries()
			.map(RollingItemGroups::maxTier)
			.forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY));
	}

	private static ItemStack maxTier(RegistryEntry<Upgrade> entry) {
		return SmithingStoneItem.forUpgrade(entry, entry.value().getDefinition().getMaxLevel());
	}

	private static void addAllTierSmithingStones(ItemGroup.Entries entries, RegistryWrapper<Upgrade> registryWrapper) {
		registryWrapper.streamEntries()
			.flatMap(RollingItemGroups::allTiers)
			.forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.SEARCH_TAB_ONLY));
	}

	private static Stream<ItemStack> allTiers(RegistryEntry<Upgrade> entry) {
		return IntStream.rangeClosed(1, entry.value().getDefinition().getMaxLevel()).mapToObj(tier -> SmithingStoneItem.forUpgrade(entry, tier));
	}

}
