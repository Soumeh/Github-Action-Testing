package org.solstice.rollingStones.handler;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

public class RollingItemChanges {

	public static void modifyItems(DefaultItemComponentEvents.ModifyContext context) {

	}

	public static void addSmithingStones(FabricItemGroupEntries entries) {
		entries.getContext().lookup().getWrapperOrThrow(RollingRegistryKeys.UPGRADE).streamEntries().forEach(entry -> {
			int maxTier = entry.value().getDefinition().getMaxLevel();
			for (int tier = 1; tier <= maxTier; tier++)
				entries.add(SmithingStoneItem.forUpgrade(entry, tier));
		});
	}

}
