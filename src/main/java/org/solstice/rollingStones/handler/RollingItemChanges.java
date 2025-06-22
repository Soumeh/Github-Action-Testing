package org.solstice.rollingStones.handler;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RollingItemChanges {

	public static void modifyItems(DefaultItemComponentEvents.ModifyContext context) {

	}

	public static void addSmithingStones(FabricItemGroupEntries entries) {
		entries.getContext().lookup().getOptionalWrapper(RollingRegistryKeys.UPGRADE).ifPresent(registry -> {
			addMaxTierSmithingStones(entries, registry);
			addAllTierSmithingStones(entries, registry);
		});
	}

	private static void addMaxTierSmithingStones(ItemGroup.Entries entries, RegistryWrapper<Upgrade> registryWrapper) {
		registryWrapper.streamEntries()
			.map(RollingItemChanges::maxTier)
			.forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY));
	}

	private static ItemStack maxTier(RegistryEntry<Upgrade> entry) {
		return SmithingStoneItem.forUpgrade(entry, entry.value().getDefinition().getMaxLevel());
	}

	private static void addAllTierSmithingStones(ItemGroup.Entries entries, RegistryWrapper<Upgrade> registryWrapper) {
		registryWrapper.streamEntries()
			.flatMap(RollingItemChanges::allTiers)
			.forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.SEARCH_TAB_ONLY));
	}

	private static Stream<ItemStack> allTiers(RegistryEntry<Upgrade> entry) {
		return IntStream.rangeClosed(1, entry.value().getDefinition().getMaxLevel()).mapToObj(tier -> SmithingStoneItem.forUpgrade(entry, tier));
	}

}
