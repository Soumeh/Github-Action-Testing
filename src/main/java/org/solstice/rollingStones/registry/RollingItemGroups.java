package org.solstice.rollingStones.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import org.solstice.rollingStones.content.item.SmithingStoneItem;

public class RollingItemGroups {

	public static void addSmithingStones(FabricItemGroupEntries entries) {
		entries.getContext().lookup().getWrapperOrThrow(RollingRegistryKeys.UPGRADE).streamEntries().forEach(entry -> {
			int maxTier = entry.value().getDefinition().getMaxLevel();
			for (int tier = 1; tier <= maxTier; tier++)
				entries.add(SmithingStoneItem.forUpgrade(entry, tier));
		});
	}

}
