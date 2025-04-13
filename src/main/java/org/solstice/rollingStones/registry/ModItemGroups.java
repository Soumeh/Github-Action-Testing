package org.solstice.rollingStones.registry;

import net.minecraft.item.ItemGroups;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.solstice.rollingStones.content.item.SmithingStoneItem;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModItemGroups {

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() != ItemGroups.INGREDIENTS) return;
		event.getParameters().lookup().getWrapperOrThrow(ModRegistryKeys.UPGRADE).streamEntries().forEach(entry -> {
			int maxTier = entry.value().getDefinition().getMaxLevel();
			for (int tier = 1; tier <= maxTier; tier++)
				event.add(SmithingStoneItem.forUpgrade(entry, tier));
		});
	}

}
